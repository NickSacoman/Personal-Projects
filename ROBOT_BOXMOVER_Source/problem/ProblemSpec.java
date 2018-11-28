package problem;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This class represents the specifications of a given problem and solution;
 * that is, it provides a structured representation of the contents of a problem
 * text file and associated solution text file, as described in the assignment
 * specifications.
 * 
 * This class doesn't do any validity checking - see the code in tester.Tester
 * for this.
 * 
 * @author Sergiy Dudnikov
 * Updated on 3/9/19 by Harrison Lucas
 */
public class ProblemSpec {
	/** True iff a problem is currently loaded */
	private boolean problemLoaded = false;
	/** True iff a solution is currently loaded */
	private boolean solutionLoaded = false;

    /** The static obstacles */
	private List<StaticObstacle> staticObstacles;

    /** The static obstacles */
	private double robotWidth;

	/** The initial configuration */
	private RobotConfig initialRobotConfig;

	/** An array of moving boxes and obstacles */	
	private List<Box> movingBoxes;
    private List<Box> movingObstacles;
    private List<Point2D> movingBoxEndPositions;

	/** The number of each type of obstacle **/	
    private int numMovingBoxes;
    private int numMovingObstacles;
	private int numStaticObstacles;
	
	/** The path of the robot, moving boxes, and moving obstacles **/		
	private List<RobotConfig> robotPath = new ArrayList<>();
	private List<List<Box>> movingBoxPath = new ArrayList<>();
    private List<List<Box>> movingObstaclePath = new ArrayList<>();

	
	/** Returns the width of the robot **/		
	public double getRobotWidth() {return robotWidth;}

	/** Returns a list of static obstacles **/		
    public List<StaticObstacle> getStaticObstacles() {return staticObstacles;}

	/** Returns the initial robot config **/		
    public RobotConfig getInitialRobotConfig() { return initialRobotConfig; }

	/** Returns a list of moving boxes **/		
	public List<Box> getMovingBoxes() { return movingBoxes; }

	/** Returns a list of moving obstacles **/		
	public List<Box> getMovingObstacles() { return movingObstacles; }

	/** Returns the robot path **/		
	public List<RobotConfig> getRobotPath() { return robotPath;}

	/** Returns the moving box path **/		
	public List<List<Box>> getMovingBoxPath() { return movingBoxPath; }

	/** Returns the moving obstacle path **/		
	public List<List<Box>> getMovingObstaclePath() { return movingObstaclePath; }

	public List<Point2D> getMovingBoxEndPositions() { return movingBoxEndPositions; }

	public boolean getProblemLoaded() { return problemLoaded; }

	public boolean getSolutionLoaded() { return solutionLoaded; }

    /**
	 * Loads a problem from a problem text file.
	 * 
	 * @param filename
	 *            the path of the text file to load.
	 * @throws IOException
	 *             if the text file doesn't exist or doesn't meet the assignment
	 *             specifications.
	 */
	public void loadProblem(String filename) throws IOException {
		problemLoaded = false;
		BufferedReader input = new BufferedReader(new FileReader(filename));
		String line;
		int lineNo = 0;
		Scanner s;
		try {
			// line 1
			line = input.readLine();
			lineNo++;
			s = new Scanner(line);

			robotWidth = s.nextDouble();
			initialRobotConfig =  new RobotConfig(
				new Point2D.Double(s.nextDouble(), s.nextDouble()), s.nextDouble());
			s.close();

			// line 2
			line = input.readLine();
			lineNo++;
			s = new Scanner(line);
			numMovingBoxes = s.nextInt();
			numMovingObstacles = s.nextInt();
			numStaticObstacles = s.nextInt();
			s.close();

			// this section covers moving boxes
			movingBoxEndPositions = new ArrayList<Point2D>();
			movingBoxes = new ArrayList<>();
			for (int i = 0; i < numMovingBoxes; i++) {
				line = input.readLine();
				lineNo++;
				s = new Scanner(line);
				// The box creation function requires the bottom left corner.
				movingBoxes.add(new MovingBox(
					new Point2D.Double(s.nextDouble()-robotWidth/2,
										s.nextDouble()-robotWidth/2),	robotWidth));
				movingBoxEndPositions.add(
					new Point2D.Double(s.nextDouble()-robotWidth/2,
										s.nextDouble()-robotWidth/2));
				s.close();
			}

            movingObstacles = new ArrayList<>();

            // this section covers moving Obstacles (still boxes)
			for (int i = 0; i < numMovingObstacles; i++) {
				line = input.readLine();
				lineNo++;
				s = new Scanner(line);
				// The box creation take the bottom left corner
				double x = s.nextDouble();
				double y = s.nextDouble();
				double w = s.nextDouble();
				movingObstacles.add(new MovingObstacle(
					new Point2D.Double(x-w/2, y-w/2),	w));
				s.close();
			}
			
			// this section represents static staticObstacles
			staticObstacles = new ArrayList<StaticObstacle>();
			for (int i = 0; i < numStaticObstacles; i++) {
				line = input.readLine();
				lineNo++;
				staticObstacles.add(new StaticObstacle(line));
			}
			
			problemLoaded = true;
		} catch (InputMismatchException e) {
			System.out.format("Invalid number format on input file - line %d: %s", lineNo,
                    e.getMessage());
			System.exit(1);
		} catch (NoSuchElementException e) {
            System.out.format("Not enough tokens on input file - line %d",
                    lineNo);
            System.exit(2);
		} catch (NullPointerException e) {
            System.out.format("Input file - line %d expected, but file ended.", lineNo);
            System.exit(3);
		} finally {
			input.close();
		}
	}

