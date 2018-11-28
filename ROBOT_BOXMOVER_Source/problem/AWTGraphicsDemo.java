package problem;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import  java.awt.geom.Point2D;
public class AWTGraphicsDemo extends Frame {

	public AWTGraphicsDemo(){
		super("Java AWT Examples");
		prepareGUI();
	}
	public static void main(String[] args){
		AWTGraphicsDemo  awtGraphicsDemo = new AWTGraphicsDemo();  
		awtGraphicsDemo.setVisible(true);
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
	public void paint(Graphics g) {
		Rectangle2D shape = new Rectangle2D.Float();
		shape.setFrame(100,100, 100,100);
		Rectangle2D shape2 = new Rectangle2D.Float();
		shape2.setRect(shape.getX()+10,shape.getY()+10, 50,50);
		//shape.add(shape2);

		// Point2D p1 =  new Point2D.Double(200.0,200.0);
		//Point2D p2 =  new Point2D.Double(50.0,50.0);
		// shape.setFrameFromCenter(p1,p2);

		if (shape.contains(shape2))
		{
			System.out.println("yes it  contains ");
		}
		else
		{
			System.out.println("does not contain");
		}

		if ( shape.intersects(shape2))
		{
			System.out.println("yes it  intersect ");
		}
		else
		{
			System.out.println("does not contain");
		}



		Graphics2D g2 = (Graphics2D) g; 
		g2.draw (shape);
		g2.draw(shape2);
		g2.drawLine((int)shape.getX()+100,(int) shape.getY()+100,(int) shape.getX()+200, (int) shape.getY()+100);

		/*  Font font = new Font("Serif", Font.PLAIN, 24);
      g2.setFont(font);
      g.drawString("Welcome to TutorialsPoint", 50, 70);
      g2.drawString("Rectangle2D.Rectangle", 100, 120);*/
	}
} 