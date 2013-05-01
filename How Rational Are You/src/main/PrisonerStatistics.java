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

public class PrisonerStatistics extends BasicTWLGameState {

	public Client client;
	DialogLayout prisonerStatPanel, leftPanel, rightPanel;
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
	Label lblTotal, lblTotalProfit, lTotalProfit, lblTotalAvg, lTotalAvg;
	Label lblBB, lblCC, lblCB, lblBC;
	Label lblNoOfPrisonScores;
	Label lblNoOfPrisonScoreCoop;
	Label lblNoOfPrisonScoreBetray;
	Label lblNoOfPrisonScoreCC, lblNoOfPrisonScoreCB, lblNoOfPrisonScoreBC, lblNoOfPrisonScoreBB;
	Label lblPsPlayerProfitCC, lblPsOtherPlayerProfitCC;
	Label lblPsPlayerProfitBC, lblPsOtherPlayerProfitBC;
	Label lblPsPlayerProfitCB, lblPsOtherPlayerProfitCB;
	Label lblPsPlayerProfitBB, lblPsOtherPlayerProfitBB;
	Label lblPsCAvg, lblPsCTotal, lblPsBAvg, lblPsBTotal;
	Label lblPsAvg, lblPsTotal, lblPsCoopWin, lblPsCoopLose, lblPsBetrayWin, lblPsBetrayLose;
	
	Label lNoOfPrisonScores;
	Label lNoOfPrisonScoreCoop;
	Label lNoOfPrisonScoreBetray;
	Label lNoOfPrisonScoreCC, lNoOfPrisonScoreCB, lNoOfPrisonScoreBC, lNoOfPrisonScoreBB;
	Label lPsPlayerProfitCC, lPsOtherPlayerProfitCC;
	Label lPsPlayerProfitBC, lPsOtherPlayerProfitBC;
	Label lPsPlayerProfitCB, lPsOtherPlayerProfitCB;
	Label lPsPlayerProfitBB, lPsOtherPlayerProfitBB;
	Label lPsCAvg, lPsCTotal, lPsBAvg, lPsBTotal;
	Label lPsAvg, lPsTotal, lPsCoopWin, lPsCoopLose, lPsBetrayWin, lPsBetrayLose;
	
	private PrisonScore prisonScore;
	private ArrayList<PrisonScore> prisonScores;
	
	// PrisonScore Statistics
	private int noOfPrisonScores;
	private int noOfPrisonScoreCoop;
	private int noOfPrisonScoreBetray;
	private int noOfPrisonScoreCC, noOfPrisonScoreCB, noOfPrisonScoreBC, noOfPrisonScoreBB;
	private int psPlayerProfitCC, psOtherPlayerProfitCC;
	private int psPlayerProfitBC, psOtherPlayerProfitBC;
	private int psPlayerProfitCB, psOtherPlayerProfitCB;
	private int psPlayerProfitBB, psOtherPlayerProfitBB;
	private int psCAvg, psAvg, psBAvg;
	private double psTotal, psCTotal, psBTotal;
	private final int psCoopWin = 150;
	private final int psCoopLose = 50;
	private final int psBetrayWin = 250;
	private final int psBetrayLose = 0;

	private int playerID;
	private int playerScore;
	
	void reset() {
		// Reset Prison result variables
		noOfPrisonScores = 0;
		noOfPrisonScoreCoop = 0;
		noOfPrisonScoreBetray = 0;
		noOfPrisonScoreCC = 0; noOfPrisonScoreCB = 0; noOfPrisonScoreBC = 0; noOfPrisonScoreBB = 0;
		psPlayerProfitCC = 0; psOtherPlayerProfitCC = 0;
		psPlayerProfitBC = 0; psOtherPlayerProfitBC = 0;
		psPlayerProfitCB = 0; psOtherPlayerProfitCB = 0;
		psPlayerProfitBB = 0; psOtherPlayerProfitBB = 0;
		psCAvg = 0; psCTotal = 0; psBAvg = 0; psBTotal = 0;
		psAvg = 0; psTotal = 0;
				
	}
	
