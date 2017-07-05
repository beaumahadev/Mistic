/*
 * GDXRoot.java
 *
 * This is the primary class file for running the game.  It is the "static main" of
 * LibGDX.  In the first lab, we extended ApplicationAdapter.  In previous lab
 * we extended Game.  This is because of a weird graphical artifact that we do not
 * understand.  Transparencies (in 3D only) is failing when we use ApplicationAdapter. 
 * There must be some undocumented OpenGL code in setScreen.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
 package edu.cornell.gdiac;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.assets.loaders.resolvers.*;

import edu.cornell.gdiac.mistic.*;
import edu.cornell.gdiac.util.*;
import org.lwjgl.Sys;

/**
 * Root class for a LibGDX.  
 * 
 * This class is technically not the ROOT CLASS. Each platform has another class above
 * this (e.g. PC games use DesktopLauncher) which serves as the true root.  However, 
 * those classes are unique to each platform, while this class is the same across all 
 * plaforms. In addition, this functions as the root class all intents and purposes, 
 * and you would draw it as a root class in an architecture specification.  
 */
public class GDXRoot extends Game implements ScreenListener {
	/** AssetManager to load game assets (textures, sounds, etc.) */
	private AssetManager manager;
	/** Drawing context to display graphics (VIEW CLASS) */
	private GameCanvas canvas; 
	/** Player mode for the asset loading screen (CONTROLLER CLASS) */
	private LoadingMode loading;

	private MenuController menu;
	private LevelSelectController levelSelect;
	private CreditsController credits;
	private OptionsController options;
	/** Player mode for the the game proper (CONTROLLER CLASS) */
	private int current;
	/** List of all WorldControllers */
	private WorldController[] controllers;
	
	/**
	 * Creates a new game from the configuration settings.
	 *
	 * This method configures the asset manager, but does not load any assets
	 * or assign any screen.
	 */
	public GDXRoot() {
		// Start loading with the asset manager
		manager = new AssetManager();
		
		// Add font support to the asset manager
		FileHandleResolver resolver = new InternalFileHandleResolver();
		manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}

	/** 
	 * Called when the Application is first created.
	 * 
	 * This is method immediately loads assets for the loading screen, and prepares
	 * the asynchronous loader for all other assets.
	 */
	public void create() {
		canvas  = new GameCanvas();
		loading = new LoadingMode(canvas,manager,1);
		menu = new MenuController();
		levelSelect = new LevelSelectController();
		credits = new CreditsController();
		options = new OptionsController();
		
		// Initialize game world
		controllers = new WorldController[1];
		controllers[0] = new GameController(this);
		controllers[0].preLoadContent(manager);
		menu.preLoadContent(manager);
		menu.setScreenListener(this);
		levelSelect.preLoadContent(manager);
		levelSelect.setScreenListener(this);
		credits.preLoadContent(manager);
		credits.setScreenListener(this);
		options.preLoadContent(manager);
		options.setScreenListener(this);
		loading.setScreenListener(this);
		setScreen(loading);
	}

	/** 
	 * Called when the Application is destroyed. 
	 *
	 * This is preceded by a call to pause().
	 */
	public void dispose() {
		// Call dispose on our children
		setScreen(null);
		for(int ii = 0; ii < controllers.length; ii++) {
			controllers[0].unloadContent(manager);
			controllers[0].dispose();
		}

		canvas.dispose();
		canvas = null;
	
		// Unload all of the resources
		manager.clear();
		manager.dispose();
		super.dispose();
	}
	
	/**
	 * Called when the Application is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to create().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		canvas.resize();
		super.resize(width,height);
	}

	
	/**
	 * The given screen has made a request to exit its player mode.
	 *
	 * The value exitCode can be used to implement menu options.
	 *
	 * @param screen   The screen requesting to exit
	 * @param exitCode The state of the screen upon exit
	 */
	public void exitScreen(Screen screen, int exitCode) {
		if (screen == loading) {
			menu.loadContent(manager,canvas);
			menu.setScreenListener(this);
			menu.setCanvas(canvas);
			menu.reset();
			setScreen(menu);
			//loading.dispose();
			//loading = null;
		} else if (exitCode == WorldController.EXIT_NEXT) {
			current = (current+1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_PREV) {
			current = (current+controllers.length-1) % controllers.length;
			controllers[current].reset();
			setScreen(controllers[current]);
		} else if (exitCode == WorldController.EXIT_QUIT) {
			// We quit the main application
			Gdx.app.exit();
		} else if (exitCode == MenuController.EXIT_TO_PLAY) {
			controllers[0] = new GameController(this);
			controllers[0].preLoadContent(manager);
			controllers[0].loadContent(manager, canvas);
			controllers[0].setScreenListener(this);
			controllers[0].setCanvas(canvas);
			controllers[0].reset();
			setScreen(controllers[0]);
			//menu.dispose();
		} else if (exitCode == MenuController.EXIT_TO_LEVEL_SELECT) {
			levelSelect.loadContent(manager, canvas);
			levelSelect.setScreenListener(this);
			levelSelect.setCanvas(canvas);
			levelSelect.reset();
			setScreen(levelSelect);
		} else if (exitCode == LevelSelectController.EXIT_TO_MENU) {
			menu.reset();
			setScreen(menu);
		} else if (exitCode == MenuController.EXIT_TO_CREDITS) {
			credits.loadContent(manager, canvas);
			credits.setScreenListener(this);
			credits.setCanvas(canvas);
			credits.reset();
			setScreen(credits);
		} else if (exitCode == MenuController.EXIT_TO_OPTIONS) {
			options.loadContent(manager, canvas);
			options.setScreenListener(this);
			options.setCanvas(canvas);
			options.reset();
			setScreen(options);
		}
	}

}
