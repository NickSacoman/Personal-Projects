import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * GameControl class is used by the main class for the controls of the brick breaker game itself.
 * This class defines methods and implements the controls of the actual game.
 * 
 * @author Nick Sacoman
 * @version May 27, 2018
 */
public class GameControl extends JPanel implements KeyListener, ActionListener {

	// Overall game properties
	private boolean win = false;
	private boolean start = false;
	private boolean loss = false;
	private boolean play = false;
	private int playerScore = 0;

	// Brick properties
	private int totalBricks = 49;
	private Bricks brickLayout;

	// Timer properties, used for ball movement
	private Timer time;
	private int delay = 3;

	// Player properties, used for the paddle
	private int playerPos = 500;

	// Ball properties
	private int ballPosX = 575;
	private int ballPosY = 850;
	private int ballXDir = -1;
	private int ballYDir = -2;
	private int ballsRemaining = 3;

	/**
	 * Initialize the game controls, visuals, timer, and components
	 */
	public GameControl() {
		// Create an instance of the bricks
		brickLayout = new Bricks(7, 7);

		// Initialize timer and other component properties
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		time = new Timer(delay, this);
		time.start();
	}

	/**
	 * This method will create the display for the game window upon running.
	 * 
	 * @param g, Graphic package
	 */
	public void paint(Graphics g) {
		// Background paint
		g.setColor(Color.black);
		g.fillRect(1, 1, 1195, 995);

		// Border paint
		g.setColor(Color.red);
		g.fillRect(0, 0, 3, 995);
		g.fillRect(0, 0, 1195, 3);
		g.fillRect(1191, 0, 3, 995);

		// Ball paint
		g.fillOval(ballPosX, ballPosY, 20, 20);

		// Paddle (player) paint
		g.setColor(Color.cyan);
		g.fillRect(playerPos, 875, 175, 10);

		// Score paint
		g.setFont(new Font("serif", Font.BOLD, 25));
		g.drawString("Score:" + playerScore, 1050, 925);

		// Replays left (balls remaining)
		g.drawString("Balls Left:" + ballsRemaining, 10, 925);

		// Brick paint
		brickLayout.draw((Graphics2D) g);

		//Starting display
		if (!start) {
			whiteScreen(g);

			// Title display
			((Graphics2D) g).setStroke(new BasicStroke(5));
			g.setColor(Color.cyan);
			g.setFont(new Font("serif", Font.BOLD, 65));
			g.drawString("NICK'S BRICKBREAKER EXTREME", 35, 100);
			g.drawRect(20, 25, 1150, 100);

			// Control display
			g.setColor(Color.black);
			g.setFont(new Font("serif", Font.PLAIN, 50));
			g.drawString("CONTROLS:", 450, 200);
			g.drawRect(250, 220, 100, 100);
			g.drawLine(270, 270, 325, 270);
			g.drawLine(325, 270, 310, 255);
			g.drawLine(325, 270, 310, 285);
			g.setFont(new Font("serif", Font.BOLD, 25));
			g.drawString(": to move paddle right", 380, 280);
			g.drawRect(250, 350, 100, 100);
			g.drawLine(270, 400, 325, 400);
			g.drawLine(270, 400, 285, 385);
			g.drawLine(270, 400, 285, 415);
			g.drawString(": to move paddle left", 380, 410);
			g.drawString("Player Paddle:", 750, 320);
			g.setColor(Color.cyan);
			g.fillRect(740, 350, 175, 10);

			// Directions display
			g.setColor(Color.black);
			g.setFont(new Font("serif", Font.PLAIN, 50));
			g.drawString("Break all the bricks with the ball to win!", 190, 650);
			g.drawString("Press the space bar to play!", 320, 720);

			// Paddle control animations FINISH
		}

		// Losing display
		if (ballsRemaining == -1) {
			// Clear the gameplay board
			whiteScreen(g);

			// Game over display message
			loss = true;
			g.setColor(Color.red);
			g.setFont(new Font("serif", Font.BOLD, 175));
			g.drawString("GAME OVER", 40, 350);
			g.setColor(Color.black);
			g.setFont(new Font("serif", Font.PLAIN, 50));
			g.drawString("You finished with a final score of: " + playerScore, 195, 450);

			// Draw sad face
			((Graphics2D) g).setStroke(new BasicStroke(5));
			g.drawOval(490, 480, 150, 150);
			g.fillOval(530, 520, 20, 20);
			g.fillOval(580, 520, 20, 20);
			g.drawArc(525, 570, 80, 50, 0, 180);

			// Replay message
			g.drawString("Press the space bar to play again!", 240, 700);
		}

		// Win message 
		if (playerScore == 490) {
			win = true;
			whiteScreen(g);

			// Bold win message
			g.setColor(Color.cyan);
			g.setFont(new Font("serif", Font.BOLD, 175));
			g.drawString("YOU WIN!", 140, 300);

			// Draw happy face
			((Graphics2D) g).setStroke(new BasicStroke(5));
			g.setColor(Color.black);
			g.drawOval(490, 480, 150, 150);
			g.fillOval(530, 520, 20, 20);
			g.fillOval(580, 520, 20, 20);
			g.drawArc(525, 560, 80, 50, 0, -180);

			// Replay message
			g.setColor(Color.black);
			g.setFont(new Font("serif", Font.PLAIN, 50));
			g.drawString("Press the space bar to play again!", 230, 700);
		}

		g.dispose();
	}

