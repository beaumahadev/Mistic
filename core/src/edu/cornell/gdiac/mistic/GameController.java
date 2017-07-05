/*
 * GameController.java
 * Copyright Mishka 2017
 *
 */
package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.*;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.util.*;

import edu.cornell.gdiac.InputController;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.obstacle.Obstacle;
import edu.cornell.gdiac.obstacle.PolygonObstacle;


import javax.swing.*;
import javax.xml.soap.Text;
import java.util.*;

import static com.badlogic.gdx.math.MathUtils.random;
import edu.cornell.gdiac.mistic.Lantern;
import javafx.scene.PointLight;
import org.lwjgl.Sys;
//import org.lwjgl.Sys;

/**
 * Gameplay specific controller for the Mistic game.
 *
 * You will notice that asset loading is not done with static methods this time.
 * Instance asset loading makes it easier to process our game modes in a loop, which
 * is much more scalable. However, we still want the assets themselves to be static.
 * This is the purpose of our AssetState variable; it ensures that multiple instances
 * place nicely with the static assets.
 */
public class GameController extends WorldController implements ContactListener{
    private int inputTimer = 20;
    private boolean timerGo = true;

    private int inputTimerText = 3;
    private boolean timerGoText = true;

    private ScreenListener listener;

    /** Reference to the rocket texture */
    private static final String[] GORF_TEXTURES = {"mistic/gorfs/gorfD.png","mistic/gorfs/gorfDL.png","mistic/gorfs/gorfDR.png",
            "mistic/gorfs/gorfL.png","mistic/gorfs/gorfR.png","mistic/gorfs/gorfBL.png", "mistic/gorfs/gorfBR.png",
            "mistic/gorfs/gorfB.png", "mistic/gorfs/gorf.png"};
    private static final String[] GORF_HATS = {"mistic/gorfs/gorfDtop.png","mistic/gorfs/gorfDLtop.png","mistic/gorfs/gorfDRtop.png",
            "mistic/gorfs/gorfLtop.png","mistic/gorfs/gorfRtop.png","mistic/gorfs/gorfBLtop.png", "mistic/gorfs/gorfBRtop.png",
            "mistic/gorfs/gorfBtop.png"};
    private static final String[] ENEMY_TEXTURES = {"mistic/enemy/enemy1sheet.png","mistic/enemy/enemy2sheet.png",
            "mistic/enemy/enemy3sheet.png","mistic/enemy/enemy4sheet.png"};
    private static final String BACKGROUND = "mistic/backgroundvibrant.png";
    private static final String MINIMAP_BACKGROUND = "mistic/mini_map_background.png";
    private static final String FIRE_FLY= "mistic/firefly.png";
    private static final String FIRE_TRACK="mistic/fireflyicon.png";
    private static final String MONSTER_TEXTURE = "mistic/enemyplaceholder.png";
    private static final String MONSTER_TEXTURE_DEAD = "mistic/enemydead.png";
    private static final String[] MIST_WALLS= {"mistic/mistblock/mistblock1.png",
            "mistic/mistblock/mistblock2.png", "mistic/mistblock/mistblock3.png", "mistic/mistblock/mistblock4.png",
            "mistic/mistblock/mistblock5.png", "mistic/mistblock/mistblock6.png", "mistic/mistblock/mistblock7.png",
            "mistic/mistblock/mistblock8.png", "mistic/mistblock/mistblock9.png", "mistic/mistblock/mistblock10.png",
            "mistic/mistblock/mistblock11.png", "mistic/mistblock/mistblock12.png", "mistic/mistblock/mistblock13.png",
            "mistic/mistblock/mistblock14.png","mistic/mistblock/mistblock15.png", "mistic/mistblock/mistblock16.png",
            "mistic/mistblock/mistblock17.png","mistic/mistblock/mistblock18.png", "mistic/mistblock/mistblock19.png",
            "mistic/mistblock/mistblock20.png","mistic/mistblock/mistblock21.png", "mistic/mistblock/mistblock22.png",
            "mistic/mistblock/mistblock23.png","mistic/mistblock/mistblock24.png", "mistic/mistblock/mistblock25.png",
            "mistic/mistblock/mistblock26.png","mistic/mistblock/mistblock27.png", "mistic/mistblock/mistblock28.png",
            "mistic/mistblock/mistblock29.png","mistic/mistblock/mistblock30.png"};

    private static final String[] TREES = { "mistic/environment/tree1.png","mistic/environment/tree2.png",
            "mistic/environment/tree3.png","mistic/environment/tree4.png"
    };
    private static final String[] TREETOPS = { "mistic/environment/tree1top.png","mistic/environment/tree2top.png",
            "mistic/environment/tree3top.png","mistic/environment/tree4top.png"
    };
    private static final String[] ROCKS = { "mistic/environment/rock1.png","mistic/environment/rock2.png",
            "mistic/environment/rock3.png"
    };
    private static final String[] ROCKTOPS = { "mistic/environment/rock1top.png","mistic/environment/rock2top.png",
            "mistic/environment/rock3top.png"
    };
    private static final String[] FAMILIARS={
            "mistic/familiars/cat.png","mistic/familiars/chicken.png","mistic/familiars/hedgehog.png",
            "mistic/familiars/tortoise.png",
    };
    /** The reference for the afterburner textures */
    /** Reference to the crate image assets */
    private static final String LIT_LANTERN = "mistic/lit.png";
    private static final String UNLIT_LANTERN = "mistic/unlit.png";
    private static final String LITTOP_LANTERN = "mistic/littop.png";
    private static final String UNLITTOP_LANTERN = "mistic/unlittop.png";
    private static final String FIREFLY_ANIMATE="mistic/spritesheet_firefly.png";
    //private static final String FIREFLY_ANIMATE = "mistic/spritesheet_firefly_menu.png";

    /** Texture references for the HUD */
    private static final String HUD_WINDOW_TEXTURE = "mistic/hud_window.png";
    private static final String HUD_WHITE_FIREFLY_TEXTURE = "mistic/white_firefly.png";
    private static final String HUD_WHITE_NUMBER_X = "mistic/numbers_white/numbers_x.png";
    private static final String HUD_WHITE_NUMBER_0 = "mistic/numbers_white/numbers_0.png";
    private static final String HUD_WHITE_NUMBER_1 = "mistic/numbers_white/numbers_1.png";
    private static final String HUD_WHITE_NUMBER_2 = "mistic/numbers_white/numbers_2.png";
    private static final String HUD_WHITE_NUMBER_3 = "mistic/numbers_white/numbers_3.png";
    private static final String HUD_WHITE_NUMBER_4 = "mistic/numbers_white/numbers_4.png";
    private static final String HUD_WHITE_NUMBER_5 = "mistic/numbers_white/numbers_5.png";
    private static final String HUD_WHITE_NUMBER_6 = "mistic/numbers_white/numbers_6.png";
    private static final String HUD_WHITE_NUMBER_7 = "mistic/numbers_white/numbers_7.png";
    private static final String HUD_WHITE_NUMBER_8 = "mistic/numbers_white/numbers_8.png";
    private static final String HUD_WHITE_NUMBER_9 = "mistic/numbers_white/numbers_9.png";
    private static final String HUD_WHITE_NUMBER_SLASH = "mistic/numbers_white/numbers_slash.png";
    private static final String HUD_PAW_ANIMATE = "mistic/spritesheet_paw.png";
    private static final String HUD_PURPLE_FIREFLY = "mistic/purple_firefly.png";

    private static final String PAUSE_INSTRUC = "mistic/tutorial/level1/map_pause.png";

    private static final String LEVEL_1_TUTORIAL_1 = "mistic/tutorial/level1/firefly.png";
    private static final String LEVEL_1_TUTORIAL_2 = "mistic/tutorial/level1/delve.png";
    private static final String LEVEL_1_TUTORIAL_3 = "mistic/tutorial/level1/morefireflies.png";

    private static final String LEVEL_2_TUTORIAL_1 = "mistic/tutorial/level2/seqfam.png";

    private static final String LEVEL_3_TUTORIAL_1 = "mistic/tutorial/level3/spacebar.png";

    private static final String COURIER_TAKEOUT = "mistic/tutorial/takeout.png";



    /** Menu Screen Texture References*/
    private static final String PAUSE_SCREEN = "mistic/spritesheet_pause.png";
    private static final String WIN_SCREEN = "mistic/spritesheet_win.png";
    private static final String GAME_OVER_SCREEN = "mistic/lose_sprite.png";

    /** The SoundController, Music and sfx */
    SoundController sounds = SoundController.getInstance();
    private static final String A_PEACE_SONG = "sounds/A_Peace_DEMO2.mp3";
    private static final String B_MARSH_SONG = "sounds/B_Marsh_DEMO2.mp3";
    private static final String D_PEACE_SONG = "sounds/D_Peace_DEMO2.mp3";
    private static final String E_MARSH_SONG = "sounds/E_Marsh_DEMO2.mp3";
    private static final String F_MARSH_SONG = "sounds/F_Marsh_DEMO2.mp3";
    private static final String G_PEACE_SONG = "sounds/G_Wander_DEMO2.mp3";
    private static final String FX_FIREFLY = "sounds/_FX_firefly_FX.mp3";
    private static final String FX_FAMILIAR = "sounds/_FX_familiar_FX.mp3";

