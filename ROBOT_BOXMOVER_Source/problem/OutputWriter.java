package problem;

import rrt.Point;
import rrt.RobotMovementV2;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harry on 9/13/2018.
 */
public class OutputWriter {

    private String fileName;
    private ProblemSpec ps;
    private RectangleEnvironment env;
    private BufferedWriter writer;
    private int lineCounter;
    public static robotFlagEnum robotFlag;
    private float alpha;

    public OutputWriter(String file, ProblemSpec problemSpec, RectangleEnvironment environment) throws IOException {
        fileName = file;
        ps = problemSpec;
        env = environment;
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("temp.txt"), "utf-8"));
        robotFlag = robotFlagEnum.RIGHT;
        alpha = (float) problemSpec.getInitialRobotConfig().getOrientation();
    }


    public void initaliseFile() throws IOException {
        try {
            writer.flush();
            // line 1
            writer.write("");
            writer.newLine();
            writer.write(ps.getInitialRobotConfig().getPos().getX() + " " + ps.getInitialRobotConfig().getPos().getY() + " " +
                    ps.getInitialRobotConfig().getOrientation());
            for (int j = 0; j < ps.getMovingBoxes().size(); j++) {
                writer.write(" " + (ps.getMovingBoxes().get(j).getPos().getX() + ps.getRobotWidth() / 2) + " " +
                        (ps.getMovingBoxes().get(j).getPos().getY() + ps.getRobotWidth() / 2));
            }
            for (int j = 0; j < ps.getMovingObstacles().size(); j++) {
                writer.write(" " + (ps.getMovingObstacles().get(j).getPos().getX() + ps.getMovingObstacles().get(j).getWidth() / 2) + " " +
                        (ps.getMovingObstacles().get(j).getPos().getY() + ps.getMovingObstacles().get(j).getWidth() / 2));
            }
            writer.newLine();
            lineCounter++;

        } finally {
            writer.flush();
        }
    }

    public void writeRobotPath(List<RobotConfig> robotPath) throws IOException {
        try {
            writer.flush();
            for (int i = 0; i < robotPath.size(); i++) {
                writer.write(robotPath.get(i).getPos().getX() + " " + robotPath.get(i).getPos().getY()
                        + " " + robotPath.get(i).getOrientation());
                for (int j = 0; j < env.MBRectangleList.size(); j++) {
                    writer.write(" " + (env.MBList.get(j).getPos().getX() + env.robotWidthAndW/2)  + " " + (env.MBList.get(j).getPos().getY() + env.robotWidthAndW/2));
                }
                for (int j = 0; j < ps.getMovingObstacles().size(); j++) {
                    writer.write(" " + env.MORectangleList.get(j).getCenterX() + " " + env.MORectangleList.get(j).getCenterY());
                }
                writer.newLine();
                lineCounter++;
                writer.flush();
            }
        } finally {

            writer.flush();
        }
    }

    /**
     * @param currentRobot
     * @param boxPath
     * @param currentBox
     * @param s            O for movable obstacle being moved B for moving box being moved
     */
    public void writeRobotAndBox(RobotConfig currentRobot, List<StateCostPair> boxPath, int currentBox, char s, int side) throws IOException {
        writer.flush();
        ;
        if(side==0){robotFlag = robotFlagEnum.DOWN;}
        if(side==1){robotFlag = robotFlagEnum.UP;}
        if(side==2){robotFlag = robotFlagEnum.RIGHT;}
        if(side==3){robotFlag = robotFlagEnum.LEFT;}
        alpha = (float)currentRobot.getOrientation();
        //boxPath = checkBoxPath(boxPath);
        for (int i = 0; i < boxPath.size(); i++) {
            if(robotFlag==robotFlagEnum.LEFT){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX() + boxPath.get(i).getState().getRect().getWidth(),
                    boxPath.get(i).getState().getPoint().getY()+ boxPath.get(i).getState().getRect().getWidth()/2), alpha);}
            if(robotFlag==robotFlagEnum.RIGHT){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX(),
                    boxPath.get(i).getState().getPoint().getY()+ boxPath.get(i).getState().getRect().getWidth()/2), alpha);}
            if(robotFlag==robotFlagEnum.UP){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX()+ boxPath.get(i).getState().getRect().getWidth()/2,
                    boxPath.get(i).getState().getPoint().getY() + boxPath.get(i).getState().getRect().getWidth()), alpha);}
            if(robotFlag==robotFlagEnum.DOWN){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX() + boxPath.get(i).getState().getRect().getWidth()/2,
                    boxPath.get(i).getState().getPoint().getY()), alpha);}
            if (i < boxPath.size()-1) {
                List<String> turning = rotateRobot(boxPath.get(i).getState().getRect(), currentRobot, (float)alpha, robotFlag.toString(), boxPath.get(i + 1).getState().getPoint());
                if (turning != null) {
                    String returnFlag = turning.get(0);
                    if(returnFlag=="LEFT"){robotFlag = robotFlagEnum.LEFT;}
                    if(returnFlag=="RIGHT"){robotFlag = robotFlagEnum.RIGHT;}
                    if(returnFlag=="UP"){robotFlag = robotFlagEnum.UP;}
                    if(returnFlag=="DOWN"){robotFlag = robotFlagEnum.DOWN;}
                    turning.remove(0);
                    for (int j = 0; j < turning.size(); j++) {
                        writer.write(turning.get(j));
                        for (int l = 0; l < env.MBRectangleList.size(); l++) {
                            if (s == 'B' && l == env.orderList[currentBox]) {
                                writer.write(" " + (boxPath.get(i).getState().getPoint().getX() + ps.getRobotWidth() / 2)
                                        + " " + (boxPath.get(i).getState().getPoint().getY() + ps.getRobotWidth() / 2));
                            } else {
                                writer.write(" " + (env.MBList.get(l).pos.getX() + env.robotWidthAndW/2) + " " + (env.MBList.get(l).getPos().getY() + env.robotWidthAndW/2));
                            }
                        }
                        for (int l = 0; l < ps.getMovingObstacles().size(); l++) {
                            if (s == 'O' && l == currentBox) {
                                writer.write(" " + (boxPath.get(i).getState().getPoint().getX() + ps.getMovingObstacles().get(l).getWidth() / 2)
                                        + " " + (boxPath.get(i).getState().getPoint().getY() + ps.getMovingObstacles().get(l).getWidth() / 2));
                            } else {
                                writer.write(" " + env.MORectangleList.get(l).getCenterX() + " " + env.MORectangleList.get(l).getCenterY());
                            }
                        }
                        writer.newLine();
                        lineCounter++;
                        writer.flush();
                    }
                    String[] split = turning.get(turning.size()-1).split("\\s+");
                    alpha = Float.parseFloat(split[2]);
                }

            }
            if(robotFlag==robotFlagEnum.LEFT){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX() + boxPath.get(i).getState().getRect().getWidth(),
                    boxPath.get(i).getState().getPoint().getY()+ boxPath.get(i).getState().getRect().getWidth()/2), alpha);}
            if(robotFlag==robotFlagEnum.RIGHT){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX(),
                    boxPath.get(i).getState().getPoint().getY()+ boxPath.get(i).getState().getRect().getWidth()/2), alpha);}
            if(robotFlag==robotFlagEnum.UP){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX()+ boxPath.get(i).getState().getRect().getWidth()/2,
                    boxPath.get(i).getState().getPoint().getY() + boxPath.get(i).getState().getRect().getWidth()), alpha);}
            if(robotFlag==robotFlagEnum.DOWN){currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX() + boxPath.get(i).getState().getRect().getWidth()/2,
                    boxPath.get(i).getState().getPoint().getY()), alpha);}
            //currentRobot = new RobotConfig(new Point2D.Double(boxPath.get(i).getState().getPoint().getX(), boxPath.get(i).getState().getPoint().getY()), alpha);
            writer.write((currentRobot.getPos().getX() + " " + (currentRobot.getPos().getY()) + " " + currentRobot.getOrientation()));
            for (int l = 0; l < env.MBRectangleList.size(); l++) {
                if (s == 'B' && l == env.orderList[currentBox]) {
                    writer.write(" " + (boxPath.get(i).getState().getPoint().getX() + ps.getRobotWidth() / 2)
                            + " " + (boxPath.get(i).getState().getPoint().getY() + ps.getRobotWidth() / 2));
                } else {
                    writer.write(" " + (env.MBList.get(l).pos.getX()+ env.robotWidthAndW/2) + " " + (env.MBList.get(l).getPos().getY()+ env.robotWidthAndW/2));
                }
            }
            for (int j = 0; j < ps.getMovingObstacles().size(); j++) {
                if (s == 'O' && j == currentBox) {
                    writer.write(" " + (boxPath.get(i).getState().getPoint().getX() + ps.getMovingObstacles().get(j).getWidth() / 2)
                            + " " + (boxPath.get(i).getState().getPoint().getY() + ps.getMovingObstacles().get(j).getWidth() / 2));
                } else {
                    writer.write(" " + env.MORectangleList.get(j).getCenterX() + " " + env.MORectangleList.get(j).getCenterY());
                }
            }
            writer.newLine();
            lineCounter++;
            writer.flush();
        }
        //TODO Change end angle to match with side
        env.updateRobotPos(currentRobot);
    }

    public List<String> rotateRobot(Rectangle2D rect, RobotConfig robot, float alpha, String flag, Point2D point) {
        DecimalFormat df = new DecimalFormat("#.####");
        double[] robotArr = {Double.valueOf(df.format(robot.getPos().getX())), Double.valueOf(df.format(robot.getPos().getY()))};
        Rectangle2D d = new Rectangle2D.Double(Double.valueOf(df.format(rect.getMinX())), Double.valueOf(df.format(rect.getMinY()))
                , rect.getWidth(), rect.getHeight());
        Point2D p = new Point2D.Double(Double.valueOf(df.format(point.getX())) + rect.getWidth() / 2, Double.valueOf(df.format(point.getY())) + rect.getWidth() / 2);
        List<String> rotations = RobotMovementV2.Setup(d, robotArr, alpha, flag, p, ps.getRobotWidth());
        return rotations;
    }

    public void writeFileLength() throws IOException {
        BufferedWriter newWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
        newWriter.flush();
        newWriter.write("" + lineCounter);
        newWriter.flush();
        newWriter.close();
        FileInputStream input = new FileInputStream(new File("temp.txt"));
        BufferedReader in = new BufferedReader(new InputStreamReader(input));

        FileWriter output = new FileWriter(fileName, true);
        BufferedWriter out = new BufferedWriter(output);
        String aLine = null;
        while ((aLine = in.readLine()) != null) {
            //Process each line and add output to Dest.txt file
            out.write(aLine);
            out.newLine();
        }
        out.flush();
        out.close();
        in.close();

    }

    public void CloseWriter() throws IOException {
        writer.flush();
        writer.close();
    }

    public enum robotFlagEnum{
        LEFT, RIGHT, UP, DOWN
    }

    public List<StateCostPair> checkBoxPath(List<StateCostPair> boxPath){
        List<StateCostPair> currentPath = new ArrayList<>();
        for (int j=0; j<10;j++){
            Point2D currentPoint = boxPath.get(j).getState().getPoint();
            currentPath.add(new StateCostPair(new ObjectState(currentPoint, ps.getRobotWidth()), 0d));
            if(j < boxPath.size()-1){
                if (getDistance(currentPoint, boxPath.get(j+1).getState().getPoint())>0.001){
                    List<StateCostPair> inbetweenPoints = getMiddlePoints(currentPoint, boxPath.get(j+1).getState().getPoint());
                    for(int l=0; l<inbetweenPoints.size();l++){
                        currentPath.add(inbetweenPoints.get(l));
                    }
                }
            }
        }
        return currentPath;
    }

    public double getDistance(Point2D a, Point2D b)
    {
        return Math.sqrt(Math.pow(b.getX() - a.getX(),2) + Math.pow(b.getY() - a.getY(),2));
    }

    List<StateCostPair> getMiddlePoints(Point2D a, Point2D b){
        List<StateCostPair> points = new ArrayList<>();
        double Distance = getDistance(a, b);
        Point2D workingPoint = a;
        while (Distance > 0.0005){
                Double maxDistance = 0.0005 / Distance;
                double x = ((1 - maxDistance) * workingPoint.getX() + maxDistance * b.getX());
                double y = ((1 - maxDistance) * workingPoint.getY() + maxDistance * b.getY());
                workingPoint = new Point2D.Double(x,y);
                points.add(new StateCostPair(new ObjectState(workingPoint, ps.getRobotWidth()),0d));
                Distance = getDistance(workingPoint, b);
        }

        return points;
    }
}