	/**
	 * From interface, implements all controls and collisions for the gameplay and will also construct
	 * the bricks 
	 */
	public void actionPerformed(ActionEvent arg0) {
		// Start the timer
		time.start();

		// Check if the game is being played then configure controls
		if (play) {

			/** THE COLLISIONS NEEDS DEBUGGING, THE BALL SOMETIMES BECOMES STUCK IN THE PADDLE
			 *  OR CONTINUE TO COLLIDE WITHIN THE PADDLE IF IT HITS A CORNER
			 */

			// Create a rectangle around the ball for collision detection (needs debugged)
			Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);

			// Check for collision of the top of the paddle, reverse ball direction vertically
			if (ballRect.intersects(new Rectangle(playerPos, 875, 175, 10))) {
				ballYDir = -ballYDir;
			}

			// Check for collision of the side of the paddle, reverse ball direction horizontally
			if (ballRect.intersects(new Rectangle(playerPos-1, 875, 1, 10)) 
					|| ballRect.intersects(new Rectangle(playerPos+175, 875, 1, 10))) {
				ballXDir = -ballXDir;
			}

			// Loop to check all bricks that are visible should be configured for collision detection
			BrickCollision: for (int i = 0; i < brickLayout.brickMatrix.length; i++) {
				for (int j = 0; j < brickLayout.brickMatrix[0].length; j++) {
					if (brickLayout.brickMatrix[i][j] > 0) {
						int XBrick = j * brickLayout.brickWidth + 100;
						int YBrick = i * brickLayout.brickHeight + 80;
						int WBrick = brickLayout.brickWidth;
						int HBrick = brickLayout.brickHeight;

						// Create a rectangle around the bricks for collision detection, clone it
						Rectangle brickR = new Rectangle(XBrick, YBrick, WBrick, HBrick);
						Rectangle curBrick = brickR;

						// Collision has occurred, brick should now disappear from game board
						if (ballRect.intersects(curBrick)) {
							brickLayout.setBrickValue(0, i, j);
							totalBricks--;
							playerScore += 10;

							// Reverse ball direction accordingly to collision of the brick
							if (ballPosX + 19 <= curBrick.x || ballPosX + 1 >= curBrick.x + curBrick.width) {
								ballXDir = -ballXDir;
							} else {
								ballYDir = -ballYDir;
							}

							break BrickCollision;
						}
					}
				}
			}

			// Begin the ball movement
			ballPosX+=ballXDir;
			ballPosY+=ballYDir;

			// Detect boundary collision
			if (ballPosX < 0) {
				ballXDir = -ballXDir;
			}
			if (ballPosY < 0) {
				ballYDir = -ballYDir;
			}
			if (ballPosX > 1180) {
				ballXDir = -ballXDir;
			}

			// Missed ball with paddle, end current game and restart configuration
			if (ballPosY > 1000) {
				reset();
			}
		}
		repaint();
	}

	/**
	 * From interface, the right and left key on the keyboard will control the paddle that
	 * is given in the position of the player, this is the only event that is handled.
	 * 
	 * @param event, either right or left on the keyboard, no other key is used
	 */
	public void keyPressed(KeyEvent event) {
		// If the game has been lost there is no moving the paddle
		if (!loss && start && !win) {
			// Detect key movements, not allowing the player to travel outside of the gameboard
			if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (playerPos>=1015) {
					playerPos = 1015;
				} else {
					moveRight();
				}
			}

			if (event.getKeyCode() == KeyEvent.VK_LEFT) {
				if (playerPos <= 5) {
					playerPos = 5;
				} else {
					moveLeft();
				}
			}
		} else {
			if (event.getKeyCode() == KeyEvent.VK_SPACE) {
				reset();
			}
		}
	}


	/** NOTE: moving the paddle left or right will begin the game and move the ball. **/

	/**
	 * Move the paddle to the right by 30 pixels.
	 */
	public void moveRight() {
		play = true;
		playerPos+=30;
	}

	/**
	 * Move the paddle to the left by 30 pixels.
	 */
	public void moveLeft() {
		play = true;
		playerPos-=30;
	}

	/**
	 * Reset the configuration of the game. Includes the ball, the paddle, the ball direction
	 * and the score if there is a loss
	 */
	public void reset() {
		play = false;

		// Reset paddle and ball
		playerPos = 500;
		ballPosX = 575;
		ballPosY = 850;
		ballXDir = -1;
		ballYDir = -2;
		ballsRemaining--;

		// If this reset is a loss or first game then the player score is reset
		if (loss || !start || win) {
			if (!start) {
				start = true;
			}
			if (win) {
				win = false;
			}
			playerScore = 0;
			ballsRemaining = 3;
			loss = false;

			// Reconstruct bricks
			brickLayout = new Bricks(7, 7);
			totalBricks = 49;
		}
	}

	public void whiteScreen(Graphics g) {
		// Black out all game play items
		g.setColor(Color.white);
		g.fillRect(1, 1, 1195, 995);
	}

	/**
	 * From interface, unused methods.
	 */
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
}
