package main;

import java.io.*;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

public class QuestionList {
	// create a list array of questions
	private Question[] question_list;
	private String FILE_NAME;
	private static int number_of_questions;
	private int counter;
	
	public QuestionList(String file_name) throws SlickException, NumberFormatException, IOException
	{
		
		FILE_NAME = file_name;
		InputStream file_stream = ResourceLoader.getResourceAsStream("text/" + FILE_NAME);
		DataInputStream in = new DataInputStream(file_stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = br.readLine();
		
		number_of_questions = Integer.parseInt(strLine);
		HRRUClient.cs.no_of_questions = number_of_questions;
		question_list = new Question[number_of_questions];
		// set up question variables
		int question_id, number_of_answers, answer, difficulty;
		String file, answerFile;
		String choices[];
		Question new_question;
		counter = 0;
		
		while((strLine = br.readLine()) != null)
		{
			// parse the question id
			strLine = br.readLine();
			question_id = Integer.parseInt(strLine);
			// number of answers
			strLine = br.readLine();
			number_of_answers = Integer.parseInt(strLine);
			// id of answer in list supplied
			strLine = br.readLine();
			answer = Integer.parseInt(strLine);
			// difficulty level of question
			strLine = br.readLine();
			difficulty = Integer.parseInt(strLine);
			// read question file
			strLine = br.readLine();
			file = strLine;
			// read question file
			strLine = br.readLine();
			answerFile = strLine;
			// parase in the choices of answers
			choices = new String[number_of_answers];
			for(int i = 0; i < number_of_answers; i++)
			{
				strLine = br.readLine();
				choices[i] = strLine;
			}
			new_question = new Question(question_id, number_of_answers, answer, difficulty, file, answerFile, choices);
			question_list[counter] = new_question;
			counter++;
		}
		br.close();
	}
	
	
	public int getNumberOfQuestions()
	{
		return number_of_questions;
	}
	
	public Question[] getQuestion_list()
	{
		return question_list;
	}
}
