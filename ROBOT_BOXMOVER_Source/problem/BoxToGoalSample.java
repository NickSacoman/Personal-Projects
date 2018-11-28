package problem;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * A generic implementation of Uniform Cost Search.
 *
 * Part of the solution code for COMP3702/7702 Tutorial 2.
 *
 * Created by Nicholas Collins on 8/08/2017.
 * Updated by Sergiy Dudnikov on 5/08/2018
 * Updated by Yukai Qiao on 12/8/2018
 * Updated by Harrison Lucas on 27/08/2018
 * Updated by Nick Sacoman on 2/09/2018
 */
public class BoxToGoalSample implements SearchAgent {

	// Container for searched traversals
	private PriorityQueue<SearchTreeNode> container;
	private int totalNodes = 0;

	// Environment that is searched
	private RectangleEnvironment searchEnvi;
	private State<Point2D> initState;
	private State<Point2D> initGoal;
	int loops = 0;

	// List to goal
	public List<StateCostPair> pathGoal = new ArrayList<>();

	/**
	 * Create a UCS search agent instance.
	 */
	public BoxToGoalSample(RectangleEnvironment envi) {
		// Initialize the searching container
		container = new PriorityQueue<SearchTreeNode>();

		// Read the given environment
		searchEnvi = envi;
	}

	/**
	 * Search for a path between a given initial state and goal state.
	 * @param initial the initial stage
	 * @param goal the goal state
	 * @return the list of states and costs representing the path from the
	 * initial state to the goal state found by the UCS agent.
	 */
	public List<StateCostPair> search(State<Point2D> initial, State<Point2D> goal, List<StateCostPair> curList, boolean firstSearch) {
		container.add(new SearchTreeNode(new StateCostPair(initial, 0)));
		if (firstSearch) {
			initState = initial;
			initGoal = goal;
		}
		long start = System.currentTimeMillis();

		boolean addedAny = false;


		while(container.size() > 0) {
			// select the search tree node with lowest total path cost
			SearchTreeNode currentNode = container.poll();
			totalNodes--;
			State<Point2D> currentState = currentNode.stateCostPair.getState();
			// check if this state is the goal
			if (currentState.equals(goal)) {
				// goal found - return all steps from initial to goal
				List<StateCostPair> pathToGoal = new LinkedList<StateCostPair>();

				while (currentNode.parent != null) {
					pathToGoal.add(currentNode.stateCostPair);
					currentNode = currentNode.parent;
				}

				Collections.reverse(curList);

				if (!curList.isEmpty()) {
					for (int i = 0; i < curList.size()-1; i ++) {
						pathToGoal.add(curList.get(i));
					}
				}

				Collections.reverse(pathToGoal);

				// reset for next search
				reset();

				//				for (int i = 0; i < pathToGoal.size(); i++) {
				//					System.out.println(pathToGoal.get(i).getState().getRect());
				//				}

				return pathToGoal;

			}

			// not the goal - add all successors to container
			List<StateCostPair> successors = currentState.getSuccessors(goal);
			for (StateCostPair s : successors) {
				totalNodes++;
				SearchTreeNode tempNode = currentNode;
				boolean visited = false;
				while(tempNode.parent != null) {
					if(tempNode.stateCostPair.getState().equals(s.getState())) {
						// this state has been visited on this path
						visited = true;
					}
					tempNode = tempNode.parent;
				}
				if(initial.equals(s.getState())) {
					// this state is the initial state
					visited = true;
				}

				if(!visited) {
					// Check for collisions for the unvisited successors
					boolean collision = false;
					for (int i = 0; i < searchEnvi.SORectangleList.size(); i++) {
						for (int j = 0; j < s.getState().getBuffers().size(); j++) {
							if ((searchEnvi.SORectangleList.get(i).contains(s.getState().getBuffers().get(j)))) {
								collision = true;
							}
						}
					}

					for (int i = 0; i < searchEnvi.MORectangleList.size(); i++) {
						for (int j = 0; j < s.getState().getBuffers().size(); j++) {
							if ((searchEnvi.MORectangleList.get(i).contains(s.getState().getBuffers().get(j)))) {
								collision = true;
							}
						}
					}
					for (int i = 0; i < searchEnvi.MBRectangleList.size(); i++) {
						for (int j = 0; j < s.getState().getBuffers().size(); j++) {
							if ((searchEnvi.MBRectangleList.get(i).contains(s.getState().getBuffers().get(j)))) {
								collision = true;
							}
						}
					}

					// Add the successor if no collision is detected
					if (!collision) {
						addedAny = true;
						container.add(new SearchTreeNode(currentNode, s));
					}
				}

			}
			long end = System.currentTimeMillis();
			if (loops > 7) {
				reset();
				loops = 0;
				curList.clear();
				return null;
			}
			if (end-start > 300) {
				loops++;
				ObjectState tempGoal = findRandomGoal(initial, goal);
				reset();
				List<StateCostPair> rndList = search(initial, tempGoal, curList, false);
				if (rndList != null) {
					return search(tempGoal, goal, rndList, false);
				}
				else {
					return null;
				}
			}
		}
		if (!addedAny) {
			return searchNoBuffer(initial, goal);
		}
		// no solution
		reset();
		return null;
	}