    /** Noise textures */
    private static final String PERLIN_NOISE = "mistic/noise/noise";

    private TextureRegion fireflyAnimation;
    private FilmStrip pawAnimation;

    /** Texture assets for the rocket */
    private TextureRegion backgroundTexture;
    private TextureRegion minimapBackgroundTexture;
    private TextureRegion fogTexture;
    private TextureRegion fireflyTrack;
    private TextureRegion minimapFirefly;
    private TextureRegion monsterTextureDead;
    private TextureRegion[] monsterTextures = new TextureRegion[ENEMY_TEXTURES.length];
    private TextureRegion[] gorfTextures = new TextureRegion[GORF_TEXTURES.length];
    private TextureRegion[] gorfHats= new TextureRegion[GORF_HATS.length];
    private TextureRegion[] mistwalls = new TextureRegion[MIST_WALLS.length];
    private TextureRegion[] familiarTex = new TextureRegion[FAMILIARS.length];
    private TextureRegion[] trees = new TextureRegion[TREES.length];
    private TextureRegion[] rocks = new TextureRegion[ROCKS.length];
    private TextureRegion[] treetops= new TextureRegion[TREETOPS.length];
    private TextureRegion[] rocktops= new TextureRegion[ROCKTOPS.length];
    /** Texture assets for the crates */
    private TextureRegion litTexture;
    private TextureRegion unlitTexture;
    private TextureRegion litTextureTop;
    private TextureRegion unlitTextureTop;

    /** Texture assets for HUD **/
    private TextureRegion HUDWindow;
    private TextureRegion HUDWhiteFirefly;
    private TextureRegion HUDWhiteNumber_x;
    private TextureRegion HUDWhiteNumber_0;
    private TextureRegion HUDWhiteNumber_1;
    private TextureRegion HUDWhiteNumber_2;
    private TextureRegion HUDWhiteNumber_3;
    private TextureRegion HUDWhiteNumber_4;
    private TextureRegion HUDWhiteNumber_5;
    private TextureRegion HUDWhiteNumber_6;
    private TextureRegion HUDWhiteNumber_7;
    private TextureRegion HUDWhiteNumber_8;
    private TextureRegion HUDWhiteNumber_9;
    private TextureRegion HUDWhiteNumber_slash;
    private TextureRegion HUDPurpleFirefly;

    private TextureRegion pauseInstructions;

    private TextureRegion level1tutorial1;
    private TextureRegion level1tutorial2;
    private TextureRegion level1tutorial3;
    private TextureRegion level2tutorial1;
    private TextureRegion level3tutorial1;
    private TextureRegion takeouttutorial;

    private boolean textboxAnimateTop;
    private boolean textboxAnimateBottom;
    private int textboxAnimateOffset;

    private FilmStrip pause;
    private FilmStrip win;
    private FilmStrip gameOver;

    private int state;
    private static final int PLAY = 1;
    private static final int PAUSE = 2;
    private static final int WIN = 3;
    private static final int LOSE = 4;
    private static final int OFF = 5;

    private static final int MAX_MONSTER=4;
    private static final int MAX_FIREFLY = 10;


    /** Track asset loading from all instances and subclasses */
    private AssetState rocketAssetState = AssetState.EMPTY;

    Rectangle screenSize;

    /** animation span for fog **/
    public final int FOG_ANIM_SPAN = 360;
    public Texture[] perlinTex = new Texture[FOG_ANIM_SPAN];

    // make sound objects for sfx
    Sound fireflyFX = Gdx.audio.newSound(Gdx.files.internal(FX_FIREFLY));
    Sound familiarFX = Gdx.audio.newSound(Gdx.files.internal(FX_FAMILIAR));


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
        if (rocketAssetState != AssetState.EMPTY) {
            return;
        }

        rocketAssetState = AssetState.LOADING;

        //Background
        manager.load(BACKGROUND, Texture.class);
        assets.add(BACKGROUND);
        //Minimap map backing
        manager.load(MINIMAP_BACKGROUND, Texture.class);
        assets.add(MINIMAP_BACKGROUND);
        //Firefly
        manager.load(FIRE_FLY, Texture.class);
        assets.add(FIRE_FLY);
        //Lantern
        manager.load(LIT_LANTERN,Texture.class);
        manager.load(UNLIT_LANTERN,Texture.class);
        manager.load(LITTOP_LANTERN,Texture.class);
        manager.load(UNLITTOP_LANTERN, Texture.class);
        assets.add(LIT_LANTERN);
        assets.add(UNLIT_LANTERN);
        assets.add(LITTOP_LANTERN);
        assets.add(UNLITTOP_LANTERN);
        // Ship textures

        manager.load(FIRE_TRACK,Texture.class);
        assets.add(FIRE_TRACK);

        // Monster textures
        manager.load(MONSTER_TEXTURE_DEAD, Texture.class);
        assets.add(MONSTER_TEXTURE_DEAD);

        manager.load(FIREFLY_ANIMATE,Texture.class);
        assets.add(FIREFLY_ANIMATE);

        //HUD textures
        manager.load(HUD_WINDOW_TEXTURE,Texture.class);
        assets.add(HUD_WINDOW_TEXTURE);

        manager.load(HUD_WHITE_FIREFLY_TEXTURE, Texture.class);
        assets.add(HUD_WHITE_FIREFLY_TEXTURE);

        manager.load(HUD_WHITE_NUMBER_0, Texture.class);
        assets.add(HUD_WHITE_NUMBER_0);

        manager.load(HUD_WHITE_NUMBER_1, Texture.class);
        assets.add(HUD_WHITE_NUMBER_1);

        manager.load(HUD_WHITE_NUMBER_2, Texture.class);
        assets.add(HUD_WHITE_NUMBER_2);

        manager.load(HUD_WHITE_NUMBER_3, Texture.class);
        assets.add(HUD_WHITE_NUMBER_3);

        manager.load(HUD_WHITE_NUMBER_4, Texture.class);
        assets.add(HUD_WHITE_NUMBER_4);

        manager.load(HUD_WHITE_NUMBER_5, Texture.class);
        assets.add(HUD_WHITE_NUMBER_5);

        manager.load(HUD_WHITE_NUMBER_6, Texture.class);
        assets.add(HUD_WHITE_NUMBER_6);

        manager.load(HUD_WHITE_NUMBER_7, Texture.class);
        assets.add(HUD_WHITE_NUMBER_7);

        manager.load(HUD_WHITE_NUMBER_8, Texture.class);
        assets.add(HUD_WHITE_NUMBER_8);

        manager.load(HUD_WHITE_NUMBER_9, Texture.class);
        assets.add(HUD_WHITE_NUMBER_9);

        manager.load(HUD_WHITE_NUMBER_X, Texture.class);
        assets.add(HUD_WHITE_NUMBER_X);

        manager.load(HUD_WHITE_NUMBER_SLASH, Texture.class);
        assets.add(HUD_WHITE_NUMBER_SLASH);

        manager.load(HUD_PURPLE_FIREFLY, Texture.class);
        assets.add(HUD_PURPLE_FIREFLY);

        manager.load(HUD_PAW_ANIMATE, Texture.class);
        assets.add(HUD_PAW_ANIMATE);

        manager.load(PAUSE_SCREEN, Texture.class);
        assets.add(PAUSE_SCREEN);

        manager.load(WIN_SCREEN, Texture.class);
        assets.add(WIN_SCREEN);

        manager.load(GAME_OVER_SCREEN, Texture.class);
        assets.add(GAME_OVER_SCREEN);

        manager.load(PAUSE_INSTRUC, Texture.class);
        assets.add(PAUSE_INSTRUC);

        manager.load(LEVEL_1_TUTORIAL_1, Texture.class);
        assets.add(LEVEL_1_TUTORIAL_1);

        manager.load(LEVEL_1_TUTORIAL_2, Texture.class);
        assets.add(LEVEL_1_TUTORIAL_2);

        manager.load(LEVEL_1_TUTORIAL_3, Texture.class);
        assets.add(LEVEL_1_TUTORIAL_3);

        manager.load(LEVEL_2_TUTORIAL_1, Texture.class);
        assets.add(LEVEL_2_TUTORIAL_1);

        manager.load(LEVEL_3_TUTORIAL_1, Texture.class);
        assets.add(LEVEL_1_TUTORIAL_1);

        manager.load(LEVEL_3_TUTORIAL_1, Texture.class);
        assets.add(LEVEL_1_TUTORIAL_1);

        manager.load(COURIER_TAKEOUT, Texture.class);
        assets.add(COURIER_TAKEOUT);



