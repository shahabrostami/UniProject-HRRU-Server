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

public class TrustStatistics extends BasicTWLGameState {
	// set up state variables
	public Client client;
	DialogLayout trustStatPanel, leftPanel, rightPanel;
	Button btnBack;
	ToggleButton btnBid, btnUlt, btnTrust, btnPrisoner;
	private int enterState;
	
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
	
	// Trust UI
	Label lblGive, lblReturn, lblTotal;
	Label lblNoOfTrustScores, lblNoOfTrustScoreGiver, lblNoOfTrustScoreReturner;
	Label lblTsPlayerGiveAvg, lblTsPlayerReturnAvg, lblTsPlayerReceiveAvg, lblTsPlayerGiveProfitAvg, lblTsPlayerReturnProfitAvg;
	Label lblTsPlayerGiveTotal, lblTsPlayerReturnTotal, lblTsPlayerReceiveTotal, lblTsPlayerGiveProfitTotal, lblTsPlayerReturnProfitTotal;
	Label lblTsAvg, lblTsTotal;
	
	Label lNoOfTrustScores, lNoOfTrustScoreGiver, lNoOfTrustScoreReturner;
	Label lTsPlayerGiveAvg, lTsPlayerReturnAvg, lTsPlayerReceiveAvg, lTsPlayerGiveProfitAvg, lTsPlayerReturnProfitAvg;
	Label lTsPlayerGiveTotal, lTsPlayerReturnTotal, lTsPlayerReceiveTotal, lTsPlayerGiveProfitTotal, lTsPlayerReturnProfitTotal;
	Label lTsAvg, lTsTotal;
	
	private TrustScore trustScore;
	private ArrayList<TrustScore> trustScores;
	
	// TrustScore Statistics
	private int noOfTrustScores;
	private int noOfTrustScoreGiver;
	private int noOfTrustScoreReturner;
	private int tsPlayerGiveAvg, tsPlayerReturnAvg, tsPlayerReceiveAvg, tsPlayerGiveProfitAvg, tsPlayerReturnProfitAvg;
	private double tsPlayerGiveTotal, tsPlayerReturnTotal, tsPlayerReceiveTotal, tsPlayerGiveProfitTotal, tsPlayerReturnProfitTotal;
	private int tsAvg;
	private double tsTotal;
	private int maxToGive, maxToReturn, maxToReceive, playerReceiveValue, playerGiveValue, playerReturnValue, multiplier, playerGiveProfit, playerReturnProfit;
	private int playerID;
	private int playerScore;
	
	void reset() {
		// Reset Trust result variables
		noOfTrustScores = 0;
		noOfTrustScoreGiver = 0;
		noOfTrustScoreReturner = 0;
		tsPlayerGiveAvg = 0; tsPlayerReturnAvg = 0; tsPlayerReceiveAvg = 0; tsPlayerGiveProfitAvg = 0; tsPlayerReturnProfitAvg = 0;
		tsPlayerGiveTotal = 0; tsPlayerReturnTotal = 0; tsPlayerReceiveTotal = 0; tsPlayerGiveProfitTotal = 0; tsPlayerReturnProfitTotal = 0;
		setMaxToGive(0); maxToReturn = 0; maxToReceive = 0; playerReceiveValue = 0; playerGiveValue = 0; playerReturnValue = 0; setMultiplier(0); playerGiveProfit = 0; playerReturnProfit = 0;
		tsTotal = 0; tsAvg = 0;
	}
	
