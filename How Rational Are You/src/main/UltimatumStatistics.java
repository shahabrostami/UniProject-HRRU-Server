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

public class UltimatumStatistics extends BasicTWLGameState {
	// initialise ultimatum statistics variables and GUI
	public Client client;
	DialogLayout ultStatPanel, leftPanel, rightPanel;
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
	
	// Questions UI
	Label lblProposer, lblDecider, lblTotaler;
	Label lNoOfUltimatumScores;
	Label lNoOfUltimatumScoreProp, lNoOfUltimatumScoreDec;
	Label lNoOfPropSuccess, lNoOfDecSuccess;
	Label lOverallValueAvgP, lPlayerPropAvgP, lPlayerDecAvgP;
	Label lOverallValueAvgD, lPlayerPropAvgD, lPlayerDecAvgD;
	Label lOverallValueTotalP, lPlayerPropTotalP, lPlayerDecTotalP;
	Label lOverallValueTotalD, lPlayerPropTotalD, lPlayerDecTotalD;
	Label lAvg, lTotal;
	
	Label lblNoOfUltimatumScores;
	Label lblNoOfUltimatumScoreProp, lblNoOfUltimatumScoreDec;
	Label lblNoOfPropSuccess, lblNoOfDecSuccess;
	Label lblOverallValueAvgP, lblPlayerPropAvgP, lblPlayerDecAvgP;
	Label lblOverallValueAvgD, lblPlayerPropAvgD, lblPlayerDecAvgD;
	Label lblOverallValueTotalP, lblPlayerPropTotalP, lblPlayerDecTotalP;
	Label lblOverallValueTotalD, lblPlayerPropTotalD, lblPlayerDecTotalD;
	Label lblAvg, lblTotal;
	

	private UltimatumScore ultimatumScore;
	private ArrayList<UltimatumScore> ultimatumScores;
	
	// UltimatumScore Statistics
	private int noOfUltimatumScores;
	private int noOfUltimatumScoreProp, noOfUltimatumScoreDec;
	private int noOfPropSuccess, noOfDecSuccess;
	private int usOverallValueAvgP, usPlayerPropAvgP, usPlayerDecAvgP;
	private int usOverallValueAvgD, usPlayerPropAvgD, usPlayerDecAvgD;
	private double usOverallValueTotalP, usPlayerPropTotalP, usPlayerDecTotalP;
	private double usOverallValueTotalD, usPlayerPropTotalD, usPlayerDecTotalD;
	private int usAvg;
	private double usTotal;
	
	private int playerID;
	private int playerScore;
	
	void reset() {
		// Reset Ult result variables
		noOfUltimatumScores = 0;
		noOfUltimatumScoreProp = 0; noOfUltimatumScoreDec = 0;
		noOfPropSuccess = 0; noOfDecSuccess = 0;
		usOverallValueAvgP = 0; usPlayerPropAvgP = 0; usPlayerDecAvgP = 0;
		usOverallValueAvgD = 0; usPlayerPropAvgD = 0; usPlayerDecAvgD = 0;
		usOverallValueTotalP = 0; usPlayerPropTotalP = 0; usPlayerDecTotalP = 0;
		usOverallValueTotalD = 0; usPlayerPropTotalD = 0; usPlayerDecTotalD = 0;
		usAvg = 0; usTotal = 0;
	}
	
