package problem;

import java.awt.geom.Point2D;

public class BoxRobot {
	
	String sideTag;
	Point2D coords;
	String NextMovement;
	public String getSideTag() {
		return sideTag;
	}
	public void setSideTag(String sideTag) {
		this.sideTag = sideTag;
	}
	public Point2D getCoords() {
		return coords;
	}
	public void setCoords(Point2D coords) {
		this.coords = coords;
	}

	public BoxRobot(String sideTag, Point2D coords, String nextMovement) {
		super();
		this.sideTag = sideTag;
		this.coords = coords;
		NextMovement = nextMovement;
	}
	public String getNextMovement() {
		return NextMovement;
	}
	public void setNextMovement(String nextMovement) {
		NextMovement = nextMovement;
	}
	
	

}
