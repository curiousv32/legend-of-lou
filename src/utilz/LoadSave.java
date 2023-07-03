
package utilz;

import java.awt.Color;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import entities.Crabby;
import main.Game;

import static utilz.Constants.EnemyConstants.CRABBY;

public class LoadSave {

	// animation file paths 
	public static final String LEVEL_ATLAS = "outside_sprites.png";
	public static final String LEVEL_ONE_DATA = "level_one_data_long.png";
	public static final String MENU_BUTTONS = "button_atlas.png";
	public static final String MENU_BACKGROUND = "menu_background.png";
	public static final String PLAYING_BG_IMG = "playing_bg_img.png";
	public static final String BIG_CLOUDS = "big_clouds.png";
	public static final String SMALL_CLOUDS = "small_clouds.png";
	public static final String CRABBY_SPRITE = "crabby_sprite.png";
	public static final String STATUS_BAR = "health_power_bar.png";
	public static final String COMPLETED_IMG = "completed_sprite.png";

	// animation file path for the player actions
	public static final String[] ANIMATION_FILE_PATHS = { 
			"idle.png",
			"run.png" ,
			"jump.png",
			"attack.png",
			"die.png",
			"glide.png"
			};

	
	public static BufferedImage GetSpriteAtlas(String fileName) {// processes images and gets the sprites
	    BufferedImage img = null;
	    InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
	    try {
	        img = ImageIO.read(is);
	        if (img == null) {
	            System.out.println("Failed to load sprite atlas: " + fileName);
	        } else {
	            System.out.println("Successfully loaded sprite atlas: " + fileName);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	        	
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return img;
	}
	
	

	public static ArrayList<Crabby> GetCrabs() {// stores and retrieves the crab
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		ArrayList<Crabby> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == CRABBY)
					list.add(new Crabby(i * Game.TILES_SIZE, j * Game.TILES_SIZE));
			}
		return list;

	}
	
	public static int[][] GetLevelData() {// gets the level data
		BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];

		for (int j = 0; j < img.getHeight(); j++)
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 48)
					value = 0;
				lvlData[j][i] = value;
			}
		return lvlData;

	}
}










