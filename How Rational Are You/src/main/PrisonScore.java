package main;

public class PrisonScore {
	// set up prisoner score for player
	private int playerChoice; // what was this players choicse
	private int otherPlayerChoice; // what was the other players choice
	private int playerProfit; // what profit this player made
	private int otherPlayerProfit; // what profit other player made
	private int playerTime; // time taken to decide 
	private int otherPlayerTime; // time taken for other player to decide 
	private boolean bPlayerProfit; // did they make a profit
	private boolean bOtherPlayerProfit; // did the other player make a profit
	// initialise the prison score
	public PrisonScore(int playerChoice, int otherPlayerChoice, int playerProfit, int otherPlayerProfit, 
			boolean bPlayerProfit, boolean bOtherPlayerProfit)
	{
		this.setPlayerChoice(playerChoice);
		this.setOtherPlayerChoice(otherPlayerChoice);
		this.setPlayerProfit(playerProfit);
		this.setOtherPlayerProfit(otherPlayerProfit);
		this.setbPlayerProfit(bPlayerProfit);
		this.setbOtherPlayerProfit(bOtherPlayerProfit);
	}
	// get and set functions for prison scores
	public int getPlayerChoice() {
		return playerChoice;
	}

	public void setPlayerChoice(int playerChoice) {
		this.playerChoice = playerChoice;
	}

	public int getOtherPlayerChoice() {
		return otherPlayerChoice;
	}

	public void setOtherPlayerChoice(int otherPlayerChoice) {
		this.otherPlayerChoice = otherPlayerChoice;
	}

	public int getPlayerProfit() {
		return playerProfit;
	}

	public void setPlayerProfit(int playerProfit) {
		this.playerProfit = playerProfit;
	}

	public int getOtherPlayerProfit() {
		return otherPlayerProfit;
	}

	public void setOtherPlayerProfit(int otherPlayerProfit) {
		this.otherPlayerProfit = otherPlayerProfit;
	}

	public boolean isbPlayerProfit() {
		return bPlayerProfit;
	}

	public void setbPlayerProfit(boolean bPlayerProfit) {
		this.bPlayerProfit = bPlayerProfit;
	}

	public boolean isbOtherPlayerProfit() {
		return bOtherPlayerProfit;
	}

	public void setbOtherPlayerProfit(boolean bOtherPlayerProfit) {
		this.bOtherPlayerProfit = bOtherPlayerProfit;
	}

	public int getPlayerTime() {
		return playerTime;
	}

	public void setPlayerTime(int playerTime) {
		this.playerTime = playerTime;
	}

	public int getOtherPlayerTime() {
		return otherPlayerTime;
	}

	public void setOtherPlayerTime(int otherPlayerTime) {
		this.otherPlayerTime = otherPlayerTime;
	}
}
