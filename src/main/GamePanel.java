package main;


import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import inputs.KeyboardInputs;
import inputs.MouseInputs;
import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;
// panel of the game
public class GamePanel extends JPanel {
	
	//mouse inputs 
	private MouseInputs mouseInputs;
	private Game game; 
	
	
	// game panel constuctor
	public GamePanel(Game game) {
		
		
		mouseInputs = new MouseInputs(this);
		this.game = game;
		setPanelSize();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);
		
	}
	
	
	//sets  the panel size
	private void setPanelSize() {
		Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(size);
		}
	
	//paints game component
	public void paintComponent(Graphics g) {	
		super.paintComponent(g);
		
		game.render(g);

	}
	
	//gets the game
	public Game getGame() {
		return game;
	}

}
