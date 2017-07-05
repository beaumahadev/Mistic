package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.obstacle.PolygonObstacle;

import java.util.ArrayList;

/**
 * Created by Altair on 3/6/2017.
 */

public class BoxFog extends PolygonObstacle {
    private float[] points;
    private static final EarClippingTriangulator TRIANGULATOR = new EarClippingTriangulator();
    private ArrayList<BoxFogParticle> particles = new ArrayList<BoxFogParticle>();

    public BoxFog(float[] points, float x, float y, Vector2 idx) {
        super(points, x, y);
        particles.add(new BoxFogParticle(points,x,y,idx));
//        this.points = points;
//        addJunction();
    }

    public BoardTuple expand(boolean[][] board, boolean[][] fogBoard) {
        boolean[][] newFogBoard = new boolean[fogBoard.length][fogBoard[0].length];
        BoardTuple fogBoards = new BoardTuple(fogBoard, newFogBoard);
        for (int i=0; i<particles.size(); i++) {
            fogBoards = particles.get(i).expand(board, fogBoard, newFogBoard);
        }
        return fogBoards;
    }

    public void addParticle(BoxFogParticle particle) {
        particles.add(particle);
    }
}