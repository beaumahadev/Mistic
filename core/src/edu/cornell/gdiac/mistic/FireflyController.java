package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import edu.cornell.gdiac.obstacle.BoxObstacle;
import edu.cornell.gdiac.util.FilmStrip;


import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.badlogic.gdx.math.MathUtils.degreesToRadians;
import static com.badlogic.gdx.math.MathUtils.random;

/**
 * This class is the Firefly pool that handles allocation and deletion of
 * firefly objects. On its update loop new firelies are spawned if  certain
 * time has elapsed, and collected firefly objects are removed from memory.
 * Also controls the small up-down motion for static fireflies.
 */
public class FireflyController {

    private static final float FIREFLY_DENSITY = 1.0f;
    private static final float FIREFLY_FRICTION  = 0.3f;
    private static final float FIREFLY_RESTITUTION = 0.1f;
    BoardModel board;
    private TextureRegion tex;
    private Vector2 scale;
    /**The time between firefly spawns*/
    private float SPAWN_TIME;
    private BoardModel.Tile[] fireflyLocations;
    public Firefly[] fireflies;
    private FilmStrip animate;

    public FireflyController(TextureRegion texture, ArrayList<BoardModel.Tile> locations, Vector2 scale, BoardModel board){
        fireflies=new Firefly[locations.size()];
        tex=texture;
        this.board=board;
        this.fireflyLocations=locations.toArray(new BoardModel.Tile[locations.size()]);
        this.scale=scale;
    }

    public void populate(){
        for(int i=0;i<fireflyLocations.length;i++){
            float fx = board.boardtoScreenX(fireflyLocations[i].x);
            float fy = board.boardToScreenY(fireflyLocations[i].y);
            Firefly f= new Firefly(fx,fy,tex);
            fireflies[i]=f;
        }
    }

    public boolean update(GorfModel gorf){
        boolean collected=false;
        for(Firefly F : fireflies) {
            F.fireflyAnimate();
            fogDeath(F);
            if (!F.isDestroyed()) {
                float dx = Math.abs((F.getX() / scale.x) - gorf.getX()+4);
                float dy = Math.abs((F.getY() / scale.y) - gorf.getY()+4);
                if (dx < gorf.getWidth() && dy < gorf.getHeight()) {
                    F.setDestroyed(true);
                    collected=true;
                }
            }else{
                F.respawnTimer++;
                if(F.respawnTimer==F.RESPAWN){
                    F.setDestroyed(false);
                    F.respawnTimer=0;
                }
            }
        }

        return collected;
    }

    public void fogDeath(Firefly f){
        if (board.tiles[board.screenToBoardX(f.getX())][board.screenToBoardY(f.getY())].isFog){
            f.setDestroyed(true);
        }
    }

    public void reset(){
        //TODO
    }


    public Firefly[] getFireflies(){
        return fireflies;
    }

}
