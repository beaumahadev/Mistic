package edu.cornell.gdiac.mistic;

import com.badlogic.gdx.math.Vector2;
import edu.cornell.gdiac.GameCanvas;
import edu.cornell.gdiac.InputController;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by beau on 3/18/17.
 */
public class AIController extends InputController {

    private enum FSMState {
        /** The monster just spawned */
        SPAWN,
        /** The monster is patrolling around without a target */
        WANDER,
        /** The ship has a target*/
        CHASE,
    }

    private enum direction {
        STOP, UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, UP_LEFT,
    }

    //Instance Attributes
    /** The monster being controlled by this AIController */
    private MonsterModel monster;
    /** The game board */
    private BoardModel board;
    /** Gorf, the monster's potential (and only) target */
    private GorfModel gorf;
    /** The monster's current state in the FSM */
    private FSMState state;
    /** The monster's next action */
    private direction move;
    /** The number of ticks since we started this controller */
    private long ticks;

    /** How much did we move horizontally? */
    private float horizontal;
    /** How much did we move vertically? */
    private float vertical;

    public Vector2 next_move = new Vector2(0, 0);

    private Vector2 scale;



    private LinkedList<Pair> finalpath;

    public AIController(MonsterModel monster, BoardModel board, GorfModel gorf, Vector2 scale) {
        this.monster = monster;
        this.board = board;
        this.gorf = gorf;
        this.scale = scale;

        state = FSMState.SPAWN;
        move  = direction.STOP;
        ticks = 0;

    }



    /**
     * Returns the amount of sideways movement.
     *
     * -1 = left, 1 = right, 0 = still
     *
     * @return the amount of sideways movement.
     */
    public float getHorizontal() {
        return horizontal;
    }

    /**
     * Returns the amount of vertical movement.
     *
     * -1 = down, 1 = up, 0 = still
     *
     * @return the amount of vertical movement.
     */
    public float getVertical() {
        return vertical;
    }

    /**
     * Returns the action selected by this InputController
     *
     * The returned int is a bit-vector of more than one possible input
     * option. This is why we do not use an enumeration of Control Codes;
     * Java does not (nicely) provide bitwise operation support for enums.
     *
     * This function tests the environment and uses the FSM to chose the next
     * action of the ship. This function SHOULD NOT need to be modified.  It
     * just contains code that drives the functions that you need to implement.
     *
     * @return the action selected by this InputController
     */
    public direction getAction() {
        // Increment the number of ticks.
        ticks++;

        // Do not need to rework ourselves every frame. Just every 10 ticks.
        if ((ticks) % 10 == 0) {
            // Process the FSM
            changeStateIfApplicable();

            // Pathfinding
            markGoalTiles();
            move = getMoveAlongPathToGoalTile();
        }

        direction action = move;

        // If we're attacking someone and we can shoot him now, then do so.

        return action;
    }

    // FSM Code for Targeting (MODIFY ALL THE FOLLOWING METHODS)

    /**
     * Change the state of the ship.
     *
     * A Finite State Machine (FSM) is just a collection of rules that,
     * given a current state, and given certain observations about the
     * environment, chooses a new state. For example, if we are currently
     * in the ATTACK state, we may want to switch to the CHASE state if the
     * target gets out of range.
     */
    private void changeStateIfApplicable() {
        // Add initialization code as necessary
        //#region PUT YOUR CODE HERE
        Vector2 gorfPos = new Vector2(gorf.getPosition().x, gorf.getPosition().y);
        int gorfTileX = board.screenToBoardX(gorfPos.x);
        int gorfTileY = board.screenToBoardY(gorfPos.y);
        //#endregion

        // Next state depends on current state.
        switch (state) {
            case SPAWN: // Do not pre-empt with FSMState in a case
                // Insert checks and spawning-to-??? transition code here
                //#region PUT YOUR CODE HERE
                state = FSMState.WANDER;
                //#endregion
                break;

            case WANDER: // Do not pre-empt with FSMState in a case
                // Insert checks and moving-to-??? transition code here
                //#region PUT YOUR CODE HERE
                //if (board.isFog(gorfTileX, gorfTileY)) {
                    state = FSMState.CHASE;
                //}
                //#endregion
                break;

            case CHASE: // Do not pre-empt with FSMState in a case
                // insert checks and chasing-to-??? transition code here
                //#region PUT YOUR CODE HERE
                //if (board.isFog(gorfTileX, gorfTileY)) {
                    //state = FSMState.WANDER;
                //}

                //#endregion
                break;

            default:
                // Unknown or unhandled state, should never get here
                assert (false);
                state = FSMState.WANDER; // If debugging is off
                break;
        }
    }

