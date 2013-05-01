package main;

public class ActivityScore {
	// create activity score for players question answer
	private int activity; // activity, question or game
	private int activity_id; // which question or game
	private int choice; // question choice
	private int points; // points won
	private int difficulty; // dificulty of question
	private int elapsedtime; // time taken to answer
	private int overall; // overall points 
	private boolean correct; // correct or not
	// initiate activity score
	public ActivityScore(int activity, int points, int difficulty, int elapsedtime, int overall, boolean correct)
	{
		this.setActivity(activity);
		this.setPoints(points);
		this.setDifficulty(difficulty);
		this.setElapsedtime(elapsedtime);
		this.setOverall(overall);
		this.setCorrect(correct);
	}
	// get and set functions for activity score
	public int getActivity() {
		return activity;
	}

	public void setActivity(int activity) {
		this.activity = activity;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getElapsedtime() {
		return elapsedtime;
	}

	public void setElapsedtime(int elapsedtime) {
		this.elapsedtime = elapsedtime;
	}
	
	public boolean getCorrect() {
		return this.correct;
	}
	
	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public int getOverall() {
		return overall;
	}

	public void setOverall(int overall) {
		this.overall = overall;
	}

	public int getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(int activity_id) {
		this.activity_id = activity_id;
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}



}
