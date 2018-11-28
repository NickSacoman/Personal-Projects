package problem;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RectangleEnvironment {

	// Robot for this specific environment
	public RobotConfig robot;
	public double robotWidthAndW;
	// Robot Points for the environment
	public Point2D robotCenter;
	public Point2D robotEnd1;
	public Point2D robotEnd2;
	public double robotAngle;
	public Line2D robotLine;

	// Define the boxes and goals in the environment
	public List<Box> MBList;
	public List<Box> MOList;
	public List<StaticObstacle> SOList;
	public List<Point2D> MBGoalList;

	// Define the lists of rectangles that are in the environment
	public List<Rectangle2D> MBRectangleList;
	public List<Rectangle2D> MORectangleList;
	public List<Rectangle2D> SORectangleList;
	public List<Point2D> MBGoalPoints;
	public int[] orderList;

	// Outer zone levels for goals
	HashMap<Point2D, Tier> goalTiers;

	/**
	 * Construct the environment with the given problem specs.
	 */
	public RectangleEnvironment(ProblemSpec specs) {
		// Initialize robot
		robot = specs.getInitialRobotConfig();
		robotWidthAndW = specs.getRobotWidth();
		// Add the robot's points
		robotCenter = robot.getPos();
		robotEnd1 = new Point2D.Double(robot.getX1(robotWidthAndW), robot.getY1(robotWidthAndW));
		robotEnd2 = new Point2D.Double(robot.getX2(robotWidthAndW), robot.getY2(robotWidthAndW));
		robotAngle = robot.getOrientation();
		robotLine = new Line2D.Double(robotEnd1, robotEnd2);

		// Initialize boxes and obstacles
		MBList = specs.getMovingBoxes();
		MOList = specs.getMovingObstacles();
		SOList = specs.getStaticObstacles();
		MBGoalList = specs.getMovingBoxEndPositions();

		// Initialize Rectangle lists of boxes/obstacles
		MBRectangleList = new ArrayList<Rectangle2D>();
		MORectangleList = new ArrayList<Rectangle2D>();
		SORectangleList = new ArrayList<Rectangle2D>();
		MBGoalPoints = new ArrayList<Point2D>();

		// Add the moving boxes
		for (int i = 0; i < MBList.size(); i++) {
			MBRectangleList.add(MBList.get(i).getRect());
		}
		// Add the moving obstacles
		for (int i = 0; i < MOList.size(); i++) {
			MORectangleList.add(MOList.get(i).getRect());
		}
		// Add the static obstacles
		for (int i = 0; i < SOList.size(); i++) {
			SORectangleList.add(SOList.get(i).getRect());
		}

		// Add the goals and their tiers
		goalTiers = new HashMap<Point2D, Tier>();
		for (int i = 0; i < MBGoalList.size(); i++) {
			Point2D curPoint = new Point2D.Double(MBGoalList.get(i).getX(), MBGoalList.get(i).getY());
			Tier goalTier = setTier(curPoint);
			goalTiers.put(curPoint, goalTier);
			MBGoalPoints.add(curPoint);
		}

		// Reorder accordingly with newly set tiers
		reorderPointsAndBoxes();
		orderList = new int[MBList.size()];
		for(int i=0; i<MBList.size();i++){
			for(int j=0; j<MBRectangleList.size();j++){
				if(Math.abs((MBList.get(i).pos.getX() - MBRectangleList.get(j).getX()) +
						(MBList.get(i).pos.getY() - MBRectangleList.get(j).getY()))< 0.00001){
					orderList[j] = i;
				}

			}
		}
	}

	/**
	 * Add a goal point to its matching tier
	 *
	 * @param curPoint
	 */
	private Tier setTier(Point2D curPoint) {
		Tier curTier = new Tier();

		// Find the X value tiers
		if (curPoint.getX() <= 0.1 || curPoint.getX() > 0.9) {
			curTier.setXTier(1);
		} else if ((curPoint.getX() <= 0.2 && curPoint.getX() > 0.1) || (curPoint.getX() > 0.8 && curPoint.getX() <= 0.9)) {
			curTier.setXTier(2);
		} else if ((curPoint.getX() <= 0.3 && curPoint.getX() > 0.2) || (curPoint.getX() > 0.7 && curPoint.getX() <= 0.8)) {
			curTier.setXTier(3);
		} else if ((curPoint.getX() <= 0.4 && curPoint.getX() > 0.3) || (curPoint.getX() > 0.6 && curPoint.getX() <= 0.7)) {
			curTier.setXTier(4);
		} else {
			curTier.setXTier(5);
		}

		// Find the Y value tiers
		if (curPoint.getY() <= 0.1 || curPoint.getY() > 0.9) {
			curTier.setYTier(1);
		} else if ((curPoint.getY() <= 0.2 && curPoint.getY() > 0.1) || (curPoint.getY() > 0.8 && curPoint.getY() <= 0.9)) {
			curTier.setYTier(2);
		} else if ((curPoint.getY() <= 0.3 && curPoint.getY() > 0.2) || (curPoint.getY() > 0.7 && curPoint.getY() <= 0.8)) {
			curTier.setYTier(3);
		} else if ((curPoint.getY() <= 0.4 && curPoint.getY() > 0.3) || (curPoint.getY() > 0.6 && curPoint.getY() <= 0.7)) {
			curTier.setYTier(4);
		} else {
			curTier.setYTier(5);
		}

		curTier.addTotal();
		return curTier;
	}

	/**
	 * Removes a moving box from the environment and returns it
	 * Box is removed so that calculations can be done without interference involving collisions
	 * Box should be placed back in list once done moving it
	 *
	 * @param place place in the list of the moving box you want to move
	 * @return Rectangle of the moving box
	 */
	public Rectangle2D removeMovingBox(int place) {
		Rectangle2D result = MBRectangleList.get(place);
		MBRectangleList.remove(place);
		return result;
	}

	/**
	 * Places a box in the moving box list
	 *
	 * @param rect  rectangle to be placed in the moving box list
	 * @param place index for where the rectangle should be placed
	 */
	public void placeMovingBox(Rectangle2D rect, int place) {
		MBRectangleList.add(place, rect);
	}

	public void moveMB(Point2D p, int i){
		MBList.remove(orderList[i]);
		MBList.add(orderList[i], new MovingBox(p, robotWidthAndW));
	}
	/**
	 * Removes a moving obstacle from the environment and returns it
	 * Obstacle is removed so that calculations can be done without interference involving collisions
	 * Obstacle should be placed back in list once done moving it
	 *
	 * @param place place in the list of the moving obstacle you want to move
	 * @return Rectangle of the moving obstacle
	 */
	public Rectangle2D removeMovingObstacle(int place) {
		Rectangle2D result = MORectangleList.get(place);
		MORectangleList.remove(place);
		return result;
	}

	/**
	 * Places an obstacle in the moving obstacle list
	 *
	 * @param rect  rectangle to be placed in the moving box list
	 * @param place index for where the rectangle should be placed
	 */
	public void placeMovingObstacle(Rectangle2D rect, int place) {
		MORectangleList.add(place, rect);
	}

	/**
	 * Update the robot position in the environment
	 * 
	 * @param r, the new robot configs
	 */
	public void updateRobotPos(RobotConfig r) {
		robot = r;
		robotCenter = robot.getPos();
		robotEnd1 = new Point2D.Double(robot.getX1(robotWidthAndW), robot.getY1(robotWidthAndW));
		robotEnd2 = new Point2D.Double(robot.getX2(robotWidthAndW), robot.getY2(robotWidthAndW));
		robotAngle = robot.getOrientation();
		robotLine = new Line2D.Double(robotEnd1, robotEnd2);
	}

	/**
	 * Reorder the boxes and their goals to fit the ideal searches
	 * given the tiers
	 */
	public void reorderPointsAndBoxes() {
		for (int i = 0; i < goalTiers.size() - 1; i++) {
			for (int j = 1; j < goalTiers.size(); j++) {
				if (goalTiers.get(MBGoalPoints.get(j)).totalTier < goalTiers.get(MBGoalPoints.get(i)).totalTier) {
					//					for (int z = 0; z < MBGoalPoints.size(); z++) {
					//						System.out.println("Point " + z + ": " + MBGoalPoints.get(z).getX() + " " + MBGoalPoints.get(z).getY());
					//					}
					Point2D tempPt = MBGoalPoints.get(i);
					MBGoalPoints.set(i, MBGoalPoints.get(j));
					MBGoalPoints.set(j, tempPt);
					//					for (int z = 0; z < MBGoalPoints.size(); z++) {
					//						System.out.println("Point " + z + ": " + MBGoalPoints.get(z).getX() + " " + MBGoalPoints.get(z).getY());
					//					}

					//					for (int z = 0; z < MBRectangleList.size(); z++) {
					//						System.out.println("Rect " + z + ": " + MBRectangleList.get(z));
					//					}
					Rectangle2D tempBox = MBRectangleList.get(i);
					MBRectangleList.set(i, MBRectangleList.get(j));
					MBRectangleList.set(j, tempBox);
				}
			}
		}
	}



	/**
	 * Tier class to detect where each goal point lies. Lower numbered
	 * tiers will be outermost tiers.
	 */
	private class Tier {

		// Used for detection
		int[] tierNum = new int[2];

		// Adds the total number for x and y tiers
		int totalTier;

		void setXTier(int xTier) {
			tierNum[0] = xTier;
		}

		void setYTier(int yTier) {
			tierNum[1] = yTier;
		}

		void addTotal() {
			totalTier = (tierNum[0] * 10) + tierNum[1];
			if (totalTier == 11) {
				totalTier = 1;
			} else if (totalTier == 12 || totalTier == 21) {
				totalTier = 2;
			} else if (totalTier == 13 || totalTier == 31) {
				totalTier = 3;
			} else if (totalTier == 14 || totalTier == 41) {
				totalTier = 4;
			} else if (totalTier == 22) {
				totalTier = 5;
			} else if (totalTier == 23 || totalTier == 32) {
				totalTier = 6;
			} else if (totalTier == 24 || totalTier == 42) {
				totalTier = 7;
			} else if (totalTier == 33) {
				totalTier = 8;
			} else if (totalTier == 34 || totalTier == 43) {
				totalTier = 9;
			} else if (totalTier == 44) {
				totalTier = 10;
			}
		}
	}

	/**
	 * Find a place to put the MO that will not cause an issue when searching
	 * boxes to goals
	 */
	public Point2D findMOGoal() {
		Point2D temp = new Point2D.Double();
		return temp;
	}
}