	public UltimatumStatistics(int main) {
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
		btnTrust.setActive(false);
		btnPrisoner.setActive(false);
		btnUlt.setActive(true);
		
		// SETUP GUI
		rootPane.removeAllChildren();
		rootPane.add(ultStatPanel);
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
		ultStatPanel = new DialogLayout();
		ultStatPanel.setTheme("ultgamestat-panel");
		ultStatPanel.setSize(630,270);
		ultStatPanel.setPosition(50,100);
		
		leftPanel = new DialogLayout();
		rightPanel = new DialogLayout();
		
		lblProposer = new Label("When Proposing: ");
		lblProposer.setTheme("labelscoretotalleft");
		lblDecider = new Label("When Deciding: ");
		lblDecider.setTheme("labelscoretotalleft");
		lblTotaler = new Label("Total: ");
		lblTotaler.setTheme("labelscoretotalleft");
		
		lblNoOfUltimatumScoreProp = new Label("Number of Games Proposing: ");
		lNoOfUltimatumScoreProp = new Label("");
		lblNoOfUltimatumScoreProp.setTheme("questionatari8r");
		lNoOfUltimatumScoreProp.setTheme("questionatari8r");
		
		lblOverallValueAvgP = new Label("Average Amount to Share: ");
		lOverallValueAvgP = new Label("0");
		lblOverallValueAvgP.setTheme("questionatari8r");
		lOverallValueAvgP.setTheme("questionatari8r");
		
		lblOverallValueTotalP = new Label("Toal Amount to Share: ");
		lOverallValueTotalP = new Label("0");
		lblOverallValueTotalP.setTheme("questionatari8r");
		lOverallValueTotalP.setTheme("questionatari8r");
		
		lblPlayerPropAvgP  = new Label("Your Average Share: ");
		lPlayerPropAvgP  = new Label("0");
		lblPlayerPropAvgP.setTheme("questionatari8r");
		lPlayerPropAvgP.setTheme("questionatari8r");
		
		lblPlayerPropTotalP = new Label("Your Total Share: ");
		lPlayerPropTotalP  = new Label("0");
		lblPlayerPropTotalP.setTheme("questionatari8r");
		lPlayerPropTotalP.setTheme("questionatari8r");
		
		lblPlayerDecAvgP  = new Label("Other Player Average Share: ");
		lPlayerDecAvgP  = new Label("0");
		lblPlayerDecAvgP.setTheme("questionatari8r");
		lPlayerDecAvgP.setTheme("questionatari8r");
		
		lblPlayerDecTotalP = new Label("Other Player Total Share: ");
		lPlayerDecTotalP  = new Label("0");
		lblPlayerDecTotalP.setTheme("questionatari8r");
		lPlayerDecTotalP.setTheme("questionatari8r");

		
		DialogLayout.Group hLPLeft = leftPanel.createParallelGroup(lblNoOfUltimatumScoreProp , lblOverallValueAvgP , lblOverallValueTotalP , lblPlayerPropAvgP  , lblPlayerPropTotalP , lblPlayerDecAvgP  , lblPlayerDecTotalP );
		DialogLayout.Group hLPRight = leftPanel.createParallelGroup(lNoOfUltimatumScoreProp , lOverallValueAvgP , lOverallValueTotalP , lPlayerPropAvgP  , lPlayerPropTotalP , lPlayerDecAvgP  , lPlayerDecTotalP );
		
		leftPanel.setHorizontalGroup(leftPanel.createParallelGroup()
				.addWidget(lblProposer)
				.addGroup(leftPanel.createSequentialGroup(hLPLeft, hLPRight)));
		
		leftPanel.setVerticalGroup(leftPanel.createSequentialGroup()
				.addWidget(lblProposer)
				.addGap(10).addGroup(leftPanel.createParallelGroup(lblNoOfUltimatumScoreProp, lNoOfUltimatumScoreProp))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblOverallValueAvgP, lOverallValueAvgP))
				.addGroup(leftPanel.createParallelGroup(lblOverallValueTotalP, lOverallValueTotalP))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblPlayerPropAvgP, lPlayerPropAvgP))
				.addGroup(leftPanel.createParallelGroup(lblPlayerPropTotalP, lPlayerPropTotalP))
				.addGap(20).addGroup(leftPanel.createParallelGroup(lblPlayerDecAvgP, lPlayerDecAvgP))
				.addGroup(leftPanel.createParallelGroup(lblPlayerDecTotalP, lPlayerDecTotalP)));
		
		lblNoOfUltimatumScoreDec = new Label("Number of Games Deciding: ");
		lNoOfUltimatumScoreDec = new Label("");
		lblNoOfUltimatumScoreDec.setTheme("questionatari8r");
		lNoOfUltimatumScoreDec.setTheme("questionatari8r");
		
		lblOverallValueAvgD = new Label("Average Amount to Share: ");
		lOverallValueAvgD = new Label("0");
		lblOverallValueAvgD.setTheme("questionatari8r");
		lOverallValueAvgD.setTheme("questionatari8r");
		
		lblOverallValueTotalD = new Label("Toal Amount to Share: ");
		lOverallValueTotalD = new Label("0");
		lblOverallValueTotalD.setTheme("questionatari8r");
		lOverallValueTotalD.setTheme("questionatari8r");
		
		lblPlayerPropAvgD  = new Label("Your Average Share: ");
		lPlayerPropAvgD  = new Label("0");
		lblPlayerPropAvgD.setTheme("questionatari8r");
		lPlayerPropAvgD.setTheme("questionatari8r");
		
		lblPlayerPropTotalD = new Label("Your Total Share: ");
		lPlayerPropTotalD  = new Label("0");
		lblPlayerPropTotalD.setTheme("questionatari8r");
		lPlayerPropTotalD.setTheme("questionatari8r");
		
		lblPlayerDecAvgD  = new Label("Other Player Average Share: ");
		lPlayerDecAvgD  = new Label("0");
		lblPlayerDecAvgD.setTheme("questionatari8r");
		lPlayerDecAvgD.setTheme("questionatari8r");
		
		lblPlayerDecTotalD = new Label("Other Player Total Share: ");
		lPlayerDecTotalD  = new Label("0");
		lblPlayerDecTotalD.setTheme("questionatari8r");
		lPlayerDecTotalD.setTheme("questionatari8r");
		
		lblNoOfUltimatumScores = new Label("Number of Games: ");
		lNoOfUltimatumScores  = new Label("0");
		lblNoOfUltimatumScores.setTheme("questionatari8r");
		lNoOfUltimatumScores.setTheme("questionatari8r");
		
		lblAvg = new Label("Average Share: ");
		lAvg  = new Label("0");
		lblAvg.setTheme("questionatari8r");
		lAvg.setTheme("questionatari8r");

		lblTotal = new Label("Total Share: ");
		lTotal  = new Label("0");
		lblTotal.setTheme("questionatari8r");
		lTotal.setTheme("questionatari8r");
		Label gap = new Label("");
		
		
		DialogLayout.Group hLDLeft = rightPanel.createParallelGroup(lblTotal, lblNoOfUltimatumScoreDec, lblOverallValueAvgD, lblOverallValueTotalD, lblPlayerPropAvgD , lblPlayerPropTotalD, lblPlayerDecAvgD , lblPlayerDecTotalD, lblNoOfUltimatumScores, lblAvg);
		DialogLayout.Group hLDRight = rightPanel.createParallelGroup(gap, lNoOfUltimatumScoreDec, lOverallValueAvgD, lOverallValueTotalD, lPlayerPropAvgD , lPlayerPropTotalD, lPlayerDecAvgD , lPlayerDecTotalD, lNoOfUltimatumScores, lAvg);
		
		rightPanel.setHorizontalGroup(rightPanel.createParallelGroup()
				.addWidget(lblDecider)
				.addWidget(lblTotaler)
				.addGap(30).addGroup(rightPanel.createSequentialGroup(hLDLeft, hLDRight))
				.addWidget(lTotal));
		
		rightPanel.setVerticalGroup(rightPanel.createSequentialGroup()
				.addWidget(lblDecider)
				.addGap(10).addGroup(rightPanel.createParallelGroup(lblNoOfUltimatumScoreDec, lNoOfUltimatumScoreDec))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblOverallValueAvgD, lOverallValueAvgD))
				.addGroup(rightPanel.createParallelGroup(lblOverallValueTotalD, lOverallValueTotalD))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblPlayerPropAvgD, lPlayerPropAvgD))
				.addGroup(rightPanel.createParallelGroup(lblPlayerPropTotalD, lPlayerPropTotalD))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblPlayerDecAvgD, lPlayerDecAvgD))
				.addGroup(rightPanel.createParallelGroup(lblPlayerDecTotalD, lPlayerDecTotalD))
				.addGap(20).addWidget(lblTotaler)
				.addGroup(rightPanel.createParallelGroup(lblNoOfUltimatumScores, lNoOfUltimatumScores))
				.addGroup(rightPanel.createParallelGroup(lblAvg, lAvg))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblTotal, gap))
				.addWidget(lTotal));
		
		
		DialogLayout.Group hLeftPanel = ultStatPanel.createSequentialGroup(leftPanel).addGap(50);
		DialogLayout.Group hRightPanel = ultStatPanel.createSequentialGroup(rightPanel);
		
		ultStatPanel.setHorizontalGroup(ultStatPanel.createParallelGroup()
				.addGroup(ultStatPanel.createSequentialGroup(hLeftPanel, hRightPanel)));
		
		ultStatPanel.setVerticalGroup(ultStatPanel.createSequentialGroup()
				.addGroup(ultStatPanel.createParallelGroup(leftPanel, rightPanel)));

		// RESET VARIABLES
		reset();
		// set player variable
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			playerScore = HRRUClient.cs.getP1().getScore();
			ultimatumScores = HRRUClient.cs.getP1().getUltimatumScores();
		}
		else
		{
			playerScore = HRRUClient.cs.getP2().getScore();
			ultimatumScores = HRRUClient.cs.getP2().getUltimatumScores();
		}
		
		////////////////////////
		////ultimatum scores////
		////////////////////////
		if(ultimatumScores.size() > 0) {
			for(int i = 0; i < ultimatumScores.size(); i++)
			{
				noOfUltimatumScores++;
				ultimatumScore = ultimatumScores.get(i);
				// if player was proposer in instance of ultimatum score
				if(ultimatumScore.getPlayerProp() == playerID)
				{
					noOfUltimatumScoreProp++;
					if(ultimatumScore.isSuccess())
					{
						noOfPropSuccess++;
						usOverallValueTotalP += ultimatumScore.getOverallValue();
						usPlayerPropTotalP += ultimatumScore.getPlayerPropValue();
						usPlayerDecTotalP += ultimatumScore.getPlayerDecValue();
					}
				}
				else
				{

					noOfUltimatumScoreDec++;
					if(ultimatumScore.isSuccess())
					{
						noOfDecSuccess++;
						usOverallValueTotalD += ultimatumScore.getOverallValue();
						usPlayerPropTotalD += ultimatumScore.getPlayerPropValue();
						usPlayerDecTotalD += ultimatumScore.getPlayerDecValue();
					}
				}
			}
			// calculate averages
			if(noOfUltimatumScores > 0)
			{	
				if(noOfUltimatumScoreProp > 0 )
				{
					usOverallValueAvgP = (int) (usOverallValueTotalP / noOfPropSuccess + 0.5);
					usPlayerPropAvgP = (int) (usPlayerPropTotalP / noOfPropSuccess + 0.5);
					usPlayerDecAvgP = (int) (usPlayerDecTotalP / noOfPropSuccess + 0.5);
				}
				if(noOfUltimatumScoreDec > 0 )
				{
					usOverallValueAvgD = (int) (usOverallValueTotalD / noOfDecSuccess + 0.5);
					usPlayerPropAvgD = (int) (usPlayerPropTotalD / noOfDecSuccess + 0.5);
					usPlayerDecAvgD = (int) (usPlayerDecTotalD / noOfDecSuccess + 0.5);
				}
				usTotal = (int) (usPlayerPropTotalP + usPlayerDecTotalD);
				usAvg = (int) (usTotal / noOfUltimatumScores);
				
				lNoOfUltimatumScores.setText("" + noOfUltimatumScores);
				lTotal.setText("" + (int) usTotal);
				lAvg.setText("" + (int) usAvg);
			}
		// 	Prop Text
			lNoOfUltimatumScoreProp.setText("" + noOfUltimatumScoreProp);
			lOverallValueAvgP.setText("" + usOverallValueAvgP);
			lOverallValueTotalP.setText("" + (int) usOverallValueTotalP);
			lPlayerPropAvgP.setText("" + usPlayerPropAvgP);
			lPlayerPropTotalP.setText("" + (int) usPlayerPropTotalP);
			lPlayerDecAvgP.setText("" + usPlayerDecAvgP);
			lPlayerDecTotalP.setText("" + (int) usPlayerDecTotalP);
		// 	Decider Text
			lPlayerDecTotalD.setText("" + (int) usPlayerPropTotalD);
			lNoOfUltimatumScoreDec.setText("" + noOfUltimatumScoreDec);
			lOverallValueAvgD.setText("" + usOverallValueAvgD);
			lOverallValueTotalD.setText("" + (int) usOverallValueTotalD);
			lPlayerPropAvgD.setText("" + usPlayerDecAvgD);
			lPlayerPropTotalD.setText("" + (int) usPlayerDecTotalD);
			lPlayerDecAvgD.setText("" + usPlayerPropAvgD);
		}
		
		
		UltScoreResult ultScoreResult = new UltScoreResult(
				noOfUltimatumScores, 
				noOfUltimatumScoreProp, 
				noOfUltimatumScoreDec, 
				noOfPropSuccess, 
				noOfDecSuccess, 
				usOverallValueAvgP, 
				usPlayerPropAvgP, 
				usPlayerDecAvgP, 
				usOverallValueAvgD, 
				usPlayerPropAvgD, 
				usPlayerDecAvgD, 
				usOverallValueTotalP, 
				usPlayerPropTotalP, 
				usPlayerDecTotalP, 
				usOverallValueTotalD, 
				usPlayerPropTotalD, 
				usPlayerDecTotalD, 
				usAvg, 
				usTotal);
		
		if(noOfUltimatumScores > 0)
		{
			ultScoreResult.setPercentage((int)((usTotal/(playerScore-1000))*100+0.5));
			lTotal.setText("" + (int) usTotal + " (" + (int)(((usTotal/(playerScore-1000))*100+0.5)) +  "% of your score)");
		}
		
		if(playerID == 1)
			HRRUClient.cs.getP1().setUltScoreResult(ultScoreResult);
		else
			HRRUClient.cs.getP2().setUltScoreResult(ultScoreResult);
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// draw main graphics for screen and title
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
		return 24;
	}

}