	public PrisonerStatistics(int main) {
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
		btnPrisoner.setActive(true);
		btnUlt.setActive(false);
		
		// SETUP GUI
		rootPane.removeAllChildren();
		rootPane.add(prisonerStatPanel);
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
		prisonerStatPanel = new DialogLayout();
		prisonerStatPanel.setTheme("coopbetraygamestat-panel");
		prisonerStatPanel.setSize(630,300);
		prisonerStatPanel.setPosition(50,100);
		
		leftPanel = new DialogLayout();
		rightPanel = new DialogLayout();
		
		lblCC = new Label("Both Cooperate: ");
		lblCC.setTheme("labelscoretotalleft");
		lblBC = new Label("Successful Betray: ");
		lblBC.setTheme("labelscoretotalleft");
		lblTotal = new Label("Total: ");
		lblTotal.setTheme("labelscoretotalleft");
		
		lblNoOfPrisonScoreCC = new Label("Number of Games: ");
		lNoOfPrisonScoreCC = new Label("");
		lblNoOfPrisonScoreCC.setTheme("questionatari8r");
		lNoOfPrisonScoreCC.setTheme("questionatari8r");
		
		lblPsPlayerProfitCC = new Label("Your Total Profit: ");
		lPsPlayerProfitCC = new Label("0");
		lblPsPlayerProfitCC.setTheme("questionatari8r");
		lPsPlayerProfitCC.setTheme("questionatari8r");
		
		lblPsOtherPlayerProfitCC = new Label("Their Total Profit: ");
		lPsOtherPlayerProfitCC = new Label("0");
		lblPsOtherPlayerProfitCC.setTheme("questionatari8r");
		lPsOtherPlayerProfitCC.setTheme("questionatari8r");
		
		lblNoOfPrisonScoreBC = new Label("Number of Games: ");
		lNoOfPrisonScoreBC = new Label("");
		lblNoOfPrisonScoreBC.setTheme("questionatari8r");
		lNoOfPrisonScoreBC.setTheme("questionatari8r");
		
		lblPsPlayerProfitBC = new Label("Your Total Profit: ");
		lPsPlayerProfitBC = new Label("0");
		lblPsPlayerProfitBC.setTheme("questionatari8r");
		lPsPlayerProfitBC.setTheme("questionatari8r");
		
		lblPsOtherPlayerProfitBC = new Label("Their Total Profit: ");
		lPsOtherPlayerProfitBC = new Label("0");
		lblPsOtherPlayerProfitBC.setTheme("questionatari8r");
		lPsOtherPlayerProfitBC.setTheme("questionatari8r");
		
		lblNoOfPrisonScoreBC = new Label("Number of Games: ");
		lNoOfPrisonScoreBC = new Label("");
		lblNoOfPrisonScoreBC.setTheme("questionatari8r");
		lNoOfPrisonScoreBC.setTheme("questionatari8r");
		
		lblNoOfPrisonScoreCoop = new Label("Games Cooperated: ");
		lNoOfPrisonScoreCoop = new Label("");
		lblNoOfPrisonScoreCoop.setTheme("questionatari8r");
		lNoOfPrisonScoreCoop.setTheme("questionatari8r");
		
		lblPsCAvg = new Label("Average When Cooperating: ");
		lPsCAvg = new Label("");
		lblPsCAvg.setTheme("questionatari8r");
		lPsCAvg.setTheme("questionatari8r");
		
		lblPsCTotal = new Label("Total When Cooperating: ");
		lPsCTotal = new Label("");
		lblPsCTotal.setTheme("questionatari8r");
		lPsCTotal.setTheme("questionatari8r");
		
		lblNoOfPrisonScoreBetray = new Label("Games Betrayed: ");
		lNoOfPrisonScoreBetray = new Label("");
		lblNoOfPrisonScoreBetray.setTheme("questionatari8r");
		lNoOfPrisonScoreBetray.setTheme("questionatari8r");
		
		lblPsBAvg = new Label("Average When Betraying: ");
		lPsBAvg = new Label("");
		lblPsBAvg.setTheme("questionatari8r");
		lPsBAvg.setTheme("questionatari8r");
		
		lblPsBTotal = new Label("Total When Betraying: ");
		lPsBTotal = new Label("");
		lblPsBTotal.setTheme("questionatari8r");
		lPsBTotal.setTheme("questionatari8r");
		
		
		DialogLayout.Group hLPLeft = leftPanel.createParallelGroup(lblNoOfPrisonScoreCC, lblPsPlayerProfitCC, 
				lblPsOtherPlayerProfitCC, lblNoOfPrisonScoreBC, lblPsPlayerProfitBC, lblPsOtherPlayerProfitBC,
				lblNoOfPrisonScoreCoop, lblPsCAvg, lblPsCTotal, lblNoOfPrisonScoreBetray, lblPsBAvg, lblPsBTotal);
		DialogLayout.Group hLPRight = leftPanel.createParallelGroup(lNoOfPrisonScoreCC, lPsPlayerProfitCC, 
				lPsOtherPlayerProfitCC, lNoOfPrisonScoreBC, lPsPlayerProfitBC, lPsOtherPlayerProfitBC,
				lNoOfPrisonScoreCoop, lPsCAvg, lPsCTotal, lNoOfPrisonScoreBetray, lPsBAvg, lPsBTotal);
		
		leftPanel.setHorizontalGroup(leftPanel.createParallelGroup()
				.addWidget(lblCC)
				.addWidget(lblBC)
				.addGroup(leftPanel.createSequentialGroup(hLPLeft, hLPRight))
				.addWidget(lblTotal));
		
		leftPanel.setVerticalGroup(leftPanel.createSequentialGroup()
				.addWidget(lblCC)
				.addGroup(leftPanel.createParallelGroup(lblNoOfPrisonScoreCC, lNoOfPrisonScoreCC))
				.addGroup(leftPanel.createParallelGroup(lblPsPlayerProfitCC, lPsPlayerProfitCC))
				.addGroup(leftPanel.createParallelGroup(lblPsOtherPlayerProfitCC, lPsOtherPlayerProfitCC))
				.addGap(20).addWidget(lblBC)
				.addGroup(leftPanel.createParallelGroup(lblNoOfPrisonScoreBC, lNoOfPrisonScoreBC))
				.addGroup(leftPanel.createParallelGroup(lblPsPlayerProfitBC, lPsPlayerProfitBC))
				.addGroup(leftPanel.createParallelGroup(lblPsOtherPlayerProfitBC, lPsOtherPlayerProfitBC))
				.addGap(20).addWidget(lblTotal)
				.addGroup(leftPanel.createParallelGroup(lblNoOfPrisonScoreCoop, lNoOfPrisonScoreCoop))
				.addGroup(leftPanel.createParallelGroup(lblPsCAvg, lPsCAvg))
				.addGroup(leftPanel.createParallelGroup(lblPsCTotal, lPsCTotal))
				.addGap(10).addGroup(leftPanel.createParallelGroup(lblNoOfPrisonScoreBetray, lNoOfPrisonScoreBetray))
				.addGroup(leftPanel.createParallelGroup(lblPsBAvg, lPsBAvg))
				.addGroup(leftPanel.createParallelGroup(lblPsBTotal, lPsBTotal)));
		
		lblCB = new Label("Fail Cooperate: ");
		lblCB.setTheme("labelscoretotalleft");
		lblBB = new Label("Both Betray: ");
		lblBB.setTheme("labelscoretotalleft");
		
		lblNoOfPrisonScoreCB = new Label("Number of Games: ");
		lNoOfPrisonScoreCB = new Label("");
		lblNoOfPrisonScoreCB.setTheme("questionatari8r");
		lNoOfPrisonScoreCB.setTheme("questionatari8r");
		
		lblPsPlayerProfitCB = new Label("Your Total Profit: ");
		lPsPlayerProfitCB = new Label("0");
		lblPsPlayerProfitCB.setTheme("questionatari8r");
		lPsPlayerProfitCB.setTheme("questionatari8r");
		
		lblPsOtherPlayerProfitCB = new Label("Their Total Profit: ");
		lPsOtherPlayerProfitCB = new Label("0");
		lblPsOtherPlayerProfitCB.setTheme("questionatari8r");
		lPsOtherPlayerProfitCB.setTheme("questionatari8r");
		
		lblNoOfPrisonScoreBB = new Label("Number of Games: ");
		lNoOfPrisonScoreBB = new Label("");
		lblNoOfPrisonScoreBB.setTheme("questionatari8r");
		lNoOfPrisonScoreBB.setTheme("questionatari8r");
		
		lblPsPlayerProfitBB = new Label("Your Total Profit: ");
		lPsPlayerProfitBB = new Label("0");
		lblPsPlayerProfitBB.setTheme("questionatari8r");
		lPsPlayerProfitBB.setTheme("questionatari8r");
		
		lblPsOtherPlayerProfitBB = new Label("Their Total Profit: ");
		lPsOtherPlayerProfitBB = new Label("0");
		lblPsOtherPlayerProfitBB.setTheme("questionatari8r");
		lPsOtherPlayerProfitBB.setTheme("questionatari8r");
		
		lblNoOfPrisonScores = new Label("Total Number of Games: ");
		lNoOfPrisonScores = new Label("0");
		lblNoOfPrisonScores.setTheme("questionatari8r");
		lNoOfPrisonScores.setTheme("questionatari8r");
		
		lblTotalProfit = new Label("Total Profit: ");
		lTotalProfit = new Label("0");
		lblTotalProfit.setTheme("questionatari8r");
		lTotalProfit.setTheme("questionatari8r");
		
		lblTotalAvg = new Label("Total Average: ");
		lTotalAvg = new Label("0");
		lblTotalAvg.setTheme("questionatari8r");
		lTotalAvg.setTheme("questionatari8r");
		
		Label gap = new Label("");
		
		DialogLayout.Group hRPLeft = rightPanel.createParallelGroup(lblNoOfPrisonScoreCB, lblPsPlayerProfitCB, 
				lblPsOtherPlayerProfitCB, lblNoOfPrisonScoreBB, lblPsPlayerProfitBB, lblPsOtherPlayerProfitBB,
				lblNoOfPrisonScores, lblTotalProfit, lblTotalAvg);
		DialogLayout.Group hRPRight = rightPanel.createParallelGroup(lNoOfPrisonScoreCB, lPsPlayerProfitCB, 
				lPsOtherPlayerProfitCB, lNoOfPrisonScoreBB, lPsPlayerProfitBB, lPsOtherPlayerProfitBB,
				lNoOfPrisonScores,lTotalAvg, gap);
		
		rightPanel.setHorizontalGroup(rightPanel.createParallelGroup()
				.addWidget(lblCB)
				.addWidget(lblBB)
				.addGroup(rightPanel.createSequentialGroup(hRPLeft, hRPRight))
				.addWidget(lTotalProfit));
		
		rightPanel.setVerticalGroup(rightPanel.createSequentialGroup()
				.addWidget(lblCB)
				.addGroup(rightPanel.createParallelGroup(lblNoOfPrisonScoreCB, lNoOfPrisonScoreCB))
				.addGroup(rightPanel.createParallelGroup(lblPsPlayerProfitCB, lPsPlayerProfitCB))
				.addGroup(rightPanel.createParallelGroup(lblPsOtherPlayerProfitCB, lPsOtherPlayerProfitCB))
				.addGap(20).addWidget(lblBB)
				.addGroup(rightPanel.createParallelGroup(lblNoOfPrisonScoreBB, lNoOfPrisonScoreBB))
				.addGroup(rightPanel.createParallelGroup(lblPsPlayerProfitBB, lPsPlayerProfitBB))
				.addGroup(rightPanel.createParallelGroup(lblPsOtherPlayerProfitBB, lPsOtherPlayerProfitBB))
				.addGap(94)
				.addGroup(rightPanel.createParallelGroup(lblNoOfPrisonScores, lNoOfPrisonScores))
				.addGroup(rightPanel.createParallelGroup(lblTotalAvg, lTotalAvg))
				.addGap(20).addGroup(rightPanel.createParallelGroup(lblTotalProfit, gap))
				.addWidget(lTotalProfit));
		
		DialogLayout.Group hLeftPanel = prisonerStatPanel.createSequentialGroup(leftPanel).addGap(50);
		DialogLayout.Group hRightPanel = prisonerStatPanel.createSequentialGroup(rightPanel);
		
		
		prisonerStatPanel.setHorizontalGroup(prisonerStatPanel.createParallelGroup()
				.addGroup(prisonerStatPanel.createSequentialGroup(hLeftPanel, hRightPanel)));
		
		prisonerStatPanel.setVerticalGroup(prisonerStatPanel.createSequentialGroup()
				.addGroup(prisonerStatPanel.createParallelGroup(leftPanel, rightPanel)));
		
		// RESET VARIABLES
		reset();
		// set player variable
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			playerScore = HRRUClient.cs.getP1().getScore();
			prisonScores = HRRUClient.cs.getP1().getPrisonScores();
		}
		else
		{
			playerScore = HRRUClient.cs.getP2().getScore();
			prisonScores = HRRUClient.cs.getP2().getPrisonScores();
		}
		
