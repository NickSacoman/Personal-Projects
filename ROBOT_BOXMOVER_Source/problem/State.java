package problem;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * An interface for classes representing states in an agent problem.
 *
 * Created by Nicholas Collins on 8/08/2017.
 * Updated by Harrison Lucas on 27/08/2018
 * Updated by Nick Sacoman on 31/08/2018
 */
@SuppressWarnings("hiding")
public interface State<Point2D> {

    /**
     * Return the set of states which can be reached from this state by
     * performing a valid action.
     * @return list of successor states
     */
    List<StateCostPair> getSuccessors();

    /**
     * Return the set of states which can be reached from the given state
     * @param goal The state of which its successors will be found
     * @return list of successor states
     */
    List<StateCostPair> getSuccessors(State<Point2D> goal);

    /**
     * Return true if this state is the same as state s
     * @param s state to check equality with
     * @return true if this state is equal to state s
     */
    boolean equals(State<Point2D> s);

    /**
     * Returns the Point2D of the state. This is the centre of the object.
     * @return Point2d of the state
     */
    Point2D getPoint();
    
    /**
     * Returns the Rectangle of the state, used for collisions
     * @return rectangle of state
     */
    Rectangle2D getRect();
    
    /**
     * Returns the buffer points of the state, used for collision
     * @return buffer points
     */
    List<Point2D> getBuffers();

    /**
     * Returns the X coordinate of the states points.
     * @return double of the x coordinate
     */
    double getX();

    /**
     * Returns the Y coordinate of the states points.
     * @return double of the y coordinate
     */
    double getY();

    /**
     * Represent this state as a string
     * @return string representation of this state
     */
    String outputString();

    /**
     * Return the heuristic for the function of search that may be applied
     * @param s
     * @return
     */
    Double heuristic(State<Point2D> s);
}
