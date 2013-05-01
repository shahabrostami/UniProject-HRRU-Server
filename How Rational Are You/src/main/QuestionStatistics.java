package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;

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

public class QuestionStatistics extends BasicTWLGameState {

	public Client client;
	DialogLayout questionPanel, leftPanel, rightPanel;
	Button btnBack;
	
	private final int questionfeedback = 19;
	private int enterState;
	int gcw;
	int gch;
	
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
	
	// Questions UI
	ToggleButton btnStats, btnFeedback;
	Label lQAmount, lQEasy, lQEasyCorrect, lQEasyTimeBonusAvg, lQEasyTimeBonusOverall, lQEasyPointsAvg, lQEasyPointsOverall;
	Label lQMedium, lQMediumCorrect, lQMediumTimeBonusAvg, lQMediumTimeBonusOverall, lQMediumPointsAvg, lQMediumPointsOverall;
	Label lQHard, lQHardCorrect, lQHardTimeBonusAvg, lQHardTimeBonusOverall, lQHardPointsAvg, lQHardPointsOverall;
	Label lQTotal, lQTotalCorrect, lQTotalTimeBonusAvg, lQTotalTimeBonusOverall, lQTotalPointsAvg, lQTotalPointsOverall;
	
	Label lQAmountR, lQEasyR, lQEasyCorrectR, lQEasyTimeBonusAvgR, lQEasyTimeBonusOverallR, lQEasyPointsAvgR, lQEasyPointsOverallR;
	Label lQMediumR, lQMediumCorrectR, lQMediumTimeBonusAvgR, lQMediumTimeBonusOverallR, lQMediumPointsAvgR, lQMediumPointsOverallR;
	Label lQHardR, lQHardCorrectR, lQHardTimeBonusAvgR, lQHardTimeBonusOverallR, lQHardPointsAvgR, lQHardPointsOverallR;
	Label lQTotalR, lQTotalCorrectR, lQTotalTimeBonusAvgR, lQTotalTimeBonusOverallR, lQTotalPointsAvgR, lQTotalPointsOverallR;

	
	// Question variables
	private ActivityScore activityScore;
	private ArrayList<ActivityScore> activityScores;
	// Question Statistics
	private int noOfEasyQuestions;
	private int noOfEasyQCorrect;
	private int easyQTimeBonusAvg;
	private double easyQTimeBonusOverall;
	private int easyQPointsAvg;
	private double easyQPointsOverall;
	
	private int noOfMediumQuestions;
	private int noOfMediumQCorrect;
	private int mediumQTimeBonusAvg;
	private double mediumQTimeBonusOverall;
	private int mediumQPointsAvg;
	private double mediumQPointsOverall;
	
	private int noOfHardQuestions;
	private int noOfHardQCorrect;
	private int hardQTimeBonusAvg;
	private double hardQTimeBonusOverall;
	private int hardQPointsAvg;
	private double hardQPointsOverall;
	
	private int noOfTotalQuestions;
	private int noOfTotalQCorrect;
	private int totalQTimeBonusAvg;
	private double totalQTimeBonusOverall;
	private int totalQPointsAvg;
	private double totalQPointsOverall;
	
	private int playerID;
	private int playerScore;
	
	void reset() {
		// Reset Question result variables
	    noOfEasyQuestions = 0; easyQTimeBonusAvg = 0; easyQTimeBonusOverall = 0; easyQPointsAvg = 0; easyQPointsOverall = 0;
	    noOfMediumQuestions = 0; mediumQTimeBonusAvg = 0; mediumQTimeBonusOverall = 0; mediumQPointsAvg = 0; mediumQPointsOverall = 0;
	    noOfHardQuestions = 0; hardQTimeBonusAvg = 0; hardQTimeBonusOverall = 0; hardQPointsAvg = 0; hardQPointsOverall = 0;
		noOfTotalQuestions = 0; totalQTimeBonusAvg = 0; totalQTimeBonusOverall = 0; totalQPointsAvg = 0; totalQPointsOverall = 0;
	}
	
