package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.InputController;
import edu.cornell.gdiac.WorldController;
import edu.cornell.gdiac.util.ScreenListener;

/**
 * Created by tbkepler on 4/24/17.
 */
public class DefeatScreenController extends WorldController implements Screen {
    //private static final String BACKGROUND;


    private AssetState menuAssetState = AssetState.EMPTY;
    private ScreenListener listener;

    public static final int EXIT_TO_MENU = 100;
    public static final int EXIT_TO_PLAY = 101;

    public void preLoadContent(AssetManager manager) {
        if (menuAssetState != AssetState.EMPTY) {
            return;
        }

        super.preLoadContent(manager);
    }

    public void loadContent(AssetManager manager, GameCanvas canvas) {
        if (menuAssetState != AssetState.LOADING) {
            return;
        }
    }

    public DefeatScreenController() {
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
        world = new World(gravity,false);
    }

    public void update(float dt) {
        boolean pressing = InputController.getInstance().didEnter();
        boolean back = InputController.getInstance().didExit();
        if (back) {
            listener.exitScreen(this, EXIT_TO_MENU);
        } else if (pressing) {

        }

    }

    @Override
    public void render(float dt) {
        super.render(dt);
        update(dt);
        draw(dt);
    }

    public void draw(float dt) {
        canvas.clear();
        canvas.resetCamera();
        canvas.begin();
        String vic = "Game Over!";
        displayFont.setColor(Color.PURPLE);
        canvas.drawText(vic, displayFont, canvas.getWidth(), canvas.getHeight());
    }

    public void setScreenListener(ScreenListener listener) {
        this.listener = listener;
    }
}