    /**
	 * Loads a solution from a solution text file.
	 * 
	 * @param filename
	 *            the path of the text file to load.
	 * @throws IOException
	 *             if the text file doesn't exist or doesn't meet the assignment
	 *             specifications.
	 */
    public void loadSolution(String filename) throws IOException {
        solutionLoaded = false;
        if (!problemLoaded) {
            System.out.println("Problem not loaded, exiting!");
            System.exit(4);
        }

        BufferedReader input = new BufferedReader(new FileReader(filename));
        String line;
        int lineNo = 0;
        Scanner s;
        try {
            // line 1
            line = input.readLine();
            lineNo++;
            s = new Scanner(line);
            int p = s.nextInt();
            s.close();

            for (int i = 0; i < p; i++) {
                line = input.readLine();
                lineNo++;
                s = new Scanner(line);
                robotPath.add(new RobotConfig(
                        new Point2D.Double(s.nextDouble(),s.nextDouble()),
                                s.nextDouble()));
                List<Box> movingBoxState = new ArrayList<>();
                for (int j = 0; j < numMovingBoxes; j++) {
                    movingBoxState.add(new MovingBox(new Point2D.Double(s.nextDouble() - robotWidth/2,
																		s.nextDouble() - robotWidth/2),
																		robotWidth));
                }
                movingBoxPath.add(movingBoxState);
                List<Box> movingObstacleState = new ArrayList<>();
                for (int k = 0; k < numMovingObstacles; k++) {
                    movingObstacleState.add(new MovingObstacle(new Point2D.Double(s.nextDouble() - movingObstacles.get(k).getWidth() / 2,
							s.nextDouble() - movingObstacles.get(k).getWidth() / 2),
                            movingObstacles.get(k).getWidth()));
                }
                movingObstaclePath.add(movingObstacleState);
                s.close();
            }
            solutionLoaded = true;
        } catch (InputMismatchException e) {
            System.out.format("Invalid number format on input file - line %d: %s", lineNo,
                    e.getMessage());
            System.exit(1);
        } catch (NoSuchElementException e) {
            System.out.format("Not enough tokens on input file - line %d",
                    lineNo);
            System.exit(2);
        } catch (NullPointerException e) {
            System.out.format("Input file - line %d expected, but file ended.", lineNo);
            System.exit(3);
        } finally {
            input.close();
        }
    }

	/**
	 * Add a new robot config to the robot path
	 * @param rc
	 */
	public void addToRobatPath(RobotConfig rc){
    	robotPath.add(rc);
	}

	/**
	 * Add a list of a box's path to goal to the moving box path
	 * @param b
	 */
	public void setMovingBoxPath(List<List<Box>> b){
		movingBoxPath = b;
	}

	/**
	 * Add a list of an obstacle's path to end position
	 * @param b
	 */
	public void setMovingObstaclePath(List<List<Box>> b){
		movingObstaclePath =b;
	}


	/**
	 * OBSOLETE NOW REPLACED BY OUTPUTWRITER CLASS
	 * Writes the solution to a given file.
	 * Uses the three lists robotpath, nmovingboxPath and movingObstaclePath to find out the solution.
	 * @param filename File to write to
	 * @throws IOException
<<<<<<< HEAD

=======
>>>>>>> origin/Nick_Dev
	public void writeSolutionToFile(String filename) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
		try {
			writer.flush();
			// line 1
			int length = robotPath.size();
			writer.write("" +length);
			writer.newLine();
			for (int i = 0; i < robotPath.size(); i++) {
				writer.write(robotPath.get(i).getPos().getX() + " " + robotPath.get(i).getPos().getY() + " " +
						robotPath.get(i).getOrientation());
				for(int j = 0; j < movingBoxPath.size();j++){
					writer.write(" " + movingBoxPath.get(j).get(i).getPos().getX() + " " +
							movingBoxPath.get(j).get(i).getPos().getY());
				}
				for(int j = 0; j < movingObstaclePath.size();j++){
					writer.write(" "+ movingObstaclePath.get(j).get(i).getPos().getX() + " " +
							movingObstaclePath.get(j).get(i).getPos().getY());
				}
				writer.newLine();
			}
		} finally {
			writer.flush();
			writer.close();
		}
	}
**/

