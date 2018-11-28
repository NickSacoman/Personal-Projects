package rrt;

import problem.RectangleEnvironment;
import problem.RobotConfig;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * RRT Sample used in a singular direction
 * 
 * @author Guarav Sood
 * updated by Nick Sacoman 8/09/18
 * Updated by Harrison Lucas 10/9/19
 */
public class RRTSample {

	// Path ordering stack and queue
	Stack <ConfigTree> stackForReversingConfigTree = new Stack<ConfigTree>();
	Queue <ConfigTree> queueToMaintainOrder = new LinkedList<ConfigTree>();

	// Miscellaneous sample tools
	boolean flag;
	int dimensions;
	double weight;
	int iterations;
	double maxDistance;

	// Environment of obstacles/boxes
	RectangleEnvironment sampleEnvi;

	public RRTSample(RectangleEnvironment envi) {
		// Initialize sample tools
		flag = false;
		dimensions = 3;
		weight = 0.05;
		iterations = 400000;
		maxDistance = 0.005;
		// Initialize environment
		sampleEnvi = envi;
	}

	/**
	 * Get a random point within the constraints ([0,100],[0,100])
	 * 
	 * @param dimensions, dimension of the point
	 * @return the point
	 */
	public Point getRandomConfig(int dimensions)
	{
		Point pt = new Point(dimensions);
		for(int i = 0; i < dimensions; i++) {
				Random randomGenerator = new Random();
				pt.setCoordinate(randomGenerator.nextInt(100), i);
		}

			// Scale down x and y coordinates
			double[] scaledCoords = pt.coordinates;		
			double x = scaledCoords[0];
			double y = scaledCoords[1];		
			x = x*.01;
			y = y*.01;

			// Assign alpha one of the six useful alpha values in radians
			double alpha = scaledCoords[2];
			Random randomGenerator = new Random();
			int randomnum = randomGenerator.nextInt(2);
			double num =0;
			if(randomnum ==0){num =0;}
			if(randomnum ==1){num = 1.570796;}
			// Set the newly scaled coordinates and return the point
			pt.setCoordinate(x, 0);
			pt.setCoordinate(y, 1);
			pt.setCoordinate(num, 2);

			return pt;

	}

	/**
	 * Given a Point, find the point in our ConfigTree that is closest to it
	 * @param explored, the ConfigTree to search
	 * @param a, the point being search
	 * @return the vertex closest to the given point
	 */
	public ConfigTree findNearestVertex(ConfigTree explored, Point a)
	{
		double shortestDistance = 999;

		ConfigTree closestVertex;

		//check every point in the ConfigTree and record which point in the ConfigTree
		//is closest to the random point
		closestVertex = explored.closestVertex(shortestDistance, a);

		return closestVertex;
	}

