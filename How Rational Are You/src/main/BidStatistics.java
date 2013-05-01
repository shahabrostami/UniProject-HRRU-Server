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

public class BidStatistics extends BasicTWLGameState {
	// initialise gui
	public Client client;
	DialogLayout bidStatPanel, leftPanel, rightPanel;
	ToggleButton btnBid, btnUlt, btnTrust, btnPrisoner;
	// initialise state variables
	int gcw;
	int gch;
	boolean calculated;
	private final int bidstats = 21;
	private final int prisonerstats = 22;
	private final int truststats = 23;
	private final int ultstats = 24;
	// Ticker variables
	private int titleFontSize = 60;
	private Font loadFont, loadTitleFont;
	private BasicFont titleFont;
	private String start_message = "";
	private String full_start_message = "Let's see how you did...";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2 = 0;
	
	// Questions UI 
	Label lblWin, lblLose;
	Label lNoOfBidScores, lNoOfBidScoreWin, lNoOfBidScoreLose;
	Label lItemValueAvgW,  lItemValueTotalW,  lPlayerBidAvgW,  lPlayerBidTotalW,  lOtherPlayerBidAvgW,  lOtherPlayerBidTotalW,  lAmountWonAvgW,  lAmountWonTotalW;
	Label lItemValueAvgL,  lItemValueTotalL,  lPlayerBidAvgL,  lPlayerBidTotalL,  lOtherPlayerBidAvgL,  lOtherPlayerBidTotalL,  lAmountWonAvgL,  lAmountWonTotalL;
	
	Label lblNoOfBidScores, lblNoOfBidScoreWin, lblNoOfBidScoreLose;
	Label lblItemValueAvgW,  lblItemValueTotalW,  lblPlayerBidAvgW,  lblPlayerBidTotalW,  lblOtherPlayerBidAvgW,  lblOtherPlayerBidTotalW,  lblAmountWonAvgW,  lblAmountWonTotalW;
	Label lblItemValueAvgL,  lblItemValueTotalL,  lblPlayerBidAvgL,  lblPlayerBidTotalL,  lblOtherPlayerBidAvgL,  lblOtherPlayerBidTotalL,  lblAmountWonAvgL,  lblAmountWonTotalL;
	Button btnBack;
	
	private int enterState;
	private BiddingScore bidScore;
	private ArrayList<BiddingScore> bidScores;
	
	// BidScore Statistics
	private int noOfBidScores;
	private int noOfBidScoreWin;
	private int noOfBidScoreLose;
	private int bsItemValueAvgW, bsPlayerBidAvgW, bsOtherPlayerBidAvgW, bsAmountWonAvgW;
	private double bsAmountWonTotalW, bsItemValueTotalW, bsPlayerBidTotalW, bsOtherPlayerBidTotalW, bsItemValueTotalL, bsPlayerBidTotalL, bsOtherPlayerBidTotalL,bsAmountWonTotalL;
	private int bsItemValueAvgL, bsPlayerBidAvgL, bsOtherPlayerBidAvgL, bsAmountWonAvgL;
	private int itemValueW, playerBidW, otherPlayerBidW, amountWonW;
	private int itemValueL, playerBidL, otherPlayerBidL, amountWonL;
	private int playerID;
	private int playerScore;
	
	void reset() {
		// Reset Bid result variables
		 noOfBidScores = 0;
		 noOfBidScoreWin = 0;
		 noOfBidScoreLose = 0;
		 bsItemValueAvgW = 0; bsItemValueTotalW = 0; bsPlayerBidAvgW = 0; bsPlayerBidTotalW = 0; bsOtherPlayerBidAvgW = 0; bsOtherPlayerBidTotalW = 0; bsAmountWonAvgW = 0; bsAmountWonTotalW = 0;
		 bsItemValueAvgL = 0; bsItemValueTotalL = 0; bsPlayerBidAvgL = 0; bsPlayerBidTotalL = 0; bsOtherPlayerBidAvgL = 0; bsOtherPlayerBidTotalL = 0; bsAmountWonAvgL = 0; bsAmountWonTotalL = 0;
		 itemValueW = 0; playerBidW = 0; otherPlayerBidW = 0; amountWonW = 0;
		 itemValueL = 0; playerBidL = 0; otherPlayerBidL = 0; amountWonL = 0;
	}
	
