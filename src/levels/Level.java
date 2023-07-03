package levels;

//levels in the game
public class Level {

	private int[][] lvlData;//level data

	public Level(int[][] lvlData) {//constructor
		this.lvlData = lvlData;
	}

	public int getSpriteIndex(int x, int y) {//returns the sprite in index
	    if (y < 0 || y >= lvlData.length || x < 0 || x >= lvlData[y].length) {
	        throw new IllegalArgumentException("Invalid x or y value");
	    }
	    return lvlData[y][x];
	}

	public int[][] getLevelData() {//returns level data
		return lvlData;
	}

}