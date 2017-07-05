package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.ai.DefaultTimepiece;
import com.badlogic.gdx.ai.steer.Proximity;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteerableAdapter;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.*;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import org.lwjgl.Sys;

import java.util.ArrayList;

/**
 * Created by Nathaniel on 4/12/17.
 */
public class AIControllerS {
    DefaultTimepiece timePiece;
    private Seek<Vector2> seekTarget;
    private Wander<Vector2> wandering;
    private Flee<Vector2> flee;
    public int FRAMES=5;

    EnemyWrapper enemyWrapper = new EnemyWrapper();
    TargetWrapper targetWrapper = new TargetWrapper();
    EmptyWrapper emptyWrapper = new EmptyWrapper();

    EnemyProximity enemyProximity;

    ArrayList<MonsterModel> monster;
    GorfModel gorf;
    BoardModel board;

    PrioritySteering<Vector2> monsterBehavior;

    private SteeringAcceleration<Vector2> steering = new SteeringAcceleration<Vector2>(new Vector2(0, 0));
    private boolean initialized = false;

    public AIControllerS(ArrayList<MonsterModel> monster, GorfModel gorf, BoardModel board) {
        timePiece = new DefaultTimepiece();
        seekTarget = new Seek<Vector2>(emptyWrapper);
        wandering = new Wander<Vector2>(emptyWrapper);
        flee = new Flee<Vector2>(emptyWrapper);
        this.monster = monster;
        this.gorf = gorf;
        this.monsterBehavior = new PrioritySteering<Vector2>(emptyWrapper);
        monsterBehavior.add(seekTarget);
        monsterBehavior.add(flee);
        this.board = board;
    }

    public void update(float dt, World world, int firefly_count) {
        seekTarget.setEnabled(false);
        flee.setEnabled(false);

        for (MonsterModel m : monster) {
                m.setFY(0.0f);
                m.setFX(0.0f);
                m.applyForce();
        }
        timePiece.update(dt);

        initializeSteering();

        Vector2 gorfPos = gorf.getPosition();
        int tileX = board.screenToBoardX(gorfPos.x * 8.0f);
        int tileY = board.screenToBoardY(gorfPos.y * 8.0f);
        BoardModel.Tile gorftile= board.tiles[tileX][tileY];
        boolean inFog=gorftile.isFog;
        boolean unsafe = false;
        if (tileX == 99 || tileY == 99 || tileX == 0 || tileY ==0) {
            unsafe = true;
        }

        for (MonsterModel m : monster) {
            enemyWrapper.model = m;
            targetWrapper.model = gorf;
            seekTarget.setTarget(targetWrapper);
            flee.setTarget(targetWrapper);
            if (!inFog && !unsafe) {
                flee.setEnabled(true);
            } else if (firefly_count < 50) {
                seekTarget.setEnabled(true);
            } else {
                flee.setEnabled(true);
            }
            //wandering.setOwner(enemyWrapper);
            //wandering.setWanderOffset(.5f);
            //wandering.setWanderRate(1);
            //wandering.setWanderRadius(40);
            //wandering.setEnabled(true);
            monsterBehavior.calculateSteering(steering);
            applySteering(steering, m);
        }

    }