	public TrustStatistics(int main) {
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
		btnBid.setActive(false);
		btnTrust.setActive(true);
		btnPrisoner.setActive(false);
		btnUlt.setActive(false);
		
		// SETUP UI
		rootPane.removeAllChildren();
		rootPane.add(trustStatPanel);
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
		trustStatPanel = new DialogLayout();
		trustStatPanel.setTheme("trustgamestat-panel");
		trustStatPanel.setSize(630,270);
		trustStatPanel.setPosition(50,100);
		
		leftPanel = new DialogLayout();
		rightPanel = new DialogLayout();
		
		lblGive = new Label("When Giving: ");
		lblGive.setTheme("labelscoretotal");
		lblReturn = new Label("When Returning: ");
		lblReturn.setTheme("labelscoretotal");
		lblTotal = new Label("Total: ");
		lblTotal.setTheme("labelscoretotalleft");
		
		lblNoOfTrustScoreGiver = new Label("Number of Games: ");
		lNoOfTrustScoreGiver = new Label("");
		lblNoOfTrustScoreGiver.setTheme("questionatari8r");
		lNoOfTrustScoreGiver.setTheme("questionatari8r");
		
		lblTsPlayerGiveAvg = new Label("Give Average: ");
		lTsPlayerGiveAvg = new Label("2");
		lblTsPlayerGiveAvg.setTheme("questionatari8r");
		lTsPlayerGiveAvg.setTheme("questionatari8r");
		
		lblTsPlayerGiveTotal = new Label("Give Total: ");
		lTsPlayerGiveTotal = new Label("2");
		lblTsPlayerGiveTotal.setTheme("questionatari8r");
		lTsPlayerGiveTotal.setTheme("questionatari8r");
		
		lblTsPlayerReceiveAvg  = new Label("Receive Average: ");
		lTsPlayerReceiveAvg  = new Label("2");
		lblTsPlayerReceiveAvg.setTheme("questionatari8r");
		lTsPlayerReceiveAvg.setTheme("questionatari8r");
		
		lblTsPlayerReceiveTotal = new Label("Receive Total: ");
		lTsPlayerReceiveTotal  = new Label("2");
		lblTsPlayerReceiveTotal.setTheme("questionatari8r");
		lTsPlayerReceiveTotal.setTheme("questionatari8r");
		
		lblTsPlayerGiveProfitAvg = new Label("Profit Average: ");
		lTsPlayerGiveProfitAvg  = new Label("2");
		lblTsPlayerGiveProfitAvg.setTheme("questionatari8r");
		lTsPlayerGiveProfitAvg.setTheme("questionatari8r");
		
		lblTsPlayerGiveProfitTotal = new Label("Profit Total: ");
		lTsPlayerGiveProfitTotal  = new Label("2");
		lblTsPlayerGiveProfitTotal.setTheme("questionatari8r");
		lTsPlayerGiveProfitTotal.setTheme("questionatari8r");
		
		DialogLayout.Group hLPLeft = leftPanel.createParallelGroup(lblNoOfTrustScoreGiver, lblTsPlayerGiveAvg, lblTsPlayerGiveTotal, lblTsPlayerReceiveAvg, lblTsPlayerReceiveTotal, lblTsPlayerGiveProfitAvg, lblTsPlayerGiveProfitTotal);
		DialogLayout.Group hLPRight = leftPanel.createParallelGroup(lNoOfTrustScoreGiver, lTsPlayerGiveAvg, lTsPlayerGiveTotal, lTsPlayerReceiveAvg, lTsPlayerReceiveTotal, lTsPlayerGiveProfitAvg, lTsPlayerGiveProfitTotal);
		
		leftPanel.setHorizontalGroup(leftPanel.createParallelGroup()
				.addWidget(lblGive)
				.addGroup(leftPanel.createSequentialGroup(hLPLeft, hLPRight)));
		
		leftPanel.setVerticalGroup(leftPanel.createSequentialGroup()
				.addWidget(lblGive)
				.addGap(10).addGroup(leftPanel.createParallelGroup(lblNoOfTrustScoreGiver, lNoOfTrustScoreGiver))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblTsPlayerGiveAvg, lTsPlayerGiveAvg))
				.addGroup(leftPanel.createParallelGroup(lblTsPlayerGiveTotal, lTsPlayerGiveTotal))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblTsPlayerReceiveAvg, lTsPlayerReceiveAvg))
				.addGroup(leftPanel.createParallelGroup(lblTsPlayerReceiveTotal, lTsPlayerReceiveTotal))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblTsPlayerGiveProfitAvg, lTsPlayerGiveProfitAvg))
				.addGroup(leftPanel.createParallelGroup(lblTsPlayerGiveProfitTotal, lTsPlayerGiveProfitTotal)));
		
		lblNoOfTrustScoreReturner = new Label("Number of Games: ");
		lNoOfTrustScoreReturner = new Label("");
		lblNoOfTrustScoreReturner.setTheme("questionatari8r");
		lNoOfTrustScoreReturner.setTheme("questionatari8r");
		
		lblTsPlayerReturnAvg  = new Label("Return Average: ");
		lTsPlayerReturnAvg = new Label("2");
		lblTsPlayerReturnAvg.setTheme("questionatari8r");
		lTsPlayerReturnAvg.setTheme("questionatari8r");

		lblTsPlayerReturnTotal  = new Label("Return Total: ");
		lTsPlayerReturnTotal = new Label("2");
		lblTsPlayerReturnTotal.setTheme("questionatari8r");
		lTsPlayerReturnTotal.setTheme("questionatari8r");
		
		lblTsPlayerReturnProfitAvg = new Label("Profit Average: ");
		lTsPlayerReturnProfitAvg = new Label("2");
		lblTsPlayerReturnProfitAvg.setTheme("questionatari8r");
		lTsPlayerReturnProfitAvg.setTheme("questionatari8r");
		
		lblTsPlayerReturnProfitTotal = new Label("Profit Total: ");
		lTsPlayerReturnProfitTotal = new Label("2");
		lblTsPlayerReturnProfitTotal.setTheme("questionatari8r");
		lTsPlayerReturnProfitTotal.setTheme("questionatari8r");
		
		lblNoOfTrustScores = new Label("Number of Games: ");
		lNoOfTrustScores = new Label("2");
		lblNoOfTrustScores.setTheme("questionatari8r");
		lNoOfTrustScores.setTheme("questionatari8r");

		lblTsAvg  = new Label("Average: ");
		lTsAvg  = new Label("2");
		lblTsAvg.setTheme("questionatari8r");
		lTsAvg .setTheme("questionatari8r");
		
		lblTsTotal  = new Label("Total: ");
		lTsTotal  = new Label("2");
		lblTsTotal.setTheme("questionatari8r");
		lTsTotal .setTheme("questionatari8r");
		
		DialogLayout.Group hRPLeft = rightPanel.createParallelGroup(lblNoOfTrustScoreReturner, lblTsPlayerReturnAvg, lblTsPlayerReturnTotal, lblTsPlayerReturnProfitAvg, lblTsPlayerReturnProfitTotal, lblNoOfTrustScores, lblTsAvg, lblTsTotal);
		DialogLayout.Group hRPRight = rightPanel.createParallelGroup(lNoOfTrustScoreReturner, lTsPlayerReturnAvg, lTsPlayerReturnTotal, lTsPlayerReturnProfitAvg, lTsPlayerReturnProfitTotal, lNoOfTrustScores, lTsAvg, lTsTotal);
		
		rightPanel.setHorizontalGroup(rightPanel.createParallelGroup()
				.addWidget(lblReturn)
				.addWidget(lblTotal)
				.addGap(30).addGroup(rightPanel.createSequentialGroup(hRPLeft, hRPRight)));
		
		rightPanel.setVerticalGroup(rightPanel.createSequentialGroup()
				.addWidget(lblReturn)
				.addGap(10).addGroup(rightPanel.createParallelGroup(lblNoOfTrustScoreReturner, lNoOfTrustScoreReturner))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblTsPlayerReturnAvg, lTsPlayerReturnAvg))
				.addGroup(rightPanel.createParallelGroup(lblTsPlayerReturnTotal, lTsPlayerReturnTotal))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblTsPlayerReturnProfitAvg, lTsPlayerReturnProfitAvg))
				.addGroup(rightPanel.createParallelGroup(lblTsPlayerReturnProfitTotal, lTsPlayerReturnProfitTotal))
				.addGap(30).addWidget(lblTotal)
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblNoOfTrustScores, lNoOfTrustScores))
				.addGap(10).addGroup(rightPanel.createParallelGroup(lblTsAvg, lTsAvg))
				.addGroup(rightPanel.createParallelGroup(lblTsTotal, lTsTotal)));
		
		
		DialogLayout.Group hLeftPanel = trustStatPanel.createSequentialGroup(leftPanel).addGap(100);
		DialogLayout.Group hRightPanel = trustStatPanel.createSequentialGroup(rightPanel);
		
		trustStatPanel.setHorizontalGroup(trustStatPanel.createParallelGroup()
				.addGroup(trustStatPanel.createSequentialGroup(hLeftPanel, hRightPanel)));
		
		trustStatPanel.setVerticalGroup(trustStatPanel.createSequentialGroup()
				.addGroup(trustStatPanel.createParallelGroup(leftPanel, rightPanel)));
		
		// RESET VARIABLES
		reset();
		// set player variable
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			playerScore = HRRUClient.cs.getP1().getScore();
			trustScores = HRRUClient.cs.getP1().getTrustScores();
		}
		else
		{
			playerScore = HRRUClient.cs.getP2().getScore();
			trustScores = HRRUClient.cs.getP2().getTrustScores();
		}
		
		
		////////////////////
		////trust scores////
		////////////////////
		for(int i = 0; i < trustScores.size(); i++)
		{
			noOfTrustScores++;
			trustScore = trustScores.get(i);
			if(trustScore.getPlayerGive() == playerID)
			{
				noOfTrustScoreGiver ++;
				playerGiveValue += trustScore.getPlayerGiveValue();
				setMaxToGive(getMaxToGive() + trustScore.getMaxToGive());
				playerReceiveValue += trustScore.getPlayerReturnValue();
				maxToReceive += trustScore.getMaxToReturn();
				setMultiplier(getMultiplier() + trustScore.getMultiplier());
				playerGiveProfit += trustScore.getPlayerGiveProfit();
			}
			else
			{
				noOfTrustScoreReturner++;
				playerReturnValue += trustScore.getPlayerReturnValue();
				maxToReturn += trustScore.getMaxToReturn();
				setMultiplier(getMultiplier() + trustScore.getMultiplier());
				playerReturnProfit += trustScore.getPlayerReturnProfit();
			}
		}
		
		// Calculate trustScore averages
		if(noOfTrustScores > 0)
		{
			if(noOfTrustScoreGiver > 0)
			{
				tsPlayerGiveAvg = (int) (playerGiveValue / noOfTrustScoreGiver + 0.5);
				tsPlayerGiveTotal = (int) playerGiveValue;
				tsPlayerReceiveAvg = (int) (playerReceiveValue / noOfTrustScoreGiver + 0.5);
				tsPlayerReceiveTotal = (int) playerReceiveValue;
				tsPlayerGiveProfitAvg = (int) (playerGiveProfit / noOfTrustScoreGiver + 0.5);
				tsPlayerGiveProfitTotal = (int) playerGiveProfit;
			}
			if(noOfTrustScoreReturner > 0)
			{
				tsPlayerReturnAvg = (int) (playerReturnValue / noOfTrustScoreReturner + 0.5);
				tsPlayerReturnTotal = (int) playerReturnValue;
				tsPlayerReturnProfitAvg = (int) (playerReturnProfit / noOfTrustScoreReturner + 0.5);
				tsPlayerReturnProfitTotal = (int) playerReturnProfit;
			}
			tsTotal = (int) tsPlayerGiveProfitTotal + tsPlayerReturnProfitTotal;
			tsAvg = (int) tsTotal / noOfTrustScores;
		}
		
		lNoOfTrustScores.setText("" + noOfTrustScores);
		lNoOfTrustScoreGiver.setText("" + noOfTrustScoreGiver);
		lNoOfTrustScoreReturner.setText("" + noOfTrustScoreReturner);
		lTsPlayerGiveAvg.setText("" + tsPlayerGiveAvg); // + " / " + maxToGiveAvg);
		lTsPlayerReturnAvg.setText("" + tsPlayerReturnAvg); //  + " / " + maxToReturnAvg);
		lTsPlayerReceiveAvg.setText("" + tsPlayerReceiveAvg); // + " / " + maxToReceiveAvg);
		lTsPlayerGiveProfitAvg.setText("" + tsPlayerGiveProfitAvg);
		lTsPlayerReturnProfitAvg.setText("" + tsPlayerReturnProfitAvg);
		lTsPlayerGiveTotal.setText("" +(int) tsPlayerGiveTotal); // + " / " + maxToGive);
		lTsPlayerReturnTotal.setText("" + (int) tsPlayerReturnTotal); // + " / " + maxToReturn);
		lTsPlayerReceiveTotal.setText("" + (int) tsPlayerReceiveTotal); // + " / " + maxToReceive);
		lTsPlayerGiveProfitTotal.setText("" + (int) tsPlayerGiveProfitTotal);
		lTsPlayerReturnProfitTotal.setText("" + (int) tsPlayerReturnProfitTotal);
		lTsAvg.setText("" + tsAvg);
		lTsTotal.setText("" + (int) tsTotal);
		
		
		TrustScoreResult trustScoreResult = new TrustScoreResult(
				noOfTrustScores, 
				noOfTrustScoreGiver, 
				noOfTrustScoreReturner, 
				tsPlayerGiveAvg, 
				tsPlayerReturnAvg, 
				tsPlayerReceiveAvg, 
				tsPlayerGiveProfitAvg, 
				tsPlayerReturnProfitAvg, 
				tsPlayerGiveTotal, 
				tsPlayerReturnTotal, 
				tsPlayerReceiveTotal, 
				tsPlayerGiveProfitTotal, 
				tsPlayerReturnProfitTotal, 
				tsAvg, 
				tsTotal);

		if(noOfTrustScores > 0)
		{
			trustScoreResult.setPointsAvailable((maxToReceive/2) + (maxToReturn/2));
			trustScoreResult.setPercentage((int)((tsTotal/(playerScore-1000))*100+0.5));
			lTsTotal.setText("" + (int) tsTotal + " (" + (int)((tsTotal/(playerScore-1000))*100+0.5) +  "% of your score)");
		}
		if(playerID == 1)
			HRRUClient.cs.getP1().setTrustScoreResult(trustScoreResult);
		else
			HRRUClient.cs.getP2().setTrustScoreResult(trustScoreResult);
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
		return 23;
	}

	public int getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(int multiplier) {
		this.multiplier = multiplier;
	}

	public int getMaxToGive() {
		return maxToGive;
	}

	public void setMaxToGive(int maxToGive) {
		this.maxToGive = maxToGive;
	}

}