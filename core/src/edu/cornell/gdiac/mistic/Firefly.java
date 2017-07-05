package edu.cornell.gdiac.mistic;


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
public class Firefly {
    private Vector2 position;
    FilmStrip fireflyAnimation;
    /** If the firefly should be scheduled for deletion*/
    private boolean destroyed;
    private BoxObstacle object;

    private static final float FIREFLY_DENSITY = 1.0f;
    private static final float FIREFLY_FRICTION  = 0.3f;
    private static final float FIREFLY_RESTITUTION = 0.1f;
    private int fireflyAnimateTimer = TIMER;
    int respawnTimer;
    private Vector2 scale = new Vector2(1f,1f);
    public static final int FRAMES = 18;
    public static final int RESPAWN=700;
    public static final int TIMER = 8;
    private boolean decreasing = true;


    public Firefly(float x, float y, TextureRegion texture) {
        this.fireflyAnimation  = new FilmStrip(texture.getTexture(),1,FRAMES,FRAMES);
        this.fireflyAnimation.setFrame(random(FRAMES-1
        ));
        position = new Vector2(x,y);
        destroyed= false;
        object = new BoxObstacle(x,y,texture.getRegionWidth()/scale.x,texture.getRegionHeight()/scale.y);
        object.setDensity(FIREFLY_DENSITY);
        object.setFriction(FIREFLY_FRICTION);
        object.setRestitution(FIREFLY_RESTITUTION);
        object.setName("firefly"+x+y);
        object.setDrawScale(scale);

        //object.setTexture(texture);
    }

    public FilmStrip getFireflyAnimation() {
        return fireflyAnimation;
    }

    public void fireflyAnimate(){
        //timer goes from 6-0 again and again
        //every time we hit 0 if !decreasing we increment frame
        //if decreasing we go backwards through frames
        fireflyAnimateTimer--;
        if(this.fireflyAnimateTimer==0) {
            this.fireflyAnimateTimer = TIMER;
            if(!decreasing){
                if(this.fireflyAnimation.getFrame() == this.fireflyAnimation.getSize()-1){
                    this.decreasing=true;
                }else{
                    this.fireflyAnimation.setFrame(this.fireflyAnimation.getFrame()+1);
//                    this.fireflyAnimation.setFrame(17);
                }
            }else if(decreasing) {
                if (this.fireflyAnimation.getFrame() == 0) {
                    this.decreasing = false;
                } else {
                    this.fireflyAnimation.setFrame(this.fireflyAnimation.getFrame() - 1);
//                    this.fireflyAnimation.setFrame(17);
                }
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

    public boolean isDestroyed(){return this.destroyed;}

    public void setDestroyed(boolean b){this.destroyed=b;}

    public void draw(GameCanvas canvas){
        this.object.draw(canvas);
        canvas.draw(fireflyAnimation,this.getX(),this.getY());
    }

}
