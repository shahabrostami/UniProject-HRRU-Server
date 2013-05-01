package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;

import main.textpage.AnswerPage.AnswerPageFrame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;

public class QuestionFeedback extends BasicTWLGameState {

	public Client client;
	DialogLayout questionPanel, questionMainPanel, leftPanel, rightPanel;
	Button btnBack, btnPrevious, btnNext;
	
	private int enterState;
	int gcw;
	int gch;
	private final int questionstats = 18;
	// Answer variables
	AnswerPageFrame textpageframe;
	private int current_question_id;
	private int question_counter = 0;
	private QuestionList question_list;
	private Question[] questions;
	private Question current_question;
	private String current_filename;
	private ActivityScore current_result;
	String[] difficulties;
	
	// Ticker variables
	private int titleFontSize = 60;
	private Font loadFont, loadTitleFont;
	private BasicFont titleFont;
	private String start_message = "";
	private String full_start_message = "QUESTION FEEDBACK...";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2 = 0;

	// GUI
	ToggleButton btnStats, btnFeedback;
	Label lblQuestion, lQuestion, lblCorrect, lCorrect, lblDifficulty, lDifficulty, lblPoints, lPoints;
	
	private ArrayList<ActivityScore> activityScores;
	private int playerID;
	
	void reset() {
		// Reset Question result variables
	}
	
	public QuestionFeedback(int main) {
		client = HRRUClient.conn.getClient();
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		enterState = 0;

		rootPane.removeAllChildren();
		// set player variables
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
			activityScores = HRRUClient.cs.getP1().getActivityScores();
		else
			activityScores = HRRUClient.cs.getP2().getActivityScores();
		
		// RESET VARIABLES
		start_message = "";
		full_start_message = "QUESTION FEEDBACK...";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		
		// reset button active
		btnFeedback.setActive(true);
		btnStats.setActive(false);
		
		rootPane.add(questionPanel);
		rootPane.add(questionMainPanel);
		rootPane.add(btnPrevious);
		rootPane.add(btnNext);
		rootPane.add(btnFeedback);
		rootPane.add(btnStats);
		rootPane.add(btnBack);
		rootPane.setTheme("");
	}
	
	void updateFeedback()
	{
		// Get next set of results
		current_result = activityScores.get(question_counter);
		current_question_id = current_result.getActivity_id();
		current_question = questions[current_question_id];
		current_filename = current_question.getAnswerFile();
		
		// recreate GUI
		questionPanel.removeChild(textpageframe);
		textpageframe = new AnswerPageFrame(current_filename);
		textpageframe.setSize(680, 350);
		textpageframe.setPosition(15,15);
		textpageframe.setTheme("textpageframeanswer");
		questionPanel.setHorizontalGroup(questionPanel.createParallelGroup()
        		.addGroup(questionPanel.createSequentialGroup(textpageframe)));
        
        questionPanel.setVerticalGroup(questionPanel.createSequentialGroup()
        		.addGroup(questionPanel.createParallelGroup(textpageframe)));
        
        // set up new variables
		lQuestion.setText(""+ (question_counter+1));
		int difficulty = current_result.getDifficulty();
		lDifficulty.setText(""+ difficulties[difficulty]);
		int points = current_result.getOverall();
		lPoints.setText(points + "");
		if(current_result.getCorrect())
			lCorrect.setText("Correct");
		else
			lCorrect.setText("Incorrect");

	}
	
	void emulateNext()
	{
		if(question_counter < (activityScores.size()-1))
		{
			question_counter++;
			btnPrevious.setVisible(true);
			updateFeedback();
		}
		if(question_counter == (activityScores.size()-1))
			btnNext.setVisible(false);
	}
	
	void emulatePrevious()
	{
		if(question_counter > 0)
		{
			question_counter--;
			btnNext.setVisible(true);
			updateFeedback();
		}
		if(question_counter == 0)
			btnPrevious.setVisible(false);
	}

