package main;

import org.newdawn.slick.SlickException;

public class Question {

	private String[] description;
	private int amountOfAnswers;
	private int descriptionLines;
	private int answer;
	private int appeared;
	private int difficulty;
	private String[] choices;
	
	public Question (int id, int descriptionLines, int amountOfAnswers, int answer, int difficulty, String[] description, String[] choices) throws SlickException
	{
		this.description = description;
		this.descriptionLines = descriptionLines;
		this.amountOfAnswers = amountOfAnswers;
		this.answer = answer;
		this.choices = choices;
		this.difficulty = difficulty;
		this.appeared = 0;
	}

	public String[] getChoices() {
		return choices;
	}

	public int getAppeared() {
		return appeared;
	}

	public void setAppeared(int appeared) {
		this.appeared = appeared;
	}

	public int getAnswer() {
		return answer;
	}

	public int getAmountOfAnswers() {
		return amountOfAnswers;
	}

	public String[] getDescription() {
		return description;
	}
	
	public int getDescriptionLines() {
		return descriptionLines;
	}

	public int getDifficulty() {
		return difficulty;
	}

}
