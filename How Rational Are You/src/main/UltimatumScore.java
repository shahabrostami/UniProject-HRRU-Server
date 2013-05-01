package main;

public class UltimatumScore {
	private int playerProp; // who is proposing
	private int playerDec; // who is deciding
	private int playerPropValue; // how much for proposer
	private int playerDecValue; // how much for decider
	private int overallValue; // overall value available
	private boolean success; // was it successful
	
	public UltimatumScore(int playerProp, int playerDec, int playerPropValue, int playerDecValue, int overallValue, boolean success)
	{
		this.setPlayerProp(playerProp);
		this.setPlayerDec(playerDec);
		this.setPlayerPropValue(playerPropValue);
		this.setPlayerDecValue(playerDecValue);
		this.setOverallValue(overallValue);
		this.setSuccess(success);
	}

	public int getPlayerProp() {
		return playerProp;
	}

	public void setPlayerProp(int playerProp) {
		this.playerProp = playerProp;
	}

	public int getPlayerDec() {
		return playerDec;
	}

	public void setPlayerDec(int playerDec) {
		this.playerDec = playerDec;
	}

	public int getPlayerPropValue() {
		return playerPropValue;
	}

	public void setPlayerPropValue(int playerPropValue) {
		this.playerPropValue = playerPropValue;
	}

	public int getPlayerDecValue() {
		return playerDecValue;
	}

	public void setPlayerDecValue(int playerDecValue) {
		this.playerDecValue = playerDecValue;
	}

	public int getOverallValue() {
		return overallValue;
	}

	public void setOverallValue(int overallValue) {
		this.overallValue = overallValue;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
	
	
