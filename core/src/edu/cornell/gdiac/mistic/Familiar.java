package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import edu.cornell.gdiac.obstacle.BoxObstacle;

import java.util.ArrayList;

import static com.badlogic.gdx.utils.JsonValue.ValueType.object;

/**
 * Created by beau on 3/20/17.
 *
 */
public class Familiar {

    private Vector2 scale;
    private Vector2 position;
    private boolean collected;
    public BoxObstacle object;
    private TextureRegion[] texList;
    private Vector2[] posList;
    private int NUM_FAM;
    public boolean collectAll;
    public boolean one_collected;



    private static final float DENSITY = 1.0f;
    private static final float FRICTION  = 0.3f;
    private static final float RESTITUTION = 0.1f;

    public Familiar(TextureRegion[] textures, Vector2[] positions, Vector2 scale){
        this.scale=scale;
        this.collectAll=false;
        this.setCollected(false);
        this.posList=positions;
        this.position=posList[0];
        this.texList=textures;
        NUM_FAM=0;
        this.object = new BoxObstacle(position.x,position.y,
                texList[0].getRegionWidth()/(scale.x*2),texList[0].getRegionHeight()/(scale.y*2));
        object.setTexture(texList[0]);
        object.setDensity(DENSITY);
        object.setFriction(FRICTION);
        object.setRestitution(RESTITUTION);
        object.setBodyType(BodyDef.BodyType.StaticBody);
        object.setName("familiar");
        object.setDrawScale(scale);
    }



    public void update(GorfModel gorf) {
        checkCollected(gorf);
        if (collected) {
            NUM_FAM += 1;
            if (NUM_FAM < posList.length) {
                this.object.setTexture(texList[NUM_FAM]);
                this.position = posList[NUM_FAM];
                this.object.setPosition(this.position);
                this.collected = false;
            }else{
                this.collectAll=true;
                this.position = new Vector2(1000,1000);
                this.object.setPosition(this.position);
            }
        }
    }

    public void checkCollected(GorfModel gorf){
        float dx = Math.abs((this.object.getX()) - gorf.getX());
        float dy = Math.abs((this.object.getY()) - gorf.getY());
        if (dx < gorf.getWidth() && dy < gorf.getHeight()) {
            setCollected(true);
        }else{
            setCollected(false);
        }
    }

    public void reset(){
        NUM_FAM=0;
    }

    public void setTexture(TextureRegion tex){
        this.object.setTexture(tex);
    }

    public void setCollected(boolean bool) {
        this.collected=bool;
    }

    public float getX(){
        return this.position.x;
    }

    public float getY(){
        return this.position.y;
    }

    public int getNumFam() { return NUM_FAM;
    }

    public Vector2[] getPosList() {
        return posList;
    }

    public TextureRegion getTexture(){return this.texList[NUM_FAM];}
}
