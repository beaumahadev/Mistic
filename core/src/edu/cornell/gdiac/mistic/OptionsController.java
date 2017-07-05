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
import org.lwjgl.Sys;

/**
 * Created by Nathaniel on 5/18/17.
 */
public class OptionsController extends WorldController implements Screen {
    private static final String GORF_TEXTURE = "mistic/gorfright.png";
    private static final String BACKGROUND = "mistic/options/options_screen.png";
    private static final String SPEAKER_ON = "mistic/options/speaker_on.png";
    private static final String SPEAKER_OFF = "mistic/options/speaker_off.png";
    private static final String MUSIC_ON = "mistic/options/music_on.png";
    private static final String MUSIC_OFF = "mistic/options/music_off.png";
    private static final String GLOW = "mistic/options/glow.png";
    private static final String ARROWS = "mistic/options/arrows.png";
    private static final String WASD = "mistic/options/wasd.png";

    private TextureRegion menu;
    private TextureRegion background;
    private TextureRegion gorf;
    private TextureRegion speakerOn;
    private TextureRegion speakerOff;
    private TextureRegion musicOn;
    private TextureRegion musicOff;
    private TextureRegion glow;
    private TextureRegion arrows;
    private TextureRegion wasd;

    private static final String FIREFLY = "mistic/firefly_static.png";
    private FilmStrip firefly;

    private static final int LEVEL_CAP = 12;

    private int optionSelect = 0;

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



    public void preLoadContent(AssetManager manager) {
        if (menuAssetState != AssetState.EMPTY) {
            return;
        }


        menuAssetState = AssetState.LOADING;

        manager.load(BACKGROUND, Texture.class);
        assets.add(BACKGROUND);

        manager.load(SPEAKER_ON, Texture.class);
        assets.add(SPEAKER_ON);
        manager.load(SPEAKER_OFF, Texture.class);
        assets.add(SPEAKER_OFF);
        manager.load(MUSIC_ON, Texture.class);
        assets.add(MUSIC_ON);
        manager.load(MUSIC_OFF, Texture.class);
        assets.add(MUSIC_OFF);
        manager.load(GLOW, Texture.class);
        assets.add(GLOW);
        manager.load(ARROWS, Texture.class);
        assets.add(ARROWS);
        manager.load(WASD, Texture.class);
        assets.add(WASD);

        manager.load(FIREFLY, Texture.class);
        assets.add(FIREFLY);


        super.preLoadContent(manager);
    }

    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (menuAssetState != AssetState.LOADING) {
            return;
        }

        menu = createTexture(manager, BACKGROUND, false);
        speakerOn = createTexture(manager, SPEAKER_ON, false);
        speakerOff = createTexture(manager, SPEAKER_OFF, false);
        musicOn = createTexture(manager, MUSIC_ON, false);
        musicOff = createTexture(manager, MUSIC_OFF, false);
        glow = createTexture(manager, GLOW, false);
        arrows = createTexture(manager, ARROWS, false);
        wasd = createTexture(manager, WASD, false);

        firefly = createFilmStrip(manager, FIREFLY, 1, 18, 18);

    }

    public OptionsController    () {
        setDebug(false);
        setComplete(false);
        setFailure(false);
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
                inputTimer = 20;
            }
        }
        float forcey = InputController.getInstance().getVertical();
        if (forcey < 0 && !timerGo) {
            timerGo = true;
            if (optionSelect != 2) {
                inputTimer = inputTimer - 1;
                optionSelect++;
            } else {
                optionSelect = 0;
            }
        } else if (forcey > 0 && !timerGo) {
            timerGo = true;
            if (optionSelect != 0) {
                optionSelect--;
            } else {
                optionSelect = 2;
            }
        }

        boolean didEnter = InputController.getInstance().didEnter();
        if (didEnter && !timerGo) {
            switch (optionSelect) {
                case 0:
                    super.MUSIC_ON = !super.MUSIC_ON;
                    timerGo = true;
                    break;
                case 1:
                    super.SFX_ON = !super.SFX_ON;
                    timerGo = true;
                    break;
                case 2:
                    InputController.getInstance().WASD_ON = !InputController.getInstance().WASD_ON;
                    timerGo = true;
                    break;
            }
        }


        firflyAnimateTimer--;
        if (firflyAnimateTimer == 0) {
            firflyAnimateTimer = 20;
        }

        boolean back = InputController.getInstance().didExit();
        if (back) {
            listener.exitScreen(this, EXIT_TO_MENU); //exit to menu when player presses escape
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
        canvas.draw(menu, Color.WHITE, 0, 0, canvas.getWidth() * 2, canvas.getHeight() * 2);

        if (firflyAnimateTimer == 1) {
            if (firefly.getFrame() != firefly.getSize() - 1) {
                firefly.setFrame(firefly.getFrame() + 1);
            } else {
                firefly.setFrame(0);
            }
        }
        switch (optionSelect) {
            case 0: canvas.draw(firefly, canvas.getWidth() / 2.0f + 150.0f, canvas.getHeight() / 2.0f + 452.0f);
                break;
            case 1: canvas.draw(firefly, canvas.getWidth() / 2.0f + 175.0f, canvas.getHeight() / 2.0f + 335.0f);
                break;
            case 2: canvas.draw(firefly, canvas.getWidth() / 2.0f - 60.0f, canvas.getHeight() / 2.0f - 50.0f);
                break;
        }

        if (super.MUSIC_ON) {
            canvas.draw(musicOn, 1100.0f, 750.0f);
        } else {
            canvas.draw(musicOff, 1155.0f, 805.0f);
        }
        if (super.SFX_ON) {
            canvas.draw(speakerOn, 1100.0f, 600.0f);
        } else {
            canvas.draw(speakerOff, 1155.0f, 675.0f);
        }
        if (!InputController.getInstance().WASD_ON) {
            canvas.draw(glow,400.0f,0.0f);
        } else {
            canvas.draw(glow,950.0f,0.0f);
        }
        canvas.draw(arrows,639.0f,290.0f);
        canvas.draw(wasd,1189.0f,290.0f);
        canvas.end();
    }

    public int getLevel() {
        return level;
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
}
