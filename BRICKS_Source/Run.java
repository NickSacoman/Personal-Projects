import javax.swing.*;

/**
 * The RunBrickBreaker class is used to run the actual game itself.
 * 
 * @author Nick Sacoman
 * @version May 27, 2018
 */
public class Run {

	public static void main(String[] args) {
		// Animation
		
		// Create the main window and an instance of the game controls
		JFrame mainWindow = new JFrame();
		GameControl gamePlay = new GameControl();
		
		// Configure the main window and add the instance of controls to the window
		mainWindow.setBounds(1200, 100, 1200, 1000);
		mainWindow.setTitle("Brick Breaker");
		mainWindow.setResizable(false);
		mainWindow.setVisible(true);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.add(gamePlay);
	}
}
