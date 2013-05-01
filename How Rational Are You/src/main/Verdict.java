package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import com.esotericsoftware.kryonet.Client;

import conn.Packet.Packet27QuestionAnswers;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TextArea;

public class Verdict extends BasicTWLGameState {

	// Verdict GUI
	public Client client;
	DialogLayout questionPanel, puzzlePanel, gamePanel;
	TextArea summaryDescription;
	HTMLTextAreaModel summaryDescriptionModel;
	Label lblFinish;
	private int finishTimer = 3;
	private final int number_of_achievements = 6;
	private ResultPercentage[] resultPercentages = new ResultPercentage[number_of_achievements];
	private String[] rankDescriptions = new String[6];
	Image scorebackground;
	Player player1, player2;
	
	// constant variables
	int gcw;
	int gch;
	int clock;
	boolean done, enter, finishLabelShown;
	private final int questionstats = 18;
	private final int questionfeedback = 19;
	private final int questionnaire = 20;
	private final int bidstats = 21;
	private final int prisonerstats = 22;
	private final int truststats = 23;
	private final int ultstats = 24;
	private final int scoreboard = 25;
	
	// overall results variables
	int overallPointsAvailable;
	double rankPercentage;
	String message1, message2, message3, message4;
	
	// Ticker variables
	private int titleFontSize = 36;
	private int mainFontSize = 24;
	private Font loadFont, loadTitleFont, loadMainFont;
	private BasicFont titleFont, mainFont;
	private String start_message = "";
	private String full_start_message = "HOW RATIONAL ARE YOU?";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2 = 0;
	
	// percentage variables
	boolean win, highRoller;
	private int bidderPercentage;
	private int coopPercentage;
	private int betrayPercentage;
	private int fastPercentage;
	private int slowPercentage;
	private int knowledgePercentage;

	// Verdict GUI
	DialogLayout verdictPanel;
	Button btnQuestion, btnGame, btnScoreboard, btnEnd;
	
	// Achievement Images
	Image imgNone, imgBetray, imgHighBidder, imgCoop, imgKnowledge, imgQuick;
	Image imgRoller, imgSlow, imgWinner, imgLose;
	Image ach1, ach2, ach3;
	
	// Rank Images;
	Image ranks[];
	Image rankS, rankA, rankB, rankC, rankD, rankE, rankLetter;
	int rankCounter, rankClockM;
	int rankClock, rankClock1;

	// Player variables
	int playerRank = 0;
	Player player, otherPlayer;
	
	// Achievement coordinates
	private int ach1_x = 71;
	private int ach1_y = 172;
	private int ach2_x = 297;
	private int ach2_y = 172;
	private int ach3_x = 523;
	private int ach3_y = 172;
	
	private int playerID;
	private int playerScore;
	private int otherPlayerScore;
	private int enterState;
	
