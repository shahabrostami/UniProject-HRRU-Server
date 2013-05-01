package main;

public class TrustScoreResult {
	// stores the total results from trust games
	private double tsTotal, percentage; // total points gained, and percentage of score belonging to main score
	private int pointsAvailable;
	
	public TrustScoreResult(
			int noOfTrustScores,
			int noOfTrustScoreGiver,
			int noOfTrustScoreReturner,
			int tsPlayerGiveAvg, int  tsPlayerReturnAvg, int  tsPlayerReceiveAvg, int  tsPlayerGiveProfitAvg, int  tsPlayerReturnProfitAvg,
			double tsPlayerGiveTotal, double  tsPlayerReturnTotal2, double  tsPlayerReceiveTotal2, double  tsPlayerGiveProfitTotal2, double  tsPlayerReturnProfitTotal2,
			int tsAvg,
			double tsTotal)
	{
		this.setTsTotal(tsTotal);
		this.percentage = 0;
	}
	public double getPercentage() {
		return percentage;
	}
	
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	
	public int getPointsAvailable() {
		return pointsAvailable;
	}
	
	public void setPointsAvailable(int pointsAvailable) {
		this.pointsAvailable = pointsAvailable;
	}
	public double getTsTotal() {
		return tsTotal;
	}
	public void setTsTotal(double tsTotal) {
		this.tsTotal = tsTotal;
	}
}
