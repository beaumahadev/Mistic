package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.obstacle.PolygonObstacle;

/**
 * Created by Altair on 3/6/2017.
 */
public class BoxFogParticle extends PolygonObstacle {
    private float x,y;
    private Vector2 idx;
    private boolean[] expandDirs = new boolean[8];
    private boolean expanding = true;

    private static final String FOG_TEXTURE = "mistic/fog.png";
    private TextureRegion fogTexture;

    public BoxFogParticle(float[] points, float x, float y, Vector2 idx) {
        super(points,x,y);
        this.x = x;
        this.y = y;
        this.idx = idx;
    }

    private void checkExpands(boolean[][] board, boolean[][] fogBoard) {
        float uw = fogBoard.length;
        float uh = fogBoard[0].length;
        Vector2[] indices = {
                new Vector2(Math.max(0,idx.x-1), Math.min(idx.y+1,uh-1)),
                new Vector2(Math.min(Math.max(0,idx.x),uw-1), Math.min(idx.y+1,uh-1)),
                new Vector2(Math.min(idx.x+1,uw-1), Math.min(idx.y+1,uh-1)),
                new Vector2(Math.max(0,idx.x-1), Math.min(Math.max(0,idx.y),uh-1)),
                new Vector2(Math.min(idx.x+1,uw-1), Math.min(Math.max(0,idx.y),uh-1)),
                new Vector2(Math.max(0,idx.x-1), Math.max(0,idx.y-1)),
                new Vector2(Math.min(Math.max(0,idx.x),uw-1), Math.max(0,idx.y-1)),
                new Vector2(Math.min(idx.x+1,uw-1), Math.max(0,idx.y-1))
        };



        for (int i=0; i<indices.length; i++) {
//            System.out.println(board[(int)indices[i].x][(int)indices[i].y]);
            if (board[(int)indices[i].x][(int)indices[i].y] || fogBoard[(int)indices[i].x][(int)indices[i].y]) {
                expandDirs[i] = false;
            } else { expandDirs[i] = true; }
        }
    }

    public BoardTuple expand(boolean[][] board, boolean[][] fogBoard, boolean[][] newFogBoard) {
//        int[] toFog = new boolean[8];
        if (expanding) {
            checkExpands(board, fogBoard);
            float uw = fogBoard.length;
            float uh = fogBoard[0].length;
            Vector2[] indices = {
                new Vector2(Math.max(0,idx.x-1), Math.min(idx.y+1,uh-1)),
                new Vector2(Math.min(Math.max(0,idx.x),uw-1), Math.min(idx.y+1,uh-1)),
                new Vector2(Math.min(idx.x+1,uw-1), Math.min(idx.y+1,uh-1)),
                new Vector2(Math.max(0,idx.x-1), Math.min(Math.max(0,idx.y),uh-1)),
                new Vector2(Math.min(idx.x+1,uw-1), Math.min(Math.max(0,idx.y),uh-1)),
                new Vector2(Math.max(0,idx.x-1), Math.max(0,idx.y-1)),
                new Vector2(Math.min(Math.max(0,idx.x),uw-1), Math.max(0,idx.y-1)),
                new Vector2(Math.min(idx.x+1,uw-1), Math.max(0,idx.y-1))
            };

            for (int i=0; i<indices.length; i++) {
//                System.out.println("idx: " + idx);
//                System.out.println(("i: " + i));
//                System.out.println(expandDirs[i]);
                if (expandDirs[i]) {
//                    System.out.println((int)indices[i].y);
                    fogBoard[(int)indices[i].x][(int)indices[i].y] = true;
                    newFogBoard[(int)indices[i].x][(int)indices[i].y] = true;
                }
            }
            expanding = false;
        }
        return new BoardTuple(fogBoard, newFogBoard);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
