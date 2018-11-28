package rrt;

import java.util.ArrayList;

/**
 * Tree representation of the points from the point class. This class also stores
 * the childrenConfig and the immediate parentConfig of the configPoint.
 * 
 * @author Guarav Sood
 * updated by Nick Sacoman 8/09/18
 */
public class ConfigTree {

	// The point the tree represents
	public Point configPoint;

	// childrenConfig of the above point (represented as trees)
	private ArrayList<ConfigTree> childrenConfig;

	// The parentConfig point represented as a tree. 
	private ConfigTree parentConfig;

	// The name of the tree
	public String TreeName;


	/**
	 * Construct a tree, initializing all the configurations
	 * @param dim, dimension of point
	 * @param x, x position
	 * @param y, y position
	 * @param parentConfig, parentConfiging tree
	 * @param TreeName, name of tree
	 */
	public ConfigTree(int dim, double x, double y, double alpha, ConfigTree parentConfig , String  TreeName)
	{
		configPoint = new Point(dim);
		configPoint.setCoordinate(x, 0);
		configPoint.setCoordinate(y, 1);
		configPoint.setCoordinate(alpha, 2);
		childrenConfig = new ArrayList<>();
		this.TreeName = TreeName;
		this.parentConfig = parentConfig;

	}

	/**
	 * Construct a tree
	 * @param dim, dimension of point
	 * @param parentConfig, parentConfiging tree
	 * @param TreeName, name of tree
	 */
	public ConfigTree(Point a, ConfigTree parentConfig, String TreeName)
	{
		configPoint = a;
		childrenConfig = new ArrayList<>();
		this.TreeName = TreeName;
		this.parentConfig = parentConfig;
	}

	/**
	 * Get the data point of the tree
	 * 
	 * @return data point
	 */
	public Point getconfigPoint()
	{
		return configPoint;
	}

	/**
	 * Get the child of the tree
	 * 
	 * @return the childrenConfig
	 */
	public ArrayList<ConfigTree> getchildrenConfig() {
		return childrenConfig;
	}

	/**
	 * Get a specified child via index
	 * 
	 * @param index, index of child
	 * @return the child
	 */
	public ConfigTree getChild(int index) {
		return childrenConfig.get(index);
	}

	/**
	 * Set the configPoint point to be a point
	 * 
	 * @param a, the point to set
	 */
	public void setconfigPoint(Point a) {
		configPoint = a;
	}

	/**
	 * Set the childrenConfig of the tree
	 * 
	 * @param kids, the childrenConfig
	 */
	public void setchildrenConfig(ArrayList<ConfigTree> kids) {
		childrenConfig.clear();
		for (ConfigTree kid : kids) {
			childrenConfig.add(kid);
		}
	}

	//	/**
	//	 * Used for graphing
	//	 */
	//	public void preorderTraversal()
	//	{
	//		System.out.print("(");
	//		for (ConfigTree achildrenConfig : childrenConfig) {
	//			for (int j = 0; j < configPoint.getDimensions(); j++) {
	//				if (j != configPoint.getDimensions() - 1) {
	//					System.out.print(configPoint.getCoordinate(j) + ", ");
	//				} else {
	//					System.out.print(configPoint.getCoordinate(j));
	//				}
	//			}
	//			System.out.print(")\n");
	//			achildrenConfig.preorderTraversal();
	//		}
	//	}

	/**
	 * Recursively search for the closest point
	 * 
	 * @param shortestDistance, the current shortest distance
	 * @param a, point to find closest vertex to
	 * @return the closest vertex (tree)
	 */
	public ConfigTree closestVertex(double shortestDistance, Point a)
	{
		ConfigTree closestVertex = this;
		for (ConfigTree achildrenConfig : this.childrenConfig) {
			if (getDistance(achildrenConfig.configPoint, a) < shortestDistance) {
				shortestDistance = getDistance(achildrenConfig.configPoint, a);
				closestVertex = achildrenConfig.closestVertex(shortestDistance, a);
			} else {
				achildrenConfig.closestVertex(shortestDistance, a);
			}
		}
		return closestVertex;
	}

	/**
	 * Return the Euclidean distance of the two specified points
	 * 
	 * @param a, first point
	 * @param b, second point
	 * @return the distance of the points
	 */
	public double getDistance(Point a, Point b)
	{
		return Math.sqrt(Math.pow(b.getCoordinate(0) - a.getCoordinate(0),2) + Math.pow(b.getCoordinate(1) - a.getCoordinate(1),2));
	}

	/**
	 * Add a child to the given tree, creating a new tree.
	 * 
	 * @param a, the point to add
	 * @param parentConfig, the parentConfiging tree
	 * @param TreeName, name of tree
	 * 
	 * @return a new tree
	 */
	public ConfigTree addChild(Point a, ConfigTree parentConfig, String TreeName)
	{
		ConfigTree tmp = new ConfigTree(a ,parentConfig, TreeName);
		this.childrenConfig.add(tmp);
		return tmp;
	}


	//	 public void add_child(Tree child, Tree parentConfig, String Tree_Name)
	//    {
	//        this.childrenConfig.add(child);
	//
	//    }
	//
	//
	//	    public void add_child_at(Tree vertex, Point a)
	//    {
	//        if(this.get_configPoint() == vertex.get_configPoint())
	//        {
	//            this.add_child(a);
	//            System.out.println("got here");
	//        }
	//        else {
	//            for (int i = 0; i < childrenConfig.size(); i++) {
	//                this.get_child(i).add_child_at(vertex, a);
	//            }
	//        }
	//    }


	/**
	 * Add a child to the specified tree
	 * 
	 * @param a, the tree to add the child to
	 */
	public void addChild(ConfigTree a)
	{
		this.childrenConfig.add(a);
	}

	/**
	 * Clear the tree and its childrenConfig
	 */
	public void delete()
	{
		for (ConfigTree achildrenConfig : childrenConfig) {
			achildrenConfig.delete();
		}
		childrenConfig.clear();
	}

	/**
	 * Get the parentConfiging tree
	 * 
	 * @return the parentConfiging tree
	 */
	public ConfigTree getparentConfig() {
		return parentConfig;
	}

	/**
	 * Set the parentConfig of the tree to the given parentConfig
	 * 
	 * @param parentConfig, the parentConfig tree
	 */
	public void setparentConfig(ConfigTree parentConfig) {
		this.parentConfig = parentConfig;
	}

	/**
	 * Return the tree name
	 * 
	 * @return tree name
	 */
	public String getTreeName() {
		return TreeName;
	}

	/**
	 * Set the name of the tree
	 * 
	 * @param treeName, name of tree
	 */
	public void setTreeName(String treeName) {
		TreeName = treeName;
	}
}