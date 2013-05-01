package main.grid;

import org.newdawn.slick.SlickException;

public class GridSquareContainer {
	// initialise the grid square container, contains all other grid squares
	private int id = 0;
	private int x;
	private int y;
	private int tile_type;
	public GridSquare gridSquare;
	
	public GridSquareContainer(int tileType, int x, int y) throws SlickException
	{
		// check the tile id and coordinates
		this.setId(this.getId() + 1);
		this.x = x;
		this.y = y;
		this.tile_type = tileType;
		
		// depending on the tile, create the required grid square and store it at this position
		if(this.tile_type == GridSquare.easyTile)
		{
			gridSquare = new QuestionGridSquare(GridSquare.easyTile);
		}
		else if(this.tile_type == GridSquare.mediumTile)
		{
			gridSquare = new QuestionGridSquare(GridSquare.mediumTile);
		}
		else if(this.tile_type == GridSquare.hardTile)
		{
			gridSquare = new QuestionGridSquare(GridSquare.hardTile);
		}
		else if(this.tile_type == GridSquare.gameTile)
		{
			gridSquare = new GameGridSquare();
		}
	}
	
	public void setx(int x) {
		this.x = x;
	}
	
	public void sety(int y) {
		this.y = y;
	}
	
	public int getx() {
		return x;
	}
	
	public int gety() {
		return y;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTileType() {
		return tile_type;
	}
}
