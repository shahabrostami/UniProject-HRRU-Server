package main.board;

import main.HRRUClient;
import main.grid.*;

import org.newdawn.slick.SlickException;

public class Board {
	private int size;
	private int scale;
	// set up board coordinates
	private int shiftx;
	private int shifty;
	private int middlex = 600/2 + 150;
	private int middley = 450/2 + 50;
	
	public GridSquareContainer[] gridSquares;
	private int counter = 0;
	
	public Board(int scale) throws SlickException
	{
		// set up size and coordinates
		this.size = (scale*4)-4;
		this.setScale(scale);
		int tempxpos = 0;
		int tempypos = 0;
		
		shiftx = middlex - ((scale*GridSquare.width)/2);
		shifty = middley -((scale*GridSquare.height)/2);
		
		
		gridSquares = new GridSquareContainer[size];
		
		int[] tileOrder = HRRUClient.cs.getBoard();
		
		// create board grid, row by row starting at top left
		for(int row1 = 0; row1 < scale-1; row1++)
		{
			gridSquares[counter] = new GridSquareContainer(tileOrder[counter], shiftx+tempxpos, shifty);
			tempxpos+=GridSquare.width;
			counter++;
		}
		
		for(int row2 = 0; row2 < scale-1; row2++)
		{
			gridSquares[counter] = new GridSquareContainer(tileOrder[counter], shiftx+tempxpos, shifty+tempypos);
			tempypos+=GridSquare.height;
			counter++;
		}
		
		for(int row3 = 0; row3 < scale-1; row3++)
		{
			gridSquares[counter] = new GridSquareContainer(tileOrder[counter], shiftx+tempxpos, shifty+tempypos);
			tempxpos-=GridSquare.width;
			counter++;
		}
		
		for(int row4 = 0; row4 < scale-1; row4++)
		{
			gridSquares[counter] = new GridSquareContainer(tileOrder[counter], shiftx, shifty+tempypos);
			tempypos-=GridSquare.height;
			counter++;
		}
	
		
	}
	
	public GridSquareContainer[] getGridSquares()
	{
		return this.gridSquares;
	}
	
	public int getCounter() 
	{
		return this.counter;
	}
	
	public int getSize() 
	{
		return this.size;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

}
