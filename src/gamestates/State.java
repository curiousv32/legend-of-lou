package gamestates;

import java.awt.event.MouseEvent;

import audio.AudioPlayer;
import main.Game;
import ui.MenuButton;
// manages the states of the game
public class State {

	protected Game game;

	public State(Game game) {
		this.game = game;
	}
// returns the boolens for mouse event in the botton
	public boolean isIn(MouseEvent e, MenuButton mb) {
		return mb.getBounds().contains(e.getX(), e.getY());
	}
// gets the games
	public Game getGame() {
		return game;
	}
// sets the current game state
	public void setGamestate(Gamestate state) {
		switch (state) {
		case MENU -> game.getAudioPlayer().playSong(AudioPlayer.MENU_1);
		case PLAYING -> game.getAudioPlayer().playSong(AudioPlayer.MAIN);
		}

		Gamestate.state = state;
	}

}