        for(String m : MIST_WALLS){
            manager.load(m, Texture.class);
            assets.add(m);
        }

        for(String f : FAMILIARS){
            manager.load(f, Texture.class);
            assets.add(f);
        }

        for(String f : ROCKS){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : TREES){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : TREETOPS){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : ROCKTOPS){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : GORF_TEXTURES){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : GORF_HATS){
            manager.load(f, Texture.class);
            assets.add(f);
        }
        for(String f : ENEMY_TEXTURES){
            manager.load(f, Texture.class);
            assets.add(f);
        }

        //noise textures
        for (int i=0; i<FOG_ANIM_SPAN; i++) {
            manager.load(PERLIN_NOISE + i + ".png", Texture.class);
            assets.add(PERLIN_NOISE + i + ".png");
        }

        //music files
        manager.load(A_PEACE_SONG, Sound.class);
        assets.add(A_PEACE_SONG);
        manager.load(B_MARSH_SONG, Sound.class);
        assets.add(B_MARSH_SONG);
        manager.load(D_PEACE_SONG, Sound.class);
        assets.add(D_PEACE_SONG);
        manager.load(E_MARSH_SONG, Sound.class);
        assets.add(E_MARSH_SONG);
        manager.load(F_MARSH_SONG, Sound.class);
        assets.add(F_MARSH_SONG);
        manager.load(G_PEACE_SONG, Sound.class);
        assets.add(G_PEACE_SONG);

        // sfx
        manager.load(FX_FIREFLY, Sound.class);
        assets.add(FX_FIREFLY);
        manager.load(FX_FAMILIAR, Sound.class);
        assets.add(FX_FAMILIAR);

        super.preLoadContent(manager);
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
        if (rocketAssetState != AssetState.LOADING) {
            return;
        }

        litTexture=createTexture(manager,LIT_LANTERN,false);
        unlitTexture=createTexture(manager,UNLIT_LANTERN,false);
        litTextureTop=createTexture(manager,LITTOP_LANTERN,false);
        unlitTextureTop=createTexture(manager,UNLITTOP_LANTERN,false);


        //gorfHat = createTexture(manager,HAT_TEXTURE,false);
        backgroundTexture = createTexture(manager,BACKGROUND,false);
        //backgroundTexture.setRegion(0,0,2560,1440);
        minimapBackgroundTexture = createTexture(manager,MINIMAP_BACKGROUND,false);
        fireflyTrack=createTexture(manager,FIRE_TRACK,false);
        minimapFirefly=createTexture(manager,FIRE_FLY,false);
        monsterTextureDead=createTexture(manager,MONSTER_TEXTURE_DEAD,false);

        HUDWindow = createTexture(manager, HUD_WINDOW_TEXTURE, false);
        HUDWhiteFirefly = createTexture(manager, HUD_WHITE_FIREFLY_TEXTURE, false);
        HUDWhiteNumber_0 = createTexture(manager, HUD_WHITE_NUMBER_0, false);
        HUDWhiteNumber_1 = createTexture(manager, HUD_WHITE_NUMBER_1, false);
        HUDWhiteNumber_2 = createTexture(manager, HUD_WHITE_NUMBER_2, false);
        HUDWhiteNumber_3 = createTexture(manager, HUD_WHITE_NUMBER_3, false);
        HUDWhiteNumber_4 = createTexture(manager, HUD_WHITE_NUMBER_4, false);
        HUDWhiteNumber_5 = createTexture(manager, HUD_WHITE_NUMBER_5, false);
        HUDWhiteNumber_6 = createTexture(manager, HUD_WHITE_NUMBER_6, false);
        HUDWhiteNumber_7 = createTexture(manager, HUD_WHITE_NUMBER_7, false);
        HUDWhiteNumber_8 = createTexture(manager, HUD_WHITE_NUMBER_8, false);
        HUDWhiteNumber_9 = createTexture(manager, HUD_WHITE_NUMBER_9, false);
        HUDWhiteNumber_x = createTexture(manager, HUD_WHITE_NUMBER_X, false);
        HUDWhiteNumber_slash = createTexture(manager, HUD_WHITE_NUMBER_SLASH, false);
        HUDPurpleFirefly = createTexture(manager, HUD_PURPLE_FIREFLY, false);
        pawAnimation = createFilmStrip(manager, HUD_PAW_ANIMATE, 1, 2, 2);

        pauseInstructions = createTexture(manager, PAUSE_INSTRUC, false);

        level1tutorial1 = createTexture(manager, LEVEL_1_TUTORIAL_1, false);
        level1tutorial2 = createTexture(manager, LEVEL_1_TUTORIAL_2, false);
        level1tutorial3 = createTexture(manager, LEVEL_1_TUTORIAL_3, false);
        level2tutorial1 = createTexture(manager, LEVEL_2_TUTORIAL_1, false);
        level3tutorial1 = createTexture(manager, LEVEL_3_TUTORIAL_1, false);
        takeouttutorial = createTexture(manager, COURIER_TAKEOUT, false);

        pause = createFilmStrip(manager, PAUSE_SCREEN, 1, 3, 3);
        win = createFilmStrip(manager, WIN_SCREEN, 1, 2, 2);
        gameOver = createFilmStrip(manager, GAME_OVER_SCREEN, 1, 2, 2);


        fireflyAnimation=createTexture(manager, FIREFLY_ANIMATE, false);

        for(int i=0;i<MIST_WALLS.length;i++){
            mistwalls[i]= createTexture(manager, MIST_WALLS[i], false);
        }
        for(int i=0;i<FAMILIARS.length;i++){
            familiarTex[i]= createTexture(manager,FAMILIARS[i], false);
        }
        for(int i=0;i<ROCKS.length;i++){
            rocks[i]= createTexture(manager,ROCKS[i], false);
        }
        for(int i=0;i<TREES.length;i++){
            trees[i]= createTexture(manager,TREES[i], false);
        }
        for(int i=0;i<TREETOPS.length;i++){
            treetops[i]= createTexture(manager,TREETOPS[i], false);
        }
        for(int i=0;i<ROCKTOPS.length;i++){
            rocktops[i]= createTexture(manager,ROCKTOPS[i], false);
        }
        for(int i=0; i<GORF_TEXTURES.length;i++){
            gorfTextures[i] = createTexture(manager,GORF_TEXTURES[i],false);
        }
        for(int i=0; i<GORF_HATS.length;i++){
            gorfHats[i] = createTexture(manager,GORF_HATS[i],false);
        }
        for(int i=0; i<ENEMY_TEXTURES.length;i++){
            monsterTextures[i] = createTexture(manager, ENEMY_TEXTURES[i], false);
        }

        for (int i=0; i<FOG_ANIM_SPAN; i++) {
            perlinTex[i] = createTexture(manager, PERLIN_NOISE + i + ".png", false).getTexture();
        }

        // allocate songs and marsh fx
        sounds.allocate(manager,A_PEACE_SONG,true);
        sounds.allocate(manager,B_MARSH_SONG,false);
        sounds.allocate(manager,D_PEACE_SONG,true);
        sounds.allocate(manager,E_MARSH_SONG,false);
        sounds.allocate(manager,F_MARSH_SONG,false);
        sounds.allocate(manager,G_PEACE_SONG,true);

