package main;

public class BiddingScore {
	// create bidding score for bidding game
	private int item_id; // item bid on
	private int itemValue; // value of item
	private int playerBid; // this players bid
	private int otherPlayerBid; // other players bid
	private int playerWon; // who won
	private int amountWon; // amount won
	private boolean win; // did this player win
	
	// initiate default bidding score
	public BiddingScore(int playerBid, int otherPlayerBid, int playerWon, boolean win)
	{
		this.playerBid = playerBid;
		this.otherPlayerBid = otherPlayerBid;
		this.playerWon = playerWon;
		this.win = win;
	}
	// initiate new bidding score
	public BiddingScore(int item_id, int itemValue, int playerBid, int otherPlayerBid, int playerWon, int amountWon, boolean win)
	{
		this.item_id = item_id;
		this.itemValue = itemValue;
		this.playerBid = playerBid;
		this.otherPlayerBid = otherPlayerBid;
		this.playerWon = playerWon;
		this.amountWon = amountWon;
		this.win = win;
	}

	// set and get functions for bidding score
	public int getPlayerBid() {
		return playerBid;
	}

	public void setPlayerBid(int playerBid) {
		this.playerBid = playerBid;
	}

	public int getPlayerWon() {
		return playerWon;
	}

	public void setPlayerWon(int playerWon) {
		this.playerWon = playerWon;
	}

	public boolean isWin() {
		return win;
	}

	public void setWin(boolean win) {
		this.win = win;
	}

	public int getOtherPlayerBid() {
		return otherPlayerBid;
	}

	public void setOtherPlayerBid(int otherPlayerBid) {
		this.otherPlayerBid = otherPlayerBid;
	}

	public int getAmountWon() {
		return amountWon;
	}

	public void setAmountWon(int amountWon) {
		this.amountWon = amountWon;
	}

	public int getItemValue() {
		return itemValue;
	}

	public void setItemValue(int itemValue) {
		this.itemValue = itemValue;
	}


	public int getItem_id() {
		return item_id;
	}


	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}
}
