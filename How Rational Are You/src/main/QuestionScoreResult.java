package main;

public class QuestionScoreResult {
	// set up question point scale
	private final int easyPoints = 50;
	private final int mediumPoints = 100;
	private final int hardPoints = 150;
	
	private int noOfTotalQuestions; // number of questions asked
	private int noOfTotalQCorrect; // total of question correct
	private double totalQTimeBonusOverall; // total time bonus gained 
	private double totalQPointsOverall; // total points gained
	
	private int pointsAvailable;
	
	private double percentage; 
	
	public QuestionScoreResult(
			int noOfEasyQuestions,
			int noOfEasyQCorrect,
			int easyQTimeBonusAvg,
			double easyQTimeBonusOverall,
			int easyQPointsAvg,
			double easyQPointsOverall,
			int noOfMediumQuestions,
			int noOfMediumQCorrect,
			int mediumQTimeBonusAvg,
			double mediumQTimeBonusOverall,
			int mediumQPointsAvg,
			double mediumQPointsOverall,
			int noOfHardQuestions,
			int noOfHardQCorrect,
			int hardQTimeBonusAvg,
			double hardQTimeBonusOverall,
			int hardQPointsAvg,
			double hardQPointsOverall,
			int noOfTotalQuestions,
			int noOfTotalQCorrect,
			int totalQTimeBonusAvg,
			double totalQTimeBonusOverall,
			int totalQPointsAvg,
			double totalQPointsOverall)
	{
		this.setNoOfTotalQuestions(noOfTotalQuestions);
		this.setNoOfTotalQCorrect(noOfTotalQCorrect);
		this.setTotalQTimeBonusOverall(totalQTimeBonusOverall); 
		this.setTotalQPointsOverall(totalQPointsOverall);
		this.percentage = 0;
		this.setPointsAvailable((noOfEasyQuestions * easyPoints) + (noOfMediumQuestions * mediumPoints) + (noOfHardQuestions * hardPoints) + (55*noOfTotalQuestions));
	}
	
	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public double getTotalQTimeBonusOverall() {
		return totalQTimeBonusOverall;
	}

	public void setTotalQTimeBonusOverall(double totalQTimeBonusOverall) {
		this.totalQTimeBonusOverall = totalQTimeBonusOverall;
	}

	public int getNoOfTotalQuestions() {
		return noOfTotalQuestions;
	}

	public void setNoOfTotalQuestions(int noOfTotalQuestions) {
		this.noOfTotalQuestions = noOfTotalQuestions;
	}

	public int getNoOfTotalQCorrect() {
		return noOfTotalQCorrect;
	}

	public void setNoOfTotalQCorrect(int noOfTotalQCorrect) {
		this.noOfTotalQCorrect = noOfTotalQCorrect;
	}

	public int getPointsAvailable() {
		return pointsAvailable;
	}

	public void setPointsAvailable(int pointsAvailable) {
		this.pointsAvailable = pointsAvailable;
	}

	public double getTotalQPointsOverall() {
		return totalQPointsOverall;
	}

	public void setTotalQPointsOverall(double totalQPointsOverall) {
		this.totalQPointsOverall = totalQPointsOverall;
	}
}