		if(prisonScores.size() > 0) {
			for(int i = 0; i < prisonScores.size(); i++)
			{
				noOfPrisonScores++;
				prisonScore = prisonScores.get(i);
				// if betray
				if(prisonScore.getPlayerChoice() == 0) {
					noOfPrisonScoreBetray++;
					// both betray
					if(prisonScore.getOtherPlayerChoice() == 0) {
						noOfPrisonScoreBB++;
						psBTotal+= psBetrayLose;
					}
					// successful betray
					else {
						noOfPrisonScoreBC++;
						psBTotal+= psBetrayWin;
						psPlayerProfitBC += psBetrayWin;
						psOtherPlayerProfitBC += psCoopLose;
					}
				}
				else {
					noOfPrisonScoreCoop++;
					// both coop
					if(prisonScore.getOtherPlayerChoice() == 1) {
						noOfPrisonScoreCC++;
						psCTotal+= psCoopWin;
						psPlayerProfitCC += psCoopWin;
						psOtherPlayerProfitCC += psCoopWin;
					}
					// get betrayed
					else {
						noOfPrisonScoreCB++;
						psCTotal+= psCoopLose;
						psPlayerProfitCB += psCoopLose;
						psOtherPlayerProfitCB += psBetrayWin;
						}
				}
			}
			if(noOfPrisonScoreCoop > 0) {
				psCAvg = (int) (psCTotal / noOfPrisonScoreCoop + 0.5);
				psCTotal = (int)psCTotal;
			}
			if(noOfPrisonScoreBetray > 0) {
				psBAvg = (int) (psBTotal / noOfPrisonScoreBetray + 0.5);
				psBTotal = (int) psBTotal;
			}
			psTotal = psCTotal + psBTotal;
			psAvg = (int) (psTotal / noOfPrisonScores);
			lTotalProfit.setText(""+ (int) psTotal);
		}
		
