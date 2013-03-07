package main;

import com.esotericsoftware.kryonet.Connection;

public class ConnectionObject {
	private Connection p1;
	private Connection p2;
	private String p1name;
	private String p2name;
	private int p1position;
	private int p2position;
	private int p1tempvalue;
	private int p2tempvalue;
	private int p1tile;
	private int p2tile;
	private boolean p1ReadyToPlay;
	private boolean p2ReadyToPlay;
	private int playerTurnCounterTrust;
	private int playerTurnCounterUlt;
	private String password;
	private int sessionID;
	private boolean established;
	private int[] question_list_check1;
	private int[] question_list_check2;
	private boolean[] gamesDone;
	private boolean prisonerGameDone;
	private boolean bidGameDone;
	private boolean trustGameDone;
	private boolean ultGameDone;
	
	public ConnectionObject(Connection p1, String p1name, int sessionID, String password)
	{
		this.p1 = p1;
		this.p2 = null;
		this.p1name = p1name;
		this.p2name = null;
		this.password = password;
		this.sessionID = sessionID;
		this.established = false;
		this.setPlayerTurnCounterTrust(1);
		this.setPlayerTurnCounterUlt(1);
		this.question_list_check1 = new int[HRRUServer.question_list.getNumberOfQuestions()];
		this.question_list_check2 = new int[HRRUServer.question_list.getNumberOfQuestions()];
		
		for(int i = 0; i < question_list_check1.length; i++)
		{
			question_list_check1[i] = 0;
			question_list_check2[i] = 0;
		}
		
		
		this.setPrisonerGameDone(false);
		this.setBidGameDone(false);
		this.setTrustGameDone(false);
		this.setUltGameDone(false);
		this.gamesDone = new boolean[5];
		gamesDone[1] = this.bidGameDone;
		gamesDone[2] = this.trustGameDone;
		gamesDone[3] = this.trustGameDone;
		gamesDone[4] = this.ultGameDone;
	}
	
	public Connection getP1() {
		return p1;
	}

	public Connection getP2() {
		return p2;
	}

	public void setP2(Connection p2) {
		this.p2 = p2;
	}

	public int getSessionID() {
		return sessionID;
	}

	public String getPassword() {
		return password;
	}
	
	public void setEstablished() {
		this.established = true;
	}

	public boolean isEstablished() {
		return established;
	}
	
	public String getP1name() {
		return p1name;
	}

	public String getP2name() {
		return p2name;
	}

	public void setP2name(String p2name) {
		this.p2name = p2name;
	}
	
	public void setP1characterID(int p1characterID) {
	}

	public void setP2characterID(int p2characterID) {
	}

	public int getP1position() {
		return p1position;
	}

	public void updateP1position(int p1position) {
		this.p1position += p1position;
	}

	public int getP2position() {
		return p2position;
	}

	public void updateP2position(int p2position) {
		this.p2position += p2position;
	}

	public int getP1tile() {
		return p1tile;
	}

	public void setP1tile(int p1tile) {
		this.p1tile = p1tile;
	}

	public int getP2tile() {
		return p2tile;
	}

	public void setP2tile(int p2tile) {
		this.p2tile = p2tile;
	}

	public void setP1ReadyToPlay(boolean b) {
		this.p1ReadyToPlay = b;
	}
	
	public boolean getP1ReadyToPlay() {
		return this.p1ReadyToPlay;
	}
	
	public void setP2ReadyToPlay(boolean b) {
		this.p2ReadyToPlay = b;
	}
	
	public boolean getP2ReadyToPlay() {
		return this.p2ReadyToPlay;
	}
	
	public int[] getQuestion_list_check1() {
		return question_list_check1;
	}

	public void setQuestion_list_check1(int[] question_list_check1) {
		this.question_list_check1 = question_list_check1;
	}

	public int[] getQuestion_list_check2() {
		return question_list_check2;
	}

	public void setQuestion_list_check2(int[] question_list_check2) {
		this.question_list_check2 = question_list_check2;
	}

	public int getP1tempvalue() {
		return p1tempvalue;
	}

	public void setP1tempvalue(int p1tempvalue) {
		this.p1tempvalue = p1tempvalue;
	}

	public int getP2tempvalue() {
		return p2tempvalue;
	}

	public void setP2tempvalue(int p2tempvalue) {
		this.p2tempvalue = p2tempvalue;
	}

	public int getPlayerTurnCounterUlt() {
		return playerTurnCounterUlt;
	}

	public void setPlayerTurnCounterUlt(int playerTurnCounterUlt) {
		this.playerTurnCounterUlt = playerTurnCounterUlt;
	}

	public int getPlayerTurnCounterTrust() {
		return playerTurnCounterTrust;
	}

	public void setPlayerTurnCounterTrust(int playerTurnCounterTrust) {
		this.playerTurnCounterTrust = playerTurnCounterTrust;
	}

	public boolean[] getGamesDone() {
		return gamesDone;
	}

	public void setGamesDone(boolean[] gamesDone) {
		this.gamesDone = gamesDone;
	}

	public boolean isPrisonerGameDone() {
		return prisonerGameDone;
	}

	public void setPrisonerGameDone(boolean prisonerGameDone) {
		this.prisonerGameDone = prisonerGameDone;
	}

	public boolean isBidGameDone() {
		return bidGameDone;
	}

	public void setBidGameDone(boolean bidGameDone) {
		this.bidGameDone = bidGameDone;
	}

	public boolean isTrustGameDone() {
		return trustGameDone;
	}

	public void setTrustGameDone(boolean trustGameDone) {
		this.trustGameDone = trustGameDone;
	}

	public boolean isUltGameDone() {
		return ultGameDone;
	}

	public void setUltGameDone(boolean ultGameDone) {
		this.ultGameDone = ultGameDone;
	}

}