    /**
     * Mark all desirable tiles to move to.
     *
     * This method implements pathfinding through the use of goal tiles.
     * It searches for all desirable tiles to move to (there may be more than
     * one), and marks each one as a goal. Then, the pathfinding method
     * getMoveAlongPathToGoalTile() moves the ship towards the closest one.
     *
     * POSTCONDITION: There is guaranteed to be at least one goal tile
     * when completed.
     */
    private void markGoalTiles() {
        // Clear out previous pathfinding data.
        board.clearMarks();
        boolean setGoal = false; // Until we find a goal

        // Add initialization code as necessary
        int theX = board.screenToBoardX(monster.getPosition().x);
        int theY = board.screenToBoardY(monster.getPosition().y);
        //#region PUT YOUR CODE HERE
        Vector2 gorfPos = new Vector2((gorf.getPosition().x *scale.x), (gorf.getPosition().y * scale.y));
        int gorfTileX = board.screenToBoardX(gorfPos.x);
        int gorfTileY = board.screenToBoardY(gorfPos.y);
        //#endregion

        switch (state) {
            case SPAWN: // Do not pre-empt with FSMState in a case
                // insert code here to mark tiles (if any) that spawning ships
                // want to go to, and set setGoal to true if we marked any.
                // Ships in the spawning state will immediately move to another
                // state, so there is no need for goal tiles here.

                //#region PUT YOUR CODE HERE
                //#endregion
                break;

            case WANDER: // Do not pre-empt with FSMState in a case
                // Insert code to mark tiles that will cause us to move around;
                // set setGoal to true if we marked any tiles.
                // NOTE: this case must work even if the ship has no target
                // (and changeStateIfApplicable should make sure we are never
                // in a state that won't work at the time)

                //#region PUT YOUR CODE HERE
                //#endregion
                break;

            case CHASE: // Do not pre-empt with FSMState in a case
                // Insert code to mark tiles that will cause us to chase the target;
                // set setGoal to true if we marked any tiles.

                //#region PUT YOUR CODE HERE
                board.setGoal(gorfTileX, gorfTileY);
                setGoal = true;
                //#endregion
                break;
        }

        // If we have no goals, mark current position as a goal
        // so we do not spend time looking for nothing:
        if (!setGoal) {
            int sx = board.screenToBoardX(monster.getPosition().x);
            int sy = board.screenToBoardY(monster.getPosition().y);
            board.setGoal(sx, sy);
        }
    }

    private class Pair { //based on http://stackoverflow.com/questions/10234487/storing-number-pairs-in-java
        private int x;
        private int y;

        Pair(int x, int y) {
            this.x=x;this.y=y;
        }
    }

    /**
     * Returns a movement direction that moves towards a goal tile.
     *
     * This is one of the longest parts of the assignment. Implement
     * breadth-first search (from 2110) to find the best goal tile
     * to move to. However, just return the movement direction for
     * the next step, not the entire path.
     *
     * The value returned should be a control code.  See PlayerController
     * for more information on how to use control codes.
     *
     * @return a movement direction that moves towards a goal tile.
     */
    //Algorithm below was based on https://gist.github.com/gennad/791932 and
    // http://stackoverflow.com/questions/8922060/how-to-trace-the-path-in-a-breadth-first-search
    private direction getMoveAlongPathToGoalTile() {
        //#region PUT YOUR CODE HERE
        Vector2 monsterPos = monster.getPosition();
        float new_monster_posx = (monsterPos.x * scale.x);
        float new_monster_posy= (monsterPos.y * scale.y);
        Queue q = new LinkedList();
        int start_x = board.screenToBoardX(new_monster_posx);
        int start_y = board.screenToBoardY(new_monster_posy);
        board.setVisited(start_x, start_y);
        LinkedList<Pair> vlist;
        vlist = new LinkedList<Pair>();
        vlist.add(new Pair(start_x, start_y));
        q.add(vlist);
        while (!q.isEmpty()) {
            LinkedList<Pair> v2 = (LinkedList) q.remove();
            Pair last = v2.getLast();
            Queue<Pair> neighbors = getNeighbors(last);
            if (board.isGoal(last.x, last.y)) {
                this.finalpath = v2;
                break;
            }
            while (!neighbors.isEmpty()) {
                Pair check = neighbors.remove();
                board.setVisited(check.x,check.y);
                LinkedList<Pair> path = (LinkedList<Pair>)v2.clone();
                path.addLast(check);
                q.add(path);
            }
        }
        if (this.finalpath == null) {
            return direction.STOP;
        }
        if (this.finalpath.size() == 1) {
            return direction.STOP;
        }
        Pair wanted = finalpath.get(1);
        next_move = new Vector2(finalpath.get(1).x, finalpath.get(1).y);
        direction the_move = findMove(new Pair(start_x, start_y), wanted);
        return the_move;
        //#endregion
    }

