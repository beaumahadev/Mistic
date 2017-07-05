package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.InputController;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.util.FilmStrip;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Created by Nathaniel on 4/22/17.
 */
public class LevelSelectController extends WorldController implements Screen {
    private static final String GORF_TEXTURE = "mistic/gorfpose.png";
    private static final String BACKGROUND = "mistic/levelselectnames.png";
    private static final String BACKGROUND_OVERLAY = "mistic/levelmockup_wood.png";
    private static final String WHITE_MIST = "mistic/whitemistresize.png";
    private static final String PURPLE_MIST = "mistic/purplemistresize.png";


    private TextureRegion menu;
    private TextureRegion background;
    private TextureRegion gorf;
    private TextureRegion whiteMist;
    private TextureRegion purpleMist;

    private static final int LEVEL_CAP = 12;

    private int inputTimer = 20;
    private boolean timerGo = true;
    private static int level;

    private int firflyAnimateTimer = 15;

    private AssetState menuAssetState = AssetState.EMPTY;
    private ScreenListener listener;

    public static final int EXIT_TO_PLAY = 100;
    public static final int EXIT_TO_LEVEL_SELECT = 101;
    public static final int EXIT_TO_OPTIONS = 102;
    public static final int EXIT_TO_MENU = 103;

    public String jsonFileName;


    public static final String level1 = "final_release/level1_unfamiliar.json";
    public static final String level2 = "final_release/level2_thefish.json";
    public static final String level3 = "final_release/level3_lumina.json";
    public static final String level4 = "final_release/level4_hourglass.json"; //better name for this level please
    public static final String level5 = "final_release/level5_cross.json";
    public static final String level6 = "final_release/level7_misty.json";
    public static final String level7 = "final_release/level6_rooms2.json";
    public static final String level8 = "final_release/level14_courier.json";
    public static final String level9 = "final_release/level13_arena.json";
    public static final String level10 = "final_release/levelstar2.json";
    public static final String level11 = "final_release/levelboxes.json";
    public static final String level12 = "final_release/levelmistic.json";



    public static final String level1minimap = "minimaps/level1.png";
    public static final String level2minimap = "minimaps/level2.png";
    public static final String level3minimap = "minimaps/level3.png";
    public static final String level4minimap = "minimaps/level4.png";
    public static final String level5minimap = "minimaps/level5.png";
    public static final String level6minimap = "minimaps/level7.png";
    public static final String level7minimap = "minimaps/level6.png";
    public static final String level8minimap = "minimaps/level14.png";
    public static final String level9minimap = "minimaps/level13.png";
    public static final String level10minimap = "minimaps/levelstar.png";
    public static final String level11minimap = "minimaps/levelboxes.png";
    public static final String level12minimap = "minimaps/levelmistic.png";




    public static boolean level1complete = false;
    public static boolean level2complete = false;
    public static boolean level3complete = false;
    public static boolean level4complete = false;
    public static boolean level5complete = false;
    public static boolean level6complete = false;
    public static boolean level7complete = false;
    public static boolean level8complete = false;
    public static boolean level9complete = false;
    public static boolean level10complete = false;
    public static boolean level11complete = false;
    public static boolean level12complete = false;




    public void preLoadContent(AssetManager manager) {
        if (menuAssetState != AssetState.EMPTY) {
            return;
        }


        menuAssetState = AssetState.LOADING;

        manager.load(BACKGROUND, Texture.class);
        assets.add(BACKGROUND);

        manager.load(BACKGROUND_OVERLAY, Texture.class);
        assets.add(BACKGROUND_OVERLAY);

        manager.load(GORF_TEXTURE, Texture.class);
        assets.add(GORF_TEXTURE);

        manager.load(WHITE_MIST, Texture.class);
        assets.add(WHITE_MIST);

        manager.load(PURPLE_MIST, Texture.class);
        assets.add(PURPLE_MIST);


        super.preLoadContent(manager);
    }

    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (menuAssetState != AssetState.LOADING) {
            return;
        }