    private void applySteering(SteeringAcceleration<Vector2> steering, MonsterModel m) {
        Vector2 monsterPos = m.getPosition();
        int tileX = board.screenToBoardX(monsterPos.x * 8.0f);
        if (tileX>99) {tileX = 99;} else if (tileX<0) {tileX = 0;}  // should fix bug?
        int tileY = board.screenToBoardY(monsterPos.y * 8.0f);
        if (tileY>99) {tileY = 99;} else if (tileY<0) {tileY = 0;}  // should fix bug?
        BoardModel.Tile gorftile= board.tiles[tileX][tileY];        // NOTE: got an ArrayIndexOutOfBoundsException at some obscure tile?
        //BoardModel.Tile gorftile2 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f) + 1][board.screenToBoardY(monsterPos.y * 8.0f) + 1];
        //BoardModel.Tile gorftile3 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f) + 1][board.screenToBoardY(monsterPos.y * 8.0f)];
        //BoardModel.Tile gorftile4 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f)][board.screenToBoardY(monsterPos.y * 8.0f) + 1];
        //BoardModel.Tile gorftile5 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f) + 1][board.screenToBoardY(monsterPos.y * 8.0f) - 1];
        //BoardModel.Tile gorftile6 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f) - 1][board.screenToBoardY(monsterPos.y * 8.0f) - 1];
        //BoardModel.Tile gorftile7 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f) - 1][board.screenToBoardY(monsterPos.y * 8.0f)];
        //BoardModel.Tile gorftile8 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f)][board.screenToBoardY(monsterPos.y * 8.0f) - 1];
        //BoardModel.Tile gorftile9 = board.tiles[board.screenToBoardX(monsterPos.x * 8.0f) - 1][board.screenToBoardY(monsterPos.y * 8.0f) + 1];
        boolean inFog=gorftile.isFog;
        boolean inFogSpawn = gorftile.isFogSpawn;
        //boolean inFog2=gorftile2.isFog;
        //boolean inFogSpawn2 = gorftile2.isFogSpawn;
        //boolean inFog3=gorftile3.isFog;
        //boolean inFogSpawn3 = gorftile3.isFogSpawn;
        //boolean inFog4=gorftile4.isFog;
        //boolean inFogSpawn4 = gorftile4.isFogSpawn;
        //boolean inFog5=gorftile5.isFog;
        //boolean inFogSpawn5 = gorftile5.isFogSpawn;
        //boolean inFog6=gorftile6.isFog;
        //boolean inFogSpawn6 = gorftile6.isFogSpawn;
        //boolean inFog7=gorftile7.isFog;
        //boolean inFogSpawn7 = gorftile7.isFogSpawn;
        //boolean inFog8=gorftile8.isFog;
        //boolean inFogSpawn8 = gorftile8.isFogSpawn;
        //boolean inFog9=gorftile9.isFog;
        //boolean inFogSpawn9 = gorftile9.isFogSpawn;
        if (inFog || inFogSpawn) {
            m.setFX(steering.linear.x * 7.0f);
            m.setFY(steering.linear.y * 7.0f);
            if (m.halved) {
                halfSpeed(m);
            }
            m.applyForce();
        }

        //enemyWrapper.model.setAngularVelocity(steering.angular);
    }

    private void initializeSteering() {
        if (!initialized) {

            seekTarget.setOwner(enemyWrapper);
            flee.setOwner(enemyWrapper);
        }

        initialized = true;
    }

    public void halfSpeed(MonsterModel m) {
        m.setFX(m.getFX()/2.0f);
        m.setFY(m.getFY()/2.0f);
    }



    private class EnemyProximity implements Proximity<Vector2> {

        World world;
        SteeringAgent owner;

        public EnemyProximity() {
        }

        @Override
        public Steerable<Vector2> getOwner() {
            return owner;
        }

        @Override
        public void setOwner(Steerable<Vector2> owner) {
            this.owner = (SteeringAgent) owner;
        }

        @Override
        public int findNeighbors(com.badlogic.gdx.ai.steer.Proximity.ProximityCallback<Vector2> callback) {

            int numNeighbors = 0;

            return numNeighbors;
        }

    }

    private class TargetWrapper implements Steerable<Vector2> {

        GorfModel model;
        boolean isTagged;
        private Vector2 cacheVector = new Vector2();
        private Vector2 zeroVector = new Vector2(0,0);

        public TargetWrapper() { }

        @Override
        public Vector2 getPosition() {
            cacheVector.x = model.getX();
            cacheVector.y = model.getY();
            return cacheVector;
        }

        @Override
        public float getOrientation() {
            return model.getAngle();
        }

        @Override
        public void setOrientation(float orientation) {
            model.getBody().setTransform(getPosition(), orientation);
        }

        @Override
        public float vectorToAngle(Vector2 vector) {
            return (float) Math.atan2(((Vector2) vector).y, ((Vector2) vector).x);
        }

        @Override
        public Vector2 angleToVector(Vector2 outVector, float angle) {
            ((Vector2) outVector).x = MathUtils.cos(angle);
            ((Vector2) outVector).y = MathUtils.sin(angle);
            return outVector;
        }

        @Override
        public Location<Vector2> newLocation() {
            return new SteerableAdapter<Vector2>();
        }

        @Override
        public float getZeroLinearSpeedThreshold() {
            return 0;
        }

        @Override
        public void setZeroLinearSpeedThreshold(float value) { }

        @Override
        public float getMaxLinearSpeed() {
            return 0;
        }

        @Override
        public void setMaxLinearSpeed(float maxLinearSpeed) { }

        @Override
        public float getMaxLinearAcceleration() {
            return 0;
        }

        @Override
        public void setMaxLinearAcceleration(float maxLinearAcceleration) { }

        @Override
        public float getMaxAngularSpeed() {
            return 0;
        }

        @Override
        public void setMaxAngularSpeed(float maxAngularSpeed) { }

        @Override
        public float getMaxAngularAcceleration() {
            return 0;
        }

        @Override
        public void setMaxAngularAcceleration(float maxAngularAcceleration) { }

