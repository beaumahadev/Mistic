package edu.cornell.gdiac.mistic;

/**
 * Created by beau on 4/23/17.
 */

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * This is a class to store the important information for a single firefly.
 * The class is lightweight, and mainly getters and setters for location and deletion status.
 * Logic and memory management is in Firefly Controller.
 */
public class EnvAsset {
    private Vector2 position;
    FilmStrip assetAnimation;
    private BoxObstacle object;
    private BoxObstacle objecttop;
    TextureRegion top;
    TextureRegion tex;
    private static final float DENSITY = 1.0f;
    private static final float FRICTION  = 0.3f;
    private static final float RESTITUTION = 0.1f;
    private int animateTimer = 7;
    public static final int FRAMES = 15;

    public EnvAsset(float x, float y, TextureRegion tex, TextureRegion top, boolean isTree, int number, Vector2 scale) {
        position = new Vector2(x,y);
        this.top=top;
        this.tex=tex;
        BoxObstacle po;
        if(isTree) {
            if (number == 0) {
                po = new BoxObstacle(x, y, tex.getRegionWidth() / (5 * scale.x),
                        tex.getRegionHeight() / (12 * scale.y));
            } else if (number == 1) {
                po = new BoxObstacle(x,y, tex.getRegionWidth() / (8 * scale.x),
                        tex.getRegionHeight() / (12 * scale.y));
            } else if (number == 2) {
                po = new BoxObstacle(x,y, tex.getRegionWidth() / (5 * scale.x),
                        tex.getRegionHeight() / (12 * scale.y));

            } else {
                po = new BoxObstacle(x,y, tex.getRegionWidth() / (5 * scale.x),
                        tex.getRegionHeight() / (10 * scale.y));
                //tex.setRegion(tex.getRegionX()+10,tex.getRegionY()+134,tex.getRegionWidth(), tex.getRegionHeight());
            }
            po.setTexture(tex);
            po.setBodyType(BodyDef.BodyType.StaticBody);
            po.setDensity(DENSITY);
            po.setFriction(FRICTION);
            po.setRestitution(RESTITUTION);
            po.setDrawScale(scale);
        }else{
            if(number==0){
                po = new BoxObstacle(x,y,tex.getRegionWidth()/scale.x ,
                        tex.getRegionHeight() / (3*scale.y));
            }else if(number==1){
                po = new BoxObstacle(x,y,tex.getRegionWidth()/scale.x ,
                        tex.getRegionHeight() / (3*scale.y));

            }else{
                po = new BoxObstacle(x,y,tex.getRegionWidth()/scale.x ,
                        tex.getRegionHeight() / (4*scale.y));

            }
            po.setBodyType(BodyDef.BodyType.StaticBody);
            po.setDensity(DENSITY);
            po.setFriction(FRICTION);
            po.setRestitution(RESTITUTION);
            po.setDrawScale(scale);
            po.setTexture(tex);


        }
        this.object=po;
        this.objecttop = new BoxObstacle(object.getWidth(),object.getHeight());
        objecttop.setX(this.getX()*scale.x);
        objecttop.setY(this.getY()*scale.y);
        this.objecttop.setTexture(top);
    }

    public void Animate(){
        this.animateTimer--;
        if ( this.animateTimer == 0) {
            this.animateTimer  = 15;
        }
        if (this.animateTimer == 1) {
            if (this.assetAnimation.getFrame() != this.assetAnimation.getSize() - 1) {
                this.assetAnimation.setFrame(this.assetAnimation.getFrame() + 1);
            } else {
                this.assetAnimation.setFrame(0);
            }
        }

    }

    public void setPosition(float x, float y){
        this.position.set(x,y);
    }

    public BoxObstacle getObject(){return this.object;}

    public Vector2 getPosition(){
        return this.position;
    }


    public float getX(){return position.x;}

    public float getY(){return position.y;}

    public void setX(float v){this.position.x = v;}

    public void setY(float v){this.position.y = v;}


    public void drawfull(GameCanvas canvas){
        this.object.draw(canvas);

    }

    public void drawtop(GameCanvas canvas){
        this.objecttop.draw(canvas);
    }

}
