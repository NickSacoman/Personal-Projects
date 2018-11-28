package problem;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A state for the moving box or the moving obstacle.
 * State just contains the current position of the object and it's width.
 * How it will work:
 * For every move by the robot (when it's not pushing an object) an empty move (no change)
 * will be updated in all the moving box/obstacle states.
 * 
 * Created by Harrison Lucas on 27/08/2018
 * Updated by Nick Sacoman on 31/08/2018
 */
public class ObjectState implements State<Point2D>{
    private Point2D position;
    private Rectangle2D rect;
    private List<Point2D> bufferZone;
    private double width;
    private double step;

    public ObjectState(Point2D point, double width){
        this.position = point;
        this.width = width;
        step = 0.001;
        rect = this.creatRect(point, width);
        bufferZone = createBufferPoints(width, rect);
    }

    /**
     * Create a rectangle 2D with the given points, useful for collision detection
     * 
     * @param point, points of state
     * @param dim, width (w)
     * @return
     */
    private Rectangle2D creatRect(Point2D point, double dim) {
		rect = new Rectangle2D.Double(point.getX(), point.getY(), dim, dim);
		return rect;
	}
    
    /**
     * Create a list of points to represent a buffer zone of the boxes being sampled
     * @param width, given by input
     * @param rect, rect of box
     * @return list of points for buffer
     */
    private List<Point2D> createBufferPoints(double width, Rectangle2D rect) {
    	List<Point2D> buffers = new ArrayList<Point2D>();
    	
		Point2D upLeft = new Point2D.Double(rect.getX() - (width*.5), rect.getY() - (width*.5)); 
		buffers.add(upLeft);
		
		Point2D upRight = new Point2D.Double(rect.getX() + width + (width*.5), rect.getY() - (width*.5)); 
		buffers.add(upRight);
		
		Point2D downLeft = new Point2D.Double(rect.getX() - (width*.5), rect.getY() + width + (width*.5)); 
		buffers.add(downLeft);
		
		Point2D downRight = new Point2D.Double(rect.getX() + width + (width*.5), rect.getY() + width + (width*.5)); 
		buffers.add(downRight);
		
		Point2D midPointUp = new Point2D.Double(rect.getX() + (width*.5), rect.getY() - (width*.5)); 
		buffers.add(midPointUp);
		
		Point2D midPointRight = new Point2D.Double(rect.getX() + width + (width*.5), rect.getY() + (width*.5)); 
		buffers.add(midPointRight);
		
		Point2D midPointLeft = new Point2D.Double(rect.getX() - (width*.5), rect.getY() + (width*.5)); 
		buffers.add(midPointLeft);
		
		Point2D midPointDown = new Point2D.Double(rect.getX() + (width*.5), rect.getY() + width + (width*.5)); 
		buffers.add(midPointDown);
		
		return buffers;
    }
    
	/**
     * Return the a string representation of the puzzle state
     * @return String representation of state. X value followed by a space then the Y value
     */
    public String outputString(){
        StringBuilder sb = new StringBuilder(10);
        sb.append(position.getX());
        sb.append(" ");
        sb.append(position.getY());
        return sb.toString();
    }


    /**
     * Returns the position of the object state.
     * @return Point2D of the states position
     */
    public Point2D getPoint(){return this.position;}
    
    /**
     * Returns the rectangle representation of the object state.
     * @return the rectangle representation
     */
    public Rectangle2D getRect(){return this.rect;}
    
    /** 
     * Returns the buffer points of the object state
     * @return the buffer zone of points
     */
    public List<Point2D> getBuffers(){return this.bufferZone;}

    /**
     * Returns the X coordinate of the object state
     * @return double of the x  coordinate
     */
    public double getX(){return this.position.getX();}

    /**
     * Returns the Y coordinate of the object state
     * @return double of the y coordinate
     */
    public double getY(){return this.position.getY();}

    /**
     * Returns the width of the object state
     * @return int of the object width
     */
    public double getWidth(){return this.width;}

    /**
     * Generates a list of possible states generated from the current state.
     * Typically four possible states.(1 for each direction)
     * @return List of possible states to move to
     */
    public List<StateCostPair> getSuccessors(){
        List<StateCostPair> Successors = new ArrayList<StateCostPair>();
        //Creates a new statecost pair which contains a new state of the position that is 1 width in any direction
        Successors.add(new StateCostPair(new ObjectState(new Point2D.Double(position.getX() + step, position.getY()),
                width), 0));
        Successors.add(new StateCostPair(new ObjectState(new Point2D.Double(position.getX() - step, position.getY()),
                width), 0));
        Successors.add(new StateCostPair(new ObjectState(new Point2D.Double(position.getX(), position.getY() + step),
                width), 0));
        Successors.add(new StateCostPair(new ObjectState(new Point2D.Double(position.getX(), position.getY() - step),
                width), 0));
        return Successors;
    }

    /**
     * Return a list of all states reachable from this state. Each successor
     * has a cost of 1 (indicating it is reached in 1 move) plus a heuristic to the goal.
     * @return list of successors
     */
    public List<StateCostPair> getSuccessors(State<Point2D> goal){
        List<StateCostPair> Successors = new ArrayList<StateCostPair>();
        //Creates a new statecost pair which contains a new state of the position that is 1 width in any direction
        ObjectState xUp = new ObjectState(new Point2D.Double(position.getX() + step, position.getY()), width);
        ObjectState xDown = new ObjectState(new Point2D.Double(position.getX() - step, position.getY()), width);
        ObjectState yUp = new ObjectState(new Point2D.Double(position.getX(), position.getY() + step), width);
        ObjectState yDown = new ObjectState(new Point2D.Double(position.getX(), position.getY() - step), width);
        Successors.add(new StateCostPair(xUp, xUp.heuristic(goal)));
        Successors.add(new StateCostPair(xDown, xDown.heuristic(goal)));
        Successors.add(new StateCostPair(yUp, yUp.heuristic(goal)));
        Successors.add(new StateCostPair(yDown, yDown.heuristic(goal)));
        return Successors;
    }

    /**
     * Checks to see if the given state is the same as the current state
     * @param s state to check equality with
     * @return boolean for whether the state are equal
     */
    public boolean equals(State<Point2D> s){
        if(Math.abs(position.getX() - s.getX()) < 0.0001){
            if(Math.abs(position.getY() - s.getY()) < 0.0001){
                return true;
            }
        }
        return false;
    }

    /**
     * Manhattan distance used for cost from current state to s. Aims to get the shortest distance between two points.
     * Heuristic is admissible.
     * @param goal The goal state to calculate for
     * @return The cost between the two states as a double
     */
    public Double heuristic(State<Point2D> goal){
        double dx = Math.abs(this.position.getX() - goal.getX());
        double dy = Math.abs(this.position.getY() - goal.getY());
        double result = dx + dy;
        return result;
    }
}