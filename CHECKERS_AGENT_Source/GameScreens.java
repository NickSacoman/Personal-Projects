import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class GameScreens extends JPanel implements KeyListener, ActionListener {
	
	private boolean start;
	private boolean win;
	private boolean loss;
	
	public GameScreens() {
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		start = false;
		win = false;
		loss = false;
	}

	public void paint(Graphics g) {
		if (!start) {paintStart(g);}
		
		if (win) {paintWin(g);}
		
		if (loss) {paintLoss(g);}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

}