        @Override
        public Vector2 getLinearVelocity() {
            return zeroVector;
        }

        @Override
        public float getAngularVelocity() {
            return 0;
        }

        @Override
        public float getBoundingRadius() {
            return model.getWidth();
        }

        @Override
        public boolean isTagged() {
            return isTagged;
        }

        @Override
        public void setTagged(boolean tagged) {
            isTagged = tagged;
        }

    }

    private class EnemyWrapper implements Steerable<Vector2> {

        MonsterModel model;
        boolean isTagged;

        private Vector2 cacheVector = new Vector2();

        public EnemyWrapper() {
        }

        @Override
        public Vector2 getPosition() {
            cacheVector.x = model.getX();
            cacheVector.y = model.getY();
            return cacheVector;
        }

        @Override
        public float getOrientation() {
            return model.getBody().getAngle();
        }

        @Override
        public void setOrientation(float orientation) {
            model.getBody().setTransform(getPosition(), orientation);
        }

        @Override
        public float vectorToAngle(Vector2 vector) {
            return (float) Math.atan2(((Vector2) vector).y, ((Vector2) vector).x);
        }

        @Override
        public Vector2 angleToVector(Vector2 outVector, float angle) {
            ((Vector2) outVector).x = MathUtils.cos(angle);
            ((Vector2) outVector).y = MathUtils.sin(angle);
            return outVector;
        }

        @Override
        public Location<Vector2> newLocation() {
            return new SteerableAdapter<Vector2>();
        }

        @Override
        public float getZeroLinearSpeedThreshold() {
            return 0;
        }

        @Override
        public void setZeroLinearSpeedThreshold(float value) { }

        @Override
        public float getMaxLinearSpeed() {
            return 2.0f;
        }

        @Override
        public void setMaxLinearSpeed(float maxLinearSpeed) { }

        @Override
        public float getMaxLinearAcceleration() {
            return 2.0f;
        }

        @Override
        public void setMaxLinearAcceleration(float maxLinearAcceleration) { }

        @Override
        public float getMaxAngularSpeed() {
            return 2.0f;
        }

        public void setMaxAngularSpeed(float maxAngularSpeed) { }

        @Override
        public float getMaxAngularAcceleration() {
            return 2.0f;
        }

        @Override
        public void setMaxAngularAcceleration(float maxAngularAcceleration) { }

        @Override
        public Vector2 getLinearVelocity() {
            return model.getLinearVelocity();
        }

        @Override
        public float getAngularVelocity() {
            return model.getAngularVelocity();
        }

        @Override
        public float getBoundingRadius() {
            return model.getWidth();
        }

        @Override
        public boolean isTagged() {
            return isTagged;
        }

        @Override
        public void setTagged(boolean tagged) {
            isTagged = tagged;
        }

    }

    private class EmptyWrapper implements Steerable<Vector2> {

        Vector2 zeroVector = new Vector2(0,0);

        @Override
        public Vector2 getPosition() {
            return zeroVector;
        }

        @Override
        public float getOrientation() {
            return 0;
        }

        @Override
        public void setOrientation(float orientation) { }

        @Override
        public float vectorToAngle(Vector2 vector) {
            return 0;
        }

        @Override
        public Vector2 angleToVector(Vector2 outVector, float angle) {
            return zeroVector;
        }

        @Override
        public Location<Vector2> newLocation() {
            return new SteerableAdapter<Vector2>();
        }

        @Override
        public float getZeroLinearSpeedThreshold() {
            return 0;
        }

        @Override
        public void setZeroLinearSpeedThreshold(float value) { }

        @Override
        public float getMaxLinearSpeed() {
            return 0;
        }

        @Override
        public void setMaxLinearSpeed(float maxLinearSpeed) { }

        @Override
        public float getMaxLinearAcceleration() {
            return 0;
        }

        @Override
        public void setMaxLinearAcceleration(float maxLinearAcceleration) { }

        @Override
        public float getMaxAngularSpeed() {
            return 0;
        }

        @Override
        public void setMaxAngularSpeed(float maxAngularSpeed) { }

        @Override
        public float getMaxAngularAcceleration() {
            return 0;
        }

        @Override
        public void setMaxAngularAcceleration(float maxAngularAcceleration) { }

        @Override
        public Vector2 getLinearVelocity() {
            return zeroVector;
        }

        @Override
        public float getAngularVelocity() {
            return 0;
        }

        @Override
        public float getBoundingRadius() {
            return 0;
        }

        @Override
        public boolean isTagged() {
            return false;
        }

        @Override
        public void setTagged(boolean tagged) { }

    }



}