	/**
	 * Main runner
	 * No collision check at the moment while plotting the point for ConfigTree or even for edges
	 * @param
	 */
	public List<RobotConfig> rrtSample(RobotConfig initial, Point2D goal, double alphaGoal) {   
		long start = System.currentTimeMillis();
		// Initialize the initial and goal config trees
		ConfigTree ConfigTreeA = new ConfigTree(dimensions, initial.getPos().getX(), initial.getPos().getY(),
				initial.angle, null, "ConfigTree_A");   
		ConfigTree ConfigTreeB = new ConfigTree(dimensions, goal.getX(), goal.getY(), alphaGoal, null, "ConfigTree_B");   
		ConfigTree primary = ConfigTreeA;
		ConfigTree secondary = ConfigTreeB;

		for (int i = 0; i < iterations; i++) {
			// Random point (robot config)
			Point pt = getRandomConfig(dimensions);
			// Find nearest robot config to the new random point
			ConfigTree nearestConfigTree = findNearestVertex(primary, pt);
			Point closePoint = scalePoint(pt, nearestConfigTree.getconfigPoint());
            if(!flag){closePoint.setCoordinate(initial.getOrientation(), 2);}
            if(flag){closePoint.setCoordinate(alphaGoal,2);}
			//System.out.println(newChildConfig.getTreeName()+"::"+Arrays.toString(newChildConfig.configPoint.coordinates));
			if(!checkCollision(closePoint, nearestConfigTree.getconfigPoint())) {
				ConfigTree newChildConfig =  nearestConfigTree.addChild(closePoint,nearestConfigTree,nearestConfigTree.getTreeName());
				//find nearest vertex to secondary ConfigTree
				ConfigTree nearestVertexOfSecondary = findNearestVertex(secondary, closePoint);
				if (primary.getDistance(closePoint, nearestVertexOfSecondary.configPoint) <= weight) // temporary condition; change this to (if distance of two ConfigTrees is greater than delta)
				{
					//check if we can make an edge over here without collision between secondary and primary
					//Not sure if this works, bigger values on width seem to break it
					Point p1 = new Point(3);
					Point p2 = new Point(3);
					Point p3 = new Point(3);
					Point p4 = new Point(3);
					p1.setCoordinate(closePoint.getCoordinate(0),0);
					p1.setCoordinate(closePoint.getCoordinate(1),1);
					p2.setCoordinate(closePoint.getCoordinate(0),0);
					p2.setCoordinate(closePoint.getCoordinate(1),1);
					p3.setCoordinate(closePoint.getCoordinate(0),0);
					p3.setCoordinate(closePoint.getCoordinate(1),1);
					p4.setCoordinate(closePoint.getCoordinate(0),0);
					p4.setCoordinate(closePoint.getCoordinate(1),1);
					p1.setCoordinate(0,2);
					p2.setCoordinate(1.5707d,2);
					p3.setCoordinate(0.52359877559d,2);
					p4.setCoordinate(1.04d,2);
					if (!checkCollision(p1, nearestVertexOfSecondary.getconfigPoint())&&
							!checkCollision(p2, nearestVertexOfSecondary.getconfigPoint()) &&
							!checkCollision(p3, nearestVertexOfSecondary.getconfigPoint())&&
							!checkCollision(p4, nearestVertexOfSecondary.getconfigPoint())){
						ConfigTree tmp = newChildConfig;

						while (tmp != null) {
							stackForReversingConfigTree.push(tmp);
							tmp = tmp.getparentConfig();
						}

						while (!stackForReversingConfigTree.isEmpty()) {
							queueToMaintainOrder.add(stackForReversingConfigTree.pop());
						}
						stackForReversingConfigTree.clear();
						tmp = nearestVertexOfSecondary;

						while (tmp != null) {
							queueToMaintainOrder.add(tmp);
							tmp = tmp.getparentConfig();
						}

						List<RobotConfig> outputPath = new ArrayList<>();
						while (!queueToMaintainOrder.isEmpty()) {
							tmp = queueToMaintainOrder.poll();
							//System.out.println(tmp.getTreeName() + ":" + Arrays.toString(tmp.configPoint.coordinates));
							outputPath.add(new RobotConfig(new Point2D.Double(tmp.getconfigPoint().coordinates[0], tmp.getconfigPoint().coordinates[1]),
									tmp.getconfigPoint().coordinates[2]));
						}
						System.out.println("RRT Completed in " + (System.currentTimeMillis() - start));

						if((Math.abs(outputPath.get(0).getPos().getX() - initial.getPos().getX())+
								Math.abs(outputPath.get(0).getPos().getY() - initial.getPos().getY())) > 0.00001){

							Collections.reverse(outputPath);
						}
						return outputPath;
					}
				}
			}
			if(System.currentTimeMillis() - start > 22500){
				System.out.println("No path found for RRT. Restarted problem.");
            	return null;
			}
			// Swapping between ConfigTrees 
			if (!flag)
			{ 
				primary = ConfigTreeB;
				secondary =ConfigTreeA;
				flag = true;}
			else
			{
				primary = ConfigTreeA;
				secondary =ConfigTreeB;
				flag=false;
			}
		}

		System.out.println("Time in ms: " + (System.currentTimeMillis() - start));
		return null;
	}

