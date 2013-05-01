package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import main.textpage.TextPage.TextPageFrame;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.RotateTransition;
import org.newdawn.slick.state.transition.SelectTransition;

import com.esotericsoftware.kryonet.Client;

import conn.Packet.*;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;

public class PlayQuestion extends BasicTWLGameState {
	// question state variables
	private int gameState;
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int play = 11;
	private final int p1_turn = 7;
	public final int question_points_amount = 50;
	// question GUI variables
	public Client client;
	DialogLayout choicePanel, questionPanel;
	EmptyTransition emptyTransition;
    RotateTransition rotateTransition;
    SelectTransition selectTransition;
    
	int gcw;
	int gch;
	String mouse;
	
	Image scorebackground;
	
	// variables for each player
	private int playerID;
	private int otherPlayerID;
	private Player player;
	private Player otherPlayer;
	private Player player1;
	private Player player2;
	private int playerScore2;
	private int currentAnswer;
	private int otherPlayerReady;
	private ActivityScore playerResult;
	private ActivityScore otherPlayerResult;
	private int elapsedTime = 0;
	private int pointsAvailable = 0;
	private int pointsGained = 0;
	boolean ready = false;
	
	// initiate each question variables
	private int current_question_id;
	private QuestionList question_list;
	private Question[] questions;
	private Question current_question;
	private String[] current_choices;
	private int correctAnswer;
	private int question_difficulty;
	private int amountOfAnswers;
	private String FILE_NAME;
	private String start_message = "";
	private String full_start_message = "Here's your question...";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	// set up coordinates
	private int header_x = 330;
	private int header_y = 50;
	private int timer_x = 600;
	private int timer_y = 550;
	// set up font sizes
	private int mainFontSize = 24;
	private int titleFontSize = 36;
	private int timerFontSize = 40;
	private int timerMFontSize = 18;
	// set up fonts
	private Font loadFont, loadMainFont, loadTitleFont, loadTimerFont, loadTimerMFont;
	private BasicFont mainFont, titleFont, readyFont, timerFont, timerMFont;;
	// set up clocks
	private int clock2,clock3,timer,timer2,overallTimer = 0;
	private boolean end, win, finished, resume = false;
	// set up GUI 
	TextPageFrame textpageframe;
	DialogLayout p1ResultPanel, p2ResultPanel;
	
	Label lActivity, lTitle, lPoints, lDifficulty, lTime, lOverall, lNew;
	Label lActivity2, lPoints2, lDifficulty2, lTime2, lOverall2, lNew2;
	Label lblPoints1, lblDifficulty1, lblTime1, lblOverall1, lblNew1;
	Label lblPoints2, lblDifficulty2, lblTime2, lblOverall2, lblNew2;
	
	Label lblConfirmation, lblWaiting;
	Button choices[];
	Button btnYes, btnNo;
	
	DialogLayout.Group hBtn[], hBtnYes, hBtnNo, hQuestion, hLblConfirmation;
	String description;
	
	Packet00SyncMessage syncMessage;
	Packet14QuestionComplete completeMessage;
	
	public PlayQuestion(int state, QuestionList ql) {
		this.question_list = ql;
	}
	
	void disableChoices()
	{
		for(int i = 0; i < amountOfAnswers; i++)
		{
			choices[i].setVisible(false);
			choices[i].setEnabled(false);
		}
		lblConfirmation.setVisible(true);
		btnYes.setVisible(true);
		btnNo.setVisible(true);
	}
	
	void enableChoices()
	{
		for(int i = 0; i < amountOfAnswers; i++)
		{
			choices[i].setVisible(true);
			choices[i].setEnabled(true);
		}
		currentAnswer = -1;
		lblConfirmation.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
	}
	
	void disableGUI()
	{
		choicePanel.setVisible(false);
		questionPanel.setVisible(false);
	}
	
	void emulateChoice(int choice)
	{
		disableChoices();
		lblConfirmation.setText("Is your answer: \n'" + choices[choice].getText() + "' ?");
		currentAnswer = choice;
	}
	