        super.loadContent(manager, canvas);
        tileBoard=super.getTileBoard();
        rocketAssetState = AssetState.COMPLETE;
    }

    // Physics constants for initialization
    /** Density of non-crate objects */
    private static final float BASIC_DENSITY   = 0.0f;
    /** Density of the crate objects */
    private static final float CRATE_DENSITY   = 1.0f;
    /** Friction of non-crate objects */
    private static final float BASIC_FRICTION  = 0.1f;
    /** Friction of the crate objects */
    private static final float CRATE_FRICTION  = 0.3f;
    /** Collision restitution for all objects */
    private static final float BASIC_RESTITUTION = 0.1f;
    /** Threshold for generating sound on collision */
    private static final float SOUND_THRESHOLD = 1.0f;
    private int countdown = 120;
    FireflyController fireflyController;

    ArrayList<Obstacle> edgewalls = new ArrayList<Obstacle>();
    //HUD Stuff
    int pawTimer = 60;
    boolean pawTimerStart = false;

    //monster stuff
    final int MONSTERTIMER=1500;
    int monsterSpawnTimer = 900;
    BoardModel.Tile fogSpawn;


    // the number of fireflies Gorf is holding
    private static int firefly_count;
    private AIControllerS ai;
    private static BoardModel tileBoard;
    private static boolean DEAD;

    // Other game objects
    public Vector2 gorfStart = new Vector2();
    // Physics objects for the game
    /** Reference to the rocket/player avatar */
    public GorfModel gorf;
    public GorfModel gorfhat;
    /** Reference to the monster */
    public ArrayList<MonsterModel> monster;


    protected Familiar familiars;

    private FogController fog;
    private Glow glow;
    private float BW = DEFAULT_WIDTH;
    private float BH = DEFAULT_HEIGHT;
    private int UNITS_W = (int)(BW*3);
    private int UNITS_H = (int)(BH*3);
    private float UW = BW / UNITS_W;
    private float UH = BH / UNITS_H;
    private static int FOG_DELAY = 50;
    private static int FIREFLY_DELAY = 100;
    private int fogDelay = FOG_DELAY;
    private int fireflyDelay = FIREFLY_DELAY;
    private int fireflyDeathTimer;
    private int firefly_counter=0;

    private FrameBuffer fbo;
    private TextureRegion fboRegion;
    private FrameBuffer fbo2;
    private TextureRegion fboRegion2;
    private FrameBuffer fbo3;
    private TextureRegion fboRegion3;


    /** All the wall objects in the world. */

    protected PooledList<Obstacle> walls  = new PooledList<Obstacle>();
    protected PooledList<Obstacle> lanterns  = new PooledList<Obstacle>();
    /** All the "tops" of rocks and trees*/
    private PooledList<EnvAsset> landmarks = new PooledList<EnvAsset>();
    /** All the non-wall objects in the world. */
    protected PooledList<Obstacle> underFog  = new PooledList<Obstacle>();


    /** Arraylist of Lantern objects */
    ArrayList<Lantern> Lanterns=new ArrayList<Lantern>();

    int wallCount;
    int tileCount;
    int fogCount;

    /**
     * Creates and initialize a new instance of the rocket lander game
     *
     * The game has default gravity and other settings
     */
    public GameController(ScreenListener listener) {
        setDebug(false);
        setComplete(false);
        setFailure(false);
        world.setContactListener(this);
        this.firefly_count = 2;
        this.DEAD = false;
        this.fireflyDeathTimer=0;
        this.monster = new ArrayList<MonsterModel>();
        wallCount = 0;
        state = PLAY;
        textboxAnimateBottom = true;
        textboxAnimateTop = false;
        textboxAnimateOffset = 0;
        setScreenListener(listener);
    }

    /**
     * Resets the status of the game so that we can play again.
     *
     * This method disposes of the world and creates a new one.
     */
    public void reset() {
        Vector2 gravity = new Vector2(world.getGravity() );
        for(Obstacle obj : objects) {
            obj.deactivatePhysics(world);
        }
        // keep playing currently playing sounds
        if (super.MUSIC_ON) {
            sounds.playAllActive();
        }

        objects.clear();
        walls.clear();
        lanterns.clear();
        edgewalls.clear();
        underFog.clear();
        landmarks.clear();
        addQueue.clear();
        world.dispose();
        this.firefly_count = 2;
        this.fireflyDeathTimer=0;
        world = new World(gravity,false);
        world.setContactListener(this);
        Lanterns=new ArrayList<Lantern>();
        landmarks.clear();
        walls.clear();
        underFog.clear();
        lanterns.clear();
        setComplete(false);
        setFailure(false);
        textboxAnimateBottom = true;
        textboxAnimateTop = false;
        textboxAnimateOffset = 0;

        wallCount = 0;
        tileCount = tileBoard.getWidth()*tileBoard.getHeight();
        fogCount = 0;

        populateLevel();
        familiars.reset();
        monster.clear();
        ai = new AIControllerS(monster, gorf, tileBoard);
        monsterSpawnTimer = 500;
        countdown=120;
        DEAD = false;

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, false);
        fboRegion = new TextureRegion(fbo.getColorBufferTexture(), Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
        fboRegion.flip(false, true);

        fbo2 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, false);
        fboRegion2 = new TextureRegion(fbo2.getColorBufferTexture(), Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
        fboRegion2.flip(false, true);

        fbo3 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2, false);
        fboRegion3 = new TextureRegion(fbo3.getColorBufferTexture(), Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
        fboRegion3.flip(false, true);

        for (BoardModel.Tile[] ta: tileBoard.tiles) {
            for (BoardModel.Tile t : ta) {
                t.isFog = false;
            }
        }

        state = PLAY;
    }

    private void populateLevel() {
        /**
         * The GameController functions for Gorf-Lantern interactions
         * This includes code for incrementing and decrementing Gorf's firefly counter
         * And adds lanterns to the GameController object pool.
         */

        //float[] points = {6f,6f,6f,6f};
        //PolygonObstacle pol = new PolygonObstacle(points,gorfStart.x,gorfStart.y);
        //addObject(pol);

        // Initializer
        BoardModel.Tile[] familiarPositions=new BoardModel.Tile[4];
        ArrayList<BoardModel.Tile> fireflyPositions = new ArrayList<BoardModel.Tile>();


        //tileBoard.tiles[75][75].hasTree=3;
        for (BoardModel.Tile[] ta: tileBoard.tiles) {
            for(BoardModel.Tile t :ta) {
                if (t.isFogSpawn){
                    fogSpawn=t;
                }
                if (t.isLantern) {
                    Lantern l = new Lantern(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y,unlitTexture,litTexture, unlitTextureTop,
                            unlitTextureTop,scale);
                    l.setTexture(unlitTexture);
                    Lanterns.add(l);
                    addObject(l.object);
                    lanterns.add(l.object);
                }
                if (t.isWall) {

                    int wall_i = random(mistwalls.length - 1);
                    TextureRegion mistwall = mistwalls[wall_i];
                    BoxObstacle po = new BoxObstacle(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y, tileBoard.getTileWidth() / scale.x,
                            tileBoard.getTileHeight() / scale.y);
                    po.setBodyType(BodyDef.BodyType.StaticBody);
                    po.setDensity(BASIC_DENSITY);
                    po.setFriction(BASIC_FRICTION);
                    po.setRestitution(BASIC_RESTITUTION);
                    po.setDrawScale(scale);
                    po.setTexture(mistwall);
                    addObject(po);
                    if(t.x<3 || t.y<3 || t.x>tileBoard.getWidth()-4 || t.y>tileBoard.getHeight()-4 ){
                        edgewalls.add(po);
                    } else {
                        walls.add(po);
                    }
                    wallCount++;
                }
                if (t.hasFamiliarOne ) {
                    familiarPositions[0]=t;
                }
                if (t.hasFamiliarTwo) {
                    familiarPositions[1]=t;
                }
                if (t.hasFamiliarThree) {
                    familiarPositions[2]=t;
                }
                if (t.hasFamiliarFour) {
                    familiarPositions[3]=t;
                }
                if (t.isGorfStart) {
                    gorfStart = new Vector2(tileBoard.getTileCenterX(t)/scale.x, tileBoard.getTileCenterY(t)/scale.y);
                }
                if (t.spawnPoint){
                    fireflyPositions.add(t);
                }
                if(t.hasRock !=0){
                    int num = t.hasRock-1;
                    int index = num % 3;
                    EnvAsset rock = new EnvAsset(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y, rocks[index], rocktops[index],false, index, scale);
                    landmarks.add(rock);
                    addObject(rock.getObject());
                }
                if(t.hasTree!=0){
                    int num = t.hasTree-1;
                    int index = num % 4;
                    EnvAsset tree = new EnvAsset(tileBoard.getTileCenterX(t) / scale.x,
                            tileBoard.getTileCenterY(t) / scale.y, trees[index], treetops[index],true, index, scale);
                    landmarks.add(tree);
                    addObject(tree.getObject());

                }
            }
        }

        /**
         * Create Gorf
         */
        float dwidth  = gorfTextures[0].getRegionWidth()/(scale.x * 10);
        float dheight = gorfTextures[0].getRegionHeight()/(scale.y*2);
        gorf = new GorfModel(gorfStart.x, gorfStart.y, dwidth*0.75f, dheight*0.75f,gorfTextures);
        gorf.setDrawScale(scale);
        //gorfhat= new GorfModel(gorfStart.x+1,gorfStart.y+1,1, 1, gorfHats);
        //gorfhat.setDrawScale(scale);
        addObject(gorf);
        //addObject(gorfhat);
//        overFog.add(0, gorf);

        fireflyController=new FireflyController(fireflyAnimation,fireflyPositions,scale,tileBoard);
        fireflyController.populate();
        int size =0;

        for(BoardModel.Tile  t : familiarPositions){
            if(t!=null){
                size++;
            }
        }

        Vector2[] familiarVectors= new Vector2[size];
        for(int k=0;k<size;k++) {
            familiarVectors[k]= new Vector2(familiarPositions[k].fx/scale.x,familiarPositions[k].fy/scale.y);
        }

        if(familiarVectors.length!=0) {
            Collections.shuffle(Arrays.asList(familiarTex));
            familiars = new Familiar(familiarTex, familiarVectors, scale);
            addObject(familiars.object);
            underFog.add(familiars.object);
        }

        //this.ai = new AIController(monster, tileBoard, gorf, scale);
        this.ai = new AIControllerS(monster, gorf, tileBoard);
        fog = new FogController(tileBoard, canvas, super.screenSize, 2.0f, scale, perlinTex);
        glow = new Glow(canvas, super.screenSize, scale);

        // play a random peace marsh pair of songs for the level ONLY IF
        // there are no active songs playing
        if (super.MUSIC_ON) {
            if (sounds.activesIsEmpty()) {
                Random rand = new Random();
                int r1 = rand.nextInt(3) + 1;
                int r2 = rand.nextInt(3) + 1;
                if (r1 == 1) {
                    sounds.play("A", A_PEACE_SONG, true);
                } else if (r1 == 2) {
                    sounds.play("D", D_PEACE_SONG, true);
                } else if (r1 == 3) {
                    sounds.play("G", G_PEACE_SONG, true);
                }
                if (r2 == 1) {
                    sounds.play("B", B_MARSH_SONG, true);
                } else if (r2 == 2) {
                    sounds.play("E", E_MARSH_SONG, true);
                } else if (r2 == 3) {
                    sounds.play("F", F_MARSH_SONG, true);
                }
            }
        }
    }

    private void createMonster(float x, float y) {
        //System.out.println("Create Monster: " + x +", "+y);
        TextureRegion[] tex = monsterTextures;
        float dwidth  = tex[0].getRegionWidth()/(ai.FRAMES* scale.x*2);
        float dheight = tex[0].getRegionHeight()/(ai.FRAMES *scale.y*2);
        MonsterModel monster = new MonsterModel(x, y, dwidth, dheight,tex,monsterTextureDead, ai.FRAMES);
        monster.setDrawScale(scale);
        this.monster.add(monster);
        //System.out.println("ADDING MOSNTER");
        addObject(monster);
        monster.getBody().setUserData("monster");
    }

    private BoardModel.Tile getSpawnPoint(int timer, int gx, int gy){
        Array<Vector2> fogtiles = fog.getFogTiles();
        Vector2 v = fogtiles.get(random(fogtiles.size-1));
        BoardModel.Tile t = tileBoard.getTile((int)v.x,(int)v.y);
        if (t.isGorfGlow || t.isLanternGlow || (t.x-gx < 5 && t.x-gx < 5)){
            //System.out.println("WHOOPS! BAD SPAWN AREA, TRYING AGAIN");
            //System.out.println("Gorf glow?: " + t.isGorfGlow);
            //System.out.println("on top of Gorf?: " + (t.x-gx < 5 && t.x-gx < 5));
            timer--;
            if(timer==0){
                return fogSpawn;
            }else{
                return getSpawnPoint(timer, gx,gy);
            }
        }else{
            return t;
        }
    }

    private void respawnMonster(int gx, int gy, MonsterModel m){
        int timer=50;
        BoardModel.Tile t = getSpawnPoint(timer,gx,gy);
        m.setPosition(t.fx/scale.x,t.fy/scale.y);
        m.monsterDeathReset();
    }

    private void despawnMonster(ArrayList<MonsterModel> monster, GorfModel gorf){
        for(MonsterModel m : monster){
            float posx=m.getX()*scale.x;
            float posy=m.getY()*scale.y;
            int tx=tileBoard.screenToBoardX(posx);
            int ty=tileBoard.screenToBoardY(posy);
            int gx=tileBoard.screenToBoardX(gorf.getX()*scale.x);
            int gy=tileBoard.screenToBoardY(gorf.getY()*scale.y);
            if(tileBoard.isLanternGlow(tx,ty)){
                m.updateDeathTimer();
                if(m.getMonsterDeathTimer()==0){
                    m.setHalved(false);
                    m.setDeadTexture(monsterTextureDead);
                    m.deadmonster.setPosition(m.getX(),m.getY());
                    m.deadx=posx;
                    m.deady=posy-monsterTextures[0].getRegionHeight();
                    m.dead=true;
                    respawnMonster(gx,gy,m);
                }
            } else if (tileBoard.isGorfGlow(tx,ty)){
                m.setHalved(true);
            } else {
                m.setHalved(false);
                m.monsterDeathReset();
                //System.out.println("Monster timer reset");
            }
            //System.out.println(m.getMonsterDeathTimer());
        }
    }

    //Get the lantern at this position
    private Lantern getLantern(float x, float y){
        int xi= (int)x;
        int yi=(int)y;
        for(Lantern l : Lanterns){
            if ((Math.abs((int)l.getX() - xi ) < 6)
                    && (Math.abs((int)l.getY() - yi ) < 6))return l;
        }
        return null;
    }

    void toggle(Lantern l) {
        if (l.lit){
            firefly_count+=1;
            l.toggleLantern();
        }
        else if(!l.lit && firefly_count >= 1){
            firefly_count = firefly_count - 1;
            l.toggleLantern();
        }
    }







    /**
     * The core gameplay loop of this world.
     *
     * This method contains the specific update code for this mini-game. It does
     * not handle collisions, as those are managed by the parent class WorldController.
     * This method is called after input is read, but before collisions are resolved.
     * The very last thing that it should do is apply forces to the appropriate objects.
     *
     * @param dt Number of seconds since last animation frame
     */

    public void update(float dt) {
        if (state == PLAY) {
            boolean pressing = InputController.getInstance().didSecondary();
            if (pressing) {
                Lantern l = getLantern(gorf.getX(), gorf.getY());
                if (l != null) {
                    toggle(l);
                }
            }
            int f = familiars.getNumFam();
            familiars.update(gorf);
            int f2 = familiars.getNumFam();
            if (f2 > f) {
                if (SFX_ON) {
                    familiarFX.play();
                }
                pawAnimation.setFrame(1);
                pawTimerStart = true;
            }

            boolean isPaused = InputController.getInstance().didPause();
            if (isPaused) {
                sounds.setAllActiveVolume(0.5f);
                state = PAUSE;
            }


            if (pawTimerStart == true) {
                pawTimer = pawTimer - 1;
                if (pawTimer == 0) {
                    pawAnimation.setFrame(0);
                    pawTimer = 60;
                    pawTimerStart = false;
                }
            }

            float Gorfx = gorf.getPosition().x * scale.x;
            float Gorfy = gorf.getPosition().y * scale.y;

            BoardModel.Tile gorftile = tileBoard.tiles[tileBoard.screenToBoardX(Gorfx)][tileBoard.screenToBoardY(Gorfy)];        // NOTE: got an ArrayIndexOutOfBoundsException at some obscure tile?
            boolean inFog = gorftile.isFog;

            if (inFog) {
                fireflyDeathTimer += 1;
                if (fireflyDeathTimer > fireflyDelay) {
                    if (firefly_count != 0) {
                        firefly_count -= 1;
                    }
                    fireflyDeathTimer = 0;
                }
            }
            if (!inFog) {
                fireflyDeathTimer = 0;
            }

        if(monster.size() < MAX_MONSTER);
        if (monsterSpawnTimer != 0) {
            monsterSpawnTimer--;
        } else {
            monsterSpawnTimer = MONSTERTIMER;
            Array<Vector2> fogtiles = fog.getFogTiles();
            Vector2 v = fogtiles.get(random(fogtiles.size - 1));
            BoardModel.Tile t = tileBoard.getTile((int) v.x, (int) v.y);
            createMonster(t.fx/scale.x,t.fy/scale.y);
        }


            fog.update(gorf, Lanterns, familiars, firefly_count, tileBoard, canvas, dt);

            float forcex = InputController.getInstance().getHorizontal();
            float forcey = InputController.getInstance().getVertical();
            float moveacc = gorf.getThrust();

            // make all movement equispeed
            Vector2 temp = new Vector2(forcex * moveacc, forcey * moveacc);
            if (temp.len() > gorf.getThrust()) {
                temp = temp.setLength(gorf.getThrust());
            }

            this.gorf.setFX(temp.x);
            //this.gorfhat.setFX(temp.x);
            this.gorf.setFY(temp.y);
           // this.gorfhat.setFY(temp.y);
            gorf.applyForce();
            //gorfhat.applyForce();
            gorf.updateTexture();
            //gorfhat.updateTexture();
            gorf.gorfAnimate();
            //gorfhat.gorfAnimate();
            wrapInBounds(gorf);
            //wrapInBounds(gorfhat);


            gorf.setCollidingX(false);
            gorf.setCollidingY(false);

            for (MonsterModel m : (ai.monster)) {
                wrapInBounds(m);
                m.updateTexture();
                m.gorfAnimate();
            }
            ai.update(dt, world, firefly_count);
            despawnMonster(monster, gorf);

            if (familiars.collectAll) {
                sounds.setAllActiveVolume(0.5f);
                //victoryFX.play();
                state = WIN;
            } else if (DEAD) {
                sounds.setAllActiveVolume(0.5f);
                //deathFX.play();
                state = LOSE;
            }

            //ai.setInput();
            //float forceXMonster = ai.getHorizontal();
            //float forceYMonster = ai.getVertical();
            //float monsterthrust = monster.getThrust();
            //this.monster.setFX(forceXMonster * monsterthrust);
            //this.monster.setFY(forceYMonster * monsterthrust);
            //monster.applyForce();


            SoundController.getInstance().update();
            if (fireflyController.update(gorf)) {
                if (SFX_ON) {
                    fireflyFX.play();
                }
                if (firefly_count<MAX_FIREFLY) {
                    firefly_count++;
                }
            }

            if (InputController.getInstance().didDebug()) {
                setDebug(!isDebug());
            }

        } else if (state == PAUSE) {
            if (timerGo) { //code to slow down multiple inputs and not register all of them
                inputTimer--;
                if (inputTimer == 0) {
                    timerGo = false;
                    inputTimer = 20;
                }
            }
            float forcex= InputController.getInstance().getHorizontal();
            if (forcex > 0 && !timerGo) {
                timerGo = true;
                if (pause.getFrame() == (pause.getSize() - 1)) {
                    pause.setFrame(0);
                } else {
                    pause.setFrame(pause.getFrame() + 1);
                }
            } else if (forcex < 0 && !timerGo) {
                timerGo = true;
                if (pause.getFrame() == (0)) {
                    pause.setFrame(pause.getSize() - 1);
                } else {
                    pause.setFrame(pause.getFrame() - 1);
                }
            }
            boolean enter = InputController.getInstance().didEnter();
            if (enter && !timerGo) {
                timerGo = true;
                switch (pause.getFrame()) {
                    case 0: sounds.setAllActiveVolume(1f); state = PLAY; break;
                    case 1:
                        sounds.stopAllActive();
                        listener.exitScreen(this, LevelSelectController.EXIT_TO_MENU);
                        break;
                    case 2: sounds.setAllActiveVolume(1f); reset();
                }
            }
        } else if (state == WIN){
            //victoryFX.play();
            switch (LevelSelectController.getLevel()) {
                case 1: LevelSelectController.level1complete = true; break;
                case 2: LevelSelectController.level2complete = true; break;
                case 3: LevelSelectController.level3complete = true; break;
                case 4: LevelSelectController.level4complete = true; break;
                case 5: LevelSelectController.level5complete = true; break;
                case 6: LevelSelectController.level6complete = true; break;
                case 7: LevelSelectController.level7complete = true; break;
                case 8: LevelSelectController.level8complete = true; break;
                case 9: LevelSelectController.level9complete = true; break;
                case 10: LevelSelectController.level10complete = true; break;
                case 11: LevelSelectController.level11complete = true; break;
                case 12: LevelSelectController.level12complete = true; break;
            }
            if (timerGo) { //code to slow down multiple inputs and not register all of them
                inputTimer--;
                if (inputTimer == 0) {
                    timerGo = false;
                    inputTimer = 20;
                }
            }

            float forcex= InputController.getInstance().getHorizontal();
            if (forcex > 0 && !timerGo) {
                timerGo = true;
                if (win.getFrame() == (win.getSize() - 1)) {
                    win.setFrame(0);
                } else {
                    win.setFrame(win.getFrame() + 1);
                }
            } else if (forcex < 0 && !timerGo) {
                timerGo = true;
                if (win.getFrame() == (0)) {
                    win.setFrame(win.getSize() - 1);
                } else {
                    win.setFrame(win.getFrame() - 1);
                }
            }

            boolean enter = InputController.getInstance().didEnter();
            if (enter && !timerGo) {
                timerGo = true;
                switch (win.getFrame()) {
                    case 0: LevelSelectController.setLevel(LevelSelectController.getLevel() + 1);
                        sounds.setAllActiveVolume(1f);
                        listener.exitScreen(this, LevelSelectController.EXIT_TO_PLAY); break;
                    case 1:
                        sounds.stopAllActive();
                        listener.exitScreen(this, LevelSelectController.EXIT_TO_MENU);
                        break;
                }
            }
        } else if (state == LOSE) {
            //deathFX.play();
            if (timerGo) { //code to slow down multiple inputs and not register all of them
                inputTimer--;
                if (inputTimer == 0) {
                    timerGo = false;
                    inputTimer = 20;
                }
            }

            float forcex= InputController.getInstance().getHorizontal();
            if (forcex > 0 && !timerGo) {
                timerGo = true;
                if (gameOver.getFrame() == (gameOver.getSize() - 1)) {
                    gameOver.setFrame(0);
                } else {
                    gameOver.setFrame(gameOver.getFrame() + 1);
                }
            } else if (forcex < 0 && !timerGo) {
                timerGo = true;
                if (gameOver.getFrame() == (0)) {
                    gameOver.setFrame(gameOver.getSize() - 1);
                } else {
                    gameOver.setFrame(gameOver.getFrame() - 1);
                }
            }

            boolean enter = InputController.getInstance().didEnter();
            if (enter && !timerGo) {
                timerGo = true;
                switch (gameOver.getFrame()) {
                    case 0: sounds.setAllActiveVolume(1f); reset(); break;
                    case 1:
                        sounds.stopAllActive();
                        listener.exitScreen(this, LevelSelectController.EXIT_TO_MENU);
                        break;
                }
            }
        }
    }

    /**
     * Function to tell if Gorf (rocket) is off screen and to wrap him around, with a
     * 0.1f position buffer
     *
     * @param rocket   Gorf character
     */

    private void wrapInBounds(GorfModel rocket) {
        if (!inBounds(rocket)) {
            Vector2 currentPos = rocket.getPosition();
            if (currentPos.x<=bounds.getX()) {
                rocket.setPosition(bounds.getX()+(bounds.getWidth()*2)-0.1f,currentPos.y);
            } else if (currentPos.x>=bounds.getX()+(bounds.getWidth()*2)) {
                rocket.setPosition(bounds.getX()+0.1f,currentPos.y);
            }
            if (currentPos.y<=bounds.getY()) {
                rocket.setPosition(currentPos.x,bounds.getY()+(bounds.getHeight()*2)-0.1f);
            } else if (currentPos.y>=bounds.getY()+(bounds.getHeight()*2)) {
                rocket.setPosition(currentPos.x,bounds.getY()+0.1f);
            }
        }
    }

    public void wrapInBounds(MonsterModel m) {
        if (!inBounds(m)) {
            Vector2 currentPos = m.getPosition();
            if (currentPos.x<=bounds.getX()) {
                m.setPosition(bounds.getX()+(bounds.getWidth()*2)-0.1f,currentPos.y);
            } else if (currentPos.x>=bounds.getX()+(bounds.getWidth()*2)) {
                m.setPosition(bounds.getX()+0.1f,currentPos.y);
            }
            if (currentPos.y<=bounds.getY()) {
                m.setPosition(currentPos.x,bounds.getY()+(bounds.getHeight()*2)-0.1f);
            } else if (currentPos.y>=bounds.getY()+(bounds.getHeight()*2)) {
                m.setPosition(currentPos.x,bounds.getY()+0.1f);
            }
        }
    }

    public void draw(float dt) {
        canvas.clear();

        // Draw background on all sides and diagonals for wrap illusion
        canvas.resetCamera();
        canvas.getSpriteBatch().setShader(fog.getShader());

        // main canvas

        fbo.begin();
        canvas.clear();
        canvas.setShader(null);
        canvas.begin();
        canvas.setBlendState(GameCanvas.BlendState.ALPHA_BLEND);
        for(Obstacle obj : walls) {if(obj.isActive()){obj.draw(canvas);}}
        canvas.end();
        fbo.end();

        canvas.setShader(null);
        fog.prepShader(firefly_count);
        glow.prepShader(gorf, familiars, Lanterns, fireflyController.getFireflies(), firefly_count);

        canvas.clear();
        canvas.setBlendState(GameCanvas.BlendState.NO_PREMULT);

        canvas.begin(gorf.getPosition());
        canvas.draw(backgroundTexture, Color.WHITE, 0, canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, 0, -canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, -canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, canvas.getWidth()*2, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, -canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, -canvas.getWidth()*2, canvas.getHeight()*2, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        canvas.end();

        // Draw gorf glow
        canvas.setShader(glow.getGorfShader());
        canvas.begin(gorf.getPosition());
        drawGlow();
        canvas.end();

        // Draw lantern back glows
        canvas.setShader(glow.getLanternBackShader());
        canvas.begin(gorf.getPosition());
        drawGlow();
        canvas.end();

        // Draw familiar back glow
        canvas.setShader(glow.getFamiliarBackShader());
        canvas.begin(gorf.getPosition());
        drawGlow();
        canvas.end();

        // Draw firefly glow
        canvas.setShader(glow.getFireflyShader());
        canvas.begin(gorf.getPosition());
        //drawGlow();
        canvas.end();

        canvas.setShader(null);

        canvas.begin();
        if (LevelSelectController.getLevel() == 1) {
            drawTextbox(pauseInstructions, 0.0f, 480.0f );
            if (firefly_count > 5) {
                drawTextbox(level1tutorial3,310.0f, 330.0f);
            } else  {
                drawTextbox(level1tutorial1, 310.0f, 330.0f);
            }
            drawTextbox(level1tutorial2, 700.0f, 480.0f);
        } else if (LevelSelectController.getLevel() == 2 && familiars.getNumFam() == 1) {
            drawTextbox(level2tutorial1,1610.0f, 530.0f);
        } else if (LevelSelectController.getLevel() == 3 && familiars.getNumFam() == 0) {
            drawTextbox(level3tutorial1,900.0f, 880.0f);
        } else if (LevelSelectController.getLevel() == 8) {
            drawTextbox(takeouttutorial,1160.0f, 420.0f);
        }
        canvas.end();

        // Draw under fog
        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,-bounds.getHeight()*2));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,-bounds.getHeight()*2));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }
        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,bounds.getHeight()*2));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,bounds.getHeight()*2));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,0));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,bounds.getHeight()*2));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,0));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,-bounds.getHeight()*2));