	public Verdict(int main) {
		client = HRRUClient.conn.getClient();
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		/*
		System.out.println(playerScore);
		activityScore = new ActivityScore(1, 100, 3, 30, 330, true);
		activityScore.setActivity_id(2);
		HRRUClient.cs.getP1().addActivityScore(activityScore);
		
		activityScore = new ActivityScore(3, 100, 3, 50, 150, true);
		activityScore.setActivity_id(3);
		HRRUClient.cs.getP1().addActivityScore(activityScore);
		
		activityScore = new ActivityScore(3, 0, 1, 0, 0, false);
		activityScore.setActivity_id(5);
		HRRUClient.cs.getP1().addActivityScore(activityScore);
		
		TrustScore trustScore = new TrustScore(1, 2, 200, 600, 100, 150, 3, 250, 150);
		HRRUClient.cs.getP1().addTrustScore(trustScore);
		
		trustScore = new TrustScore(1, 2, 100, 300, 50, 75, 3, 125, 75);
		HRRUClient.cs.getP1().addTrustScore(trustScore);
		
		trustScore = new TrustScore(2, 1, 200, 600, 100, 150, 3, 250, 150);
		HRRUClient.cs.getP1().addTrustScore(trustScore);
		
		
		PrisonScore prisonScore = new PrisonScore(0, 0, 0, 0, true, true);
		HRRUClient.cs.getP1().addPrisonScore(prisonScore);
		prisonScore = new PrisonScore(0, 0, 0, 0, true, true);
		HRRUClient.cs.getP1().addPrisonScore(prisonScore);
		prisonScore = new PrisonScore(0, 0, 0, 0, true, true);
		HRRUClient.cs.getP1().addPrisonScore(prisonScore);
		prisonScore = new PrisonScore(0, 1, 0, 0, true, true);
		HRRUClient.cs.getP1().addPrisonScore(prisonScore);
		prisonScore = new PrisonScore(0, 1, 0, 0, true, true);
		HRRUClient.cs.getP1().addPrisonScore(prisonScore);
		prisonScore = new PrisonScore(0, 1, 0, 0, true, true);
		HRRUClient.cs.getP1().addPrisonScore(prisonScore);
		prisonScore = new PrisonScore(1, 1, 0, 0, true, true);
		HRRUClient.cs.getP1().addPrisonScore(prisonScore);
		
		UltimatumScore ultimatumScore = new UltimatumScore(1, 2, 100, 150, 250, true);
		HRRUClient.cs.getP1().addUltimatumScore(ultimatumScore);
		ultimatumScore = new UltimatumScore(1, 2, 50, 150, 200, true);
		HRRUClient.cs.getP1().addUltimatumScore(ultimatumScore);
		ultimatumScore = new UltimatumScore(1, 2, 100, 50, 150, true);
		HRRUClient.cs.getP1().addUltimatumScore(ultimatumScore);
		*/
		// RESET VARIABLES
		start_message = "";
		full_start_message = "HOW RATIONAL ARE YOU?";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		enterState = 0;
		
		if(!enter)
		{
			sbg.addState(new QuestionStatistics(questionstats));
			sbg.getState(questionstats).init(gc, sbg);
			sbg.addState(new QuestionFeedback(questionfeedback));
			sbg.getState(questionfeedback).init(gc, sbg);
			sbg.addState(new BidStatistics(bidstats));
			sbg.getState(bidstats).init(gc, sbg);
			sbg.addState(new PrisonerStatistics(prisonerstats));
			sbg.getState(prisonerstats).init(gc, sbg);
			sbg.addState(new TrustStatistics(truststats));
			sbg.getState(truststats).init(gc, sbg);
			sbg.addState(new UltimatumStatistics(ultstats));
			sbg.getState(ultstats).init(gc, sbg);
			sbg.addState(new Scoreboard(scoreboard));
			sbg.getState(scoreboard).init(gc, sbg);
			sbg.addState(new Questionnaire(questionnaire));
			sbg.getState(questionnaire).init(gc, sbg);
			
			if(playerScore > otherPlayerScore)
			{
				win = true;
				ach1 = imgWinner;
				message1 = "Congratulations, " + player.getName() + " you won!\n";
			}
			else
			{
				win = false;
				ach1 = imgLose;
				message1 = "Sorry, " + player.getName() + " you've lost!\n";
			}
			
			// Calculate percentages and achievements
			BiddingScoreResult biddingScore = player.getBiddingScoreResult();
			QuestionScoreResult questionScore = player.getQuestionScoreResult();
			PrisonerScoreResult prisonScore = player.getPrisonerScoreResult();
			UltScoreResult ultimatumScore = player.getUltScoreResult();
			TrustScoreResult trustScore = player.getTrustScoreResult();
			
			if((biddingScore.getBsItemValueTotalL() + biddingScore.getBsItemValueTotalW()) > 0)
				bidderPercentage =  (int) ((((biddingScore.getBsPlayerBidTotalL() + biddingScore.getBsPlayerBidTotalL()) /
					(biddingScore.getBsItemValueTotalL() + biddingScore.getBsItemValueTotalW())) * 100) + 0.5);
			
			if(player.getTotalRolled() > otherPlayer.getTotalRolled())
				highRoller = true;
			
			if(questionScore.getNoOfTotalQuestions()>0)
			{
				fastPercentage = (int) (((questionScore.getTotalQTimeBonusOverall() / (questionScore.getNoOfTotalQuestions() * 80)) * 100) + 0.5);
				slowPercentage = (int) (100 -  fastPercentage);
				knowledgePercentage = questionScore.getNoOfTotalQCorrect() / questionScore.getNoOfTotalQuestions() * 100;
			}
			
			if(prisonScore.getNoOfPrisonScores() > 0)
			{
				coopPercentage = (int) ((prisonScore.getNoOfPrisonScoreCoop() / prisonScore.getNoOfPrisonScores()  * 100) + 0.5);
				betrayPercentage = (int) ((prisonScore.getNoOfPrisonScoreBetray() / prisonScore.getNoOfPrisonScores()  * 100) + 0.5);
			}
			resultPercentages[0] = new ResultPercentage("bid", imgHighBidder, bidderPercentage, "\nYou're a bit of a high bidder, you tend to bid around 50% and above during the bidding games.");
			System.out.println(""+ bidderPercentage);
			resultPercentages[1] = new ResultPercentage("fast", imgQuick, fastPercentage, "\nYou respond to questions very quickly, on average, in under 40% of the time you have available.");
			resultPercentages[2] = new ResultPercentage("slow", imgSlow, slowPercentage, "\nYou respond to questions quite slowly, on average, you use over 60% of the time you have available.");
			resultPercentages[3] = new ResultPercentage("knowledge", imgKnowledge, knowledgePercentage, "\nYou seem very kowledgeable, you've gotten over 70% of your questions correct.");
			resultPercentages[4] = new ResultPercentage("coop", imgCoop, coopPercentage, "\nYou prefer to cooperate rather than betray.");
			resultPercentages[5] = new ResultPercentage("betray", imgBetray, betrayPercentage, "\nYou prefer to betray rather than cooperate.");
			
			Arrays.sort(resultPercentages);
			
			ach2 = resultPercentages[0].getImgRank();
			message3 = resultPercentages[0].getAchievementDescription();
			message4 = resultPercentages[1].getAchievementDescription();
				
				
			ach3 = resultPercentages[1].getImgRank();
			if(resultPercentages[1].getPercentage() < 50)
			{
				ach3 = imgRoller;
				message4 = "You seemed quite lucky, achieving a number of high rolls! But that doesn't really matter too much.";
			}
			if(ach3 == null)
			{
				ach3 = imgRoller;
				message4 = "You seemed quite lucky, achieving a number of high rolls! But that doesn't really matter too much.";
			}
			
			if(resultPercentages[0].getPercentage() == 0)
			{
				ach2 = imgNone;
				ach3 = imgNone;
				message3 = "";
				message4 = "";
			}
			if(resultPercentages[1].getPercentage() == 0)
			{
				ach3 = imgNone;
				message4 = "";
			}
			
			overallPointsAvailable = (int) (biddingScore.getPointsAvailable() + questionScore.getPointsAvailable() 
					+ prisonScore.getPointsAvailable() + ultimatumScore.getPointsAvailable() + trustScore.getPointsAvailable());
			if(overallPointsAvailable == 0 || (playerScore-1000 <= 0))
				rankPercentage = 0;
			else
				rankPercentage = ((((double)playerScore-1000) / (double)overallPointsAvailable)) * 100;
			
			if(rankPercentage > 90)
			{
				message2 = rankDescriptions[0];
				playerRank = 0;
			}
			else if(rankPercentage > 70)
			{
				message2 = rankDescriptions[1];
				playerRank = 1;
			}
			else if(rankPercentage > 50)
			{
				message2 = rankDescriptions[2];
				playerRank = 2;
			}
			else if(rankPercentage > 30)
			{
				message2 = rankDescriptions[3];
				playerRank = 3;
			}
			else if(rankPercentage > 10)
			{
				message2 = rankDescriptions[4];
				playerRank = 4;
			}
			else
			{
				message2 = rankDescriptions[5];
				playerRank = 5;
			}
			
			// Calculate summary message
			summaryDescriptionModel.setHtml("<p>" + message1 + "</p><p>" + message2 + "</p><p>" + message3 + "</p><p>" + message4 + "</p>");
			
			Packet27QuestionAnswers questionAnswers = new Packet27QuestionAnswers();
			int answers[] = new int[HRRUClient.cs.getNo_of_questions()];
			for(int i = 0; i < answers.length; i++)
				answers[i] = -1;
			ArrayList<ActivityScore> activityScores = player.getActivityScores();
			for(int i = 0; i < activityScores.size(); i++)
			{
				answers[activityScores.get(i).getActivity_id()] = activityScores.get(i).getChoice();
			}
			questionAnswers.answers = answers;
			questionAnswers.questionScore = (int) player.getQuestionScoreResult().getTotalQPointsOverall();
			questionAnswers.bidScore = (int) player.getBiddingScoreResult().getBsAmountWonTotalW();
			questionAnswers.prisonScore = (int) player.getPrisonerScoreResult().getPsTotal();
			questionAnswers.trustScore = (int) player.getTrustScoreResult().getTsTotal();
			questionAnswers.ultScore = (int) player.getUltScoreResult().getUsTotal();
			questionAnswers.playerScore = player.getScore();
			client.sendTCP(questionAnswers);
			
			enter = true;
		}
		verdictPanel.setVisible(false);
		btnQuestion.setVisible(false);
		btnGame.setVisible(false);
		btnScoreboard.setVisible(false);
		btnEnd.setVisible(false);
		// Create Question UI
		rootPane.removeAllChildren();
		rootPane.add(verdictPanel);
		rootPane.add(btnQuestion);
		rootPane.add(btnGame);
		rootPane.add(btnScoreboard);
		rootPane.add(btnEnd);
		rootPane.add(lblFinish);
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
		/*
		player = new Player("player1");
		player.setPlayerCharacterID(2);
		player.setPlayerCharacter(characters[2]);
		player.setScore(10000);
		HRRUClient.cs.setP1(player);
		HRRUClient.cs.setP2(player);
		HRRUClient.cs.setPlayer(1);
		*/
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			player = HRRUClient.cs.getP1();
			playerScore = HRRUClient.cs.getP1().getScore();
			otherPlayer = HRRUClient.cs.getP2();
			otherPlayerScore = HRRUClient.cs.getP2().getScore();
		}
		else
		{
			player = HRRUClient.cs.getP2();
			playerScore = HRRUClient.cs.getP2().getScore();
			otherPlayer = HRRUClient.cs.getP1();
			otherPlayerScore = HRRUClient.cs.getP1().getScore();
		}
		
