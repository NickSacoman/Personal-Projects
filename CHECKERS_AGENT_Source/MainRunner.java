import java.awt.Color;

import javax.swing.JFrame;

public class MainRunner {

	public static void main(String[] args) throws InterruptedException {
		// Init title window
		JFrame mainFrame = new JFrame();
		mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		mainFrame.setUndecorated(false);
		mainFrame.setBackground(Color.BLACK);
		mainFrame.setVisible(true);
		mainFrame.setTitle("Checkers Challenge");
		
		// Init starting screen
		GameScreens start = new GameScreens();
		mainFrame.add(start);
		Thread.sleep(5000);
		mainFrame.dispose();
	}
}
