package problem;

import java.awt.geom.Point2D;

/**
 * A class storing a state and a cost to travel to it.
 *
 * Part of the solution code for COMP3702/7702 Tutorial 2.
 *
 * Created by Nicholas Collins on 8/08/2017.
 * Updated by Harrison Lucas on 27/08/2018
 * Updated by Nick Sacoman on 31/08/2018
 */
public class StateCostPair {

    private State<Point2D> state;
    private double cost;

    /**
     * Construct a state cost pair
     * @param s an agent problem state
     * @param c cost to travel to the state
     */
    public StateCostPair(State<Point2D> s, double c){
        this.state = s;
        this.cost = c;
    }

    /**
     * Required to allow StateCostPair to be used in a priority queue. Refer to
     * comment in SearchTreeNode.java
     * @param s StateCostPair to compare to
     * @return -1 if this node has a lower travel cost than pair s
     *          0 if this node has the same travel cost as pair s
     *          1 if this node has a greater travel cost than pair s
     */
    public int compareTo(StateCostPair s) {
        return Double.compare(this.cost, s.cost);
    }

    /**
     * Return the cost asossiated with the state cost pair
     * @return double of the cost
     */
    public double getCost(){ return this.cost;}

    /**
     * Return the state assiocated with the state cost pair
     * @return State of the pair
     */
    public State<Point2D> getState(){ return this.state;}

}