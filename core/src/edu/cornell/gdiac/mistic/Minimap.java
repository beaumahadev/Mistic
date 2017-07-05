package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.GameCanvas;
import com.badlogic.gdx.graphics.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by tkepler on 4/19/17.
 */
public class Minimap {
    /** size variables */
    private float width;
    private float height;
    private float tileWidth;
    private float tileHeight;

    /** private magic scale numbers and board to screen slash screen to
     * board accidental rounding error make up numbers SHHH don't tell anyone */
    private static float MAGIC_SCALE_X = 7.92f;
    private static float MAGIC_SCALE_Y = 5.94f;
    private static float MAGIC_ERROR_MAKEUP_X = 1.04f;
    private static float MAGIC_ERROR_MAKEUP_Y = 1.06f;


    /** frame counters for "moving" assets so people can tell
     * where the familiars are on the minimap because eyes track movement and
     * people are stupid */
    private int on = 30;
    private int off = 30;

    /** Texture for the minimap */
    private TextureRegion minimapTexture;

    /** getters */
    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    public TextureRegion getTexture() {
        return minimapTexture;
    }

    /** setters */
    public void setTexture(TextureRegion t) {
        this.minimapTexture = t;
    }

    /** constructor */
    public Minimap(float w, float h, int i, int j, TextureRegion t) {
        this.width = w;
        this.height = h;
        this.tileWidth = w/i*3f;
        this.tileHeight = h/j*4f;
        this.minimapTexture = t;
    }

    /** draw this minimaps gorf object */
    public void drawGorf(GameCanvas canvas, float x, float y, float oX, float oY) {
        // draw rectangle of tileWidth and tileHeight at point
        // (oX + (tileWidth * p.x), oY + (tileHeight * p.y)), of gorf color

        canvas.beginMinimapDrawRect(new Color(0x37c48dff),
                oX+(tileWidth*(x/MAGIC_SCALE_X)),oY+(tileHeight*(y/MAGIC_SCALE_Y)),
                tileWidth,tileHeight);
    }

    /** draw this minimaps familiars */
    public void drawFamiliar(GameCanvas canvas, float x, float y, float oX, float oY, float r) {
        // draw circle of tileWidth and tileHeight at point
        // (oX + (tileWidth * p.x), oY + (tileHeight * p.y)), of gorf color
        // modulate the size every half second
        if (on>0) {
            canvas.beginMinimapDrawCir(new Color(0x51D7FFff),
                    oX+(tileWidth*(x/MAGIC_SCALE_X)),oY+(tileHeight*(y/MAGIC_SCALE_Y)),r*1.8f);
            on--;
        } else {
            if (off>0) {
                canvas.beginMinimapDrawCir(new Color(0x51D7FFff),
                        oX+(tileWidth*(x/MAGIC_SCALE_X)),oY+(tileHeight*(y/MAGIC_SCALE_Y)),r*1.3f);
                off--;
            } else {
                on = 30;
                off = 30;
            }
        }
    }

    /** draw this minimaps firefly assets */
    public void drawFirefly(GameCanvas canvas, float x, float y, float oX, float oY, float r) {
        canvas.beginMinimapDrawCir(new Color(0xffdc00ff),
                oX+(tileWidth*(x/MAGIC_SCALE_X)*MAGIC_ERROR_MAKEUP_X),
                oY+(tileHeight*(y/MAGIC_SCALE_Y)*MAGIC_ERROR_MAKEUP_Y),r);
    }
}
