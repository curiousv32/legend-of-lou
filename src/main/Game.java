package main;

import java.awt.Graphics;

import audio.AudioPlayer;
import gamestates.Gamestate;
import gamestates.Menu;
import gamestates.Playing;

//game class
public class Game implements Runnable {
	
	// game window variables 
	private GameWindow gameWindow;
	private GamePanel gamePanel;
	private Thread gameThread;
	private final int FPS_SET = 120;
	private final int UPS_SET = 200;
	
	// game states and interface
	private Playing playing;
	private Menu menu;
	private AudioPlayer audioPlayer;

	// tiles variables
	public final static int TILES_DEFAULT_SIZE = 32;
	public final static float SCALE = 1.5f;
	public final static int TILES_IN_WIDTH = 26;
	public final static int TILES_IN_HEIGHT = 14;
	public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
	
	
	public Game() {//constructor
		
		initClasses();
		
		gamePanel = new GamePanel(this);
		gameWindow = new GameWindow(gamePanel);
		gamePanel.setFocusable(true);
		gamePanel.requestFocus();
		
		
		startGameLoop();
	}
	
	

	private void initClasses() {//initializes the classes
		menu = new Menu(this);
		playing = new Playing(this);
		audioPlayer = new AudioPlayer();
	} 
	
	private void startGameLoop() {//satrts the game loop
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	
	public void update() {//updates the game
		switch (Gamestate.state) {
		case MENU:
			menu.update();
			break;
		case PLAYING:
			playing.update();
			break;
		case OPTIONS:
		case QUIT:
		default:
			System.exit(0);
			break;

		}
	}
	
	public void render(Graphics g) {//render all the game images
		switch (Gamestate.state) {
		case MENU:
			menu.draw(g);
			break;
		case PLAYING:
			playing.draw(g);
			break;
		default:
			break;
		}
	}

	
	@Override
	public void run() {//runs the game

		double timePerFrame = 1000000000.0 / FPS_SET;
		double timePerUpdate = 1000000000.0 / UPS_SET;

		long previousTime = System.nanoTime();

		int frames = 0;
		int updates = 0;
		long lastCheck = System.currentTimeMillis();

		double deltaU = 0;
		double deltaF = 0;

		while (true) {
			long currentTime = System.nanoTime();

			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;

			if (deltaU >= 1.0) {
				update();
				updates++;
				deltaU--;
			}

			if (deltaF >= 1.0) {
				gamePanel.repaint();
				frames++;
				deltaF--;
			}

			if (System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames + " | UPS: " + updates);
				frames = 0;
				updates = 0;

			}
		}

	}
	public void windowFocusLost() {// window focus
		if (Gamestate.state == Gamestate.PLAYING)
			playing.getPlayer().resetDirBooleans();
	}

	public Menu getMenu() {//returns game menu
		return menu;
	}

	public Playing getPlaying() {//returns state for playing
		return playing;
	}

	public AudioPlayer getAudioPlayer() {// returns the audio component
		return audioPlayer;
	}
}