	/**
	 * Search for a path between a given initial state and goal state without using a buffer
	 * @param initial the initial stage
	 * @param goal the goal state
	 * @return the list of states and costs representing the path from the
	 * initial state to the goal state found by the UCS agent.
	 */
	public List<StateCostPair> searchNoBuffer(State<Point2D> initial, State<Point2D> goal) {
		container.add(new SearchTreeNode(new StateCostPair(initial, 0)));
		while(container.size() > 0) {
			// select the search tree node with lowest total path cost
			SearchTreeNode currentNode = container.poll();
			totalNodes--;
			State<Point2D> currentState = currentNode.stateCostPair.getState();
			// check if this state is the goal
			if (currentState.equals(goal)) {
				// goal found - return all steps from initial to goal
				List<StateCostPair> pathToGoal = new LinkedList<StateCostPair>();

				while (currentNode.parent != null) {
					pathToGoal.add(currentNode.stateCostPair);
					currentNode = currentNode.parent;
				}
				Collections.reverse(pathToGoal);

				// reset for next search
				reset();

				return pathToGoal;

			}

			// not the goal - add all successors to container
			List<StateCostPair> successors = currentState.getSuccessors(goal);
			for (StateCostPair s : successors) {
				totalNodes++;
				SearchTreeNode tempNode = currentNode;
				boolean visited = false;
				while(tempNode.parent != null) {
					if(tempNode.stateCostPair.getState().equals(s.getState())) {
						// this state has been visited on this path
						visited = true;
					}
					tempNode = tempNode.parent;
				}
				if(initial.equals(s.getState())) {
					// this state is the initial state
					visited = true;
				}

				if(!visited) {
					// Check for collisions for the unvisited successors
					boolean collision = false;
					for (int j = 0; j < searchEnvi.SORectangleList.size(); j++) {
						if (searchEnvi.SORectangleList.get(j).intersects(s.getState().getRect())) {
							collision = true;
						}

					}
					for (int z = 0; z < searchEnvi.MORectangleList.size(); z++) {
						if (searchEnvi.MORectangleList.get(z).intersects(s.getState().getRect())) {
							collision = true;
							// System.out.println("Colliding movable");
						}
					}
					for (int i = 0; i < searchEnvi.MBRectangleList.size(); i++) {
						if (searchEnvi.MBRectangleList.get(i).intersects(s.getState().getRect())) {
							collision = true;
						}
					}

					// Add the successor if no collision is detected
					if (!collision) {
						//						System.out.println(currentNode.stateCostPair.getState().getRect());
						container.add(new SearchTreeNode(currentNode, s));
					}
				}

			}
		}
		// no solution
		reset();
		return null;
	}