    // Add any auxiliary methods or data structures here
    //#region PUT YOUR CODE HERE
    /**
     * Returns the safe and unvisited neighbors of a given tile position*/
    private Queue<Pair> getNeighbors(Pair tile) {
        Queue<Pair> neighbors = new LinkedList<Pair>();
        if (board.isSafeAt((tile.x + 1),(tile.y)) && !board.isVisited((tile.x + 1),(tile.y))) {
            neighbors.add(new Pair(tile.x +1, tile.y));
        }
        if (board.isSafeAt((tile.x - 1),(tile.y)) && !board.isVisited((tile.x - 1),(tile.y))) {
            neighbors.add(new Pair(tile.x - 1, tile.y));
        }
        if (board.isSafeAt((tile.x),(tile.y + 1)) && !board.isVisited((tile.x),(tile.y + 1))) {
            neighbors.add(new Pair(tile.x, tile.y + 1));
        }
        if (board.isSafeAt((tile.x),(tile.y - 1)) && !board.isVisited((tile.x),(tile.y - 1))) {
            neighbors.add(new Pair(tile.x , tile.y - 1));
        }
        if (board.isSafeAt((tile.x+ 1),(tile.y +1)) && !board.isVisited((tile.x + 1),(tile.y + 1))) {
            neighbors.add(new Pair(tile.x + 1, tile.y + 1));
        }
        if (board.isSafeAt((tile.x + 1),(tile.y - 1)) && !board.isVisited((tile.x + 1),(tile.y - 1))) {
            neighbors.add(new Pair(tile.x + 1, tile.y - 1));
        }

        if (board.isSafeAt((tile.x - 1),(tile.y + 1)) && !board.isVisited((tile.x - 1),(tile.y + 1))) {
            neighbors.add(new Pair(tile.x - 1, tile.y + 1));
        }
        if (board.isSafeAt((tile.x - 1),(tile.y - 1)) && !board.isVisited((tile.x - 1),(tile.y - 1))) {
            neighbors.add(new Pair(tile.x - 1, tile.y - 1));
        }

        return neighbors;
    }

    private direction findMove(Pair start, Pair end) {
        if ((end.x < start.x) && (start.y == end.y)) {
            return direction.LEFT;
        }
        if ((end.x > start.x) && (start.y == end.y)){
            return direction.RIGHT;
        }
        if (end.y < start.y && end.x == start.x) {
            return direction.UP;
        }
        if (end.y > start.y && start.x == end.x) {
            return direction.DOWN;
        }
        if (end.x < start.x && end.y < start.y) {
            return direction.UP_LEFT;
        }
        if (end.x < start.x && end.y > start.y) {
            return direction.DOWN_LEFT;
        }
        if (end.x > start.x && end.y < start.y) {
            return direction.UP_RIGHT;
        }
        if (end.x > start.x && end.y > start.y) {
            return direction.DOWN_RIGHT;
        }
        return direction.STOP;
    }

    public void setInput() {
        horizontal = 0;
        vertical = 0;
        changeStateIfApplicable();
        markGoalTiles();
        direction the_move = getMoveAlongPathToGoalTile();

        switch (the_move) {

            case LEFT:
               horizontal -= 1.9f;
               break;
            case RIGHT:
                horizontal += 1.9f;
                break;
            case UP:
                vertical -= 1.9f;
                break;
            case DOWN:
                vertical += 1.9f;
                break;
            case UP_LEFT:
                vertical -= 1.9f;
                horizontal -= 1.9f;
                break;
            case DOWN_LEFT:
                vertical += 1.9f;
                horizontal -= 1.9f;
                break;
            case UP_RIGHT:
                vertical -= 1.9f;
                horizontal += 1.9f;
                break;
            case DOWN_RIGHT:
                vertical += 1.9f;
                horizontal += 1.9f;
                break;

        }
    }


}