//            canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
            for(MonsterModel m : monster) {
                m.draw(canvas);
                if(m.dead){
                    m.drawDead(canvas);
                }
            }
            for(EnvAsset env : landmarks){env.drawfull(canvas);}
            for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
            for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
            canvas.end();
        }

        canvas.begin(gorf.getPosition());
//        canvas.draw(backgroundTexture, Color.WHITE, 0, 0, canvas.getWidth()*2,canvas.getHeight()*2);
        for(EnvAsset env : landmarks){env.drawfull(canvas);}
        for(Obstacle obj : underFog) {if(obj.isActive()){obj.draw(canvas);}}
        for(MonsterModel m : monster) {
            m.draw(canvas);
            if(m.dead){
                m.drawDead(canvas);
            }
        }
        for(Firefly f : fireflyController.fireflies) {if(f!=null &&!f.isDestroyed()){f.draw(canvas);}};
        canvas.setShader(fog.getShader());
        fog.draw(canvas, backgroundTexture, new Vector2(0,0));
        canvas.end();

        canvas.begin(gorf.getPosition());
        // Draw fog
        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(0,canvas.getHeight()*2));
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(canvas.getWidth()*2,canvas.getHeight()*2));
        }
        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(0,-canvas.getHeight()*2));
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(canvas.getWidth()*2,-canvas.getHeight()*2));
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(canvas.getWidth()*2,0));
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(-canvas.getWidth()*2,-canvas.getHeight()*2));
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(-canvas.getWidth()*2,0));
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            fog.draw(canvas, backgroundTexture, new Vector2(-canvas.getWidth()*2,canvas.getHeight()*2));
        }
