package main.grid;

import org.newdawn.slick.Image;

public interface GridSquare {

	public Image getImage();
	
	public static final int easyTile = 0;
	public static final int mediumTile = 1;
	public static final int hardTile = 2;
	public static final int gameTile = 3;
	public static int width = 36;
	public static int height = 36;

}