	/**
	 * Find a random goal to search to, avoiding or dissolving infinite loops
	 *
	 * @param init, initial node
	 * @return a random goal
	 */
	private ObjectState findRandomGoal(State<Point2D> init, State<Point2D> goal) {
		boolean collision = false;
		Random rnd = new Random();
		ObjectState rndGoal = null;

		double scaledX = 0;
		double scaledY = 0;

		while (!collision) {
			collision = true;
			int xVal = rnd.nextInt(80);
			int yVal = rnd.nextInt(80);

			if (xVal >= 0 && xVal < 25) {
				xVal = 25;
			}

			if (yVal >= 0 && yVal < 25) {
				yVal = 25;
			}

			scaledX = xVal * .01;
			scaledY = yVal * .01;

			ObjectState temp = new ObjectState(new Point2D.Double(scaledX, scaledY), searchEnvi.robotWidthAndW);

			for (int i = 0; i < searchEnvi.SORectangleList.size(); i++) {
				for (int j = 0; j < temp.getBuffers().size(); j++) {
					if ((searchEnvi.SORectangleList.get(i).contains(temp.getBuffers().get(j)))) {
						collision = false;
					}
				}
			}
			for (int i = 0; i < searchEnvi.MORectangleList.size(); i++) {
				for (int j = 0; j < temp.getBuffers().size(); j++) {
					if ((searchEnvi.MORectangleList.get(i).contains(temp.getBuffers().get(j)))) {
						collision = false;
					}
				}
			}
			for (int i = 0; i < searchEnvi.MBRectangleList.size(); i++) {
				//System.out.println("Search box " + i + " " + searchEnvi.MBRectangleList.get(i));
				for (int j = 0; j < temp.getBuffers().size(); j++) {
					if ((searchEnvi.MBRectangleList.get(i).contains(temp.getBuffers().get(j)))) {
						collision = false;
					}
				}
			}
			for (int i = 0; i < searchEnvi.MBRectangleList.size(); i++) {
				if ((searchEnvi.MBRectangleList.get(i).intersects(temp.getRect()))) {
					collision = false;
				}
			}
			for (int i = 0; i < searchEnvi.MORectangleList.size(); i++) {
				if ((searchEnvi.MORectangleList.get(i).intersects(temp.getRect()))) {
					collision = false;
				}
			}
			for (int i = 0; i < searchEnvi.SORectangleList.size(); i++) {
				if ((searchEnvi.SORectangleList.get(i).intersects(temp.getRect()))) {
					collision = false;
				}
			}

			if (Math.abs(temp.getPoint().getX() - goal.getPoint().getX()) < 0.3 && Math.abs(temp.getPoint().getY() - goal.getPoint().getY()) < 0.3) {
				collision = false;
			}

		}

		rndGoal = new ObjectState(new Point2D.Double(scaledX, scaledY), searchEnvi.robotWidthAndW);
		return rndGoal;
	}

	//	private ObjectState findCloserGoal(State<Point2D> init, State<Point2D> goal) {
	//		ObjectState tempGoal = null;
	//
	//		double xDif = Math.abs(init.getPoint().getX() - goal.getPoint().getX());
	//		double yDif = Math.abs(init.getPoint().getY() - goal.getPoint().getY());
	//
	//		double newX = 0;
	//		double newY = 0;
	//
	//		if (xDif > yDif) {
	//			if (goal.getPoint().getY() > 0.65) {
	//
	//			}
	//		}
	//
	//		return tempGoal;
	//	}

