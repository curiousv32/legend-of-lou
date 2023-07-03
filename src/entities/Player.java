package entities;

import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utilz.LoadSave;


//the main character of the game
public class Player extends Entity {
	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 15;
	private int playerAction = IDLE;
	private boolean moving = false, attacking = false;
	private boolean left, up, right, down, jump;
	private float playerSpeed = 2.15f;
	private int[][] lvlData;
	private float xDrawOffset = (-5 * Game.SCALE);
	private float yDrawOffset = (float) (0.5 * Game.SCALE);
	
	
	
	// Jumping / Gravity
		private float airSpeed = 0f;
		private float gravity = 0.04f * Game.SCALE;
		private float jumpSpeed = -3.18f * Game.SCALE;
		private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
		private boolean inAir = false;
	
		
		// StatusBarUI
		private BufferedImage statusBarImg;

		private int statusBarWidth = (int) (192 * Game.SCALE);
		private int statusBarHeight = (int) (58 * Game.SCALE);
		private int statusBarX = (int) (10 * Game.SCALE);
		private int statusBarY = (int) (10 * Game.SCALE);

		private int healthBarWidth = (int) (150 * Game.SCALE);
		private int healthBarHeight = (int) (4 * Game.SCALE);
		private int healthBarXStart = (int) (34 * Game.SCALE);
		private int healthBarYStart = (int) (14 * Game.SCALE);

		private int maxHealth = 100;
		private int currentHealth = maxHealth;
		private int healthWidth = healthBarWidth;
		
		
		// AttackBox
		private Rectangle2D.Float attackBox;

		private int flipX = 0;
		private int flipW = 1;

		private boolean attackChecked;
		private Playing playing;
		
		//animation dimensions
	private static final int[][] ANIMATION_DIMENSIONS = {
		    {30, 60}, // IDLE
		    {40, 70}, // RUNNING
		    {40, 65}, // JUMPING
		    {100, 100}, // ATTACK
		    {30, 60}, // DIE
		    {40, 70}, // GLIDE
		};

	
	  // constructor for the player class
	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		loadAnimations();
		initHitbox(x, y, (int) (20 * Game.SCALE), (int) (40 * Game.SCALE));
		initAttackBox();
	}
	
	// initilizes the attack box
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
	}

	// updates the player character
	public void update() {
		updateHealthBar();

		if (currentHealth <= 0) {
			playing.setGameOver(true);
			playing.getGame().getAudioPlayer().stopSong();
			playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
			playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
			return;
		}

		updateAttackBox();

		updatePos();
		if (attacking)
			checkAttack();
		updateAnimationTick();
		setAnimation();
	}
	
	// checks for attacks on the player
	private void checkAttack() {
		if (attackChecked || aniIndex != 1)
			return;
		attackChecked = true;
		playing.checkEnemyHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();

	}
	
	//updates the attack box
	private void updateAttackBox() {
		if (right)
			attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
		else if (left)
			attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);

		attackBox.y = hitbox.y + (Game.SCALE * 10);
	}


	//updates the animation tick for the player
	private void updateAnimationTick() {
		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex >= GetSpriteAmount(playerAction)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
			}

		}

	}

	//sets the animation for the player
	private void setAnimation() {
		int startAni = playerAction;

		if (moving)
			playerAction = RUNNING;
		else
			playerAction = IDLE;

		if (inAir) {
			if (airSpeed < 0)
				playerAction = JUMP;
			else
				playerAction = GLIDE;
		}
		
		if (attacking) {
			playerAction = ATTACK;
			if (startAni != ATTACK) {
				aniIndex = 1;
				aniTick = 0;
				return;
			}
		}
		if (startAni != playerAction)
			resetAniTick();
	}

	//resets the animation tick
	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}


           //updates the player position
	private void updatePos() {
		moving = false;

		if (jump)
			jump();

		if (!inAir)
			if ((!left && !right) || (right && left))
				return;

		float xSpeed = 0;

		if (left) {
			xSpeed -= playerSpeed;
			flipX = width;
			flipW = -1;
		}
		if (right) {
			xSpeed += playerSpeed;
			flipX = 0;
			flipW = 1;
		}
		if (!inAir)
			if (!IsEntityOnFloor(hitbox, lvlData))
				inAir = true;

		if (inAir) {
			if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += gravity;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if (airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}

		} else
			updateXPos(xSpeed);
		moving = true;
	
	}


	
     //resets the air position
	private void resetInAir() {
		inAir = false;
		airSpeed = 0;

	}

	//jump action
	private void jump() {
		if (inAir)
			return;
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir = true;
		airSpeed = jumpSpeed;

	}

	
	//updates the horizontal position
	private void updateXPos(float xSpeed) {
		if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
		}

	}
	
	//updates the health
	public void changeHealth(int value) {
		currentHealth += value;

		if (currentHealth <= 0)
			currentHealth = 0;
		else if (currentHealth >= maxHealth)
			currentHealth = maxHealth;
	}
	
	//updates the health bar 
	private void updateHealthBar() {
		healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
	}
	
	//draws elements of the player
	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset-15) - lvlOffset + flipX, (int) (hitbox.y - yDrawOffset), width * flipW, height, null);
//		drawHitbox(g, lvlOffset);
//		drawAttackBox(g, lvlOffset);
		drawUI(g);
	}
             // draws the attack box for debug
	private void drawAttackBox(Graphics g, int lvlOffsetX) {
		g.setColor(Color.red);
		g.drawRect((int) attackBox.x - lvlOffsetX, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);

	}
	
	// draws the ui 
	private void drawUI(Graphics g) {
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		g.setColor(Color.red);
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
	}
	
	//loads the player animations
	private void loadAnimations() {
	    animations = new BufferedImage[LoadSave.ANIMATION_FILE_PATHS.length][];

	    for (int j = 0; j < LoadSave.ANIMATION_FILE_PATHS.length; j++) {
	        BufferedImage spriteSheet = LoadSave.GetSpriteAtlas(LoadSave.ANIMATION_FILE_PATHS[j]);
	        if (spriteSheet == null) {
	            System.err.println("Error: Sprite atlas file not found at " + LoadSave.ANIMATION_FILE_PATHS[j]);
	            continue;
	        }
	        int numFrames = GetSpriteAmount(j) + 1;
	        int frameWidth = spriteSheet.getWidth() / numFrames;
	        animations[j] = new BufferedImage[numFrames];
	        for (int i = 0; i < numFrames; i++) {
	            int startX = i * frameWidth;
	            int endX = startX + frameWidth;
	            int startY = 0;
	            int endY = spriteSheet.getHeight();
	            animations[j][i] = spriteSheet.getSubimage(startX, startY, frameWidth, spriteSheet.getHeight());
	        }
	    }

	    // Check if all animation sprite sheets were loaded successfully
	    boolean allAnimationsLoaded = true;
	    for (BufferedImage[] animation : animations) {
	        if (animation == null) {
	            allAnimationsLoaded = false;
	            break;
	        }
	    }

	    if (!allAnimationsLoaded) {
	        // Exit the program if any of the animation sprite sheets were not loaded successfully
	        System.err.println("Error: One or more animation sprite sheets could not be loaded.");
	        System.exit(1);
	    }
	    statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
	}
	
//	loads level date
	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

	//bolean for directions
	public void resetDirBooleans() {
		left = false;
		right = false;
		up = false;
		down = false;
	}

	//sets the attack
	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}
	public void setJump(boolean jump) {
		this.jump = jump;
	}

	//resets playes moves
	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		playerAction = IDLE;
		currentHealth = maxHealth;

		hitbox.x = x;
		hitbox.y = y;

		if (!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}

}