package main;

import org.newdawn.slick.SlickException;

public class Question {
	// this is the question object that holds each individual question attributes
	private String file; // question file name
	private String answerFile; // answer file name 
	private int amountOfAnswers; // amount of answers for question 
	private int answer; // answer id for answer
	private int appeared; // has the question appeared
	private int difficulty; // whats the difficulty
	private String[] choices; // what are the choices
	// initialise question
	public Question (int id, int amountOfAnswers, int answer, int difficulty, String description, String answerDescription, String[] choices) throws SlickException
	{
		this.file = description;
		this.setAnswerFile(answerDescription);
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

	public String getFile() {
		return file;
	}
	
	public int getDifficulty() {
		return difficulty;
	}

	public String getAnswerFile() {
		return answerFile;
	}

	public void setAnswerFile(String answerFile) {
		this.answerFile = answerFile;
	}

}
