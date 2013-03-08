package main;

public class Score implements Comparable<Score>{
	
	private int score;
	private String name;

	public Score(String name, int score)
	{
		super();
		this.setScore(score);
		this.setName(name);
	}
	@Override
	public int compareTo(Score o) {
	// 	TODO Auto-generated method stub
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