	public BidStatistics(int main) {
		client = HRRUClient.conn.getClient();
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		// RESET VARIABLES
		start_message = "";
		full_start_message = "Let's see how you did...";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		enterState = 0;
		
		// reset button active
		btnBid.setActive(true);
		btnTrust.setActive(false);
		btnPrisoner.setActive(false);
		btnUlt.setActive(false);
		
		// setup gui
		rootPane.removeAllChildren();
		rootPane.add(bidStatPanel);
		rootPane.add(btnBack);
		rootPane.add(btnBid);
		rootPane.add(btnTrust);
		rootPane.add(btnPrisoner);
		rootPane.add(btnUlt);
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
		calculated = false;
		
		// set up font variables
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
		
		//btn back
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
		// btnbid
		btnBid = new ToggleButton("Bid Game");
		btnBid.setSize(160, 40);
		btnBid.setPosition(60,500);
		btnBid.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 2;
			}
		});
		btnBid.setTheme("menubutton");
		// btnprisoner
		btnPrisoner = new ToggleButton("Cooperate Betray\n Game");
		btnPrisoner.setSize(160, 40);
		btnPrisoner.setPosition(230,500);
		btnPrisoner.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 3;
			}
		});
		btnPrisoner.setTheme("menubutton");
		// btntrust
		btnTrust = new ToggleButton("Trust Game");
		btnTrust.setSize(160, 40);
		btnTrust.setPosition(400,500);
		btnTrust.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 4;
			}
		});
		btnTrust.setTheme("menubutton");
		// btnult
		btnUlt = new ToggleButton("Ultimatum Game");
		btnUlt.setSize(160, 40);
		btnUlt.setPosition(580,500);
		btnUlt.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 5;
			}
		});
		btnUlt.setTheme("menubutton");
		
		// Set up trustpanel widgets
		bidStatPanel = new DialogLayout();
		bidStatPanel.setTheme("bidgamestat-panel");
		bidStatPanel.setSize(630,300);
		bidStatPanel.setPosition(50,100);
		
		leftPanel = new DialogLayout();
		rightPanel = new DialogLayout();
		
		lblWin = new Label("Won: ");
		lblWin.setTheme("labelscoretotalleft");
		lblLose = new Label("Lost: ");
		lblLose.setTheme("labelscoretotalleft");
		
		lblNoOfBidScores = new Label("Number of Games: ");
		lNoOfBidScores = new Label("");
		lblNoOfBidScores.setTheme("questionatari8r");
		lNoOfBidScores.setTheme("questionatari8r");
		
		lblNoOfBidScoreWin = new Label("Number of Games Won: ");
		lNoOfBidScoreWin = new Label("0");
		lblNoOfBidScoreWin.setTheme("questionatari8r");
		lNoOfBidScoreWin.setTheme("questionatari8r");
		
		lblItemValueAvgW  = new Label("Item Value Average: ");
		lItemValueAvgW  = new Label("0");
		lblItemValueAvgW.setTheme("questionatari8r");
		lItemValueAvgW.setTheme("questionatari8r");
		
		lblItemValueTotalW  = new Label("Item Value Total: ");
		lItemValueTotalW  = new Label("0");
		lblItemValueTotalW.setTheme("questionatari8r");
		lItemValueTotalW.setTheme("questionatari8r");
		
		lblPlayerBidAvgW = new Label("Your Average Bid: ");
		lPlayerBidAvgW  = new Label("0");
		lblPlayerBidAvgW.setTheme("questionatari8r");
		lPlayerBidAvgW.setTheme("questionatari8r");
		
		lblPlayerBidTotalW = new Label("Your Total Bids: ");
		lPlayerBidTotalW  = new Label("0");
		lblPlayerBidTotalW.setTheme("questionatari8r");
		lPlayerBidTotalW.setTheme("questionatari8r");
		
		lblOtherPlayerBidAvgW = new Label("Other Player Average Bid: ");
		lOtherPlayerBidAvgW  = new Label("0");
		lblOtherPlayerBidAvgW.setTheme("questionatari8r");
		lOtherPlayerBidAvgW.setTheme("questionatari8r");
		
		lblOtherPlayerBidTotalW = new Label("Other Player Total Bids: ");
		lOtherPlayerBidTotalW  = new Label("0");
		lblOtherPlayerBidTotalW.setTheme("questionatari8r");
		lOtherPlayerBidTotalW.setTheme("questionatari8r");
		
		lblAmountWonAvgW = new Label("Your Average Profit: ");
		lAmountWonAvgW  = new Label("0");
		lblAmountWonAvgW.setTheme("questionatari8r");
		lAmountWonAvgW.setTheme("questionatari8r");
		
		lblAmountWonTotalW = new Label("Your Total Profit: ");
		lAmountWonTotalW  = new Label("0");
		lblAmountWonTotalW.setTheme("questionatari8r");
		lAmountWonTotalW.setTheme("questionatari8r");
		
		Label gap = new Label("");
		
		DialogLayout.Group hLPLeft = leftPanel.createParallelGroup(lblNoOfBidScoreWin, lblItemValueAvgW, lblItemValueTotalW, lblPlayerBidAvgW, lblPlayerBidTotalW, lblOtherPlayerBidAvgW, lblOtherPlayerBidTotalW, lblAmountWonAvgW, lblAmountWonTotalW);
		DialogLayout.Group hLPRight = leftPanel.createParallelGroup(lNoOfBidScoreWin,lItemValueAvgW,lItemValueTotalW,lPlayerBidAvgW, lPlayerBidTotalW, lOtherPlayerBidAvgW,lOtherPlayerBidTotalW,lAmountWonAvgW,gap);
		
		leftPanel.setHorizontalGroup(leftPanel.createParallelGroup()
				.addWidget(lblWin)
				.addGroup(leftPanel.createSequentialGroup(hLPLeft, hLPRight))
				.addWidget(lAmountWonTotalW));
		
		leftPanel.setVerticalGroup(leftPanel.createSequentialGroup()
				.addWidget(lblWin)
				.addGroup(leftPanel.createParallelGroup(lblNoOfBidScoreWin, lNoOfBidScoreWin))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblItemValueAvgW, lItemValueAvgW))
				.addGroup(leftPanel.createParallelGroup(lblItemValueTotalW, lItemValueTotalW))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblPlayerBidAvgW, lPlayerBidAvgW))
				.addGroup(leftPanel.createParallelGroup(lblPlayerBidTotalW, lPlayerBidTotalW))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblOtherPlayerBidAvgW, lOtherPlayerBidAvgW))
				.addGroup(leftPanel.createParallelGroup(lblOtherPlayerBidTotalW, lOtherPlayerBidTotalW))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblAmountWonAvgW, lAmountWonAvgW))
				.addGap(30).addGroup(leftPanel.createParallelGroup(lblAmountWonTotalW, gap))
				.addWidget(lAmountWonTotalW));
		
		lblNoOfBidScoreLose = new Label("Number of Games Lost: ");
		lNoOfBidScoreLose = new Label("0");
		lblNoOfBidScoreLose.setTheme("questionatari8r");
		lNoOfBidScoreLose.setTheme("questionatari8r");
		
		lblItemValueAvgL  = new Label("Item Value Average: ");
		lItemValueAvgL  = new Label("0");
		lblItemValueAvgL.setTheme("questionatari8r");
		lItemValueAvgL.setTheme("questionatari8r");
		
		lblItemValueTotalL  = new Label("Item Value Total: ");
		lItemValueTotalL  = new Label("0");
		lblItemValueTotalL.setTheme("questionatari8r");
		lItemValueTotalL.setTheme("questionatari8r");
		
		lblPlayerBidAvgL = new Label("Your Average Bid: ");
		lPlayerBidAvgL  = new Label("0");
		lblPlayerBidAvgL.setTheme("questionatari8r");
		lPlayerBidAvgL.setTheme("questionatari8r");
		
		lblPlayerBidTotalL = new Label("Your Total Bids: ");
		lPlayerBidTotalL  = new Label("0");
		lblPlayerBidTotalL.setTheme("questionatari8r");
		lPlayerBidTotalL.setTheme("questionatari8r");
		
		lblOtherPlayerBidAvgL = new Label("Other Player Average Bid: ");
		lOtherPlayerBidAvgL  = new Label("0");
		lblOtherPlayerBidAvgL.setTheme("questionatari8r");
		lOtherPlayerBidAvgL.setTheme("questionatari8r");
		
		lblOtherPlayerBidTotalL = new Label("Other Player Total Bids: ");
		lOtherPlayerBidTotalL  = new Label("0");
		lblOtherPlayerBidTotalL.setTheme("questionatari8r");
		lOtherPlayerBidTotalL.setTheme("questionatari8r");
		
		lblAmountWonAvgL = new Label("Other Player Average Profit: ");
		lAmountWonAvgL  = new Label("0");
		lblAmountWonAvgL.setTheme("questionatari8r");
		lAmountWonAvgL.setTheme("questionatari8r");
		
		lblAmountWonTotalL = new Label("Other Player Total Profit: ");
		lAmountWonTotalL  = new Label("0");
		lblAmountWonTotalL.setTheme("questionatari8r");
		lAmountWonTotalL.setTheme("questionatari8r");
		
		DialogLayout.Group hRPLeft = rightPanel.createParallelGroup(lblNoOfBidScoreLose, lblItemValueAvgL, lblItemValueTotalL, lblPlayerBidAvgL, lblPlayerBidTotalL, lblOtherPlayerBidAvgL, lblOtherPlayerBidTotalL, lblAmountWonAvgL, lblAmountWonTotalL);
		DialogLayout.Group hRPRight = rightPanel.createParallelGroup(lNoOfBidScoreLose,lItemValueAvgL,lItemValueTotalL,lPlayerBidAvgL, lPlayerBidTotalL, lOtherPlayerBidAvgL,lOtherPlayerBidTotalL,lAmountWonAvgL,lAmountWonTotalL);
		
		rightPanel.setHorizontalGroup(rightPanel.createParallelGroup()
				.addWidget(lblLose)
				.addGroup(rightPanel.createSequentialGroup(hRPLeft, hRPRight)));
		
		rightPanel.setVerticalGroup(rightPanel.createSequentialGroup()
				.addWidget(lblLose)
				.addGroup(rightPanel.createParallelGroup(lblNoOfBidScoreLose, lNoOfBidScoreLose))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblItemValueAvgL, lItemValueAvgL))
				.addGroup(rightPanel.createParallelGroup(lblItemValueTotalL, lItemValueTotalL))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblPlayerBidAvgL, lPlayerBidAvgL))
				.addGroup(rightPanel.createParallelGroup(lblPlayerBidTotalL, lPlayerBidTotalL))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblOtherPlayerBidAvgL, lOtherPlayerBidAvgL))
				.addGroup(rightPanel.createParallelGroup(lblOtherPlayerBidTotalL, lOtherPlayerBidTotalL))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblAmountWonAvgL, lAmountWonAvgL))
				.addGroup(rightPanel.createParallelGroup(lblAmountWonTotalL, lAmountWonTotalL)));
		
		
		DialogLayout.Group hLeftPanel = bidStatPanel.createSequentialGroup(leftPanel).addGap(100);
		DialogLayout.Group hRightPanel = bidStatPanel.createSequentialGroup(rightPanel);
		
		DialogLayout.Group hLeftPanel2 = bidStatPanel.createSequentialGroup(lblNoOfBidScores);
		DialogLayout.Group hRightPanel2 = bidStatPanel.createSequentialGroup(lNoOfBidScores).addGap(380);
		
		bidStatPanel.setHorizontalGroup(bidStatPanel.createParallelGroup()
				.addGroup(bidStatPanel.createSequentialGroup(hLeftPanel2, hRightPanel2))
				.addGroup(bidStatPanel.createSequentialGroup(hLeftPanel, hRightPanel)));
		
		bidStatPanel.setVerticalGroup(bidStatPanel.createSequentialGroup()
				.addGroup(bidStatPanel.createParallelGroup(lblNoOfBidScores, lNoOfBidScores))
				.addGap(30).addGroup(bidStatPanel.createParallelGroup(leftPanel, rightPanel)));
		
		// RESET VARIABLES
		reset();
		// set player variable
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			playerScore = HRRUClient.cs.getP1().getScore();
			bidScores = HRRUClient.cs.getP1().getBiddingScores();
		}
		else
		{
			playerScore = HRRUClient.cs.getP2().getScore();
			bidScores = HRRUClient.cs.getP2().getBiddingScores();
		}
		
		//////////////////
		////bid scores////
		//////////////////
		if(bidScores.size() > 0)
		{
			for(int i = 0; i < bidScores.size(); i++)
			{
				noOfBidScores++;
				bidScore = bidScores.get(i);
				if(bidScore.isWin())
				{
					noOfBidScoreWin++;
					itemValueW += bidScore.getItemValue();
					playerBidW += bidScore.getPlayerBid();
					otherPlayerBidW += bidScore.getOtherPlayerBid();
					amountWonW += bidScore.getAmountWon();
					
				}
				else
				{
					noOfBidScoreLose++;
					itemValueL += bidScore.getItemValue();
					playerBidL += bidScore.getPlayerBid();
					otherPlayerBidL += bidScore.getOtherPlayerBid();
					amountWonL += (bidScore.getItemValue() - bidScore.getOtherPlayerBid());
				}
			}
			
		// 	Calculate bidScore averages
			if(noOfBidScoreWin > 0)
			{
				bsItemValueAvgW = (int) (itemValueW / noOfBidScoreWin + 0.5);
				bsItemValueTotalW = (int) itemValueW;
				bsPlayerBidAvgW = (int) (playerBidW / noOfBidScoreWin + 0.5);
				bsPlayerBidTotalW = (int) playerBidW;
				bsOtherPlayerBidAvgW = (int) (otherPlayerBidW / noOfBidScoreWin + 0.5);
				bsOtherPlayerBidTotalW = (int) otherPlayerBidW;
				bsAmountWonAvgW = (int) (amountWonW / noOfBidScoreWin + 0.5);
				bsAmountWonTotalW = (int) amountWonW;
			}
			if(noOfBidScoreLose > 0)
			{
				bsItemValueAvgL = (int) (itemValueL / noOfBidScoreLose + 0.5);
				bsItemValueTotalL = (int) itemValueL;
				bsPlayerBidAvgL = (int) (playerBidL / noOfBidScoreLose + 0.5);
				bsPlayerBidTotalL = (int) playerBidL;
				bsOtherPlayerBidAvgL = (int) (otherPlayerBidL / noOfBidScoreLose + 0.5);
				bsOtherPlayerBidTotalL = (int) otherPlayerBidL;
				bsAmountWonAvgL = (int) (amountWonL / noOfBidScoreLose + 0.5);
				bsAmountWonTotalL = (int) amountWonL;
			}
		}
		
		lNoOfBidScores.setText("" + noOfBidScores);
		lNoOfBidScoreWin.setText("" + noOfBidScoreWin);
		lItemValueAvgW.setText("" + bsItemValueAvgW); // + " / " + maxToGiveAvg);
		lItemValueTotalW.setText("" + (int) bsItemValueTotalW); //  + " / " + maxToReturnAvg);
		lPlayerBidAvgW.setText("" + bsPlayerBidAvgW); // + " / " + maxToReceiveAvg);
		lPlayerBidTotalW.setText("" + (int) bsPlayerBidTotalW);
		lOtherPlayerBidAvgW.setText("" + bsOtherPlayerBidAvgW);
		lOtherPlayerBidTotalW.setText("" + (int) bsOtherPlayerBidTotalW); // + " / " + maxToGive);
		lAmountWonAvgW.setText("" + bsAmountWonAvgW); // + " / " + maxToReturn);
				

		lNoOfBidScoreLose.setText("" + noOfBidScoreLose);
		lItemValueAvgL.setText("" + bsItemValueAvgL); 
		lItemValueTotalL.setText("" + (int) bsItemValueTotalL);
		lPlayerBidAvgL.setText("" + bsPlayerBidAvgL); 
		lPlayerBidTotalL.setText("" + (int) bsPlayerBidTotalL);
		lOtherPlayerBidAvgL.setText("" + bsOtherPlayerBidAvgL);
		lOtherPlayerBidTotalL.setText("" + (int) bsOtherPlayerBidTotalL); 
		lAmountWonAvgL.setText("" + bsAmountWonAvgL); 
		lAmountWonTotalL.setText("" + (int) bsAmountWonTotalL);
		
		BiddingScoreResult biddingScoreResult = new BiddingScoreResult(
				noOfBidScores, 
				noOfBidScoreWin, 
				noOfBidScoreLose, 
				bsItemValueAvgW, 
				bsItemValueTotalW, 
				bsPlayerBidAvgW, 
				bsPlayerBidTotalW, 
				bsOtherPlayerBidAvgW, 
				bsOtherPlayerBidTotalW, 
				bsAmountWonAvgW, 
				bsAmountWonTotalW, 
				bsItemValueAvgL, 
				bsItemValueTotalL, 
				bsPlayerBidAvgL, 
				bsPlayerBidTotalL, 
				bsOtherPlayerBidAvgL, 
				bsOtherPlayerBidTotalL, 
				bsAmountWonAvgL, 
				bsAmountWonTotalL, 
				itemValueW, 
				playerBidW, 
				otherPlayerBidW, 
				amountWonW, 
				itemValueL, 
				playerBidL, 
				otherPlayerBidL, 
				amountWonL);
		
		if(noOfBidScoreWin > 0)
		{
			biddingScoreResult.setPercentage((int)((bsAmountWonTotalW/playerScore-1000)*100+0.5));
			lAmountWonTotalW.setText("" + (int) bsAmountWonTotalW + " (" + (int)((bsAmountWonTotalW/(playerScore-1000))*100+0.5) +  "% of your score)");
		}
		
		if(playerID == 1)
			HRRUClient.cs.getP1().setBiddingScoreResult(biddingScoreResult);
		else
			HRRUClient.cs.getP2().setBiddingScoreResult(biddingScoreResult);
		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawImage(new Image("simple/questionbg.png"), 0, 0);
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, 50, 25);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
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
		if(enterState == 1)
			sbg.enterState(17);
		else if(enterState == 2)
			sbg.enterState(bidstats);
		else if(enterState == 3)
			sbg.enterState(prisonerstats);
		else if(enterState == 4)
			sbg.enterState(truststats);
		else if(enterState == 5)
			sbg.enterState(ultstats);
	}

	@Override
	public int getID() {
		return 21;
	}

}