	public List<RobotConfig> formatRobotPath(List<RobotConfig> robotPath){
		List<RobotConfig> currentPath = new ArrayList<>();
		for (int j=0; j<robotPath.size();j++){
			RobotConfig currentConfig = robotPath.get(j);
			currentPath.add(currentConfig);
			if(j < robotPath.size()-1){
				RobotConfig nextConfig = robotPath.get(j+1);
				//TODO - If angles are different rotate robot to correct angle
				//Add points at 0.001 between the two points

				if (getDistance(currentConfig, nextConfig)>0.001 || Math.abs(currentConfig.getOrientation() - nextConfig.getOrientation()) > 0.001){

					List<RobotConfig> inbetweenPoints = getMiddlePoints(currentConfig, nextConfig);
					for(int l=0; l<inbetweenPoints.size();l++){
						currentPath.add(inbetweenPoints.get(l));
					}
				}
			}
		}
		return currentPath;
	}

	/**
	 * OBSOLETE NOW REPLACED BY FORMATROBOTPATH
	 *
	 * @param robotPath
	 * @return
<<<<<<< HEAD

=======
>>>>>>> origin/Nick_Dev
	public List<List<RobotConfig>> formatRobotPath2(List<List<RobotConfig>> robotPath){
		List<List<RobotConfig>> newPath =new ArrayList<>();
		for(int i=0; i<robotPath.size(); i++){
			List<RobotConfig> currentPath = new ArrayList<>();
			for (int j=0; j<robotPath.get(i).size();j++){
				RobotConfig currentConfig = robotPath.get(i).get(j);
				currentPath.add(currentConfig);
				if(j < robotPath.get(i).size()-1){
					RobotConfig nextConfig = robotPath.get(i).get(j+1);
					//TODO - If angles are different rotate robot to correct angle
					//Add points at 0.001 between the two points
					if (getDistance(currentConfig, nextConfig)>0.001){
						List<RobotConfig> inbetweenPoints = getMiddlePoints(currentConfig, nextConfig);
						for(int l=0; l<inbetweenPoints.size();l++){
							currentPath.add(inbetweenPoints.get(l));
						}
					}
				}
			}
			newPath.add(currentPath);
		}
		return newPath;
	}
	**/

	/**
	 * Return the Euclidean distance of the two specified robot configs
	 *
	 * @param a, first robot config
	 * @param b, second robot config
	 * @return the distance of the points
	 */
	public double getDistance(RobotConfig a, RobotConfig b)
	{
		return Math.sqrt(Math.pow(b.getPos().getX() - a.getPos().getX(),2) + Math.pow(b.getPos().getY() - a.getPos().getY(),2));
	}

	/**
	 *
	 * @param a current pos
	 * @param b next pos
	 * @return
	 */
	List<RobotConfig> getMiddlePoints(RobotConfig a, RobotConfig b){
		List<RobotConfig> points = new ArrayList<>();
		double Distance = getDistance(a, b);
		Point2D workingPoint = (Point2D) a.getPos().clone();
		double currentAngle = a.getOrientation();
		while (Distance > 0.0005 | Math.abs(currentAngle - b.getOrientation())>0.01){
			if(getDistance(new RobotConfig(workingPoint, currentAngle), b) > 0.0005){
				Double maxDistance = 0.0005 / Distance;
				double x = ((1 - maxDistance) * workingPoint.getX() + maxDistance * b.getPos().getX());
				double y = ((1 - maxDistance) * workingPoint.getY() + maxDistance * b.getPos().getY());
				workingPoint.setLocation(x, y);
				points.add(new RobotConfig(workingPoint, currentAngle));
				Distance = getDistance(new RobotConfig(workingPoint, currentAngle), b);
			}
			if(Math.abs(currentAngle - b.getOrientation())>0.0001){
				if(currentAngle > b.getOrientation()){
					currentAngle-=0.001;
				} else{
					currentAngle+=0.001;
				}
				points.add(new RobotConfig(workingPoint, currentAngle));
			}
		}
		return points;
	}
}