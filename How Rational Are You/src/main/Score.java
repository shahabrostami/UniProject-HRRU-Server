package main;

public class Score implements Comparable<Score>{
	
	private int score;
	private String name;
	// record scores for the scoreboard
	// implements the comparison function to allow comparing
	// results whilst still keeping order
	public Score(String name, int score)
	{
		super();
		this.setScore(score);
		this.setName(name);
	}
	@Override
	public int compareTo(Score o) {
		int score = ((Score)o).getScore();
		return score - this.score;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

