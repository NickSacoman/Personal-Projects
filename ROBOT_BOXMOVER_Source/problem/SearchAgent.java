package problem;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * An interface for classes which allow searching for a path from an initial
 * state to a goal state.
 *
 * Part of the solution code for COMP3702/7702 Tutorial 2.
 *
 * Created by Nicholas Collins on 8/08/2017.
 * Updated by Harrison Lucas on 28/08/2018
 */
public interface SearchAgent {

    /**
     * Search for a path between a given initial state and goal state.
     *
     * @param initial the initial stage
     * @param goal    the goal state
     * @return a list of states and costs representing a path from the
     * initial state to the goal state.
     */
    List<StateCostPair> search(State<Point2D> initial, State<Point2D> goal, List<StateCostPair> curList, boolean firstSearch);

    /**
     * @return total number of nodes in the tree
     */
    int totalNodes();
}