	@Override
	protected RootPane createRootPane() {
		assert rootPane == null : "RootPane already created";

		RootPane rp = new RootPane(this);
		rp.setTheme("");
		rp.getOrCreateActionMap().addMapping(this);
		return rp;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		gcw = gc.getWidth();
		gch = gc.getHeight();
		difficulties = new String[4];
		difficulties[1] = "Easy";
		difficulties[2] = "Medium";
		difficulties[3] = "Hard";
		// font variables
		try {
			loadFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
					      org.newdawn.slick.util.ResourceLoader.getResourceAsStream("font/visitor2.ttf"));
		} catch (FontFormatException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
		loadTitleFont = loadFont.deriveFont(Font.BOLD,titleFontSize);
		titleFont = new BasicFont(loadTitleFont);
		
		btnBack = new Button("Back");
		btnBack.setSize(700, 30);
		btnBack.setPosition(50,550);
		btnBack.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 1;
			}
		});
		btnBack.setTheme("menubutton");
		// btnstats
		btnStats = new ToggleButton("Statistics");
		btnStats.setSize(340, 30);
		btnStats.setPosition(50,510);
		btnStats.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 2;
			}
		});
		btnStats.setTheme("menubutton");
		// btn feedback
		btnFeedback = new ToggleButton("Feedback");
		btnFeedback.setSize(340, 30);
		btnFeedback.setPosition(410,510);
		btnFeedback.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 3;
			}
		});
		btnFeedback.setTheme("menubutton");
		btnNext = new Button(">");
		btnNext.setSize(85, 82);
		btnNext.setPosition(665,410);
		btnNext.addCallback(new Runnable() {
			@Override
			public void run() {
				emulateNext();
			}
		});
		btnNext.setTheme("menubutton");
		btnPrevious = new Button("<");
		btnPrevious.setSize(85, 82);
		btnPrevious.setPosition(50,410);
		btnPrevious.addCallback(new Runnable() {
			@Override
			public void run() {
				emulatePrevious();
			}
		});
		btnPrevious.setTheme("menubutton");
		btnPrevious.setVisible(false);
		questionPanel = new DialogLayout();
        questionPanel.setPosition(50,50);
        questionPanel.setSize(710, 350);
        questionPanel.setTheme("questionfeedback-panel");
        
        // Feedback GUI window
        questionMainPanel = new DialogLayout();
        questionMainPanel.setPosition(135,410);
        questionMainPanel.setSize(510, 68);
        questionMainPanel.setTheme("questionfeedbackmain-panel");
			
		// RESET VARIABLES
		reset();
		// set player variable
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
			activityScores = HRRUClient.cs.getP1().getActivityScores();
		else
			activityScores = HRRUClient.cs.getP2().getActivityScores();
		
		// Sort answer panel
		try {
			question_list = new QuestionList("Question.txt");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		questions = question_list.getQuestion_list();		
		
		// answer question
		if(activityScores.size() > 0)
		{
			current_result = activityScores.get(0);
			current_question = questions[current_result.getActivity_id()];
			current_filename = current_question.getAnswerFile();
			if(activityScores.size() == 1)
				btnNext.setVisible(false);
			textpageframe = new AnswerPageFrame(current_filename);
			textpageframe.setSize(680, 350);
			textpageframe.setPosition(15,15);
			textpageframe.setTheme("textpageframeanswer");
			
	        questionPanel.setHorizontalGroup(questionPanel.createParallelGroup()
	        		.addGroup(questionPanel.createSequentialGroup(textpageframe)));
	        
	        questionPanel.setVerticalGroup(questionPanel.createSequentialGroup()
	        		.addGroup(questionPanel.createParallelGroup(textpageframe)));
	        
	        lblQuestion = new Label("Question: ");
			lQuestion = new Label("2");
			lblQuestion.setTheme("questionatari10");
			lQuestion.setTheme("questionatari8i");
			
			lblDifficulty = new Label("Difficulty: ");
			lDifficulty = new Label("2");
			lblDifficulty.setTheme("questionatari10");
			lDifficulty.setTheme("questionatari8i");
			
			lblPoints = new Label("Points Gained: ");
			lPoints = new Label("2");
			lblPoints.setTheme("questionatari10"); 
			lPoints.setTheme("questionatari8i");
			
	        lblCorrect = new Label("Answer: ");
			lCorrect = new Label("2");
			lblCorrect.setTheme("questionatari10");
			lCorrect.setTheme("questionatari8i");
			
			lQuestion.setText(""+ (question_counter+1));
			int difficulty = current_result.getDifficulty();
			lDifficulty.setText(""+ difficulties[difficulty]);
			int points = current_result.getPoints();
			lPoints.setText(points + "");
			if(current_result.getCorrect())
				lCorrect.setText("Correct");
			else
				lCorrect.setText("Incorrect");
			leftPanel = new DialogLayout();
			rightPanel = new DialogLayout();
			
			DialogLayout.Group LQLLeft = leftPanel.createParallelGroup(lblQuestion, lblDifficulty);
			DialogLayout.Group LQLRight = leftPanel.createParallelGroup(lQuestion, lDifficulty);
			
			leftPanel.setHorizontalGroup(leftPanel.createParallelGroup()
	        		.addGroup(leftPanel.createSequentialGroup(LQLLeft, LQLRight)));
	        
			leftPanel.setVerticalGroup(leftPanel.createSequentialGroup()
	        		.addGap(12).addGroup(leftPanel.createParallelGroup(lblQuestion, lQuestion))
	        		.addGap(10).addGroup(leftPanel.createParallelGroup(lblDifficulty, lDifficulty)));
			
			DialogLayout.Group LQRLeft = rightPanel.createParallelGroup(lblPoints, lblCorrect);
			DialogLayout.Group LQRRight = rightPanel.createParallelGroup(lPoints, lCorrect);
			
			rightPanel.setHorizontalGroup(rightPanel.createParallelGroup()
					.addGap(10).addGroup(rightPanel.createSequentialGroup(LQRLeft, LQRRight)));
	        
			rightPanel.setVerticalGroup(rightPanel.createSequentialGroup()
					.addGap(12).addGroup(rightPanel.createParallelGroup(lblPoints, lPoints))
	        		.addGap(10).addGroup(rightPanel.createParallelGroup(lblCorrect, lCorrect)));
			
			DialogLayout.Group hLeftPanel = questionMainPanel.createSequentialGroup(leftPanel).addGap(50);
			DialogLayout.Group hRightPanel = questionMainPanel.createSequentialGroup(rightPanel);
				
			questionMainPanel.setHorizontalGroup(questionMainPanel.createParallelGroup()
					.addGroup(questionMainPanel.createSequentialGroup(hLeftPanel, hRightPanel)));
			
			questionMainPanel.setVerticalGroup(questionMainPanel.createSequentialGroup()
					.addGroup(questionMainPanel.createParallelGroup(leftPanel, rightPanel)));
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawImage(new Image("simple/questionbg.png"), 0, 0);
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, 50, 25);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		
		if(enterState == 1)
			sbg.enterState(17);
		else if(enterState == 2)
			sbg.enterState(questionstats);
		
		clock3 += delta;
		clock2 += delta;
		// full message ticker
		if(clock3 > 100){
			if(full_start_counter < full_start_message.length())
			{
				start_message += full_start_message.substring(full_start_counter, full_start_counter+1);
				full_start_counter++;
				clock3-=100;
			}
		}
		// ticker symbol
		if(clock2>999)
		{
			clock2-=1000;
			if(tickerBoolean) 
			{
				ticker = "|";
				tickerBoolean = false;
			}
			else
			{
				ticker = "";
				tickerBoolean = true;
			}
		}
	}

	@Override
	public int getID() {
		return 19;
	}

}