//        fog.draw(canvas, backgroundTexture, new Vector2(0,0));
        canvas.end();

        // Draw familiar front glow
//        canvas.setShader(glow.getFamiliarFrontShader());
//        canvas.begin(gorf.getPosition());
//        drawGlow();
//        canvas.end();

        // Draw over fog
        canvas.setShader(null);
        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,-bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,-bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }
        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,0));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,0));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,-bounds.getHeight()*2));
            for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
            canvas.end();
        }

        // Main canvas
        canvas.begin(gorf.getPosition());
        canvas.setShader(null);
        for (Obstacle obj : lanterns) { if (obj.isActive()) { obj.draw(canvas); }}
        gorf.draw(canvas);
        //gorfhat.draw(canvas);
        canvas.draw(fboRegion, 0, 0);
        for (Lantern l : Lanterns){l.drawtop(canvas);}
        for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
        for(EnvAsset env : landmarks){env.drawtop(canvas);}

        canvas.end();

        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,-bounds.getHeight()*2));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns){l.drawtop(canvas);}
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,-bounds.getHeight()*2));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns){l.drawtop(canvas);}
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(0,bounds.getHeight()*2));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns){l.drawtop(canvas);}
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth()*2,bounds.getHeight()*2));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns){l.drawtop(canvas);}
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(-bounds.getWidth() * 2, 0));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns) {
                l.drawtop(canvas);
            }
            for (Obstacle obj : edgewalls) {
                if (obj.isActive()) {
                    obj.draw(canvas);
                }
            }
            for (EnvAsset env : landmarks) {
                env.drawtop(canvas);
            }
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,bounds.getHeight()*2));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns){l.drawtop(canvas);}
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,0));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns){l.drawtop(canvas);}
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            canvas.begin(gorf.getPosition().add(bounds.getWidth()*2,-bounds.getHeight()*2));
            canvas.draw(fboRegion, 0, 0);
            for (Lantern l : Lanterns){l.drawtop(canvas);}
            for (Obstacle obj : edgewalls) { if (obj.isActive()) { obj.draw(canvas); }}
            for(EnvAsset env : landmarks){env.drawtop(canvas);}
            canvas.end();
        }

        canvas.setShader(glow.getLanternFrontShader());
        canvas.begin(gorf.getPosition());
        drawGlow();
        canvas.end();

        canvas.setShader(null);

        canvas.begin();
        for(EnvAsset env : landmarks){env.drawtop(canvas);}

        // UI
        float Gorfx= gorf.getPosition().x * scale.x;
        float Gorfy= gorf.getPosition().y * scale.y;
        BoardModel.Tile gorftile= tileBoard.tiles[tileBoard.screenToBoardX(Gorfx)][tileBoard.screenToBoardY(Gorfy)];
        boolean inFog=gorftile.isFog;
        canvas.end();
        canvas.begin(gorf.getPosition());

        displayFont.setColor(Color.WHITE);
        canvas.draw(HUDWindow, gorf.getPosition().x * scale.x + 85.0f, gorf.getPosition().y * scale.y + 105.0f);
        if (!inFog) {
            canvas.draw(HUDWhiteFirefly, gorf.getPosition().x * scale.x + 117.0f, gorf.getPosition().y * scale.y + 122.0f);
        } else {
            canvas.draw(HUDPurpleFirefly, gorf.getPosition().x * scale.x + 117.0f, gorf.getPosition().y * scale.y + 122.0f);
        }
        canvas.draw(HUDWhiteNumber_x, gorf.getPosition().x * scale.x + 150.0f, gorf.getPosition().y * scale.y + 129.0f);
        canvas.draw(pawAnimation, gorf.getPosition().x * scale.x + 200.0f, gorf.getPosition().y * scale.y + 125.0f);
        //canvas.draw(HUDWhiteNumber_x, ai.next_move.x * scale.x, ai.next_move.y * scale.y);
        canvas.draw(HUDWhiteNumber_slash, gorf.getPosition().x * scale.x + 254.0f, gorf.getPosition().y * scale.y + 127.0f);

        if (firefly_count / 10.0 < 1) {

            switch (firefly_count) {
                case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 162.0f, gorf.getPosition().y * scale.y + 127.0f);
            }
        } else {
            int tensplace = firefly_count/10;
            int onesplace = firefly_count % 10;

            switch (tensplace) {
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 161.0f, gorf.getPosition().y * scale.y + 126.0f);
            }

            switch (onesplace) {
                case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 172.0f, gorf.getPosition().y * scale.y + 126.0f);
                    break;
            }
        }

        //canvas.draw(pauseInstructions, gorf.getPosition().x * scale.x, gorf.getPosition().y * scale.y);

        if (familiars.getNumFam() / 10.0 < 1) {

            switch (familiars.getNumFam()) {
                case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
                case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 240.0f, gorf.getPosition().y * scale.y + 127.0f);
                    break;
            }
        }

        switch(familiars.getPosList().length) {
            case 0: canvas.draw(HUDWhiteNumber_0, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 1: canvas.draw(HUDWhiteNumber_1, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 2: canvas.draw(HUDWhiteNumber_2, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 3: canvas.draw(HUDWhiteNumber_3, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 4: canvas.draw(HUDWhiteNumber_4, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 5: canvas.draw(HUDWhiteNumber_5, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 6: canvas.draw(HUDWhiteNumber_6, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 7: canvas.draw(HUDWhiteNumber_7, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 8: canvas.draw(HUDWhiteNumber_8, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
            case 9: canvas.draw(HUDWhiteNumber_9, gorf.getPosition().x * scale.x + 267.0f, gorf.getPosition().y * scale.y + 127.0f);
                break;
        }
        canvas.end();

        // the minimap
        // toggle minimap with 'm'
        if (InputController.getInstance().didM()) {
            // minimap
            canvas.begin(gorf.getPosition());
            // draw background texture
            canvas.draw(minimapBackgroundTexture, Color.WHITE,
                    gorf.getPosition().x * scale.x + 105.0f,
                    gorf.getPosition().y * scale.y - 165.0f,
                    super.getMinimap().getWidth()+20f,
                    super.getMinimap().getHeight()+20f);
            // draw custom level's minimap
            canvas.draw(super.getMinimap().getTexture(), Color.WHITE,
                    gorf.getPosition().x * scale.x + 115.0f,
                    gorf.getPosition().y * scale.y - 155.0f,
                    super.getMinimap().getWidth(),
                    super.getMinimap().getHeight());
            canvas.end();
            canvas.begin(gorf.getPosition());
            // draw gorf moving representation
            super.getMinimap().drawGorf(canvas,
                    gorf.getPosition().x,
                    gorf.getPosition().y,
                    gorf.getPosition().x * scale.x + 115.0f,
                    gorf.getPosition().y * scale.y - 155.0f);
            // draw familiars
            super.getMinimap().drawFamiliar(canvas,
                    familiars.getX(),
                    familiars.getY(),
                    gorf.getPosition().x * scale.x + 115.0f,
                    gorf.getPosition().y * scale.y - 155.0f,
                    2f);
            // draw fireflies programatically
            for(Firefly f : fireflyController.fireflies) {
                if(f!=null&&!f.isDestroyed()) {
                    super.getMinimap().drawFirefly(canvas,
                            f.getX()/scale.x,
                            f.getY()/scale.y,
                            gorf.getPosition().x * scale.x + 115.0f,
                            gorf.getPosition().y * scale.y - 155.0f,
                            2f);
                }
            }
            canvas.end();
        }

        canvas.begin();
        displayFont.setColor(Color.PURPLE);
        displayFont.getData().setScale(.5f,.5f);
        // PLACEHOLDER--will be replaced by Victory screen
        //if (familiars.collectAll) {
          //  if (countdown > 0) {
            //    String vic = "Victory!";
              //  displayFont.setColor(Color.PURPLE);
               // canvas.drawText(vic, displayFont, gorf.getPosition().x * scale.x - 200.0f, gorf.getPosition().y * scale.y);
                //countdown --;
            //} else if (countdown==0) {
              //  this.setComplete(true);
            //}
        //}

        //PLACEHOLDER--will be replaced by game over screen
        //if (DEAD) {
            //if (countdown > 0) {
               // String vic = "Game Over!";
               // displayFont.setColor(Color.PURPLE);
                //canvas.drawText(vic, displayFont, gorf.getPosition().x * scale.x - 200.0f, gorf.getPosition().y * scale.y);
               // countdown --;
           // } else if (countdown==0) {
               // DEAD = false;
               // this.setComplete(true);
           // }
      //  }

        if (isDebug()) {
            canvas.beginDebug();
            for(Obstacle obj : objects) {
                obj.drawDebug(canvas);
            }
            canvas.endDebug();
            //canvas.endDebug();
        }
        canvas.end();

        if (state == PAUSE) {
            gorf.setFX(0);
            gorf.setFY(0);
            gorf.applyForce();
            canvas.begin();
            canvas.draw(pause, (gorf.getPosition().x * scale.x - (canvas.getWidth() / 2.0f) + 190.0f), gorf.getPosition().y * scale.y - (canvas.getHeight() / 2.0f) + 116.0f);
            canvas.end();
        }
        if (state == WIN) {
            gorf.setFX(0);
            gorf.setFY(0);
            gorf.applyForce();
            canvas.begin();
            canvas.draw(win, (gorf.getPosition().x) * scale.x - (canvas.getWidth() / 2.0f) + 190.0f, gorf.getPosition().y * scale.y - (canvas.getHeight() / 2.0f) + 116.0f);
            canvas.end();
        }
        if (state == LOSE) {
            gorf.setFX(0);
            gorf.setFY(0);
            gorf.applyForce();
            canvas.begin();
            canvas.draw(gameOver, (gorf.getPosition().x) * scale.x - (canvas.getWidth() / 2.0f) + 190.0f, gorf.getPosition().y * scale.y - (canvas.getHeight() / 2.0f) + 116.0f);
            canvas.end();
        }

    }

    public void drawGlow() {
        if (gorf.getY() > DEFAULT_HEIGHT / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(0,canvas.getHeight()*2));
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(canvas.getWidth()*2,canvas.getHeight()*2));
        }
        if (gorf.getY() < DEFAULT_HEIGHT / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(0,-canvas.getHeight()*2));
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(canvas.getWidth()*2,-canvas.getHeight()*2));
        }
        if (gorf.getX() > DEFAULT_WIDTH / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(canvas.getWidth()*2,0));
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() < DEFAULT_HEIGHT / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(-canvas.getWidth()*2,-canvas.getHeight()*2));
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(-canvas.getWidth()*2,0));
        }
        if (gorf.getX() < DEFAULT_WIDTH / 2f && gorf.getY() > DEFAULT_HEIGHT / 2f) {
            glow.draw(canvas, backgroundTexture, new Vector2(-canvas.getWidth()*2,canvas.getHeight()*2));
        }
        glow.draw(canvas, backgroundTexture, new Vector2(0,0));
    }

    /// CONTACT LISTENER METHODS
    /**
     * Callback method for the start of a collision
     *
     * This method is called when we first get a collision between two objects.  We use
     * this method to test if it is the "right" kind of collision.  In particular, we
     * use it to test if we made it to the win door.
     *
     * @param contact The two bodies that collided
     */
    public void beginContact(Contact contact) {
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        WorldManifold worldManifold = contact.getWorldManifold();

        if (body1.getUserData() == "monster" && body2 == gorf.getBody()) {
            this.DEAD = true;
        }
        if (body1 == gorf.getBody() && body2.getUserData() == "monster") {
            this.DEAD = true;
        }
        if(body1.getUserData()=="hat" || body2.getUserData()=="hat"){

        }

        if (body1 == gorf.getBody() || body2 == gorf.getBody()) {
            if (worldManifold.getNormal().y == 0f) {
                gorf.setCollidingX(true);
            } else if (worldManifold.getNormal().x == 0f) {
                gorf.setCollidingY(true);
            }
        }

//        if (body1 == gorf.getBody() || body2 == gorf.getBody()) {
//            if (gorf.isColliding()) {
//                gorf.setCollidingTwice(true);
//            } else {
//                gorf.setColliding(true);
//            }
//        }
    }


    /**
     * Callback method for the start of a collision
     *
     * This method is called when two objects cease to touch.
     */
    public void endContact(Contact contact) {
//        System.out.println("ENDING CONTACT");
//        Body body1 = contact.getFixtureA().getBody();
//        Body body2 = contact.getFixtureB().getBody();
//        WorldManifold worldManifold = contact.getWorldManifold();
//
//        if (body1 == gorf.getBody() || body2 == gorf.getBody()) {
//            if (worldManifold.getNormal().y == 0f) {
//                gorf.setCollidingX(false);
//            } else if (worldManifold.getNormal().x == 0f) {
//                gorf.setCollidingY(false);
//            }
//        }
    }

    private Vector2 cache = new Vector2();

    /** Unused ContactListener method */
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    /**
     * Handles any modifications necessary before collision resolution
     *
     * This method is called just before Box2D resolves a collision.  We use this method
     * to implement sound on contact, using the algorithms outlined similar to those in
     * Ian Parberry's "Introduction to Game Physics with Box2D".
     *
     * However, we cannot use the proper algorithms, because LibGDX does not implement
     * b2GetPointStates from Box2D.  The danger with our approximation is that we may
     * get a collision over multiple frames (instead of detecting the first frame), and
     * so play a sound repeatedly.  Fortunately, the cooldown hack in SoundController
     * prevents this from happening.
     *
     * @param  contact  	The two bodies that collided
     * @param  oldManifold  	The collision manifold before contact
     */

    public void preSolve(Contact contact, Manifold oldManifold) {
        float speed = 0;

        // Use Ian Parberry's method to compute a speed threshold
        Body body1 = contact.getFixtureA().getBody();
        Body body2 = contact.getFixtureB().getBody();
        WorldManifold worldManifold = contact.getWorldManifold();
        Vector2 wp = worldManifold.getPoints()[0];
        cache.set(body1.getLinearVelocityFromWorldPoint(wp));
        cache.sub(body2.getLinearVelocityFromWorldPoint(wp));
        speed = cache.dot(worldManifold.getNormal());
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }

    private void drawTextbox(TextureRegion box, float x, float y) {
        if (timerGoText) { //code to slow down multiple inputs and not register all of them
            inputTimerText--;
            if (inputTimerText == 0) {
                timerGoText = false;
                if (LevelSelectController.getLevel() == 1) {
                    inputTimerText = 9;
                } else if (LevelSelectController.getLevel() == 2) {
                    inputTimerText = 3;
                } else if (LevelSelectController.getLevel() == 3) {
                    inputTimerText = 3;
                } else if (LevelSelectController.getLevel() == 8) {
                    inputTimerText = 3;
                }
            }
        }
        if (textboxAnimateBottom && !timerGoText) {
            textboxAnimateOffset++;
            timerGoText = true;
            if (textboxAnimateOffset == 3) {
                textboxAnimateBottom = false;
                textboxAnimateTop = true;
            }
        } else if (textboxAnimateTop && !timerGoText) {
            textboxAnimateOffset--;
            timerGoText = true;
            if (textboxAnimateOffset == -3) {
                textboxAnimateTop = false;
                textboxAnimateBottom = true;
            }
        }
        canvas.draw(box, x, y + textboxAnimateOffset);
    }
}