		player1 = HRRUClient.cs.getP1();
		player2 = HRRUClient.cs.getP2();
		
		finishLabelShown = false;
		enter = false;
		scorebackground = new Image("simple/playerscorebackground.png");
		// GUI
		btnQuestion = new Button("Question\nFeedback");
		btnQuestion.setSize(100, 50);
		btnQuestion.setPosition(75, 510);
		btnQuestion.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 1;
			}
		});
		btnQuestion.setTheme("verdictbutton");
		
		btnGame = new Button("Game\nFeedback");
		btnGame.setSize(100, 50);
		btnGame.setPosition(180,510);
		btnGame.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 2;
			}
		});
		btnGame.setTheme("verdictbutton");
		
		btnScoreboard = new Button("Scoreboard");
		btnScoreboard.setSize(100, 50);
		btnScoreboard.setPosition(285,510);
		btnScoreboard.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 3;
			}
		});
		btnScoreboard.setTheme("verdictbutton");
		
		btnEnd = new Button("Finish");
		btnEnd.setSize(100, 50);
		btnEnd.setPosition(390,510);
		btnEnd.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 4;
			}
		});
		btnEnd.setTheme("verdictbutton");
		
		
		gcw = gc.getWidth();
		gch = gc.getHeight();
		done = false;
		// setup font variables
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
		
		// setup achievement images
		imgNone = new Image("achievement/achievement_none.png");
		imgBetray = new Image("achievement/achievement_betrayer.png");
		imgHighBidder = new Image("achievement/achievement_coin.png");
		imgCoop = new Image("achievement/achievement_coop.png");
		imgKnowledge = new Image("achievement/achievement_knowledgeable.png");
		imgQuick = new Image("achievement/achievement_quick.png");
		imgRoller = new Image("achievement/achievement_roller.png");
		imgSlow = new Image("achievement/achievement_slow.png");
		imgWinner = new Image("achievement/achievement_winner2.png");
		imgLose = new Image("achievement/achievement_lose.png");
		
		// setup rank images
		rankA = new Image("achievement/rank/A.png");
		rankB = new Image("achievement/rank/B.png");
		rankC = new Image("achievement/rank/C.png");
		rankD = new Image("achievement/rank/D.png");
		rankE = new Image("achievement/rank/E.png");
		rankS = new Image("achievement/rank/S.png");
		ranks = new Image[6];
		ranks[0] = rankS;
		ranks[1] = rankA;
		ranks[2] = rankB;
		ranks[3] = rankC;
		ranks[4] = rankD;
		ranks[5] = rankE;
		rankLetter = rankE;
		
		rankDescriptions[0] = "You achieved highest rank: SUPER. You've demonstrated an exceptionally high level of rational and logical thinking!";
		rankDescriptions[1] = "\nGreat Job! You've demonstrated a very high level of rational and logical thinking!";
		rankDescriptions[2] = "\nGood Job! You've demonstrated an above average level of rational and logical thinking!";
		rankDescriptions[3] = "\nNot bad... You've demonstrated an  average level of rational and logical thinking!";
		rankDescriptions[4] = "\nYou can do better... You've demonstrated a below average level of rational and logical thinking, however this could be becuase of the other player, maybe try against someone else?";
		rankDescriptions[5] = "\nHmm... the results weren't very promising. According to the results you've demonstrated a low level of rational and logical thinking, however this could be becuase of the other player, maybe try against someone else?";
		
		
		verdictPanel = new DialogLayout();
		verdictPanel.setPosition(50,120);
		verdictPanel.setSize(701-245-20, 462-255-20);
		verdictPanel.setTheme("verdict-panel");
		
		summaryDescriptionModel = new HTMLTextAreaModel();
		summaryDescription = new TextArea(summaryDescriptionModel);
		summaryDescription.setPosition(0, 0);
		summaryDescription.setSize(700,300);
		summaryDescription.setTheme("verdicttextarea");
		
		lblFinish = new Label("The Game has Ended. \n\n Your Results Will Appear in ... " + finishTimer);
		lblFinish.setTheme("statuswhite");
		lblFinish.setPosition(0, 250);
		lblFinish.setSize(800, 100);
		verdictPanel.setHorizontalGroup(verdictPanel.createParallelGroup()
				.addWidget(summaryDescription));
		
		verdictPanel.setVerticalGroup(verdictPanel.createParallelGroup()
				.addWidget(summaryDescription));
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawImage(new Image("simple/questionbg.png"), 0, 0);
		if(finishLabelShown)
		{
			g.drawImage(ach1, ach1_x, ach1_y);
			g.drawImage(ach2, ach2_x, ach2_y);
			g.drawImage(ach3, ach3_x, ach3_y);
			g.drawImage(rankLetter, 525, 375);
			g.setFont(titleFont.get());
			g.drawString("> " + start_message + "" + ticker, 320, 80);
			g.drawImage(scorebackground, 0,0);
			g.setFont(mainFont.get());
			g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 13,13);
			g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 13,55);
			g.drawString("" + player1.getName(), 65, 22);
			g.drawString("" + player2.getName(), 65, 64);
			g.drawString("" + player1.getScore(), 204, 22);
			g.drawString("" + player2.getScore(), 204, 64);
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		clock+=delta;
		rankClock+=delta;
		clock3 += delta;
		clock2 += delta;
		
		// has the finish label been shown
		if(finishLabelShown)
		{
			verdictPanel.setVisible(true);
			btnQuestion.setVisible(true);
			btnGame.setVisible(true);
			btnScoreboard.setVisible(true);
			btnEnd.setVisible(true);
			lblFinish.setVisible(false);
		}
		// full message ticker
		if(clock3 > 100){
			if(full_start_counter < full_start_message.length()) {
				start_message += full_start_message.substring(full_start_counter, full_start_counter+1);
				full_start_counter++;
				clock3-=100;
			}
		}
		// ticker symbol
		if(clock2>999) {
			if(!finishLabelShown)
			{
				finishTimer--;
				lblFinish.setText("The Game has Ended. \n\n Your Results Will Appear in ... " + finishTimer);
				if(finishTimer == 0)
					finishLabelShown = true;
			}
			clock2-=1000;
			if(tickerBoolean) {
				ticker = "|";
				tickerBoolean = false;
			}
			else {
				ticker = "";
				tickerBoolean = true;
			}
		}
		
		if(!done) {
			// timer tick for rank image
			if(clock < 1000)
				rankClockM = 20;
			else if(clock <2000)
				rankClockM = 40;
			else if(clock <3000)
				rankClockM = 80;
			else if(clock <4000)
				rankClockM = 120;
			else if(clock <5000)
				rankClockM = 240;
			else if(clock <6000)
				rankClockM = 480;
			else
				rankClock = 1;
		
			if(rankClock % rankClockM < 10 && rankClock != 1) {
					rankLetter = ranks[rankCounter];
					rankCounter++;
					if(rankCounter==6)
						rankCounter=0;
					rankClock = 0;
			}
			// set rank image
			else if(rankClock == 1) {
				rankLetter = ranks[playerRank];
				done = true;
			}
		}
		
		if(enterState == 1)
		{
			sbg.enterState(questionstats);
		}
		else if(enterState == 2)
		{
			sbg.enterState(bidstats);
		}
		else if(enterState == 3)
		{
			sbg.enterState(scoreboard);
		}
		else if(enterState == 4)
		{
			sbg.enterState(questionnaire);
		}
	}

	@Override
	public int getID() {
		return 17;
	}

}