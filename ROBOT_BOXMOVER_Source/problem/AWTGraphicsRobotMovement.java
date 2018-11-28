package problem;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.awt.geom.Point2D;

public class AWTGraphicsRobotMovement extends Frame {


	public AWTGraphicsRobotMovement(){
		super("Java AWT Examples");
		prepareGUI();
	}

	public static void main(String[] args) {

		AWTGraphicsRobotMovement  awtGraphicsRobotDemo = new AWTGraphicsRobotMovement();  
		awtGraphicsRobotDemo.setVisible(true);


	}

	private void prepareGUI(){
		setSize(400,400);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent){
				System.exit(0);
			}        
		}); 
	}  


	@Override
	public void paint(Graphics g) 
	{

		HashMap<String, Point2D> rectangleSidesWithTags = new HashMap<String, Point2D>();
		List<Point2D> rectangleCorners = new ArrayList<Point2D>();
		// Y is increases downward, x increases on right 
		//Rectangle setup
		Rectangle2D rectangleBox = new Rectangle2D.Float();
		rectangleBox.setFrame(100,100, 100,100);

		//robot initial position when attached to box
		double width = 100;
		float angle = 0f;
		double[] coords = {150,100};
		RobotConfig robot = new RobotConfig(coords, angle);

		Graphics2D g2 = (Graphics2D) g; 


		PathIterator it = 	rectangleBox.getPathIterator(null);


		while (!it.isDone())
		{
			double[] coord = new  double[6];
			int i = it.currentSegment(coord);
			if (i ==  it.SEG_LINETO)
			{	   it.currentSegment(coord);	
			System.out.println(Arrays.toString(coord)); 
			rectangleCorners .add(new Point2D.Double(coord[0],coord[1]));
			}	
			it.next();
		}

		// L                                                                                                       //200                                                                                                                              //150
		Point2D	leftEdge = new Point2D.Double(rectangleCorners.get(0).getX(),
				((rectangleCorners.get(0).getY() + rectangleCorners.get(1).getY()) / 2));

		// UP                                                                                                     //150                                                                                                                                    // 200
		Point2D	upEdge = new Point2D.Double((rectangleCorners.get(1).getX()+rectangleCorners.get(2).getX())/2,
				rectangleCorners.get(1).getY());

		// R                                                                                                       //100                                                                                                                                      //150               
		Point2D	  rightEdge = new Point2D.Double(rectangleCorners.get(2).getX(),
				((rectangleCorners.get(2).getY() + rectangleCorners.get(3).getY()) / 2));

		// Down                                                                                             //150                                                                                                                                     //100
		Point2D	  downEdge = new Point2D.Double((rectangleCorners.get(0).getX()+rectangleCorners.get(3).getX())/2,
				rectangleCorners.get(0).getY());

		rectangleSidesWithTags.put("LEFT",leftEdge);
		rectangleSidesWithTags.put("UP",upEdge);
		rectangleSidesWithTags.put("RIGHT",rightEdge);
		rectangleSidesWithTags.put("DOWN",downEdge);

		g2.draw (rectangleBox);
		/*System.out.println(robot.getX1(width));
System.out.println(robot.getX2(width));
System.out.println(robot.getY1(width));
System.out.println(robot.getY2(width));*/
		g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));

		//go left

		for (double i = robot.pos.getX();i<rectangleSidesWithTags.get("LEFT").getX(); i++)
		{
			robot.pos.setLocation(i,robot.pos.getY() );
			g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));
		}
		// turn   
		for(int i = 1;i<91;i++)
		{
			System.out.println( robot.angle);
			robot.angle =  Math.toRadians(Math.toDegrees(robot.angle)+1);
			g2.draw( new Line2D.Double(robot.getX1(width),robot.getY1(width),robot.getX2(width),robot.getY2(width)));

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

}