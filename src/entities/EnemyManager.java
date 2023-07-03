package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

//manages the enemy
public class EnemyManager {

	//variables for the crab enemies
	private Playing playing;
	private BufferedImage[][] crabbyArr;
	private ArrayList<Crabby> crabbies = new ArrayList<>();

	//constructor
	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();
		addEnemies();
	}
//    adds enemies
	private void addEnemies() {
		crabbies = LoadSave.GetCrabs();
		System.out.println("size of crabs: " + crabbies.size());
	}

	// updates the enemies
	public void update(int[][] lvlData, Player player) {
		boolean isAnyActive = false;
		for (Crabby c : crabbies)
			if (c.isActive()) {
				c.update(lvlData, player);
				isAnyActive = true;
			}
		if (!isAnyActive)
			playing.setLevelCompleted(true);
	}

	// draws the enemies
	public void draw(Graphics g, int xLvlOffset) {
		drawCrabs(g, xLvlOffset);
	}

	//drwas the crabs
	private void drawCrabs(Graphics g, int xLvlOffset) {
		for (Crabby c : crabbies) 
			if (c.isActive()) {
			g.drawImage(crabbyArr[c.getEnemyState()][c.getAniIndex()], (int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X+ c.flipX(), (int) c.getHitbox().y + 5 , CRABBY_WIDTH* c.flipW(),
					CRABBY_HEIGHT, null);
		//c.drawHitbox(g, xLvlOffset);
		//c.drawAttackBox(g, xLvlOffset);
		}
	}

	// checks for damages on the enemy
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (Crabby c : crabbies)
			if (c.isActive())
				if (attackBox.intersects(c.getHitbox())) {
					c.hurt(10);
					return;
				}
	}
	
	//loads the enemy images
	private void loadEnemyImgs() {
		crabbyArr = new BufferedImage[5][9];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
		for (int j = 0; j < crabbyArr.length; j++)
			for (int i = 0; i < crabbyArr[j].length; i++)
				crabbyArr[j][i] = temp.getSubimage(i * CRABBY_WIDTH_DEFAULT, j * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
	}
	//resets all the enemies
	public void resetAllEnemies() {
		for (Crabby c : crabbies)
			c.resetEnemy();
	}
}