        menu = createTexture(manager, BACKGROUND, false);
        background = createTexture(manager, BACKGROUND_OVERLAY, false);
        gorf = createTexture(manager, GORF_TEXTURE, false);
        whiteMist = createTexture(manager, WHITE_MIST, false);
        purpleMist = createTexture(manager, PURPLE_MIST, false);

    }

    public LevelSelectController() {
        setDebug(false);
        setComplete(false);
        setFailure(false);
        this.level = 1;
        this.jsonFileName = level1;
    }

    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );
        objects.clear();
        addQueue.clear();
        world.dispose();
        setComplete(false);
        setFailure(false);
        timerGo = true;
        world = new World(gravity,false);
    }

    public void update(float dt) {
        if (timerGo) { //code to slow down multiple inputs and not register all of them
            inputTimer--;
            if (inputTimer == 0) {
                timerGo = false;
                inputTimer = 25;
            }
        }
        float forcex= InputController.getInstance().getHorizontal();
        boolean pressing = InputController.getInstance().didEnter();
        boolean back = InputController.getInstance().didExit();
        boolean enter = InputController.getInstance().didEnter();
        //increase level when player presses right arrow key
        if (forcex > 0 && !timerGo) {
            timerGo = true;
            if (level != LEVEL_CAP ) {
                level++;
            }
        } else if (forcex < 0 && !timerGo) { //decrease level when player presses left arrow key
            timerGo = true;
            if (level != 1) {
                level--;
            }
        }
        if (back) {
            listener.exitScreen(this, EXIT_TO_MENU); //exit to menu when player presses escape
        } else if (enter && !timerGo) {
            timerGo = true;
            switch (level) {
                case 1: WorldController.MINIMAP_FILE = level1minimap;
                        WorldController.JSON_FILE = level1; break;
                case 2: WorldController.MINIMAP_FILE = level2minimap;
                        WorldController.JSON_FILE = level2; break;
                case 3: WorldController.MINIMAP_FILE = level3minimap;
                        WorldController.JSON_FILE = level3; break;
                case 4: WorldController.MINIMAP_FILE = level4minimap;
                        WorldController.JSON_FILE = level4; break;
                case 5: WorldController.MINIMAP_FILE = level5minimap;
                        WorldController.JSON_FILE = level5; break;
                case 6: WorldController.MINIMAP_FILE = level6minimap;
                    WorldController.JSON_FILE = level6; break;
                case 7: WorldController.MINIMAP_FILE = level7minimap;
                    WorldController.JSON_FILE = level7; break;
                case 8: WorldController.MINIMAP_FILE = level8minimap;
                    WorldController.JSON_FILE = level8; break;
                case 9: WorldController.MINIMAP_FILE = level9minimap;
                    WorldController.JSON_FILE = level9; break;
                case 10: WorldController.MINIMAP_FILE = level10minimap;
                    WorldController.JSON_FILE = level10; break;
                case 11: WorldController.MINIMAP_FILE = level11minimap;
                    WorldController.JSON_FILE = level11; break;
                case 12: WorldController.MINIMAP_FILE = level12minimap;
                    WorldController.JSON_FILE = level12;

            }
            listener.exitScreen(this, this.EXIT_TO_PLAY);
        }
        firflyAnimateTimer--;
        if (firflyAnimateTimer == 0) {
            firflyAnimateTimer = 20;
        }

    }
    @Override
    public void render(float dt) {
        super.render(dt);
        update(dt);
        draw(dt);
        boolean pressing = InputController.getInstance().didSecondary();
    }


    public void draw(float dt) {
        canvas.clear();
        canvas.resetCamera();
        canvas.begin();
        //canvas.draw(background, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);
        canvas.draw(menu, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);

        if (level1complete) { canvas.draw(whiteMist, 500, 660); }
        else { canvas.draw(purpleMist, 500, 660); }
        if (level2complete) { canvas.draw(whiteMist, 770, 615); }
        else { canvas.draw(purpleMist, 770, 615); }
        if (level3complete) { canvas.draw(whiteMist, 450, 355); }
        else { canvas.draw(purpleMist, 450, 355); }
        if (level4complete) { canvas.draw(whiteMist, 720, 190); }
        else { canvas.draw(purpleMist, 720, 190); }
        if (level5complete) { canvas.draw(whiteMist, 970, 280); }
        else { canvas.draw(purpleMist, 970, 280); }
        if (level6complete) { canvas.draw(whiteMist, 930, 540); }
        else { canvas.draw(purpleMist, 930, 540); }
        if (level7complete) { canvas.draw(whiteMist, 1170, 650); }
        else { canvas.draw(purpleMist, 1170, 642); }
        if (level8complete) { canvas.draw(whiteMist, 1362, 801); }
        else { canvas.draw(purpleMist, 1362, 801); }
        if (level9complete) { canvas.draw(whiteMist, 1205, 440); }
        else { canvas.draw(purpleMist, 1205, 440); }
        if (level10complete) { canvas.draw(whiteMist, 1475, 259); }
        else { canvas.draw(purpleMist, 1475, 259); }
        if (level11complete) { canvas.draw(whiteMist, 1709, 395); }
        else { canvas.draw(purpleMist, 1695, 382); }
        if (level12complete) { canvas.draw(whiteMist, 1716, 721); }
        else { canvas.draw(purpleMist, 1706, 711); }

        switch (level) {
            case 1: canvas.draw(gorf, 500, 680); break;
            case 2: canvas.draw(gorf, 770, 635); break;
            case 3: canvas.draw(gorf, 450, 375); break;
            case 4: canvas.draw(gorf, 720, 210); break;
            case 5: canvas.draw(gorf, 970, 300); break;
            case 6: canvas.draw(gorf, 920, 560); break;
            case 7: canvas.draw(gorf, 1170, 670); break;
            case 8: canvas.draw(gorf, 1362, 821); break;
            case 9: canvas.draw(gorf, 1205, 460); break;
            case 10: canvas.draw(gorf, 1475, 279); break;
            case 11: canvas.draw(gorf, 1709, 415); break;
            case 12: canvas.draw(gorf, 1716, 741); break;
        }

        canvas.end();
    }

    public static void setLevel(int the_level) {
        level = the_level;
        if (the_level > 12) {
            the_level = 1;
        }
        switch (level) {
            case 1: WorldController.MINIMAP_FILE = level1minimap;
                WorldController.JSON_FILE = level1; break;
            case 2: WorldController.MINIMAP_FILE = level2minimap;
                WorldController.JSON_FILE = level2; break;
            case 3: WorldController.MINIMAP_FILE = level3minimap;
                WorldController.JSON_FILE = level3; break;
            case 4: WorldController.MINIMAP_FILE = level4minimap;
                WorldController.JSON_FILE = level4; break;
            case 5: WorldController.MINIMAP_FILE = level5minimap;
                WorldController.JSON_FILE = level5; break;
            case 6: WorldController.MINIMAP_FILE = level6minimap;
                WorldController.JSON_FILE = level6; break;
            case 7: WorldController.MINIMAP_FILE = level7minimap;
                WorldController.JSON_FILE = level7; break;
            case 8: WorldController.MINIMAP_FILE = level8minimap;
                WorldController.JSON_FILE = level8; break;
            case 9: WorldController.MINIMAP_FILE = level9minimap;
                WorldController.JSON_FILE = level9; break;
            case 10: WorldController.MINIMAP_FILE = level10minimap;
                WorldController.JSON_FILE = level10; break;
            case 11: WorldController.MINIMAP_FILE = level11minimap;
                WorldController.JSON_FILE = level11; break;
            case 12: WorldController.MINIMAP_FILE = level12minimap;
                WorldController.JSON_FILE = level12;
        }
    }

    public static int getLevel() {
        return level;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

}
