package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.obstacle.BoxObstacle;

import java.util.ArrayList;

import static com.badlogic.gdx.utils.JsonValue.ValueType.object;

/**
 * Created by beau on 3/20/17.
 *
 */
public class Lantern {

    Vector2 scale;
    Vector2 position;
    boolean lit;
    BoxObstacle object;
    BoxObstacle top;
    TextureRegion litTex;
    TextureRegion unlitTex;
    TextureRegion littop;
    TextureRegion unlittop;



    private static final float LAMP_DENSITY = 1.0f;
    private static final float LAMP_FRICTION  = 0.3f;
    private static final float LAMP_RESTITUTION = 0.1f;

    public Lantern(float x, float y, TextureRegion unlitTexture, TextureRegion litTexture,
                   TextureRegion unlittop, TextureRegion littop, Vector2 scale){
        this.scale=scale;
        this.unlittop=unlittop;
        this.littop=littop;
        this.position=new Vector2(x,y);
        this.lit=false;
        this.unlitTex=unlitTexture;
        this.litTex=litTexture;
        this.object = new BoxObstacle(x,y,unlitTexture.getRegionWidth()/(scale.x*8),
                unlitTexture.getRegionHeight()/(scale.y*8));
        //litTexture.setRegion( litTexture.getRegionX()-1, litTexture.getRegionY()+13, litTexture.getRegionWidth(), litTexture.getRegionHeight());
        //unlitTexture.setRegion(unlitTexture.getRegionX()-1,unlitTexture.getRegionY()+13,unlitTexture.getRegionWidth(),unlitTexture.getRegionHeight());
        object.setDensity(LAMP_DENSITY);
        object.setFriction(LAMP_FRICTION);
        object.setRestitution(LAMP_RESTITUTION);
        object.setBodyType(BodyDef.BodyType.StaticBody);
        object.setName("lantern");
        object.setDrawScale(scale);
        this.top=new BoxObstacle(object.getWidth(),object.getHeight(),object.getX()*scale.x,object.getY()*scale.y);
        this.top.setTexture(unlittop);
    }

    public void setTexture(TextureRegion tex){
        this.object.setTexture(tex);
    }


    public void toggleLantern() {
        if (lit) {
            lit = false;
            this.setTexture(unlitTex);
            top.setTexture(unlittop);
        } else if (!lit){
            lit = true;
//            this.setTexture(litTex);
//            top.setTexture(littop);
        }
    }

    public float getX(){
        return this.position.x;
    }

    public float getY(){
        return this.position.y;
    }

   public void drawtop(GameCanvas canvas){
        top.setX(this.object.getX()*scale.x);
        top.setY(this.object.getY()*scale.y);
        top.draw(canvas);
        /**if(this.lit){
            canvas.draw(this.littop,object.getX()*scale.x, object.getY()*scale.y);
        }else if (!this.lit){
            canvas.draw(this.unlittop,object.getX()*scale.x, object.getY()*scale.y);
        }*/

    }
}
