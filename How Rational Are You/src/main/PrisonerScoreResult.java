package main;

public class PrisonerScoreResult {
	// set up prisoner score result
	private int noOfPrisonScores; // number of prison scores
	private int noOfPrisonScoreCoop; // number of games cooperated
	private int noOfPrisonScoreBetray; // number of games betrayed
	private double psTotal; // total points 
	private int pointsAvailable; // total points that would be available
	
	private double percentage; // % of game score from prisoner
	// initiate prisoner score results
	public PrisonerScoreResult(
			int noOfPrisonScores,
			int noOfPrisonScoreCoop,
			int noOfPrisonScoreBetray,
			int noOfPrisonScoreCC, 
			int noOfPrisonScoreCB, 
			int noOfPrisonScoreBC, 
			int noOfPrisonScoreBB,
			int psPlayerProfitCC, 
			int psOtherPlayerProfitCC,
			int psPlayerProfitBC,  
			int psOtherPlayerProfitBC,
			int psPlayerProfitCB,  
			int psOtherPlayerProfitCB,
			int psPlayerProfitBB,  
			int psOtherPlayerProfitBB,
			int psCAvg,  
			int psAvg,  
			int psBAvg,
			double psTotal, 
			double psCTotal, 
			double psBTotal)
	{
				this.setNoOfPrisonScores(noOfPrisonScores);
				this.setNoOfPrisonScoreCoop(noOfPrisonScoreCoop);
				this.setNoOfPrisonScoreBetray(noOfPrisonScoreBetray);
				this.setPsTotal(psTotal);
				this.percentage = 0;
				this.setPointsAvailable((noOfPrisonScores * 150));
	}
	public double getPercentage() {
		return percentage;
	}
	// get and set variables for results
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public int getNoOfPrisonScoreCoop() {
		return noOfPrisonScoreCoop;
	}
	public int setNoOfPrisonScoreCoop(int noOfPrisonScoreCoop) {
		this.noOfPrisonScoreCoop = noOfPrisonScoreCoop;
		return noOfPrisonScoreCoop;
	}
	public int getNoOfPrisonScores() {
		return noOfPrisonScores;
	}
	public void setNoOfPrisonScores(int noOfPrisonScores) {
		this.noOfPrisonScores = noOfPrisonScores;
	}
	public int getPointsAvailable() {
		return pointsAvailable;
	}
	public void setPointsAvailable(int pointsAvailable) {
		this.pointsAvailable = pointsAvailable;
	}
	public int getNoOfPrisonScoreBetray() {
		return noOfPrisonScoreBetray;
	}
	public void setNoOfPrisonScoreBetray(int noOfPrisonScoreBetray) {
		this.noOfPrisonScoreBetray = noOfPrisonScoreBetray;
	}
	public double getPsTotal() {
		return psTotal;
	}
	public void setPsTotal(double psTotal) {
		this.psTotal = psTotal;
	}
}

