package entities;

import static utilz.Constants.EnemyConstants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import static utilz.Constants.Directions.*;

import main.Game;

//crabby enemy class
public class Crabby extends Enemy {

	// AttackBox
	private Rectangle2D.Float attackBox;
	private int attackBoxOffsetX;

	//initialises the enemy type
	public Crabby(float x, float y) {
		super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
		initHitbox(x, y, (int) (22 * Game.SCALE), (int) (31.35 * Game.SCALE));
		initAttackBox();
	}

	// initializes attack box
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (82 * Game.SCALE), (int) (31.35 * Game.SCALE));
		attackBoxOffsetX = (int) (Game.SCALE * 30);
	}

	//updates the crab
	public void update(int[][] lvlData, Player player) {
		updateBehavior(lvlData, player);
		updateAnimationTick();
		updateAttackBox();

	}

	//updates the attcak box
	private void updateAttackBox() {
		attackBox.x = hitbox.x - attackBoxOffsetX;
		attackBox.y = hitbox.y;

	}

	//updates the behaviour of the crab
	private void updateBehavior(int[][] lvlData, Player player) {
		if (firstUpdate)
			firstUpdateCheck(lvlData);

		if (inAir)
			updateInAir(lvlData);
		else {
			switch (enemyState) {
			case IDLE:
				newState(RUNNING);
				break;
			case RUNNING:
				if (canSeePlayer(lvlData, player)) {
					turnTowardsPlayer(player);
				if (isPlayerCloseForAttack(player))
					newState(ATTACK);
				}
				move(lvlData);
				break;
			case ATTACK:
				if (aniIndex == 0)
					attackChecked = false;

				// Changed the name for checkEnemyHit to checkPlayerHit
				if (aniIndex == 3 && !attackChecked)
					checkPlayerHit(attackBox, player);

				break;
			case HIT:
				break;
			}
		}

	}

	//draws the attack box for the crab
	public void drawAttackBox(Graphics g, int xLvlOffset) {
		g.setColor(Color.red);
		g.drawRect((int) (attackBox.x - xLvlOffset), (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
	}

	//flips the x coordinate
	public int flipX() {
		if (walkDir == RIGHT)
			return width;
		else
			return 0;
	}
	//flips the x coordinate for a mirror image of crab
	public int flipW() {
		if (walkDir == RIGHT)
			return -1;
		else
			return 1;

	}

}