		lNoOfPrisonScoreCB.setText("" + noOfPrisonScoreCB);
		lPsPlayerProfitCB.setText(""+ psPlayerProfitCB);
		lPsOtherPlayerProfitCB.setText(""+ psOtherPlayerProfitCB);
		
		lNoOfPrisonScoreBB.setText("" + noOfPrisonScoreBB);
		lPsPlayerProfitBB.setText(""+ psPlayerProfitBB);
		lPsOtherPlayerProfitBB.setText(""+ psOtherPlayerProfitBB);
		
		lNoOfPrisonScoreBC.setText("" + noOfPrisonScoreBC);
		lPsPlayerProfitBC.setText(""+ psPlayerProfitBC);
		lPsOtherPlayerProfitBC.setText(""+ psOtherPlayerProfitBC);
		
		lNoOfPrisonScoreCC.setText("" + noOfPrisonScoreCC);
		lPsPlayerProfitBB.setText(""+ psPlayerProfitBB);
		lPsOtherPlayerProfitBB.setText(""+ psOtherPlayerProfitBB);
		
		lNoOfPrisonScoreCoop.setText("" + noOfPrisonScoreCoop);
		lPsCAvg.setText("" + psCAvg);
		lPsCTotal.setText("" + (int) psCTotal);
		
