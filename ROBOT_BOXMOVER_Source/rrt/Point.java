package rrt;

/**
 * A point class that will store the coordinates of the point as floats and will
 * also specify what dimension the point is
 * 
 * @author Guarav Sood
 * updated by Nick Sacoman 8/09/18
 */
public class Point {
	
	// Stores the coordinates for the point
	public double[] coordinates;
	
	/**
	 * Construct a new point with its coordinates specified by size
	 * 
	 * @param size, dimension of coordinates
	 */
	public Point(int size)
	{
		coordinates = new double[size];
	}

	/**
	 * Set the coordinates of the point
	 * 
	 * @param value, given value (coordinates)
	 * @param index, which axis to add it on
	 */
	public void setCoordinate(double value, int index)
	{
		coordinates[index] = value;
	}

	/**
	 * Get the coordinate of the point at the axis specified by
	 * the index
	 * 
	 * @param index, the index of the axis
	 * @return the coordinate 
	 */
	public double getCoordinate(int index)
	{
		return coordinates[index];
	}

	/**
	 * Get the dimensions of point
	 * 
	 * @return dimensions of the point
	 */
	public int getDimensions() {
		return coordinates.length;
	}
}