	public QuestionStatistics(int main) {
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
		{
			playerScore = HRRUClient.cs.getP1().getScore();
			activityScores = HRRUClient.cs.getP1().getActivityScores();
		}
		else
		{
			playerScore = HRRUClient.cs.getP2().getScore();
			activityScores = HRRUClient.cs.getP2().getActivityScores();
		}
		
		// RESET VARIABLES
		start_message = "";
		full_start_message = "QUESTION STATISTICS...";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		enterState = 0;
		
		// reset button active
		btnFeedback.setActive(false);
		btnStats.setActive(true);
		
		rootPane.add(questionPanel);
		rootPane.add(btnFeedback);
		rootPane.add(btnStats);
		rootPane.add(btnBack);
		rootPane.setTheme("");
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
		
		// btn back
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
		
		// question panel
		questionPanel = new DialogLayout();
        questionPanel.setTheme("questionstat-panel");
        questionPanel.setSize(630, 270);
        questionPanel.setPosition(50,130);
        
		lQAmount = new Label("Number of Questions: ");
		lQAmountR = new Label("");
		lQAmount.setTheme("questionatari8");
		lQAmountR.setTheme("questionatari8r");
		
		lQEasy = new Label("Easy: 5 Questions");
		lQEasyR = new Label("");
		lQEasy.setTheme("questionatari9y");
		lQEasyR.setTheme("questionatari9y");
		
		lQEasyCorrect = new Label("Correctly Answered: ");
		lQEasyCorrectR = new Label("");
		lQEasyCorrect.setTheme("questionatari8r");
		lQEasyCorrectR.setTheme("questionatari8r");
		
		lQEasyTimeBonusAvg = new Label("Time Bonus Avg: ");
		lQEasyTimeBonusAvgR = new Label("");
		lQEasyTimeBonusAvg.setTheme("questionatari8r");
		lQEasyTimeBonusAvgR.setTheme("questionatari8r");
		
		lQEasyTimeBonusOverall = new Label("Time Bonus Total: ");
		lQEasyTimeBonusOverallR = new Label("");
		lQEasyTimeBonusOverall.setTheme("questionatari8r");
		lQEasyTimeBonusOverallR.setTheme("questionatari8r");
		
		lQEasyPointsAvg = new Label("Points Avg: ");
		lQEasyPointsAvgR = new Label("");
		lQEasyPointsAvg.setTheme("questionatari8r");
		lQEasyPointsAvgR.setTheme("questionatari8r");
		
		lQEasyPointsOverall = new Label("Points Total: ");
		lQEasyPointsOverallR = new Label("");
		lQEasyPointsOverall.setTheme("questionatari8r");
		lQEasyPointsOverallR.setTheme("questionatari8r");
		
		lQMedium = new Label("Medium: 5 Questions");
		lQMediumR = new Label("");
		lQMedium.setTheme("questionatari9y");
		lQMediumR.setTheme("questionatari9y");
		
		lQMediumCorrect = new Label("Correctly Answered: ");
		lQMediumCorrectR = new Label("");
		lQMediumCorrect.setTheme("questionatari8r");
		lQMediumCorrectR.setTheme("questionatari8r");
		
		lQMediumTimeBonusAvg = new Label("Time Bonus Avg: ");
		lQMediumTimeBonusAvgR = new Label("");
		lQMediumTimeBonusAvg.setTheme("questionatari8r");
		lQMediumTimeBonusAvgR.setTheme("questionatari8r");
		
		lQMediumTimeBonusOverall = new Label("Time Bonus Total: ");
		lQMediumTimeBonusOverallR = new Label("555");
		lQMediumTimeBonusOverall.setTheme("questionatari8r");
		lQMediumTimeBonusOverallR.setTheme("questionatari8r");
		
		lQMediumPointsAvg = new Label("Points Avg: ");
		lQMediumPointsAvgR = new Label("");
		lQMediumPointsAvg.setTheme("questionatari8r");
		lQMediumPointsAvgR.setTheme("questionatari8r");
		
		lQMediumPointsOverall = new Label("Points Total: ");
		lQMediumPointsOverallR = new Label("");
		lQMediumPointsOverall.setTheme("questionatari8r");
		lQMediumPointsOverallR.setTheme("questionatari8r");
		
		lQHard = new Label("Hard: 5 Questions");
		lQHardR = new Label("");
		lQHard.setTheme("questionatari9y");
		lQHardR.setTheme("questionatari9y");
		
		lQHardCorrect = new Label("Correctly Answered: ");
		lQHardCorrectR = new Label("");
		lQHardCorrect.setTheme("questionatari8r");
		lQHardCorrectR.setTheme("questionatari8r");
		
		lQHardTimeBonusAvg = new Label("Time Bonus Avg: ");
		lQHardTimeBonusAvgR = new Label("");
		lQHardTimeBonusAvg.setTheme("questionatari8r");
		lQHardTimeBonusAvgR.setTheme("questionatari8r");
		
		lQHardTimeBonusOverall = new Label("Time Bonus Total: ");
		lQHardTimeBonusOverallR = new Label("");
		lQHardTimeBonusOverall.setTheme("questionatari8r");
		lQHardTimeBonusOverallR.setTheme("questionatari8r");
		
		lQHardPointsAvg = new Label("Points Avg: ");
		lQHardPointsAvgR = new Label("");
		lQHardPointsAvg.setTheme("questionatari8r");
		lQHardPointsAvgR.setTheme("questionatari8r");
		
		lQHardPointsOverall = new Label("Points Total: ");
		lQHardPointsOverallR = new Label("");
		lQHardPointsOverall.setTheme("questionatari8r");
		lQHardPointsOverallR.setTheme("questionatari8r");
		
		lQTotal = new Label("Total: 5 Questions");
		lQTotalR = new Label("");
		lQTotal.setTheme("questionatari9y");
		lQTotalR.setTheme("questionatari9y");
		
		lQTotalCorrect = new Label("Correctly Answered: ");
		lQTotalCorrectR = new Label("");
		lQTotalCorrect.setTheme("questionatari8r");
		lQTotalCorrectR.setTheme("questionatari8r");
		
		lQTotalTimeBonusAvg = new Label("Time Bonus Avg: ");
		lQTotalTimeBonusAvgR = new Label("");
		lQTotalTimeBonusAvg.setTheme("questionatari8r");
		lQTotalTimeBonusAvgR.setTheme("questionatari8r");
		
		lQTotalTimeBonusOverall = new Label("Time Bonus Total: ");
		lQTotalTimeBonusOverallR = new Label("");
		lQTotalTimeBonusOverall.setTheme("questionatari8r");
		lQTotalTimeBonusOverallR.setTheme("questionatari8r");
		
		lQTotalPointsAvg = new Label("Points Avg: ");
		lQTotalPointsAvgR = new Label("");
		lQTotalPointsAvg.setTheme("questionatari8r");
		lQTotalPointsAvgR.setTheme("questionatari8r");
		
		lQTotalPointsOverall = new Label("Points Total: ");
		lQTotalPointsOverallR = new Label("");
		lQTotalPointsOverall.setTheme("questionatari8");
		lQTotalPointsOverallR.setTheme("questionatari8");
		
		leftPanel = new DialogLayout();
		rightPanel = new DialogLayout();
		
		DialogLayout.Group hQLLeft = leftPanel.createParallelGroup(lQAmount, lQEasy, lQMedium);
	    DialogLayout.Group hQLRight = leftPanel.createParallelGroup(lQAmountR, lQEasyR, lQMediumR);
	        
	    DialogLayout.Group hQLLabel = leftPanel.createParallelGroup(
	        		lQEasyCorrect, lQEasyTimeBonusAvg, lQEasyTimeBonusOverall, lQEasyPointsAvg, lQEasyPointsOverall, 
	        		lQMediumCorrect, lQMediumTimeBonusAvg, lQMediumTimeBonusOverall, lQMediumPointsAvg, lQMediumPointsOverall);
	        
	    DialogLayout.Group hQLResult = leftPanel.createParallelGroup(
	        		lQEasyCorrectR, lQEasyTimeBonusAvgR, lQEasyTimeBonusOverallR, lQEasyPointsAvgR, lQEasyPointsOverallR,
	        		lQMediumCorrectR, lQMediumTimeBonusAvgR, lQMediumTimeBonusOverallR, lQMediumPointsAvgR, lQMediumPointsOverallR);
	        
	    leftPanel.setHorizontalGroup(leftPanel.createParallelGroup()
	        		.addGroup(leftPanel.createSequentialGroup(hQLLeft, hQLRight))
	        		.addGroup(leftPanel.createSequentialGroup(hQLLabel, hQLResult)));
	        
	    leftPanel.setVerticalGroup(leftPanel.createSequentialGroup()
	        		.addGroup(leftPanel.createParallelGroup(lQAmount, lQAmountR))
	        		
	        		.addGap(10).addGroup(leftPanel.createParallelGroup(lQEasy, lQEasyR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQEasyCorrect, lQEasyCorrectR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQEasyTimeBonusAvg, lQEasyTimeBonusAvgR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQEasyTimeBonusOverall, lQEasyTimeBonusOverallR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQEasyPointsAvg, lQEasyPointsAvgR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQEasyPointsOverall, lQEasyPointsOverallR))
	        		
	        		.addGap(30).addGroup(leftPanel.createParallelGroup(lQMedium, lQMediumR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQMediumCorrect, lQMediumCorrectR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQMediumTimeBonusAvg, lQMediumTimeBonusAvgR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQMediumTimeBonusOverall, lQMediumTimeBonusOverallR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQMediumPointsAvg, lQMediumPointsAvgR))
	        		.addGap(5).addGroup(leftPanel.createParallelGroup(lQMediumPointsOverall, lQMediumPointsOverallR)));
	        
	    DialogLayout.Group hQRLeft = rightPanel.createParallelGroup(lQHard, lQTotal);
	    DialogLayout.Group hQRRight = rightPanel.createParallelGroup(lQHardR, lQTotalR);
	    Label gap = new Label("");
	    DialogLayout.Group hQRLabel = rightPanel.createParallelGroup(
					lQHardCorrect, lQHardTimeBonusAvg, lQHardTimeBonusOverall, lQHardPointsAvg, lQHardPointsOverall,
	        		lQTotalCorrect, lQTotalTimeBonusAvg, lQTotalTimeBonusOverall, lQTotalPointsAvg, lQTotalPointsOverall);
	        
	    DialogLayout.Group hQRResult = rightPanel.createParallelGroup(
	        		lQHardCorrectR, lQHardTimeBonusAvgR, lQHardTimeBonusOverallR, lQHardPointsAvgR, lQHardPointsOverallR,
	        		lQTotalCorrectR, lQTotalTimeBonusAvgR, lQTotalTimeBonusOverallR, lQTotalPointsAvgR, gap);
	        
	    rightPanel.setHorizontalGroup(rightPanel.createParallelGroup()
	        		.addGroup(rightPanel.createSequentialGroup(hQRLeft, hQRRight))
	        		.addGroup(rightPanel.createSequentialGroup(hQRLabel, hQRResult))
	        		.addGap(5).addWidget(lQTotalPointsOverallR));
	      
	    rightPanel.setVerticalGroup(rightPanel.createSequentialGroup()
	        		.addGap(20).addGroup(rightPanel.createParallelGroup(lQHard, lQHardR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQHardCorrect, lQHardCorrectR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQHardTimeBonusAvg, lQHardTimeBonusAvgR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQHardTimeBonusOverall, lQHardTimeBonusOverallR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQHardPointsAvg, lQHardPointsAvgR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQHardPointsOverall, lQHardPointsOverallR))
	        		
	        		.addGap(30).addGroup(rightPanel.createParallelGroup(lQTotal, lQTotalR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQTotalCorrect, lQTotalCorrectR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQTotalTimeBonusAvg, lQTotalTimeBonusAvgR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQTotalTimeBonusOverall, lQTotalTimeBonusOverallR))
	        		.addGap(5).addGroup(rightPanel.createParallelGroup(lQTotalPointsAvg, lQTotalPointsAvgR))
	        		.addGap(30).addGroup(rightPanel.createParallelGroup(lQTotalPointsOverall, gap))
	        		.addGap(5).addWidget(lQTotalPointsOverallR));
	    
	    DialogLayout.Group hLeftPanel = questionPanel.createSequentialGroup(leftPanel).addGap(50);
		DialogLayout.Group hRightPanel = questionPanel.createSequentialGroup(rightPanel);
		
		questionPanel.setHorizontalGroup(questionPanel.createParallelGroup()
				.addGroup(questionPanel.createSequentialGroup(hLeftPanel, hRightPanel)));
		
		questionPanel.setVerticalGroup(questionPanel.createSequentialGroup()
				.addGroup(questionPanel.createParallelGroup(leftPanel, rightPanel)));
		
			// RESET VARIABLES
			reset();
			// set player variable
			playerID = HRRUClient.cs.getPlayer();
			if(playerID == 1)
			{
				playerScore = HRRUClient.cs.getP1().getScore();
				activityScores = HRRUClient.cs.getP1().getActivityScores();
			}
			else
			{
				playerScore = HRRUClient.cs.getP2().getScore();
				activityScores = HRRUClient.cs.getP2().getActivityScores();
			}
			///////////////////////
			////question scores////
			///////////////////////
			for(int i = 0; i < activityScores.size(); i++)
			{
				// Calculate Total statistics
				noOfTotalQuestions++;
				activityScore = activityScores.get(i);
				totalQTimeBonusOverall += activityScore.getElapsedtime();
				totalQPointsOverall += activityScore.getOverall();
				if(activityScore.getCorrect())
					noOfTotalQCorrect++;
				
				// Calculate Easy statistics
				if(activityScore.getDifficulty() == 1)
				{
					noOfEasyQuestions++; 
					easyQTimeBonusOverall += activityScore.getElapsedtime();
					easyQPointsOverall += activityScore.getOverall();
					if(activityScore.getCorrect())
						noOfEasyQCorrect++;
				}
				// Calculate Medium statistics
				else if(activityScore.getDifficulty() == 2)
				{
					noOfMediumQuestions++; 
					mediumQTimeBonusOverall += activityScore.getElapsedtime();
					mediumQPointsOverall += activityScore.getOverall();
					if(activityScore.getCorrect())
						noOfMediumQCorrect++;
				}
				// Calculate Hard statistics
				else if(activityScore.getDifficulty() == 3)
				{
					noOfHardQuestions++; 
					hardQTimeBonusOverall += activityScore.getElapsedtime();
					hardQPointsOverall += activityScore.getOverall();
					if(activityScore.getCorrect())
						noOfHardQCorrect++;
				}
			}
			// Calculate Average statistics
			if(noOfEasyQuestions > 0)
			{
				easyQPointsAvg = (int) (easyQPointsOverall / noOfEasyQuestions + 0.5);
				easyQTimeBonusAvg = (int) (easyQTimeBonusOverall / noOfEasyQuestions + 0.5);
			}
			if(noOfMediumQuestions > 0)
			{
				mediumQPointsAvg = (int) (mediumQPointsOverall / noOfMediumQuestions + 0.5);
				mediumQTimeBonusAvg = (int) (mediumQTimeBonusOverall / noOfMediumQuestions + 0.5);
			}
			if(noOfHardQuestions > 0)
			{
				hardQPointsAvg = (int) (hardQPointsOverall / noOfHardQuestions + 0.5);
				hardQTimeBonusAvg = (int) (hardQTimeBonusOverall / noOfHardQuestions + 0.5);
			}
			if(noOfTotalQuestions > 0)
			{
				totalQPointsAvg = (int) (totalQPointsOverall / noOfTotalQuestions + 0.5);
				totalQTimeBonusAvg = (int) (totalQTimeBonusOverall / noOfTotalQuestions + 0.5);
			}
			
			// Create Question UI
			lQAmountR.setText("" + noOfTotalQuestions);
			
			lQEasy.setText("Easy: " + noOfEasyQuestions + " Questions");
			lQEasyR.setText("");
			lQEasyCorrectR.setText("" + noOfEasyQCorrect + "/" + noOfEasyQuestions);
			lQEasyTimeBonusAvgR.setText("" + easyQTimeBonusAvg);
			lQEasyTimeBonusOverallR.setText("" + (int)easyQTimeBonusOverall); 
			lQEasyPointsAvgR.setText("" + easyQPointsAvg); 
			lQEasyPointsOverallR.setText("" + (int)easyQPointsOverall);
			
			lQMedium.setText("Medium: " + noOfMediumQuestions + " Questions");
			lQMediumR.setText("");
			lQMediumCorrectR.setText("" + noOfMediumQCorrect + "/" + noOfMediumQuestions);
			lQMediumTimeBonusAvgR.setText("" + mediumQTimeBonusAvg);
			lQMediumTimeBonusOverallR.setText("" + (int)mediumQTimeBonusOverall); 
			lQMediumPointsAvgR.setText("" + mediumQPointsAvg); 
			lQMediumPointsOverallR.setText("" + (int)mediumQPointsOverall);
	        
			lQHard.setText("Hard: " + noOfHardQuestions + " Questions");
			lQHardR.setText("");
			lQHardCorrectR.setText("" + noOfHardQCorrect + "/" + noOfHardQuestions);
			lQHardTimeBonusAvgR.setText("" + hardQTimeBonusAvg);
			lQHardTimeBonusOverallR.setText("" + (int)hardQTimeBonusOverall); 
			lQHardPointsAvgR.setText("" + hardQPointsAvg); 
			lQHardPointsOverallR.setText("" + (int)hardQPointsOverall);
			
			lQTotal.setText("Total: " + noOfTotalQuestions + " Questions");
			lQTotalR.setText("");
			lQTotalCorrectR.setText("" + noOfTotalQCorrect + "/" + noOfTotalQuestions);
			lQTotalTimeBonusAvgR.setText("" + totalQTimeBonusAvg);
			lQTotalTimeBonusOverallR.setText("" + (int)totalQTimeBonusOverall); 
			lQTotalPointsAvgR.setText("" + totalQPointsAvg); 
			lQTotalPointsOverallR.setText("" + (int)totalQPointsOverall);

			
			QuestionScoreResult questionScoreResult =
					new QuestionScoreResult(
							noOfEasyQuestions, 
							noOfEasyQCorrect, 
							easyQTimeBonusAvg,
							easyQTimeBonusOverall, 
							easyQPointsAvg, 
							easyQPointsOverall, 
							noOfMediumQuestions, 
							noOfMediumQCorrect, 
							mediumQTimeBonusAvg,
							mediumQTimeBonusOverall, 
							mediumQPointsAvg, 
							mediumQPointsOverall,
							noOfHardQuestions, 
							noOfHardQCorrect, 
							hardQTimeBonusAvg,
							hardQTimeBonusOverall,
							hardQPointsAvg,
							hardQPointsOverall, 
							noOfTotalQuestions, 
							noOfTotalQCorrect,
							totalQTimeBonusAvg, 
							totalQTimeBonusOverall, 
							totalQPointsAvg, 
							totalQPointsOverall);
			
			if(noOfTotalQuestions > 0)
			{
				questionScoreResult.setPercentage((int)((totalQPointsOverall/(playerScore-1000))*100+0.5));
				lQTotalPointsOverallR.setText("" + (int) totalQPointsOverall + " (" + (int)((totalQPointsOverall/(playerScore-1000))*100+0.5) +  "% of your score)");
			}
			
			
			if(playerID == 1)
				HRRUClient.cs.getP1().setQuestionScoreResult(questionScoreResult);
			else
				HRRUClient.cs.getP2().setQuestionScoreResult(questionScoreResult);	
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
		else if(enterState == 3)
			sbg.enterState(questionfeedback);
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
		return 18;
	}

}