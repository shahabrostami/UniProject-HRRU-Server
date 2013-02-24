package main;

import java.io.*;

import org.newdawn.slick.SlickException;

public class QuestionList {
	
	private Question[] question_list;
	private String FILE_NAME;
	private static int number_of_questions;
	private int counter;
	
	public QuestionList(String file_name) throws SlickException, NumberFormatException, IOException
	{
		
		FILE_NAME = file_name;
		
		FileInputStream file_stream = new FileInputStream("res/text/" + FILE_NAME);
		DataInputStream in = new DataInputStream(file_stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = br.readLine();
		
		number_of_questions = Integer.parseInt(strLine);
		question_list = new Question[number_of_questions];
		
		int question_id, number_of_answers, answer, difficulty;
		String file;
		String choices[];
		Question new_question;
		counter = 0;
		
		while((strLine = br.readLine()) != null)
		{
			strLine = br.readLine();
			question_id = Integer.parseInt(strLine);
			
			strLine = br.readLine();
			number_of_answers = Integer.parseInt(strLine);
			
			strLine = br.readLine();
			answer = Integer.parseInt(strLine);
			
			strLine = br.readLine();
			difficulty = Integer.parseInt(strLine);
			
			strLine = br.readLine();
			file = strLine;
			
			choices = new String[number_of_answers];
			for(int i = 0; i < number_of_answers; i++)
			{
				strLine = br.readLine();
				System.out.println(strLine);
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
