import java.awt.*;
import java.util.Random;

/**
 * The Bricks class is the backing implementation and creation of all the bricks used in gameplay. The 
 * bricks will be represented using a matrix type array. Bricks will be given a numerical value of 1 or
 * 0 if visible or not respectively. 
 * 
 * @author Nick Sacoman
 * @version May 27, 2018
 *
 */
public class Bricks {

	// Brick properties
	public int brickMatrix[][];
	public int brickWidth;
	public int brickHeight;
	
	/**
	 * Construct bricks using row and column values from GameControl class. Initialize all
	 * bricks to be visible (value of 1).
	 * 
	 * @param row, rows of bricks
	 * @param col, columns of bricks
	 */
	public Bricks(int row, int col) {
		// Initialize and make the bricks visible
		brickMatrix = new int[row][col];
		for (int i = 0; i < brickMatrix.length; i++) {
			for (int j = 0; j < brickMatrix.length; j++) {
				Random brickColorGen = new Random();
				int colorNum = brickColorGen.nextInt(4) + 1;
				brickMatrix[i][j] = colorNum;
			}
		}
		
		// Properties of each brick
		brickWidth = 1000/col;
		brickHeight = 260/row;
	}
	
	/**
	 * Construct the matrix of bricks and make them visible on to the actual gameboard
	 * itself.
	 *  
	 * @param g, 2D graphics package
	 */
	public void draw(Graphics2D g) {
		// Looping through all bricks created to set their properties accordingly
		for (int i = 0; i < brickMatrix.length; i++) {
			for (int j = 0; j < brickMatrix[0].length; j++) {
				if (brickMatrix[i][j] == 1) {
					g.setColor(Color.blue);
					g.fillRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
					
					// Separate and create borders to the bricks.
					g.setStroke(new BasicStroke(5));
					g.setColor(Color.black);
					g.drawRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
				}
				if (brickMatrix[i][j] == 2) {
					g.setColor(Color.green);
					g.fillRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
					
					// Separate and create borders to the bricks.
					g.setStroke(new BasicStroke(5));
					g.setColor(Color.black);
					g.drawRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
				}
				if (brickMatrix[i][j] == 3) {
					g.setColor(Color.red);
					g.fillRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
					
					// Separate and create borders to the bricks.
					g.setStroke(new BasicStroke(5));
					g.setColor(Color.black);
					g.drawRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
				}
				if (brickMatrix[i][j] == 4) {
					g.setColor(Color.yellow);
					g.fillRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
					
					// Separate and create borders to the bricks.
					g.setStroke(new BasicStroke(5));
					g.setColor(Color.black);
					g.drawRect(j * brickWidth + 100, i * brickHeight + 80, brickWidth, brickHeight);
				}
			}
		}
	}
	
	/**
	 * Set the value of the brick, making it visible or non visible depending on collision
	 * with the ball.
	 * 
	 * @param val, value assigned to a brick
	 * @param row, row the brick is in
	 * @param col, column the brick is in
	 */
	public void setBrickValue(int val, int row, int col) {
		brickMatrix[row][col] = val;
	}
}