		lNoOfPrisonScoreBetray.setText("" + noOfPrisonScoreBetray);
		lPsBAvg.setText("" + psBAvg);
		lPsBTotal.setText("" + (int) psBTotal);
		
		lNoOfPrisonScores.setText("" + noOfPrisonScores);
		lTotalAvg.setText("" + psAvg);
		
		PrisonerScoreResult prisonerScoreResult = new PrisonerScoreResult(
				noOfPrisonScores, 
				noOfPrisonScoreCoop, 
				noOfPrisonScoreBetray, 
				noOfPrisonScoreCC, 
				noOfPrisonScoreCB, 
				noOfPrisonScoreBC, 
				noOfPrisonScoreBB, 
				psPlayerProfitCC, 
				psOtherPlayerProfitCC, 
				psPlayerProfitBC, 
				psOtherPlayerProfitBC, 
				psPlayerProfitCB, 
				psOtherPlayerProfitCB, 
				psPlayerProfitBB, 
				psOtherPlayerProfitBB, 
				psCAvg, 
				psAvg,
				psBAvg, 
				psTotal, 
				psCTotal, 
				psBTotal);
		
		if(noOfPrisonScores > 0)
		{
			prisonerScoreResult.setPercentage((int)((psTotal/(playerScore-1000))*100+0.5));
			lTotalProfit.setText("" + (int) psTotal + " (" + (int)((psTotal/(playerScore-1000))*100+0.5) +  "% of your score)");
		}
		
		if(playerID == 1)
			HRRUClient.cs.getP1().setPrisonerScoreResult(prisonerScoreResult);
		else
			HRRUClient.cs.getP2().setPrisonerScoreResult(prisonerScoreResult);
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
		return 22;
	}

}