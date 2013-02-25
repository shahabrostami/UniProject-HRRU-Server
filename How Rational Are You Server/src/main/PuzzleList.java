package main;

import java.io.*;

import org.newdawn.slick.SlickException;

public class PuzzleList {
	
	private Puzzle[] puzzle_list;
	private String FILE_NAME;
	private static int number_of_puzzles;
	private int counter;
	
	public PuzzleList(String file_name) throws SlickException, NumberFormatException, IOException
	{
		
		FILE_NAME = file_name;
		
		FileInputStream file_stream = new FileInputStream("res/text/" + FILE_NAME);
		DataInputStream in = new DataInputStream(file_stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strLine = br.readLine();
		
		number_of_puzzles = Integer.parseInt(strLine);
		puzzle_list = new Puzzle[number_of_puzzles];
		
		int puzzle_id, number_of_answers, answer, difficulty;
		String file;
		String choices[];
		Puzzle new_puzzle;
		counter = 0;
		
		while((strLine = br.readLine()) != null)
		{
			strLine = br.readLine();
			puzzle_id = Integer.parseInt(strLine);
			
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
			new_puzzle = new Puzzle(puzzle_id, number_of_answers, answer, difficulty, file, choices);
			puzzle_list[counter] = new_puzzle;
			counter++;
		}
		br.close();
	}
	
	public int getNumberOfPuzzles()
	{
		return number_of_puzzles;
	}
	
	public Puzzle[] getPuzzle_list()
	{
		return puzzle_list;
	}
}