	/**
	 * Checks for collisions that might occur when a robot moves from one point to another
	 * @param randomP The randomly sampled point after it has been acled down
	 * @param vertexP The point of the closest vertex from that point
	 * @return true if collision between two points otherwise false
	 */
	private boolean checkCollision(Point randomP, Point vertexP){

		// Check for collisions in the new point
		boolean collision = false;

		// Create a line of the new generated config and check for collisions
		Point2D midPoint = new Point2D.Double(randomP.getCoordinate(0),randomP.getCoordinate(1));
		Point2D midPoint2 = new Point2D.Double(vertexP.getCoordinate(0),randomP.getCoordinate(1));
		RobotConfig randomConfig = new RobotConfig(midPoint, randomP.getCoordinate(2));
		RobotConfig previousConfig = new RobotConfig(midPoint2, vertexP.getCoordinate(2));
		Point2D end1 = new Point2D.Double(randomConfig.getX1(sampleEnvi.robotWidthAndW), randomConfig.getY1(sampleEnvi.robotWidthAndW));
		Point2D end2 = new Point2D.Double(randomConfig.getX2(sampleEnvi.robotWidthAndW), randomConfig.getY2(sampleEnvi.robotWidthAndW));
		Point2D end3 = new Point2D.Double(previousConfig.getX1(sampleEnvi.robotWidthAndW), previousConfig.getY1(sampleEnvi.robotWidthAndW));
		Point2D end4 = new Point2D.Double(previousConfig.getX2(sampleEnvi.robotWidthAndW), previousConfig.getY2(sampleEnvi.robotWidthAndW));

		List<Line2D> linesToCheck = new ArrayList<>();
		//Robot
		linesToCheck.add(new Line2D.Double(end1, end2));
		//Line from left end of one point to left end of other point
		linesToCheck.add(new Line2D.Double(end1, end3));
		//Line from right end of one point to right end of other point
		linesToCheck.add(new Line2D.Double(end2, end4));
		//Line from mid of one point to middle of next point
		linesToCheck.add(new Line2D.Double(midPoint, midPoint2));
		//Check collisions
		for (int i = 0; i < sampleEnvi.MBRectangleList.size(); i++) {
			for(int j=0; j < linesToCheck.size(); j++){
				if (linesToCheck.get(j).intersects(sampleEnvi.MBRectangleList.get(i))){
						collision = true;
				}
			}
		}
		for (int i = 0; i < sampleEnvi.MORectangleList.size(); i++) {
			for(int j=0; j < linesToCheck.size(); j++){
				if (linesToCheck.get(j).intersects(sampleEnvi.MORectangleList.get(i))){
					collision = true;
				}
			}
		}
		for (int i = 0; i < sampleEnvi.SORectangleList.size(); i++) {
			for(int j=0; j < linesToCheck.size(); j++){
				if (linesToCheck.get(j).intersects(sampleEnvi.SORectangleList.get(i))){
					collision = true;
				}
			}
		}
		//Check for out of bounds
		if (midPoint.getX() < 0 || midPoint.getY() < 0 || midPoint.getX() > 1 || midPoint.getY() > 1 || end1.getX() < 0
				|| end1.getY() < 0 || end1.getX() > 1 || end1.getY() > 1 || end2.getX() < 0 || end2.getY() < 0 || end2.getX() > 1
				|| end2.getY() > 1) {
			collision = true;
		}
		return collision;
	}

	/**
	 * Generates a new point at a certain distance between two points
	 * Point 1 -----------New point-----------Closest Point
	 * @param randomPoint The randomly sampled point
	 * @param closestPoint The point on a tree closet to the randomly sampled point
	 * @return The new point
	 */
	Point scalePoint(Point randomPoint, Point closestPoint){
		Double distance = Math.sqrt(Math.pow(closestPoint.getCoordinate(0) - randomPoint.getCoordinate(0),2)
				+ Math.pow(closestPoint.getCoordinate(1) - randomPoint.getCoordinate(1),2));
		Double distanceClose = maxDistance/distance;
		//Double distanceClose = maxDistance;
		Point closePoint = new Point(3);
		closePoint.setCoordinate((1-distanceClose)*closestPoint.getCoordinate(0) + distanceClose * randomPoint.getCoordinate(0), 0);
		closePoint.setCoordinate((1-distanceClose)*closestPoint.getCoordinate(1) + distanceClose * randomPoint.getCoordinate(1),1);
		closePoint.setCoordinate(randomPoint.getCoordinate(2),2);
		return closePoint;
	}

}