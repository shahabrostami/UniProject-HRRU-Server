package main;

public class TrustScore {
	private int playerGive; // who is first
	private int playerReturn; // who is second
	private int maxToGive; // amount first can give 
	private int maxToReturn; // amount second can return 
	private int playerGiveValue; // amount first gave
	private int playerReturnValue; // amount second returned
	private int multiplier; // multiplier of first value 
	private int playerGiveProfit; // how much did giver profit
	private int playerReturnProfit; // how much did returner profit
	
	// intialise default and initialise finished 
	public TrustScore(int playerGive, int playerReturn, int maxToGive, int playerGiveValue, int playerReturnValue, int multiplier)
	{
		this.setPlayerGive(playerGive);
		this.setPlayerReturn(playerReturn);
		this.setMaxToGive(maxToGive);
		this.setPlayerGiveValue(playerGiveValue);
		this.setPlayerReturnValue(playerReturnValue);
		this.setMultiplier(multiplier);
	}
	public TrustScore(int playerGive, int playerReturn, int maxToGive, int maxToReturn, 
			int playerGiveValue, int playerReturnValue, int multiplier, int playerGiveProfit, int playerReturnProfit)
	{
		this.setPlayerGive(playerGive);
		this.setPlayerReturn(playerReturn);
		this.setMaxToGive(maxToGive);
		this.setMaxToReturn(maxToReturn);
		this.setPlayerGiveValue(playerGiveValue);
		this.setPlayerReturnValue(playerReturnValue);
		this.setMultiplier(multiplier);
		this.setPlayerGiveProfit(playerGiveProfit);
		this.setPlayerReturnProfit(playerReturnProfit);
	}

	public int getPlayerGive() {
		return playerGive;
	}

	public void setPlayerGive(int playerGive) {
		this.playerGive = playerGive;
	}

	public int getPlayerReturn() {
		return playerReturn;
	}

	public void setPlayerReturn(int playerReturn) {
		this.playerReturn = playerReturn;
	}

	public int getMaxToGive() {
		return maxToGive;
	}

	public void setMaxToGive(int maxToGive) {
		this.maxToGive = maxToGive;
	}

	public int getPlayerGiveValue() {
		return playerGiveValue;
	}

	public void setPlayerGiveValue(int playerGiveValue) {
		this.playerGiveValue = playerGiveValue;
	}

	public int getPlayerReturnValue() {
		return playerReturnValue;
	}

	public void setPlayerReturnValue(int playerReturnValue) {
		this.playerReturnValue = playerReturnValue;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}
	public int getMaxToReturn() {
		return maxToReturn;
	}
	public void setMaxToReturn(int maxToReturn) {
		this.maxToReturn = maxToReturn;
	}
	public int getPlayerGiveProfit() {
		return playerGiveProfit;
	}
	public void setPlayerGiveProfit(int playerGiveProfit) {
		this.playerGiveProfit = playerGiveProfit;
	}
	public int getPlayerReturnProfit() {
		return playerReturnProfit;
	}
	public void setPlayerReturnProfit(int playerReturnProfit) {
		this.playerReturnProfit = playerReturnProfit;
	}
}
	
	
