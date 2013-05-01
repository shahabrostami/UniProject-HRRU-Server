package main;

public class UltScoreResult {

	double usTotal; // total score gained from ultimatums
	private double percentage; // percentage of main score from ultimatums
	private int pointsAvailable;
	
	public UltScoreResult(
			int noOfUltimatumScores,
			int noOfUltimatumScoreProp, 
			int noOfUltimatumScoreDec,
			int noOfPropSuccess, 
			int noOfDecSuccess,
			int usOverallValueAvgP, 
			int usPlayerPropAvgP, 
			int usPlayerDecAvgP,
			int usOverallValueAvgD, 
			int usPlayerPropAvgD, 
			int usPlayerDecAvgD,
			double usOverallValueTotalP, 
			double usPlayerPropTotalP2, 
			double usPlayerDecTotalP2,
			double usOverallValueTotalD, 
			double usPlayerPropTotalD2, 
			double usPlayerDecTotalD2,
			int usAvg, 
			double usTotal2)
	{
		this.setUsTotal(usTotal2);
		this.percentage = 0;
		this.setPointsAvailable((int) ((usOverallValueTotalP + usOverallValueTotalD) / noOfUltimatumScores + 0.5));
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
	public double getUsTotal() {
		return usTotal;
	}
	public void setUsTotal(double usTotal2) {
		this.usTotal = usTotal2;
	}
}
