/*
 * WorldController.java
 *
 * This is the most important new class in this lab.  This class serves as a combination 
 * of the CollisionController and GameplayController from the previous lab.  There is not 
 * much to do for collisions; Box2d takes care of all of that for us.  This controller 
 * invokes Box2d and then performs any after the fact modifications to the data 
 * (e.g. gameplay).
 *
 * If you study this class, and the contents of the edu.cornell.cs3152.physics.obstacles
 * package, you should be able to understand how the Physics engine works.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * LibGDX version, 2/6/2015
 */
package edu.cornell.gdiac;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.*;
import edu.cornell.gdiac.mistic.BoardModel;
import edu.cornell.gdiac.mistic.Minimap;
import edu.cornell.gdiac.obstacle.Obstacle;
import edu.cornell.gdiac.obstacle.*;
import edu.cornell.gdiac.util.*;


/**
 * Base class for a world-specific controller.
 *
 *
 * A world has its own objects, assets, and input controller.  Thus this is 
 * really a mini-GameEngine in its own right.  The only thing that it does
 * not do is create a GameCanvas; that is shared with the main application.
 *
 * You will notice that asset loading is not done with static methods this time.  
 * Instance asset loading makes it easier to process our game modes in a loop, which 
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public abstract class WorldController implements Screen {
	
	/** 
	 * Tracks the asset state.  Otherwise subclasses will try to load assets 
	 */
	protected enum AssetState {
		/** No assets loaded */
		EMPTY,
		/** Still loading assets */
		LOADING,
		/** Assets are complete */
		COMPLETE
	}
	/** Track asset loading from all instances and subclasses */
	protected AssetState worldAssetState = AssetState.EMPTY;
	/** Track all loaded assets (for unloading purposes) */
	protected Array<String> assets;
	
	// Pathnames to shared assets
	/** File to texture for walls and platforms */
	private static String EARTH_FILE = "shared/earthtile.png";
	/** File to texture for the win door */
	private static String GOAL_FILE = "shared/goaldoor.png";
	/** Retro font for displaying messages */
	private static String FONT_FILE = "shared/RetroGame.ttf";
	/** Minimap asset */
	public static String JSON_FILE;
	public static String MINIMAP_FILE = "minimaps/BETA_basic_blockfog.png"; // MINIMAP ASSET PATH PUT HERE!!!
	private static int FONT_SIZE = 64;

	public static final String level1minimap = "minimaps/level1.png";
	public static final String level2minimap = "minimaps/level2.png";
	public static final String level3minimap = "minimaps/level3.png";
	public static final String level4minimap = "minimaps/level4.png";
	public static final String level5minimap = "minimaps/level5.png";
	public static final String level6minimap = "minimaps/level7.png";
	public static final String level7minimap = "minimaps/level6.png";
	public static final String level8minimap = "minimaps/levelstar.png";
	public static final String level9minimap = "minimaps/level14.png";
	public static final String level10minimap = "minimaps/level13.png";
	public static final String level11minimap = "minimaps/levelboxes.png";
	public static final String level12minimap = "minimaps/levelmistic.png";

	/** The texture for walls and platforms */
	protected TextureRegion earthTile;
	/** The texture for the exit condition */
	protected TextureRegion goalTile;
	/** The font for giving messages to the player */
	protected BitmapFont displayFont;
	/** The reader to process JSON files */
	private JsonReader jsonReader;
	/** The JSON defining the level model */
	private JsonValue levelFormat;
	private BoardModel tileBoard;
	private Minimap minimap;
	public Rectangle screenSize;

	/** Booleans for the options menu */
	public static boolean MUSIC_ON = true;
	public static boolean SFX_ON = true;

	/**
	 * Getter for this world's Board Model object
	 *
	 * @return  This world's Board Model object
	 */
	public BoardModel getTileBoard() {
		return tileBoard;
	}

	/**
	 * Getter for this world's Minimap
	 *
	 * @return  This world's Minimap
	 */
	public Minimap getMinimap() {
		return minimap;
	}

	/**
	 * Preloads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void preLoadContent(AssetManager manager) {
		if (worldAssetState != AssetState.EMPTY) {
			return;
		}

		worldAssetState = AssetState.LOADING;
		// Load the shared tiles.
		manager.load(EARTH_FILE,Texture.class);
		assets.add(EARTH_FILE);
		manager.load(GOAL_FILE,Texture.class);
		assets.add(GOAL_FILE);

		// Minimap
		manager.load(level1minimap,Texture.class);
		assets.add(level1minimap);
		manager.load(level2minimap,Texture.class);
		assets.add(level2minimap);
		manager.load(level3minimap,Texture.class);
		assets.add(level3minimap);
		manager.load(level4minimap,Texture.class);
		assets.add(level4minimap);
		manager.load(level5minimap,Texture.class);
		assets.add(level5minimap);
		manager.load(level6minimap,Texture.class);
		assets.add(level6minimap);
		manager.load(level7minimap,Texture.class);
		assets.add(level7minimap);
		manager.load(level8minimap,Texture.class);
		assets.add(level8minimap);
		manager.load(level10minimap, Texture.class);
		assets.add(level10minimap);

		manager.load(level12minimap,Texture.class);
		assets.add(level12minimap);

		manager.load(level9minimap,Texture.class);
		assets.add(level9minimap);

		manager.load(level11minimap,Texture.class);
		assets.add(level11minimap);

		// Load the font
		FreetypeFontLoader.FreeTypeFontLoaderParameter size2Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		size2Params.fontFileName = FONT_FILE;
		size2Params.fontParameters.size = FONT_SIZE;
		manager.load(FONT_FILE, BitmapFont.class, size2Params);
		assets.add(FONT_FILE);

	}

	/**
	 * Loads the assets for this controller.
	 *
	 * To make the game modes more for-loop friendly, we opted for nonstatic loaders
	 * this time.  However, we still want the assets themselves to be static.  So
	 * we have an AssetState that determines the current loading state.  If the
	 * assets are already loaded, this method will do nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void loadContent(AssetManager manager, GameCanvas canvas) {
		if (worldAssetState != AssetState.LOADING) {
			return;
		}
		
		// Allocate the tiles
		earthTile = createTexture(manager,EARTH_FILE,true);
		goalTile  = createTexture(manager,GOAL_FILE,true);
		
		// Allocate the font
		if (manager.isLoaded(FONT_FILE)) {
			displayFont = manager.get(FONT_FILE,BitmapFont.class);
		} else {
			displayFont = null;
		}

		worldAssetState = AssetState.COMPLETE;

		//Load Board.Model
		// initialize BoardModel
		// get every texture's group id in the json and map it to it's actual object's name
		jsonReader = new JsonReader();
		levelFormat = jsonReader.parse(Gdx.files.internal(JSON_FILE)); // JSON ASSET PUT PATH HERE !!!



		HashMap<Integer,Character> textureIDs = new HashMap<Integer,Character>();
		JsonValue tilesets = levelFormat.get("tilesets").child();
		while (tilesets!=null) {
			textureIDs.put(tilesets.get("firstgid").asInt(),tilesets.get("name").asChar());
			tilesets = tilesets.next();
		}

		screenSize = new Rectangle(0, 0, canvas.getWidth()*2, canvas.getHeight()*2);
		int w = levelFormat.get("width").asInt(); int h = levelFormat.get("height").asInt();
		tileBoard = new BoardModel(w, h, screenSize);
		minimap = new Minimap(canvas.getWidth()/6,canvas.getHeight()/6, w, h,
				createTexture(manager,MINIMAP_FILE,false));

		// get json data as array
		int[] maze = levelFormat.get("layers").get(1).get("data").asIntArray();

		// for loop for adding info from json data array to the board model
		int i = 0; int j = h-1;
		int rockCount = 0; int treeCount = 0;int familiarCount=0;
		for (int t : maze) {
			if (t!=0&&textureIDs.containsKey(t)) {
				Character c = textureIDs.get(t);
				switch (c) {
					case 'w':
						tileBoard.tiles[i][j].isWall=true;
						break;
					case 'l':
						tileBoard.tiles[i][j].isLantern=true;
						break;
					case 'g':
						tileBoard.tiles[i][j].isGorfStart=true;
						break;
					case 'f':
						tileBoard.tiles[i][j].isFogSpawn=true;
						break;
					// cases for sequential familiars
					case 'x':
						tileBoard.tiles[i][j].hasFamiliarOne=true;
						break;
					case 'y':
						tileBoard.tiles[i][j].hasFamiliarTwo=true;
						break;
					case 'z':
						tileBoard.tiles[i][j].hasFamiliarThree=true;
						break;
					case 'a':
						tileBoard.tiles[i][j].hasFamiliarFour=true;
						break;
					// cases for rocks and trees
					// every time there's a rock or a tree, it adds an incrementing
					// number to the tiles hasRock/hasTree value
					case 'r':
						rockCount++;
						tileBoard.tiles[i][j].hasRock=rockCount;
						break;
					case 't':
						treeCount++;
						tileBoard.tiles[i][j].hasTree=treeCount;
						break;
					case 'i':
						tileBoard.tiles[i][j].spawnPoint=true;
						break;
					default:
						break;
				}
			}

			// increment the counters
			if (i<w-1) {i++;} else {i=0;}
			if (i==w-1) {j--;}
		}
	}
	
	/**
	 * Returns a newly loaded texture region for the given file.
	 *
	 * This helper methods is used to set texture settings (such as scaling, and
	 * whether or not the texture should repeat) after loading.
	 *
	 * @param manager 	Reference to global asset manager.
	 * @param file		The texture (region) file
	 * @param repeat	Whether the texture should be repeated
	 *
	 * @return a newly loaded texture region for the given file.
	 */
	protected TextureRegion createTexture(AssetManager manager, String file, boolean repeat) {
		if (manager.isLoaded(file)) {
			TextureRegion region = new TextureRegion(manager.get(file, Texture.class));
			region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			if (repeat) {
				region.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
			}
			return region;
		}
		return null;
	}
	
	/**
	 * Returns a newly loaded filmstrip for the given file.
	 *
	 * This helper methods is used to set texture settings (such as scaling, and
	 * the number of animation frames) after loading.
	 *
	 * @param manager 	Reference to global asset manager.
	 * @param file		The texture (region) file
	 * @param rows 		The number of rows in the filmstrip
	 * @param cols 		The number of columns in the filmstrip
	 * @param size 		The number of frames in the filmstrip
	 *
	 * @return a newly loaded texture region for the given file.
	 */
	protected FilmStrip createFilmStrip(AssetManager manager, String file, int rows, int cols, int size) {
		if (manager.isLoaded(file)) {
			FilmStrip strip = new FilmStrip(manager.get(file, Texture.class),rows,cols,size);
			strip.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			return strip;
		}
		return null;
	}
	
	/** 
	 * Unloads the assets for this game.
	 * 
	 * This method erases the static variables.  It also deletes the associated textures 
	 * from the asset manager. If no assets are loaded, this method does nothing.
	 * 
	 * @param manager Reference to global asset manager.
	 */
	public void unloadContent(AssetManager manager) {
    	for(String s : assets) {
    		if (manager.isLoaded(s)) {
    			manager.unload(s);
    		}
    	}
	}
	
	/** Exit code for quitting the game */
	public static final int EXIT_QUIT = 0;
	/** Exit code for advancing to next level */
	public static final int EXIT_NEXT = 1;
	/** Exit code for jumping back to previous level */
	public static final int EXIT_PREV = 2;
    /** How many frames after winning/losing do we continue? */
	public static final int EXIT_COUNT = 120;

	/** The amount of time for a physics engine step. */
	public static final float WORLD_STEP = 1/60.0f;
	/** Number of velocity iterations for the constrain solvers */
	public static final int WORLD_VELOC = 6;
	/** Number of position iterations for the constrain solvers */
	public static final int WORLD_POSIT = 2;
	
	/** Width of the game world in Box2d units */
	protected static final float DEFAULT_WIDTH  = 128.0f;
	/** Height of the game world in Box2d units */
	protected static final float DEFAULT_HEIGHT = 72.0f;
	
	/** Reference to the game canvas */
	protected GameCanvas canvas;
	/** All the objects in the world. */
	protected PooledList<Obstacle> objects  = new PooledList<Obstacle>();
	/** Queue for adding objects */
	protected PooledList<Obstacle> addQueue = new PooledList<Obstacle>();
	/** Listener that will update the player mode when we are done */
	private ScreenListener listener;

	/** The Box2D world */
	protected World world;
	/** The boundary of the world */
	protected Rectangle bounds;
	/** The world scale */
	protected Vector2 scale;
	
	/** Whether or not this is an active controller */
	private boolean active;
	/** Whether we have completed this level */
	private boolean complete;
	/** Whether we have failed at this world (and need a reset) */
	private boolean failed;
	/** Whether or not debug mode is active */
	private boolean debug;
	/** Countdown active for winning or losing */
	private int countdown;

	/**
	 * Returns true if debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @return true if debug mode is active.
	 */
	public boolean isDebug( ) {
		return debug;
	}

	/**
	 * Sets whether debug mode is active.
	 *
	 * If true, all objects will display their physics bodies.
	 *
	 * @param value whether debug mode is active.
	 */
	public void setDebug(boolean value) {
		debug = value;
	}

	/**
	 * Returns true if the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @return true if the level is completed.
	 */
	public boolean isComplete( ) {
		return complete;
	}

	/**
	 * Sets whether the level is completed.
	 *
	 * If true, the level will advance after a countdown
	 *
	 * @param value whether the level is completed.
	 */
	public void setComplete(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		complete = value;
	}

	/**
	 * Returns true if the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @return true if the level is failed.
	 */
	public boolean isFailure( ) {
		return failed;
	}

	/**
	 * Sets whether the level is failed.
	 *
	 * If true, the level will reset after a countdown
	 *
	 * @param value whether the level is failed.
	 */
	public void setFailure(boolean value) {
		if (value) {
			countdown = EXIT_COUNT;
		}
		failed = value;
	}
	
	/**
	 * Returns true if this is the active screen
	 *
	 * @return true if this is the active screen
	 */
	public boolean isActive( ) {
		return active;
	}

	/**
	 * Returns the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers
	 */
	public GameCanvas getCanvas() {
		return canvas;
	}
	
	/**
	 * Sets the canvas associated with this controller
	 *
	 * The canvas is shared across all controllers.  Setting this value will compute
	 * the drawing scale from the canvas size.
	 */
	public void setCanvas(GameCanvas canvas) {
		this.canvas = canvas;
		this.scale.x = canvas.getWidth()/bounds.getWidth();
		this.scale.y = canvas.getHeight()/bounds.getHeight();
	}
	
	/**
	 * Creates a new game world with the default values.
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 */
	protected WorldController() {
		// gravity is now 0
		this(new Rectangle(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT), 
			 new Vector2(0,0));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param width  	The width in Box2d coordinates
	 * @param height	The height in Box2d coordinates
	 * @param gravity	The downward gravity
	 */
	protected WorldController(float width, float height, float gravity) {
		this(new Rectangle(0,0,width,height), new Vector2(0,gravity));
	}

	/**
	 * Creates a new game world
	 *
	 * The game world is scaled so that the screen coordinates do not agree
	 * with the Box2d coordinates.  The bounds are in terms of the Box2d
	 * world, not the screen.
	 *
	 * @param bounds	The game bounds in Box2d coordinates
	 * @param gravity	The gravitational force on this Box2d world
	 */
	protected WorldController(Rectangle bounds, Vector2 gravity) {
		assets = new Array<String>();
		world = new World(gravity,false);
		this.bounds = new Rectangle(0,0,bounds.width,bounds.height);
		this.scale = new Vector2(1,1);
		complete = false;
		failed = false;
		debug  = false;
		active = false;
		countdown = -1;
	}
	
	/**
	 * Dispose of all (non-static) resources allocated to this mode.
	 */
	public void dispose() {
		for(Obstacle obj : objects) {
			obj.deactivatePhysics(world);
		}
		objects.clear();
		addQueue.clear();
		world.dispose();
		objects = null;
		addQueue = null;
		bounds = null;
		scale  = null;
		world  = null;
		canvas = null;
	}

	/**
	 *
	 * Adds a physics object in to the insertion queue.
	 *
	 * Objects on the queue are added just before collision processing.  We do this to 
	 * control object creation.
	 *
	 * param obj The object to add
	 */
	public void addQueuedObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		addQueue.add(obj);
	}

	/**
	 * Immediately adds the object to the physics world
	 *
	 * param obj The object to add
	 */
	protected void addObject(Obstacle obj) {
		assert inBounds(obj) : "Object is not in bounds";
		objects.add(obj);
		obj.activatePhysics(world);
	}

	/**
	 * Returns true if the object is in bounds.
	 *
	 * This assertion is useful for debugging the physics.
	 *
	 * @param obj The object to check.
	 *
	 * @return true if the object is in bounds.
	 */
	public boolean inBounds(Obstacle obj) {
		boolean horiz = (bounds.x <= obj.getX() && obj.getX() <= bounds.x+(bounds.width*2));
		boolean vert  = (bounds.y <= obj.getY() && obj.getY() <= bounds.y+(bounds.height*2));
		return horiz && vert;
	}
	
	/**
	 * Resets the status of the game so that we can play again.
	 *
	 * This method disposes of the world and creates a new one.
	 */
	public abstract void reset();
	
	/**
	 * Returns whether to process the update loop
	 *
	 * At the start of the update loop, we check if it is time
	 * to switch to a new game mode.  If not, the update proceeds
	 * normally.
	 *
	 * 
	 * @return whether to process the update loop
	 */
	public boolean preUpdate(float dt) {
		InputController input = InputController.getInstance();
		input.readInput(bounds, scale);
		if (listener == null) {
			return true;
		}

		// Toggle debug
		if (input.didDebug()) {
			debug = !debug;
		}
		
		// Handle resets
		if (input.didReset()) {
			reset();
		}
		
		// Now it is time to maybe switch screens.
		if (input.didExit()) {

			listener.exitScreen(this, EXIT_QUIT);
			return false;
		} /**else if (input.didAdvance()) {
			listener.exitScreen(this, EXIT_NEXT);
			return false;
		} else if (input.didRetreat()) {
			//listener.exitScreen(this, EXIT_PREV);
			return false;
		} **/else if (countdown > 0) {
			countdown--;
		} else if (countdown == 0) {
			if (failed) {
				reset();
			} else if (complete) {

				//listener.exitScreen(this, EXIT_NEXT);
				//return false;
			}
		}
		return true;
	}
	
	/**
	 * The core gameplay loop of this world.
	 *
	 * This method contains the specific update code for this mini-game. It does
	 * not handle collisions, as those are managed by the parent class WorldController.
	 * This method is called after input is read, but before collisions are resolved.
	 * The very last thing that it should do is apply forces to the appropriate objects.
	 */
	public abstract void update(float dt);
	
	/**
	 * Processes physics
	 *
	 * Once the update phase is over, but before we draw, we are ready to handle
	 * physics.  The primary method is the step() method in world.  This implementation
	 * works for all applications and should not need to be overwritten.
	 *
	 */
	public void postUpdate(float dt) {
		// Add any objects created by actions
		while (!addQueue.isEmpty()) {
			addObject(addQueue.poll());
		}

		if(complete){
			displayFont.setColor(Color.PURPLE);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("Level Complete", displayFont, 0.0f);
			canvas.end();
			reset();
		}
		// Turn the physics engine crank.
		world.step(WORLD_STEP,WORLD_VELOC,WORLD_POSIT);

		// Garbage collect the deleted objects.
		// Note how we use the linked list nodes to delete O(1) in place.
		// This is O(n) without copying.
		Iterator<PooledList<Obstacle>.Entry> iterator = objects.entryIterator();
		while (iterator.hasNext()) {
			PooledList<Obstacle>.Entry entry = iterator.next();
			Obstacle obj = entry.getValue();
			if (obj.isRemoved()) {
				obj.deactivatePhysics(world);
				entry.remove();
			} else {
				// Note that update is called last!
				obj.update(dt);
			}
		}
	}
	
	/**
	 * Draw the physics objects to the canvas
	 *
	 * For simple worlds, this method is enough by itself.  It will need
	 * to be overriden if the world needs fancy backgrounds or the like.
	 *
	 * The method draws all objects in the order that they were added.
	 */
	public void draw(float delta) {
		canvas.clear();

		canvas.begin();
		for (Obstacle obj : objects) {
			obj.draw(canvas);
		}
		canvas.end();

		if (debug) {
			canvas.beginDebug();
			for (Obstacle obj : objects) {
				obj.drawDebug(canvas);
			}
			canvas.endDebug();
		}

		// Final message
		if (complete && !failed) {
			displayFont.setColor(Color.PURPLE);
			canvas.begin(); // DO NOT SCALE
			canvas.drawTextCentered("Level Complete", displayFont, 0.0f);
			canvas.end();
			if (complete) {
				System.out.print("Hello!");

			} else if (failed) {
				displayFont.setColor(Color.RED);
				canvas.begin(); // DO NOT SCALE
				canvas.drawTextCentered("FAILURE!", displayFont, 0.0f);
				canvas.end();
			}
		}
	}
	
	/**
	 * Called when the Screen is resized. 
	 *
	 * This can happen at any point during a non-paused state but will never happen 
	 * before a call to show().
	 *
	 * @param width  The new width in pixels
	 * @param height The new height in pixels
	 */
	public void resize(int width, int height) {
		// IGNORE FOR NOW
	}

	/**
	 * Called when the Screen should render itself.
	 *
	 * We defer to the other methods update() and draw().  However, it is VERY important
	 * that we only quit AFTER a draw.
	 *
	 * @param delta Number of seconds since last animation frame
	 */
	public void render(float delta) {
		if (active) {
			if (preUpdate(delta)) {
				update(delta); // This is the one that must be defined.
				postUpdate(delta);
			}
			draw(delta);
		}
	}

	/**
	 * Called when the Screen is paused.
	 * 
	 * This is usually when it's not active or visible on screen. An Application is 
	 * also paused before it is destroyed.
	 */
	public void pause() {
		// TODO Auto-generated method stub
	}

	/**
	 * Called when the Screen is resumed from a paused state.
	 *
	 * This is usually when it regains focus.
	 */
	public void resume() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Called when this screen becomes the current screen for a Game.
	 */
	public void show() {
		// Useless if called in outside animation loop
		active = true;
	}

	/**
	 * Called when this screen is no longer the current screen for a Game.
	 */
	public void hide() {
		// Useless if called in outside animation loop
		active = false;
	}

	/**
	 * Sets the ScreenListener for this mode
	 *
	 * The ScreenListener will respond to requests to quit.
	 */
	public void setScreenListener(ScreenListener listener) {
		this.listener = listener;
	}
}