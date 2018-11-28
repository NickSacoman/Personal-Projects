package problem;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.io.IOException;
import rrt.*;

/**
 * Main Runner
 */
public class Main {
	public static int alignFlag=0;

	public static void main(String[] args) throws IOException {
		// Initialize ProblemSpec
		ProblemSpec ps = new ProblemSpec();
		try {
			ps.loadProblem(args[0]);
			//ps.loadSolution("test.txt");
		} catch (IOException e) {
			System.out.println("IO Exception occured");
		}
		System.out.println("Finished loading problem");

		//Initialize Variables
		RectangleEnvironment moversScenario = new RectangleEnvironment(ps);
		OutputWriter outputWriter = new OutputWriter(args[1], ps, moversScenario);
		SearchAgent boxGoals = new BoxToGoalSample(moversScenario);
		RRTSample rrtRobotSample = new RRTSample(moversScenario);

		outputWriter.initaliseFile();

		for (int i = 0; i < moversScenario.MBRectangleList.size(); i++) {
			Rectangle2D currentBox = moversScenario.MBRectangleList.remove(i);
			ObjectState initState = new ObjectState(new Point2D.Double(currentBox.getX(), currentBox.getY()), moversScenario.robotWidthAndW);
			ObjectState goalState = new ObjectState(new Point2D.Double(moversScenario.MBGoalPoints.get(i).getX(),
					moversScenario.MBGoalPoints.get(i).getY()), moversScenario.robotWidthAndW);

			// Search the path for the current box
			List<StateCostPair> empty = new ArrayList<StateCostPair>();
			System.out.println("Beginning box search");
			List<StateCostPair> boxPath = boxGoals.search(initState, goalState, empty, true);
			while (boxPath == null) {
				boxPath = boxGoals.search(initState, goalState, empty, true);
				// boxPath = boxGoals.searchNoBuffer(initState, goalState);  Not sure if this is the best approach - also searchNoBuffer is still not in branch
			}
			boxPath.add(0, new StateCostPair(initState,0d));
			//Replace box for robot search
			moversScenario.placeMovingBox(currentBox, i);
			RobotConfig goalConfig = getRobotEndPosition(boxPath, ps.getRobotWidth());
			List<RobotConfig> robotMoves = rrtRobotSample.rrtSample(moversScenario.robot, goalConfig.getPos(), goalConfig.getOrientation());
			RobotConfig finalPos = null;
			if(alignFlag==0){finalPos = new RobotConfig(new Point2D.Double(boxPath.get(0).getState().getPoint().getX() + (ps.getRobotWidth()/2),
					boxPath.get(0).getState().getPoint().getY() - 0.001), goalConfig.getOrientation());}
			if(alignFlag==1){finalPos = new RobotConfig(new Point2D.Double(boxPath.get(0).getState().getPoint().getX() + (ps.getRobotWidth()/2),
					boxPath.get(0).getState().getPoint().getY() + boxPath.get(0).getState().getRect().getWidth() + 0.001), goalConfig.getOrientation());}
			if(alignFlag==2){finalPos = new RobotConfig(new Point2D.Double(boxPath.get(0).getState().getPoint().getX() -0.001,
					boxPath.get(0).getState().getPoint().getY() + (ps.getRobotWidth() / 2)), goalConfig.getOrientation());}
			if(alignFlag==3){finalPos = new RobotConfig(new Point2D.Double(boxPath.get(0).getState().getPoint().getX() + ps.getRobotWidth() + 0.001,
					boxPath.get(0).getState().getPoint().getY() + (ps.getRobotWidth() / 2)), goalConfig.getOrientation());}
			//if(alignFlag ==2){robotMoves.add(new RobotConfig(new Point2D.Double(boxPath.get(0).getState().getPoint().getX(),
			//		boxPath.get(0).getState().getPoint().getY() + (ps.getRobotWidth() / 2)),goalConfig.getOrientation()));}
            if(robotMoves==null) {
                main(args);
                System.exit(0);
            }
			robotMoves.add(robotMoves.size(), finalPos);
			robotMoves = ps.formatRobotPath(robotMoves);
			outputWriter.writeRobotPath(robotMoves);
			moversScenario.updateRobotPos(goalConfig);
			outputWriter.writeRobotAndBox(goalConfig, boxPath, i, 'B', alignFlag);
			moversScenario.moveMB(boxPath.get(boxPath.size()-1).getState().getPoint(), i);
			moversScenario.MBRectangleList.remove(i);
			moversScenario.placeMovingBox(boxPath.get(boxPath.size()-1).getState().getRect(), i);
		}
		outputWriter.writeFileLength();
		outputWriter.CloseWriter();
		System.out.println("Solution Loaded");
		File f = new File("temp.txt");
		f.delete();
	}


	private static RobotConfig getRobotEndPosition(List<StateCostPair> path, double robotWidth){
		double alpha;
		if (path.get(0).getState().getPoint().getX() == path.get(1).getState().getPoint().getX()) {alpha = 0;}
		else {alpha = 1.5708;}
		// Find the initial side point to align the robot to
		Point2D alignPos;
		if (path.get(0).getState().getPoint().getX() == path.get(1).getState().getPoint().getX()) {
			if (path.get(0).getState().getPoint().getY() < path.get(1).getState().getPoint().getY()) {
				alignPos = new Point2D.Double(path.get(0).getState().getPoint().getX() + (robotWidth/2),
						path.get(0).getState().getPoint().getY() - robotWidth/2);
				alignFlag = 0;
			}
			else {
				alignPos = new Point2D.Double(path.get(0).getState().getPoint().getX() + (path.get(0).getState().getRect().getWidth()),
						path.get(0).getState().getPoint().getY() + path.get(0).getState().getRect().getWidth() + robotWidth/2);
				alignFlag = 1;
			}
		}
		else {
			if (path.get(0).getState().getPoint().getX() < path.get(1).getState().getPoint().getX()) {
				alignPos = new Point2D.Double(path.get(0).getState().getPoint().getX() - robotWidth/2,
						path.get(0).getState().getPoint().getY() + (robotWidth / 2));
				alignFlag = 2;
			} else {
				alignPos = new Point2D.Double(path.get(0).getState().getPoint().getX() + robotWidth + robotWidth/2,
						path.get(0).getState().getPoint().getY()+(robotWidth / 2));
				alignFlag = 3;
			}
		}
		System.out.println(alignPos);
		RobotConfig output = new RobotConfig(alignPos, alpha);
		return  output;
	}

}

