package rrt;

import problem.BoxRobot;
import problem.RobotConfig;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotMovementV2 {

	 static float angle ; //  [1.5708f],[0]
	  static   double[] coords ;//{200,150}   {150,100}
	  String p3_robot_initial_SIDE_TAG; ///LEFT,DOWN
	  static Point2D next_point_in_path_of_box ;
	  static RobotConfig robot ;
	  static  Map <String, Point2D> rectangleSidesWithTags ;
	  static  List<Point2D> rectangleCorners;
	  static  Rectangle2D moveableBox ;
	  static double width ;
	  static Graphics2D g2;
	  static BoxRobot[] robotAttachedToBox = new BoxRobot[4];
	  static Point2D	  selected_side_mid_point = new Point2D.Double();
	  static List<String> PrintingRobotDetails;
	
	public static void main(String[] args) {
		
		 double[] coords_Of_Robot = {.05100,.8};
		
Setup(new Rectangle2D.Double(.15100,.750, .1,.1), // give me the MB
		   coords_Of_Robot, // initial robot coordinates
			1.5708f,  // angle of the robot 
			"RIGHT",  // side at which the robot is attached
			new Point2D.Double(.15200,.750),   //next  point to which we need to move
			.1// width of the robot
			);
	
	}
	
	
	public static List<String>  Setup(Rectangle2D MB,
															double[] coords_Robot, 
															float angle_Of_Robot ,
															String robot_initial_SIDE_TAG ,
															Point2D next_point_in_path_of_box,
															double width)
	   {

		PrintingRobotDetails = new ArrayList<>();
		rectangleSidesWithTags = new  HashMap();
		rectangleCorners = new ArrayList();
		 moveableBox = new Rectangle2D.Double(MB.getX()*1000,MB.getY()*1000,MB.getWidth()*1000,MB.getHeight()*1000);
	  //   moveableBox.setFrame(100,100, 100,100); // feed MO
		 coords_Robot[0] =coords_Robot[0]*1000; coords_Robot[1] =coords_Robot[1] *1000 ;
	     robot = new RobotConfig(coords_Robot, angle_Of_Robot); //feed robot
	     
	     // get the corner points of the MO
	     PathIterator iterator = 	moveableBox.getPathIterator(null);
	     while (!iterator.isDone())
			{
			  double[] coord = new  double[6];
			  int i = iterator.currentSegment(coord);
			  if (i ==  iterator.SEG_LINETO)
			  {	   iterator.currentSegment(coord);	
			   //    System.out.println(Arrays.toString(coord)); 
			       rectangleCorners .add(new Point2D.Double(coord[0],coord[1]));
			    }	
				iterator.next();
			}
	     
	 	    // L                                                                                                       //200                                                                                                                              //150
			Point2D	  leftEdge_midpoint = new Point2D.Double(rectangleCorners.get(0).getX(),((rectangleCorners.get(0).getY() + rectangleCorners.get(1).getY()) / 2));
			             
			// UP                                                                                                     //150                                                                                                                                    // 200
			Point2D	  upEdge_midpoint = new Point2D.Double((rectangleCorners.get(1).getX()+rectangleCorners.get(2).getX())/2,rectangleCorners.get(1).getY());
			
			// R                                                                                                       //100                                                                                                                                      //150               
			Point2D	  rightEdge_midpoint = new Point2D.Double(rectangleCorners.get(2).getX(),((rectangleCorners.get(2).getY() + rectangleCorners.get(3).getY()) / 2));
			
			// Down                                                                                             //150                                                                                                                                     //100
			Point2D	  downEdge_midpoint = new Point2D.Double((rectangleCorners.get(0).getX()+rectangleCorners.get(3).getX())/2,rectangleCorners.get(0).getY());
	     
			rectangleSidesWithTags.put("LEFT",leftEdge_midpoint);
			rectangleSidesWithTags.put("UP",upEdge_midpoint);
			rectangleSidesWithTags.put("RIGHT",rightEdge_midpoint);
			rectangleSidesWithTags.put("DOWN",downEdge_midpoint);
			robotAttachedToBox[0] = new BoxRobot("LEFT", leftEdge_midpoint,"LeftToUp");
			robotAttachedToBox[1] = new BoxRobot("UP", leftEdge_midpoint,"UpToRight");
			robotAttachedToBox[2] = new BoxRobot("RIGHT", leftEdge_midpoint,"RightToDown");
			robotAttachedToBox[3] = new BoxRobot("DOWN", leftEdge_midpoint,"DownToLeft");
			
			
			 Double eclu_distance = 0.0;
			 Point points_p1_test_eclucedian = new Point(2);
			 Point p2 = new Point(2); //next point in path converted to point
			 Point p3  = new Point (2); // robot initial center point 
			 String side_Selected_eclu = "";
			 p2.setCoordinate( (float)next_point_in_path_of_box.getX()*1000,0);
			 p2.setCoordinate((float)next_point_in_path_of_box.getY()*1000,1);
		     p3.setCoordinate((float)coords_Robot[0], 0);
		     p3.setCoordinate((float)coords_Robot[1], 1);
				 
			   for (Map.Entry<String, Point2D> entry : rectangleSidesWithTags.entrySet()) 
			   {
				     points_p1_test_eclucedian.setCoordinate( (float)entry.getValue().getX(),0);
					 points_p1_test_eclucedian.setCoordinate( (float)entry.getValue().getY(),1);
					 if (eclu_distance < ecludiean_Distance (points_p1_test_eclucedian,p2))
					 {
						 eclu_distance = ecludiean_Distance (points_p1_test_eclucedian,p2);
						 selected_side_mid_point.setLocation(points_p1_test_eclucedian.getCoordinate(0), points_p1_test_eclucedian.getCoordinate(1));
						 side_Selected_eclu = entry.getKey();
						 //System.out.println("side_Selected_eclu::"+side_Selected_eclu);
					 }
					 
		        }
			   
			   if(side_Selected_eclu.equals(robot_initial_SIDE_TAG)){
			   		return null;
			   }
			   //get the index of the current position
			   int index = 0;
			   for (int i = 0 ;i <robotAttachedToBox.length ;i++)
			   {
			      if (robotAttachedToBox[i].getSideTag().equals(robot_initial_SIDE_TAG))
			    	  index = i   ;
			   }
			   
			   for (int i =index ;i<robotAttachedToBox.length; i ++)
			   {
				   
				//   System.out.println("selected_side_mid_point--->"+selected_side_mid_point.getX() +","+selected_side_mid_point.getY());
				//   System.out.println("robot_coordinates--->"+robot.pos.getX()+","+ robot.pos.getY()+","+robot.angle);
				   if (!robot.pos.equals(selected_side_mid_point)  )
				   {
				   	/**
					   if(robot.angle > 3.14){
					   	robot.angle = 0;
					   }**/
					   if (robot_initial_SIDE_TAG =="LEFT")
						   {
						   LeftToUp();
						   robot_initial_SIDE_TAG ="UP";
						   }
					   else
					   if (robot_initial_SIDE_TAG =="UP")
						   {
						   UpToRight();
						   robot_initial_SIDE_TAG ="RIGHT";
						   }
					   else 
					   if (robot_initial_SIDE_TAG=="RIGHT")
					   {
						   RightToDown();
						   robot_initial_SIDE_TAG ="DOWN";
					   }
					   else
					   if (robot_initial_SIDE_TAG =="DOWN")
					   {
						   DownToLeft();
						   robot_initial_SIDE_TAG ="LEFT";
					   }
					   
				   }
				   else
				   {
					   break;
				   }
				   
				   if (i+1==robotAttachedToBox.length )
				   {
					   i =0;
				   }
				   
			   }
			   PrintingRobotDetails.add(0, robot_initial_SIDE_TAG);
			return PrintingRobotDetails;
			   
			   
			   
			
			
	   }
	
	public static double  ecludiean_Distance(Point a, Point b)
	   {
	       return Math.sqrt(Math.pow(b.getCoordinate(0) - a.getCoordinate(0),2) + Math.pow(b.getCoordinate(1) - a.getCoordinate(1),2));
	   }
		
		public static void DownToLeft()
		{
			
			//move
			   for (double i = robot.pos.getX();i<=rectangleSidesWithTags.get("LEFT").getX(); i++)
			   {
				   robot.pos.setLocation(i,robot.pos.getY() );
				   //System.out.println( robot.pos.getX()+" "+ robot.pos.getY()+" "+robot.angle);
			//	   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			// turn   
			   for(int i = 1;i<91;i++)
			 {
				//   System.out.println( robot.angle);
				   robot.angle =  Math.toRadians(Math.toDegrees(robot.angle)+1);
				//   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				  // System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			/*	   try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				   
			 }
			   
			   //move
			   for (double i = robot.pos.getY();i<=rectangleSidesWithTags.get("LEFT").getY(); i++)
			   {
				   robot.pos.setLocation(robot.pos.getX(),i);
				//   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				  // System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			
			
		}
		
		public static void LeftToUp()
		{
			
			   //move
			   for (double i = robot.pos.getY();i<=rectangleSidesWithTags.get("UP").getY(); i++)
			   {
				   robot.pos.setLocation(robot.pos.getX(),i);
				//   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				//   System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			

			// turn   
			   for(int i = 1;i<91;i++)
			 {
			//	   System.out.println( robot.angle);
				   robot.angle =  Math.toRadians(Math.toDegrees(robot.angle)+1);
				//   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				   
				//   System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   
				/*   try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				   
			 }
			   
				//move
			   for (double i = robot.pos.getX();i>=rectangleSidesWithTags.get("UP").getX(); i--)
			   {
				   robot.pos.setLocation(i,robot.pos.getY() );
			//	   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				   
				 //  System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			
			
			
		}
		
		public static void UpToRight()
		{
			
			//move
			   for (double i = robot.pos.getX();i>=rectangleSidesWithTags.get("RIGHT").getX(); i--)
			   {
				   robot.pos.setLocation(i,robot.pos.getY() );
				//   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				 //  System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			// turn   
			   for(int i = 1;i<91;i++)
			 {
				//   System.out.println( robot.angle);
				   robot.angle =  Math.toRadians(Math.toDegrees(robot.angle)+1);
			//	   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				//   System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
		/*		   try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				   
			 }
			   
			   //move
			   for (double i = robot.pos.getY();i>=rectangleSidesWithTags.get("RIGHT").getY(); i--)
			   {
				   robot.pos.setLocation(robot.pos.getX(),i);
			//	   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				   
				//   System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			
		}
		
		public static void RightToDown()
		{
			
			   //move
			   for (double i = robot.pos.getY();i>=rectangleSidesWithTags.get("DOWN").getY(); i--)
			   {
				   robot.pos.setLocation(robot.pos.getX(),i);
			//	   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				   //System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			
			// turn   
			   for(int i = 1;i<91;i++)
			 {
				//   System.out.println( robot.angle);
				   robot.angle =  Math.toRadians(Math.toDegrees(robot.angle)+1);
			//	   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				  // System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			/*	   try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				   
			 }
			   
			 //move
			   for (double i = robot.pos.getX();i<=rectangleSidesWithTags.get("DOWN").getX(); i++)
			   {
				   robot.pos.setLocation(i,robot.pos.getY() );
			//	   g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
				   
				   //System.out.println( robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
				   PrintingRobotDetails.add(robot.pos.getX()/1000+" "+ robot.pos.getY()/1000+" "+robot.angle);
			   }
			   
			
		}
	
	
}
