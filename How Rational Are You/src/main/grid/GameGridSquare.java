package main.grid;

import org.newdawn.slick.*;

public class GameGridSquare implements GridSquare{
	// game grid square represents a game
	private Image tile_image;
	// get game grid image
	SpriteSheet sheet = new SpriteSheet("img/tileset/tile2.png", width,height);
	private Image game_tile_image = sheet.getSprite(2,0);

	
	public GameGridSquare() throws SlickException {
		this.tile_image = game_tile_image;
		
	}
	
	public Image getImage()
	{
		return tile_image;
	}
}