	void emulateYes()
	{
		disableGUI();
		completeMessage = new Packet14QuestionComplete();
		completeMessage.difficulty = question_difficulty;
		completeMessage.choice = currentAnswer;
		completeMessage.elapsedtime = 0;
		completeMessage.player = playerID;
		completeMessage.points = 0;
		completeMessage.overall = 0;
		completeMessage.correct = false;
		completeMessage.sessionID = HRRUClient.cs.getSessionID();
		pointsAvailable = 0;
		pointsGained = 0;
		elapsedTime = 0;
		ready = true;
		end = true;
		if(currentAnswer == correctAnswer)
		{
			completeMessage.elapsedtime = timer;
			elapsedTime = timer; 
			pointsAvailable = question_points_amount;
			pointsGained = question_points_amount;
			completeMessage.points = pointsGained;
			completeMessage.choice = currentAnswer;

			pointsGained *= question_difficulty;
			pointsGained += timer;
			completeMessage.correct = true;
			completeMessage.overall = pointsGained;
			player.addScore(pointsGained);
			win = true;
		}
		else
		{
			win = false;
		}
		client.sendTCP(completeMessage);
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		client = HRRUClient.conn.getClient();
		rootPane.removeAllChildren();
		choicePanel.removeAllChildren();
		questionPanel.removeAllChildren();
		
		// Set up question variables
		questions = question_list.getQuestion_list();
		
		current_question_id = HRRUClient.cs.getActivity_id();
		
		current_question = questions[current_question_id];

		current_choices =  current_question.getChoices();
		FILE_NAME = current_question.getFile();
		amountOfAnswers = current_question.getAmountOfAnswers();
		correctAnswer = current_question.getAnswer();
		question_difficulty = current_question.getDifficulty();
		
		// Reset variables
		otherPlayerReady=0;
		win = false; end = false; finished = false; resume = false;
		currentAnswer = -1;
		clock2 = 0; clock3 = 0;
		timer = 80;
		timer2 = 999;
		elapsedTime = 0;
		pointsAvailable = 0;
		pointsGained = 0;
		overallTimer = 0;
		
		start_message = "";
		full_start_message = "Here's your question...";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		
		// Retrieve player information
		player1 = HRRUClient.cs.getP1();
		player2 = HRRUClient.cs.getP2();
		playerID = HRRUClient.cs.getPlayer();
		
		if(playerID == 1)
		{
			player =  player1;
			otherPlayer = player2;
			otherPlayerID = 2;
		}
		else 
		{
			player =  player2;
			otherPlayer = player1;
			otherPlayerID = 1;
		}
		playerScore2 = otherPlayer.getScore();
		
		choicePanel.setTheme("choices-panel");   
		
		textpageframe = new TextPageFrame(FILE_NAME);
		textpageframe.setSize(700, 300);
		textpageframe.setDraggable(false);
		textpageframe.setResizableAxis(ResizableAxis.NONE);
		textpageframe.setTheme("textpageframe");
		
		hQuestion = questionPanel.createSequentialGroup().addWidget(textpageframe);
		
		questionPanel.setHorizontalGroup(questionPanel.createParallelGroup()
				.addGroup(hQuestion));	
		questionPanel.setVerticalGroup(questionPanel.createSequentialGroup()
				.addWidgets(textpageframe));
		
		hBtn = new DialogLayout.Group[amountOfAnswers];
		choices = new Button[amountOfAnswers];
		
		for(int i = 0; i < amountOfAnswers; i++)
		{
			choices[i] = new Button();
			choices[i].setSize(500, 30);
			choices[i].setTheme("choicebutton");
			hBtn[i] =  choicePanel.createSequentialGroup().addWidget(choices[i]);
		}
		// Re-add previous widgets
		hLblConfirmation = choicePanel.createSequentialGroup().addWidget(lblConfirmation);
		hBtnYes = choicePanel.createSequentialGroup().addWidget(btnYes);
		hBtnNo = choicePanel.createSequentialGroup().addWidget(btnNo);
		// Create Dialog Layout
		choicePanel.setHorizontalGroup(choicePanel.createParallelGroup()
				.addGroups(hBtn)
				.addGroup(hLblConfirmation)
				.addGroup(hBtnYes)
				.addGroup(hBtnNo));
		
		choicePanel.setVerticalGroup(choicePanel.createSequentialGroup()
				.addWidgets(choices)
				.addWidget(lblConfirmation)
				.addWidget(btnYes)
				.addWidget(btnNo));
		
		// Set up buttons for choices
		for(int i = 0; i < amountOfAnswers; i++)
			choices[i].setText(current_choices[i]);
		
		// Set up callbacks for buttons
		choices[0].addCallback(new Runnable() {
            public void run() {
                emulateChoice(0);
            }
        });
		choices[1].addCallback(new Runnable() {
            public void run() {
                emulateChoice(1);
            }
        });
		if(amountOfAnswers>2)
			{
			choices[2].addCallback(new Runnable() {
				public void run() {
					emulateChoice(2);
				}
			});
		}
		if(amountOfAnswers>3)
		{
			choices[3].addCallback(new Runnable() {
				public void run() {
					emulateChoice(3);
				}
			});
			}
		if(amountOfAnswers>4)
		{
			choices[4].addCallback(new Runnable() {
				public void run() {
					emulateChoice(4);
				}
			});
		}
		if(amountOfAnswers>5)
		{
			choices[5].addCallback(new Runnable() {
				public void run() {
					emulateChoice(5);
				}
			});
		}

		
		// Add to root pane
		enableChoices();
		p1ResultPanel.setTheme("incorrect-panel");
		p2ResultPanel.setTheme("incorrect-panel");
		questionPanel.setVisible(true);
		choicePanel.setVisible(true);
		lblWaiting.setVisible(false);
		p1ResultPanel.setVisible(false);
		p2ResultPanel.setVisible(false);
		
		rootPane.add(p1ResultPanel);
		rootPane.add(p2ResultPanel);
		rootPane.add(lblWaiting);
		rootPane.add(questionPanel);
		rootPane.add(choicePanel);
		
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
		rotateTransition = new RotateTransition();
		selectTransition = new SelectTransition();
		emptyTransition = new EmptyTransition();
		scorebackground = new Image("simple/playerscorebackground.png");
		
		// Create custom font for question
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
		
		loadMainFont = loadFont.deriveFont(Font.BOLD,mainFontSize);
		mainFont = new BasicFont(loadMainFont);
		readyFont = new BasicFont(loadTitleFont, Color.red);
		
		loadTimerFont = loadFont.deriveFont(Font.BOLD,timerFontSize);
		loadTimerMFont = loadFont.deriveFont(Font.BOLD,timerMFontSize);
		timerFont = new BasicFont(loadTimerFont);
		timerMFont = new BasicFont(loadTimerMFont);
		
		// Set up question GUI widgets
		choicePanel = new DialogLayout();
		questionPanel = new DialogLayout();
		questionPanel.setTheme("question-panel");
		choicePanel.setTheme("choices-panel");				
		questionPanel.setSize(750, 300);
		questionPanel.setPosition(
				(25),
				(gch/2 - questionPanel.getHeight()/2) - 50);
		        
		choicePanel.setSize(500, 150);
		choicePanel.setPosition(
				(gcw/2 - choicePanel.getWidth()/2),
		        (gch/2 - choicePanel.getHeight()/2) + 150);
				
		lblConfirmation = new Label("");
		btnYes = new Button("Yes");
		btnNo = new Button("No");
		
		lblConfirmation.setTheme("labelscoretotal");
		btnYes.setTheme("choicebutton");
		btnNo.setTheme("choicebutton");
				
		hLblConfirmation = choicePanel.createSequentialGroup().addWidget(lblConfirmation);
		hBtnYes = choicePanel.createSequentialGroup().addWidget(btnYes);
		hBtnNo = choicePanel.createSequentialGroup().addWidget(btnNo);
		
		btnYes.addCallback(new Runnable() {
            public void run() {
                emulateYes();
            }
        });
				
		btnNo.addCallback(new Runnable() {
            public void run() {
            	enableChoices();
            }
        });
		
		choicePanel.setIncludeInvisibleWidgets(false);
		
		btnNo.setVisible(false);
		btnYes.setVisible(false);
		
		// RESULTS PANEL SETUP
		lblWaiting = new Label("");
		lblWaiting.setSize(800, 100);
		lblWaiting.setPosition(0,480);
		lblWaiting.setTheme("labelscoretotal");
		
        p1ResultPanel = new DialogLayout();
        p1ResultPanel.setTheme("incorrect-panel");
		
		p1ResultPanel.setSize(310, 310);
		p1ResultPanel.setPosition(
               (gcw/2 - p1ResultPanel.getWidth()/2 - 220),
                (gch/2 - p1ResultPanel.getHeight()/2));
		
		p2ResultPanel = new DialogLayout();
        p2ResultPanel.setTheme("incorrect-panel");
		p2ResultPanel.setSize(310, 310);
		p2ResultPanel.setPosition(
               (gcw/2 - p2ResultPanel.getWidth()/2 + 180),
                (gch/2 - p2ResultPanel.getHeight()/2));
		
		lActivity = new Label("");
		lActivity.setTheme("labelmiddle");
		lPoints = new Label("Points:");
		lDifficulty = new Label("Difficulty:");
		lTime = new Label("Time Bonus:");
		lOverall = new Label("Overall Points:");
		lNew = new Label("New Total:");
		lNew.setTheme("labelscoretotal");
		
		lActivity2 = new Label("");
		lActivity2.setTheme("labelmiddle");
		lPoints2 = new Label("Points:");
		lDifficulty2 = new Label("Difficulty:");
		lTime2 = new Label("Time Bonus:");
		lOverall2 = new Label("Overall Points:");
		lNew2 = new Label("New Total:");
		lNew2.setTheme("labelscoretotal");
				
		lblPoints1 = new Label("");
		lblPoints1.setTheme("labelright");
		lblDifficulty1 = new Label("");
		lblDifficulty1.setTheme("labelright");
		lblTime1 = new Label("");
		lblTime1.setTheme("labelright");
		lblOverall1 = new Label("");
		lblOverall1.setTheme("labelright");
		lblNew1 = new Label("");
		lblNew1.setTheme("labelscoretotalright");
		
		lblPoints2 = new Label("");
		lblPoints2.setTheme("labelright");
		lblDifficulty2 = new Label("");
		lblDifficulty2.setTheme("labelright");
		lblTime2 = new Label("");
		lblTime2.setTheme("labelright");
		lblOverall2 = new Label("");
		lblOverall2.setTheme("labelright");
		lblNew2 = new Label("");
		lblNew2.setTheme("labelscoretotalright");
		
		DialogLayout.Group hLabels1 = p1ResultPanel.createParallelGroup(lPoints, lDifficulty, lTime, lOverall, lNew).addGap(200);
		DialogLayout.Group hP1Result1 = p1ResultPanel.createParallelGroup(lblPoints1, lblDifficulty1, lblTime1, lblOverall1, lblNew1);
		DialogLayout.Group hActivity = p1ResultPanel.createSequentialGroup(lActivity);
		
		p1ResultPanel.setHorizontalGroup(p1ResultPanel.createParallelGroup()
				.addGap(120)
				.addGroup(hActivity)
				.addGroup(p1ResultPanel.createSequentialGroup(hLabels1, hP1Result1)));
		
		p1ResultPanel.setVerticalGroup(p1ResultPanel.createSequentialGroup()
				.addGap(60).addWidget(lActivity)
				.addGap(30).addGroup(p1ResultPanel.createParallelGroup(lPoints, lblPoints1))
				.addGap(30).addGroup(p1ResultPanel.createParallelGroup(lDifficulty, lblDifficulty1))
				.addGap(30).addGroup(p1ResultPanel.createParallelGroup(lTime, lblTime1))
				.addGap(30).addGroup(p1ResultPanel.createParallelGroup(lOverall, lblOverall1))
				.addGap(30).addGroup(p1ResultPanel.createParallelGroup(lNew, lblNew1)));
		
		DialogLayout.Group hLabels2 = p2ResultPanel.createParallelGroup(lPoints2, lDifficulty2, lTime2, lOverall2, lNew2).addGap(200);
		DialogLayout.Group hP1Result2 = p2ResultPanel.createParallelGroup(lblPoints2, lblDifficulty2, lblTime2, lblOverall2, lblNew2);
		DialogLayout.Group hActivity2 = p2ResultPanel.createSequentialGroup(lActivity2);
		
		p2ResultPanel.setHorizontalGroup(p2ResultPanel.createParallelGroup()
				.addGap(120)
				.addGroup(hActivity2)
				.addGroup(p2ResultPanel.createSequentialGroup(hLabels2, hP1Result2)));
		
		p2ResultPanel.setVerticalGroup(p2ResultPanel.createSequentialGroup()
				.addGap(60).addWidget(lActivity2)
				.addGap(30).addGroup(p2ResultPanel.createParallelGroup(lPoints2, lblPoints2))
				.addGap(30).addGroup(p2ResultPanel.createParallelGroup(lDifficulty2, lblDifficulty2))
				.addGap(30).addGroup(p2ResultPanel.createParallelGroup(lTime2, lblTime2))
				.addGap(30).addGroup(p2ResultPanel.createParallelGroup(lOverall2, lblOverall2))
				.addGap(30).addGroup(p2ResultPanel.createParallelGroup(lNew2, lblNew2)));
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		if(!end)
		{
		g.drawImage(new Image("simple/questionbg.png"), 0, 0);
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, header_x, header_y);
		g.drawImage(scorebackground, 0,0);
		g.setFont(mainFont.get());
		g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 13,13);
		g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 13,55);
		g.drawString("" + player1.getName(), 65, 22);
		g.drawString("" + player2.getName(), 65, 64);
		g.drawString("" + player1.getScore(), 204, 22);
		g.drawString("" + player2.getScore(), 204, 64);

		// g.drawString(mouse, 50, 550);
		
		g.setFont(timerFont.get());
		if(timer<100)
			g.drawString("TIME: 0" + timer, timer_x, timer_y);
		else if(timer<10)
			g.drawString("TIME: 00" + timer, timer_x, timer_y);
		else
		    g.drawString("TIME: " + timer, timer_x, timer_y);
		g.setFont(timerMFont.get());
		if(timer2<100)
			g.drawString("0" + timer2, timer_x+145, timer_y-10);
		else if(timer2<10)
			g.drawString("00" + timer2, timer_x+145, timer_y-10);
		else
			g.drawString("" + timer2, timer_x+145, timer_y-10);
		
		g.setFont(readyFont.get());
		
		if(otherPlayerReady == 1)
		{
			if(otherPlayerID == 1)
				g.drawString("FINISHED", 92, 19);
			else
				g.drawString("FINISHED", 92, 61);
		}
		}
		
		if(end)
		{
			g.drawImage(new Image("simple/questionbg.png"), 0, 0);
			g.drawImage(scorebackground, 0,0);
			g.setFont(mainFont.get());
			g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 13,13);
			g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 13,55);
			g.drawString("" + player1.getName(), 65, 22);
			g.drawString("" + player2.getName(), 65, 64);
			g.drawString("" + player1.getScore(), 204, 22);
			g.drawString("" + player2.getScore(), 204, 64);
			g.setFont(readyFont.get());
			if(ready)
			{
				if(playerID == 1)
					g.drawString("FINISHED", 92, 19);
				else
					g.drawString("FINISHED", 92, 61);
			}
			if(otherPlayerReady == 1)
			{
				if(otherPlayerID == 1)
					g.drawString("FINISHED", 92, 19);
				else
					g.drawString("FINISHED", 92, 61);
			}
			g.setFont(timerFont.get());
			if(timer<100)
				g.drawString("TIME: 0" + timer, timer_x, timer_y);
			else if(timer<10)
				g.drawString("TIME: 00" + timer, timer_x, timer_y);
			else
			    g.drawString("TIME: " + timer, timer_x, timer_y);
			g.setFont(timerMFont.get());
			if(timer2<100)
				g.drawString("0" + timer2, timer_x+145, timer_y-10);
			else if(timer2<10)
				g.drawString("00" + timer2, timer_x+145, timer_y-10);
			else
				g.drawString("" + timer2, timer_x+145, timer_y-10);
			
			if(finished)
			{
				g.drawImage(new Image("simple/questionbg.png"), 0, 0);
				g.setFont(titleFont.get());
				g.drawString("> " + start_message + "" + ticker, 50, 50);
				g.drawString("" + timer, 750, 550);
				g.drawImage(new Image("simple/playerbg.png"), 124, 175);
				g.drawImage(new Image("simple/playerbg.png"), 524, 175);
				g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 124, 175);
				g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 524, 175);
				g.setFont(mainFont.get());
				g.drawString("" + player1.getName(), 174, 184);
				g.drawString("" + player2.getName(), 574, 184);
			}
		}
		
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		gc.getInput();
		int xpos = Mouse.getX();
		int ypos= Mouse.getY();
		mouse = "xpos: " + xpos + "\nypos: " + ypos;
		
		// Update variables
		clock2 += delta;
		clock3 += delta;
		timer2 -= delta;
		overallTimer += delta;
		gameState = HRRUClient.cs.getState();
		
		// Connection to server lost
		if(gameState == serverlost)
			sbg.enterState(0);
		
		// Connection to other player lost
		if(gameState == cancelled) {
			if(playerID == 1)
				sbg.enterState(1);
			else sbg.enterState(2);
		}
		
		// Check if other player is finished
		if(otherPlayerID == 1)
			otherPlayerReady = HRRUClient.cs.getP1().getReady();
		else
			otherPlayerReady = HRRUClient.cs.getP2().getReady();
					
		if(clock3 > 100){
			if(full_start_counter < full_start_message.length())
			{
				start_message += full_start_message.substring(full_start_counter, full_start_counter+1);
				full_start_counter++;
				clock3-=100;
			}
		}
		
		if(end==false)
		{
			if(clock2>999)
			{
				timer--;
				timer2=999;
				if(timer<=0)
				{
					disableGUI();
					end = true;
					completeMessage = new Packet14QuestionComplete();
					completeMessage.difficulty = question_difficulty;
					completeMessage.elapsedtime = 0;
					completeMessage.overall = 0;
					completeMessage.correct = false;
					completeMessage.player = playerID;
					completeMessage.points = 0;
					completeMessage.choice = -1;
					completeMessage.sessionID = HRRUClient.cs.getSessionID();
					client.sendTCP(completeMessage);
				}
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
		if(end && !finished)
		{
			if(clock2>999)
			{
				timer--;
				timer2=999;
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
			if(otherPlayerReady == 1)
			{
				// Setup new UI
				timer = 8; // should be 10
				timer2 = 999;
				clock2 = 0;
				clock3 = 0;
				lblWaiting.setVisible(false);
				questionPanel.setVisible(false);
				full_start_message = "The results are in...";
				full_start_counter = 0;
				ticker = "";
				start_message = "";
				tickerBoolean = true;
				p1ResultPanel.setVisible(true);
				p2ResultPanel.setVisible(true);
				// Setup players results
				if(playerID == 1)
				{
					lblPoints1.setText("" + pointsAvailable);
					
					if(question_difficulty==1)
						lblDifficulty1.setText("Easy X" + question_difficulty);
					else if(question_difficulty==2)
						lblDifficulty1.setText("Medium X" + question_difficulty);
					else if(question_difficulty==3)
						lblDifficulty1.setText("Hard X" + question_difficulty);
					
					lblOverall1.setText("" + pointsGained);
					lblTime1.setText("" + elapsedTime);	
					lblNew1.setText("" + HRRUClient.cs.getP1().getScore());
					lActivity.setText("Answered a question...");
					if(win)
					{
						p1ResultPanel.setTheme("correct-panel");
						p1ResultPanel.reapplyTheme();
					}
					
					otherPlayerResult = HRRUClient.cs.getP2().getActivityScore();
					lblPoints2.setText("" + otherPlayerResult.getPoints());
					
					if(otherPlayerResult.getDifficulty()==1)
						lblDifficulty2.setText("Easy X1");
					else if(otherPlayerResult.getDifficulty()==2)
						lblDifficulty2.setText("Medium X2");
					else if(otherPlayerResult.getDifficulty()==3)
						lblDifficulty2.setText("Hard X3");
					
					lblOverall2.setText("" +  otherPlayerResult.getOverall());
					lblTime2.setText("" +  otherPlayerResult.getElapsedtime());
					lblNew2.setText("" + (playerScore2 + otherPlayerResult.getOverall()));
					
					int activity = otherPlayerResult.getActivity();
					if(activity== 1)
						lActivity2.setText("Answered a question...");
					else if(activity == 2)
						lActivity2.setText("Solved a puzzle...");
					else if(activity == 3)
						lActivity2.setText("Completed a game...");
					
					if(otherPlayerResult.getCorrect())
					{
						p2ResultPanel.setTheme("correct-panel");
						p2ResultPanel.reapplyTheme();
					}
					finished = true;
				}
				else if(playerID == 2)
				{
					otherPlayerResult = HRRUClient.cs.getP1().getActivityScore();
					lblPoints1.setText("" + otherPlayerResult.getPoints());
					
					if(otherPlayerResult.getDifficulty()==1)
						lblDifficulty1.setText("Easy X1");
					else if(otherPlayerResult.getDifficulty()==2)
						lblDifficulty1.setText("Medium X2");
					else if(otherPlayerResult.getDifficulty()==3)
						lblDifficulty1.setText("Hard X3");
					
					lblOverall1.setText("" +  otherPlayerResult.getOverall());
					lblTime1.setText("" +  otherPlayerResult.getElapsedtime());
					lblNew1.setText("" + (playerScore2 + otherPlayerResult.getOverall()));
					
					lActivity.setText("Answered a question...");
					
					if(otherPlayerResult.getCorrect())
					{
						p1ResultPanel.setTheme("correct-panel");
						p1ResultPanel.reapplyTheme();
					}
					
					lblPoints2.setText("" + pointsAvailable);
					if(question_difficulty==1)
						lblDifficulty2.setText("Easy X" + question_difficulty);
					else if(question_difficulty==2)
						lblDifficulty2.setText("Medium X" + question_difficulty);
					else if(question_difficulty==3)
						lblDifficulty2.setText("Hard X" + question_difficulty);
					lblOverall2.setText("" + pointsGained);
					lblTime2.setText("" + elapsedTime);
				
					lblNew2.setText("" + HRRUClient.cs.getP2().getScore());
					
					int activity = otherPlayerResult.getActivity();
					if(activity== 1)
						lActivity2.setText("Answered a question...");
					else if(activity == 2)
						lActivity2.setText("Solved a puzzle...");
					else if(activity == 3)
						lActivity2.setText("Completed a game...");
						
					if(win)
					{
						p2ResultPanel.setTheme("correct-panel");
						p2ResultPanel.reapplyTheme();
					}
					finished = true;
				}
			}
			else if(otherPlayerReady == 0)
			{
				questionPanel.setVisible(true);
				if(currentAnswer >= 0)
					lblWaiting.setText("You answered\n '" + choices[currentAnswer].getText() + "'\n" +"Waiting for " + otherPlayer.getName());
				else
					lblWaiting.setText("You did not answer.");
				lblWaiting.setVisible(true);
			}
		}
		else if(finished)
		{
			if(clock2>999)
			{
				timer--;
				timer2=999;
				clock2-=1000;
				if(timer<=0)
				{
					playerResult = new ActivityScore(1, pointsAvailable, question_difficulty, elapsedTime, pointsGained, win);
					playerResult.setActivity_id(current_question_id);
					playerResult.setChoice(currentAnswer);
					// If Player1, update Player2's new score and add own result to list of results.
					if(playerID == 1)
					{
						HRRUClient.cs.getP2().addScore(otherPlayerResult.getOverall());
						HRRUClient.cs.getP1().addActivityScore(playerResult);
					}
					else
					{
						HRRUClient.cs.getP1().addScore(otherPlayerResult.getOverall());
						HRRUClient.cs.getP2().addActivityScore(playerResult);
					}
					syncMessage = new Packet00SyncMessage();
					syncMessage.player = playerID;
					syncMessage.sessionID = HRRUClient.cs.getSessionID();
					client.sendTCP(syncMessage);
					finished = false;
					resume = true;
				}
			}
		}
		if(resume)
		{
			if(HRRUClient.cs.isSync())
			{
				HRRUClient.cs.updateTimer(overallTimer);
				System.out.println("Time Subtract" + (overallTimer));
				HRRUClient.cs.setSync(false);
				HRRUClient.cs.setState(p1_turn);
				sbg.enterState(play, new FadeOutTransition(), new FadeInTransition());
			}
		}
	}
	
	@Override
	public int getID() {
		return 12;
	}
}