	//	/**
	//	 * Find a temporary goal to attempt to clear away for a buffered search
	//	 *
	//	 * @param cur, the state in collision
	//	 * @param envi, the environment of the search
	//	 * @return a new temporary goal for moving
	//	 */
	//	private ObjectState findTempGoal(State<Point2D> cur, RectangleEnvironment envi) {
	//		// Initialize temp and looping state
	//		ObjectState tempGoal = null;
	//
	//		// Random used for direction
	//		int loop = 0;
	//
	//		// Loop to find a temporary goal that can be moved
	//		while (!(tempGoal == null) || loop == 4) {
	//			// Check if right is available
	//			if (loop == 0) {
	//				double upX = cur.getPoint().getX();
	//				boolean collision = false;
	//				while (tempGoal!=null && (envi.robotWidthAndW * upX) <= 1.0) {
	//					upX = upX + .001;
	//					ObjectState temp = new ObjectState(new Point2D.Double(upX, cur.getPoint().getY()), envi.robotWidthAndW);
	//					for (int i = 0; i < envi.MBRectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MBRectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.MORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.SORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.SORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					if (!collision) {
	//						tempGoal = new ObjectState(new Point2D.Double(upX, cur.getPoint().getY()), envi.robotWidthAndW);
	//						System.out.println(tempGoal.getRect());
	//						break;
	//					}
	//				}
	//			}
	//
	//			// Check if left is available
	//			if (loop == 1) {
	//				double downX = cur.getPoint().getX();
	//				boolean collision = false;
	//				while (tempGoal!=null && downX >= 0.002) {
	//					downX = downX - .001;
	//					ObjectState temp = new ObjectState(new Point2D.Double(downX, cur.getPoint().getY()), envi.robotWidthAndW);
	//					for (int i = 0; i < envi.MBRectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MBRectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.MORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.SORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.SORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					if (!collision) {
	//						tempGoal = new ObjectState(new Point2D.Double(downX, cur.getPoint().getY()), envi.robotWidthAndW);
	//						System.out.println(tempGoal.getRect());
	//						break;
	//					}
	//				}
	//			}
	//
	//			// Check if up is available
	//			if (loop == 2) {
	//				double upY = cur.getPoint().getY();
	//				boolean collision = false;
	//				while (tempGoal!=null && (upY + envi.robotWidthAndW) <= 1.0) {
	//					upY = upY + .001;
	//					ObjectState temp = new ObjectState(new Point2D.Double(cur.getPoint().getX(), upY), envi.robotWidthAndW);
	//					for (int i = 0; i < envi.MBRectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MBRectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.MORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.SORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.SORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					if (!collision) {
	//						tempGoal = new ObjectState(new Point2D.Double(cur.getPoint().getX(), upY), envi.robotWidthAndW);
	//						System.out.println(tempGoal.getRect());
	//						break;
	//					}
	//				}
	//			}
	//
	//			// Check if down is available
	//			if (loop == 3) {
	//				double downY = cur.getPoint().getY();
	//				boolean collision = false;
	//				while (tempGoal!=null && (downY + envi.robotWidthAndW) >= 0.002) {
	//					downY = downY - .001;
	//					ObjectState temp = new ObjectState(new Point2D.Double(cur.getPoint().getX(), downY), envi.robotWidthAndW);
	//					for (int i = 0; i < envi.MBRectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MBRectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.MORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.MORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					for (int i = 0; i < envi.SORectangleList.size(); i++) {
	//						for (int j = 0; j < temp.getBuffers().size(); j++) {
	//							if (envi.SORectangleList.get(i).contains(temp.getBuffers().get(j))) {
	//								collision = true;
	//							}
	//						}
	//					}
	//					if (!collision) {
	//						tempGoal = new ObjectState(new Point2D.Double(cur.getPoint().getX(), downY), envi.robotWidthAndW);
	//						System.out.println(tempGoal.getRect());
	//						break;
	//					}
	//				}
	//			}
	//			loop++;
	//		}
	//		return tempGoal;
	//	}
	//
	//	/**
	//	 * Move in a direction previously found by a temporary goal. Used to escape
	//	 * from an undefined search space with buffers.
	//	 *
	//	 * @param init, initial state
	//	 * @param goal, goal state
	//	 * @return a list of movements
	//	 */
	//	private List<StateCostPair> moveDir(State<Point2D> init, State<Point2D> goal) {
	//		List<StateCostPair> moveList = new ArrayList<StateCostPair>();
	//		StateCostPair initMove = new StateCostPair(init, 0);
	//		moveList.add(initMove);
	//		if (goal.getPoint().getX() > init.getPoint().getX()) {
	//			double upX = init.getPoint().getX();
	//			while (!(upX == (goal.getPoint().getX() - .001))) {
	//				upX = upX + .001;
	//				double sameY = init.getPoint().getY();
	//				Point2D curPoint = new Point2D.Double(upX, sameY);
	//				ObjectState newState = new ObjectState(curPoint, searchEnvi.robotWidthAndW);
	//				StateCostPair nextMove = new StateCostPair(newState, 0);
	//				moveList.add(nextMove);
	//			}
	//		}
	//		if (goal.getPoint().getX() < init.getPoint().getX()) {
	//			double downX = init.getPoint().getX();
	//			while (!(downX == (goal.getPoint().getX() + .001))) {
	//				downX = downX - .001;
	//				double sameY = init.getPoint().getY();
	//				Point2D curPoint = new Point2D.Double(downX, sameY);
	//				ObjectState newState = new ObjectState(curPoint, searchEnvi.robotWidthAndW);
	//				StateCostPair nextMove = new StateCostPair(newState, 0);
	//				moveList.add(nextMove);
	//			}
	//		}
	//		if (goal.getPoint().getY() > init.getPoint().getY()) {
	//			double upY = init.getPoint().getY();
	//			while (!(upY == (goal.getPoint().getY() - .001))) {
	//				double sameX = init.getPoint().getX();
	//				upY = upY + .001;
	//				Point2D curPoint = new Point2D.Double(sameX, upY);
	//				ObjectState newState = new ObjectState(curPoint, searchEnvi.robotWidthAndW);
	//				StateCostPair nextMove = new StateCostPair(newState, 0);
	//				moveList.add(nextMove);
	//			}
	//		}
	//		if (goal.getPoint().getY() < init.getPoint().getY()) {
	//			double downY = init.getPoint().getY();
	//			while (!(downY == (goal.getPoint().getY() + .001))) {
	//				double sameX = init.getPoint().getX();
	//				downY = downY - .001;
	//				Point2D curPoint = new Point2D.Double(sameX, downY);
	//				ObjectState newState = new ObjectState(curPoint, searchEnvi.robotWidthAndW);
	//				StateCostPair nextMove = new StateCostPair(newState, 0);
	//				moveList.add(nextMove);
	//			}
	//		}
	//		return moveList;
	//	}

	/**
	 * Resets the search agent (clears instance variables to be ready for next
	 * search request).
	 */
	private void reset() {
		container.clear();
	}

	/**
	 * Gets the number of nodes
	 * @return int of nodes
	 */
	public int totalNodes() {return totalNodes;}
}