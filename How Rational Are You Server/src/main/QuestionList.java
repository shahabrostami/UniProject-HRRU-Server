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
		// receive question HTML file and create question object
		FILE_NAME = file_name;
		InputStream file_stream = ResourceLoader.getResourceAsStream("text/" + FILE_NAME);
		DataInputStream in = new DataInputStream(file_stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = br.readLine();
		// create number of question variables
		number_of_questions = Integer.parseInt(strLine);
		question_list = new Question[number_of_questions];
		// initiate question variables
		int question_id, number_of_answers, answer, difficulty;
		String file;
		String choices[];
		Question new_question;
		counter = 0;
		// read through entire question file and record each question
		while((strLine = br.readLine()) != null)
		{
			strLine = br.readLine();
			question_id = Integer.parseInt(strLine); // record question id
			
			strLine = br.readLine();
			number_of_answers = Integer.parseInt(strLine); // record no of answers
			
			strLine = br.readLine();
			answer = Integer.parseInt(strLine); // record answer id
			
			strLine = br.readLine();
			difficulty = Integer.parseInt(strLine); // record difficulty
			
			strLine = br.readLine();
			file = strLine; // record question and answer file
			
			// record choices
			choices = new String[number_of_answers];
			for(int i = 0; i < number_of_answers; i++)
			{
				strLine = br.readLine();
				choices[i] = strLine;
			}
			new_question = new Question(question_id, number_of_answers, answer, difficulty, file, choices);
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
