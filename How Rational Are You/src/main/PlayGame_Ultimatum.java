package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.RotateTransition;
import org.newdawn.slick.state.transition.SelectTransition;
import com.esotericsoftware.kryonet.Client;

import conn.Packet.Packet00SyncMessage;
import conn.Packet.Packet22PropUlt;
import conn.Packet.packet23DecUlt;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.ValueAdjusterInt;

public class PlayGame_Ultimatum extends BasicTWLGameState {
	// game stat variables
	Input input;
	private int gameState, overallState, sessionID;
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int play = 11;
	private final int p1_turn = 7;
	public final int question_points_amount = 100;
	// gui variables
	public Client client;
	DialogLayout p1ResultPanel, p2ResultPanel;
	Label lShare, lShare2, lblShare, lblShare2;
	Label lNew, lNew2, lblNew, lblNew2;
	Label lblYourTurn;
	EmptyTransition emptyTransition;
    RotateTransition rotateTransition;
    SelectTransition selectTransition;
	
	int gcw;
	int gch;
	
	// Game variables
	int firstValue = 0;
	int secondValue = 0;
	int multiplier = 0;
	
	// Player variables
	Player player1, player2, player, otherPlayer;
	int playerID, otherPlayerID;
	int p1Score, p2Score;
	int otherPlayerReady;
	String otherPlayerName;
	int player1_x, player1_y, player2_x, player2_y;
	private boolean pShowRollBanner = false;
	
	// Game variable
	private int playerProp, playerDec;
	private String playerPropName, playerDecName;
	private int playerPropValue, playerDecValue, overallPoints;
	private boolean bPlayerProp, bPlayerDec;
	private boolean success;
	
	// Item Panel & Variables
	DialogLayout p1Panel, p2Panel;
	Label lPlayer1, lblPlayer1, lPlayer2, lblPlayer2;
	Label lStatus;
	TextArea questionDescription;
	HTMLTextAreaModel questionDescriptionModel;
	ValueAdjusterInt vaProposal;
	Button btnProposal, btnAccept, btnReject;
	
	// Confirmation Panel & Variables
	Label lblConfirmation;
	Button btnYes, btnNo;

	// Image setup
	Image scorebackground, background, playerbg, accept, reject;
	private int header_x = 330;
	private int header_y = 25;
	private int timer_x = 600;
	private int timer_y = 550;
	private int fixed_y = 150;
	private int p1fixed_x = 149; // - 1;
	private int p1fixed_y = 292; // + 34;
	private int p2fixed_x = 499; // + 5;
	private int p2fixed_y = 292; // + 34;
	
	// Ticker setup
	private String start_message = "";
	private String full_start_message = "ULTIMATUM. \n ... MAKE YOUR CHOICES.";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	
	// Fonts
	private int mainFontSize = 24;
	private int titleFontSize = 36;
	private int timerFontSize = 40;
	private int timerMFontSize = 18;
	
	private Font loadFont, loadMainFont, loadTitleFont, loadTimerFont, loadTimerMFont;
	private BasicFont mainFont, titleFont, timerFont, timerMFont;
	
	// Timers and state variables
	private int clock2,clock3,timer,timer2,overallTimer = 0;
	private boolean time_out, finished;
	
	Packet22PropUlt packetProp;
	packet23DecUlt packetDec;
	
	Packet00SyncMessage syncMessage;
	
	public PlayGame_Ultimatum(int main) {
	}
	
	void disableGUI()
	{
		btnProposal.setVisible(false);
		vaProposal.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		btnAccept.setVisible(false);
		btnReject.setVisible(false);
		lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 350);
		if(bPlayerProp)
			lblConfirmation.setText("Proposal sent. \nWaiting for " + otherPlayer.getName() + ".");
		else{
			if(success)
				lblConfirmation.setText("You accepted the proposal.");
			else
				lblConfirmation.setText("You rejected the proposal.");
		}
			
	}
	
	void disableChoices()
	{
		btnProposal.setVisible(false);
		vaProposal.setVisible(false);
		lblConfirmation.setVisible(true);
		btnYes.setVisible(true);
		btnNo.setVisible(true);
		btnAccept.setVisible(false);
		btnReject.setVisible(false);
		if(bPlayerProp)
		{
			lblConfirmation.setText(otherPlayerName + "'s share is " + vaProposal.getValue() + " / " + overallPoints + "?");
		}
			
	}
	
	void enableChoices()
	{
		// currentValue = 0;
		if(bPlayerProp)
		{
			btnProposal.setVisible(true);
			vaProposal.setVisible(true);
		}
		else if(bPlayerDec)
		{
			btnAccept.setVisible(true);
			btnReject.setVisible(true);
		}
		lblConfirmation.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
	}
	
	void emulateChoice()
	{
		disableChoices();
	}
	
	void emulateYes()
	{
		if(bPlayerProp)
		{
			// Player proposal value
			playerDecValue = vaProposal.getValue();
			playerPropValue = overallPoints - playerDecValue;
			packetProp.player = playerID;
			packetProp.playerPropValue = this.playerPropValue;
			packetProp.playerDecValue = this.playerDecValue;
			HRRUClient.cs.setGameState(1);
			client.sendTCP(packetProp);
			disableGUI();
		}
		else if(bPlayerDec)
		{
			packetDec.success = this.success;
			client.sendTCP(packetDec);
			HRRUClient.cs.setGameState(3);
			disableGUI();
		}
	}
	
	void emulateAccept()
	{
		disableChoices();
		lblConfirmation.setText("You want to accept " + otherPlayerName + "'s proposal?");
		success = true;
	}
	
	void emulateReject()
	{
		disableChoices();
		lblConfirmation.setText("You want to reject " + otherPlayerName + "'s proposal?");
		success = false;
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		client = HRRUClient.conn.getClient();	
		/*
		// Testing
		Character characters[] = (new CharacterSheet()).getCharacters();
		player1 = new Player("player1");
		player2 = new Player("player2");
		player1.setPlayerCharacterID(2);
		player1.setPlayerCharacter(characters[2]);
		player2.setPlayerCharacter(characters[4]);
		player2.setPlayerCharacterID(4);
		player1.setScore(5000);
		HRRUClient.cs.setState(6);
		HRRUClient.cs.setP1(player1);
		HRRUClient.cs.setP2(player2);
		HRRUClient.cs.setPlayer(1);
		*/
		
		// Reset main variables
		rootPane.removeAllChildren();
		timer = 70;
		timer2 = 999;
		overallTimer = 0;
		sessionID = HRRUClient.cs.getSessionID();
		HRRUClient.cs.setGameState(0);
		p1ResultPanel.setTheme("incorrectbid-panel");
		p2ResultPanel.setTheme("incorrectbid-panel");
		lblNew.setText("0");
		lblNew2.setText("0");
		
		// Reset packet variables
		packetProp = new Packet22PropUlt();
		packetDec = new packet23DecUlt();
		packetProp.sessionID = sessionID;
		packetDec.sessionID = sessionID;
		
		// Ticker options
		start_message = "";
		full_start_message = "ULTIMATUM. \n ... MAKE YOUR CHOICES.";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		
		// Reset variables
		bPlayerProp = false;
		bPlayerDec = false;
		pShowRollBanner = false;
		overallPoints = 0;
		playerPropValue = 0;
		playerDecValue = 0; 
		finished = false;
		time_out = false;
		success = false;
		vaProposal.setValue(0);
		p1fixed_x = 149;
		p1fixed_y = 292; 
		p2fixed_x = 499; 
		p2fixed_y = 292; 
		
		
		// Turn label
		lblYourTurn = new Label();
		lblYourTurn.setSize(800, 600);
		lblYourTurn.setTheme("labelyourturn");
		lblYourTurn.setPosition(0,0);
		lblYourTurn.setVisible(false);
		
		// Retrieve player information
		player1 = HRRUClient.cs.getP1();
		player2 = HRRUClient.cs.getP2();
		playerID = HRRUClient.cs.getPlayer();
		if(playerID==1)
		{
			otherPlayerID = 2;
			player = player1;
			otherPlayer = player2;
			otherPlayerName = player2.getName();
		}
		else
		{
			otherPlayerID = 1;
			player = player2;
			otherPlayer = player1;
			otherPlayerName = player1.getName();
		}
		p1Score = player1.getScore();
		p2Score = player1.getScore();
		
		// Retrieve game information
		playerProp = HRRUClient.cs.getSecondary_id();
		if(playerProp == 1)
			playerDec = 2;
		else playerDec = 1;
	
		overallPoints = HRRUClient.cs.getSecondary_value();
		
		// Set initial game state
		if(playerID == playerProp)
		{
			pShowRollBanner = true;
			lblYourTurn.setVisible(true);
			bPlayerProp = true;
			bPlayerDec = false;
			vaProposal.setMinMaxValue(0, overallPoints);
			vaProposal.setVisible(true);
			btnProposal.setVisible(true);
			lStatus.setText("What is your proposal for " + otherPlayerName + "'s share?");
			lStatus.setTheme("labelgreen");
			lStatus.reapplyTheme();
			playerPropName = player.getName();
			playerDecName = otherPlayer.getName();
		}
		else
		{
			bPlayerProp = false;
			bPlayerDec = true;
			vaProposal.setVisible(false);
			btnProposal.setVisible(false);
			lStatus.setText("Waiting for " + otherPlayerName + "to submit proposal...");
			lStatus.setTheme("labelred");
			lStatus.reapplyTheme();
			playerDecName = player.getName();
			playerPropName = otherPlayer.getName();
		}
		lblPlayer1.setText("0 / " + overallPoints);
		lblPlayer2.setText("0 / " + overallPoints);

		player1_x = p1fixed_x;
		player1_y = p1fixed_y;
		player2_x = p2fixed_x;
		player2_y = p2fixed_y;

		
		// Set up text
		
		questionDescriptionModel.setHtml("<html><body><div>"
				+ "<p style='margin-top: 10px; text-align: center;'>You will now interact and decide how to divide <a style='font-family: highlight;'>" + overallPoints + "</a> points.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'><a style='font-family: name;'>" + playerPropName + "</a> will first propose how to divide <a style='font-family: highlight;'>" + overallPoints + "</a> points between both players.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'><a style='font-family: name;'>" + playerDecName + "</a> can either <a style='font-family: name;'>accept</a> or <a style='font-family: name;'>reject</a> this proposal.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'>If <a style='font-family: name;'>" + playerDecName + " rejects</a>, both players receive <a style='font-family: highlight;'>0.</a></p>"
				+ "<p style='margin-top: 10px; text-align: center;'>If <a style='font-family: name;'>" + playerDecName + " accepts</a>, the money is <a style='font-family: name;'>split</a> according to the proposal.</p>"
				+ "</div></body></html>");
		
		
		// Reset views
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		btnAccept.setVisible(false);
		btnReject.setVisible(false);
		lStatus.setVisible(true);
		lblConfirmation.setVisible(false);
		questionDescription.setVisible(true);
		p1Panel.setPosition(75, 270);
		p2Panel.setPosition(425, 270);
		lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 340);
		btnYes.setPosition((gcw/2) - 152, fixed_y + 370);
		btnNo.setPosition((gcw/2) - 152, fixed_y + 405);
		
		// Setup new item variables
		rootPane.add(btnYes);
		rootPane.add(btnNo);
		rootPane.add(lblConfirmation);
		rootPane.add(vaProposal);
		rootPane.add(btnProposal);
		rootPane.add(p1Panel);
		rootPane.add(p2Panel);
		rootPane.add(questionDescription);
		rootPane.add(lStatus);
		rootPane.add(btnAccept);
		rootPane.add(btnReject);
		rootPane.add(lblYourTurn);
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
		
		// Set up images
		scorebackground = new Image("simple/playerscorebackground.png");
		background = new Image("simple/questionbg.png");
		playerbg = new Image("simple/playerbg.png");
		accept = new Image("simple/accept1.png");
		reject = new Image("simple/reject1.png");
		
		// Set up fonts
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
		
		loadTimerFont = loadFont.deriveFont(Font.BOLD,timerFontSize);
		loadTimerMFont = loadFont.deriveFont(Font.BOLD,timerMFontSize);
		timerFont = new BasicFont(loadTimerFont);
		timerMFont = new BasicFont(loadTimerMFont);
		
		// Setup Game GUI
		questionDescriptionModel = new HTMLTextAreaModel();
		questionDescription = new TextArea(questionDescriptionModel);
		questionDescription.setPosition(10, 110);
		questionDescription.setSize(780,300);
		questionDescription.setTheme("trusttextarea");
		
		// Setup Player GUI
		p1Panel = new DialogLayout();
		p2Panel = new DialogLayout();
		p1Panel.setSize(300, 45);
		p2Panel.setSize(300, 45);
		p1Panel.setTheme("ult-panel");
		p2Panel.setTheme("ult-panel");
		p1Panel.setPosition(75, 270);
		p2Panel.setPosition(425, 270);

		lPlayer1 = new Label("Share: ");
		lblPlayer1 = new Label("0/100");
		lPlayer2 = new Label("Share: ");
		lblPlayer2 = new Label("0/100");
		lPlayer1.setTheme("label");
		lblPlayer1.setTheme("labelscoretotal");
		lPlayer2.setTheme("label");
		lblPlayer2.setTheme("labelscoretotal");
		
		DialogLayout.Group hPlayer1 = p1Panel.createParallelGroup(lPlayer1, lblPlayer1);
		p1Panel.setVerticalGroup(p1Panel.createParallelGroup().addGroup(hPlayer1));
		p1Panel.setHorizontalGroup(p1Panel.createSequentialGroup().addWidget(lPlayer1).addWidget(lblPlayer1));
		
		DialogLayout.Group hPlayer2 = p2Panel.createParallelGroup(lPlayer2, lblPlayer2);
		p2Panel.setVerticalGroup(p2Panel.createParallelGroup().addGroup(hPlayer2));
		p2Panel.setHorizontalGroup(p2Panel.createSequentialGroup().addWidget(lPlayer2).addWidget(lblPlayer2));
		
		// StatusGUI
		lStatus = new Label("");
		lStatus.setSize(780, 50);
		lStatus.setPosition(10,420);
		
		// Confirmation GUI
		lblConfirmation = new Label("");
		btnYes = new Button("Yes");
		btnNo = new Button("No");
		
		lblConfirmation.setTheme("labelscoretotal");
		btnYes.setTheme("choicebutton");
		btnNo.setTheme("choicebutton");
		
		lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 340);
		btnYes.setPosition((gcw/2) - 152, fixed_y + 370);
		btnNo.setPosition((gcw/2) - 152, fixed_y + 405);
		btnYes.setSize(300, 30);
		btnNo.setSize(300, 30);
			
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
		
		// Bid GUI
		vaProposal = new ValueAdjusterInt();
		vaProposal.setTooltipContent(null);
		vaProposal.setSize(200, 30);
		vaProposal.setPosition((gcw/2) - vaProposal.getWidth()/2, fixed_y + 340);
		
		btnProposal = new Button("Submit Proposal");
		btnProposal.setSize(200, 30);
		btnProposal.setPosition((gcw/2) - btnProposal.getWidth()/2 - 2, fixed_y + 380);
		btnProposal.setTheme("choicebutton");
		
		btnProposal.addCallback(new Runnable() {
            public void run() {
            	emulateChoice();
            }
        });
		
		// Decider GUI
		btnAccept = new Button();
		btnAccept.setSize(319,239);
		btnAccept.setPosition(74, 300);
		btnAccept.setTheme("acceptbutton");
		btnReject = new Button();
		btnReject.setPosition(407, 300);
		btnReject.setSize(319,239);
		btnReject.setTheme("rejectbutton");
	
		btnAccept.addCallback(new Runnable() {
            public void run() {
            	emulateAccept();
            }
        });
		btnReject.addCallback(new Runnable() {
            public void run() {
            	emulateReject();
            }
        });
		// Results GUI
		
		p1ResultPanel = new DialogLayout();
        p1ResultPanel.setTheme("correctbid-panel");
		
		p1ResultPanel.setSize(300, 80);
		p1ResultPanel.setPosition(
               (gcw/2 - p1ResultPanel.getWidth()/2 - 200),
                (gch/2 - p1ResultPanel.getHeight()/2) + 100);
		
		p2ResultPanel = new DialogLayout();
        p2ResultPanel.setTheme("correctbid-panel");
		p2ResultPanel.setSize(300, 80);
		p2ResultPanel.setPosition(
               (gcw/2 - p2ResultPanel.getWidth()/2 + 160),
                (gch/2 - p2ResultPanel.getHeight()/2) + 100);
		
		lShare = new Label("Share: ");
		lblShare = new Label("");
		lblShare.setTheme("labelright");

		lNew = new Label("New Total:");
		lNew.setTheme("labelscoretotal");
		lblNew = new Label("0");
		lblNew.setTheme("labelscoretotalright");
		
		
		lShare2 = new Label("Share: ");
		lblShare2 = new Label("");
		lblShare2.setTheme("labelright");

		lNew2 = new Label("New Total:");
		lNew2.setTheme("labelscoretotal");
		lblNew2 = new Label("0");
		lblNew2.setTheme("labelscoretotalright");

		
		DialogLayout.Group hLeftLabel1 = p1ResultPanel.createParallelGroup(lShare, lNew);
		DialogLayout.Group hRightResult1 = p1ResultPanel.createParallelGroup(lblShare, lblNew);
		
		p1ResultPanel.setHorizontalGroup(p1ResultPanel.createParallelGroup()
				.addGroup(p1ResultPanel.createSequentialGroup(hLeftLabel1, hRightResult1)));
		
		p1ResultPanel.setVerticalGroup(p1ResultPanel.createSequentialGroup()
				.addGap(10).addGroup(p1ResultPanel.createParallelGroup(lShare, lblShare))
				.addGap(32).addGroup(p1ResultPanel.createParallelGroup(lNew, lblNew)));
		
		
		DialogLayout.Group hLeftLabel2 = p2ResultPanel.createParallelGroup(lShare2, lNew2);
		DialogLayout.Group hRightResult2 = p2ResultPanel.createParallelGroup(lblShare2, lblNew2);
		
		p2ResultPanel.setHorizontalGroup(p2ResultPanel.createParallelGroup()
				.addGroup(p2ResultPanel.createSequentialGroup(hLeftLabel2, hRightResult2)));
		
		p2ResultPanel.setVerticalGroup(p2ResultPanel.createSequentialGroup()
				.addGap(10).addGroup(p2ResultPanel.createParallelGroup(lShare2, lblShare2))
				.addGap(32).addGroup(p2ResultPanel.createParallelGroup(lNew2, lblNew2)));
		
		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
			// Draw main images
			g.drawImage(background, 0, 0);
			g.setFont(titleFont.get());
			g.drawString("> " + start_message + "" + ticker, header_x, header_y);

			// Draw player images
			if(!finished)
			{
				g.drawImage(playerbg, player1_x, player1_y);
				g.drawImage(playerbg, player2_x, player2_y);
				g.drawImage(player1.getPlayerCharacter().getCharacterImage(), player1_x, player1_y);
				g.drawImage(player2.getPlayerCharacter().getCharacterImage(), player2_x, player2_y);
				g.setFont(mainFont.get());
				g.drawString("" + player1.getName(), player1_x+50, player1_y+9);
				g.drawString("" + player2.getName(), player2_x+50, player2_y+9);
			}
			
			if(finished)
			{
				g.drawImage(background, 0, 0);
				g.setFont(titleFont.get());
				
				g.drawString("> " + start_message + "" + ticker, header_x, header_y);
				g.drawImage(playerbg, 146, 375);
				g.drawImage(playerbg, 506, 375);
				g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 146, 375);
				g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 506, 375);
				g.setFont(mainFont.get());
				g.drawString("" + player1.getName(), 146+50, 384);
				g.drawString("" + player2.getName(), 506+50, 384);
				if(success)
					g.drawImage(accept, 240, 110);
				else
					g.drawImage(reject, 240, 110);
			}
			
			g.drawImage(scorebackground, 0,0);
			g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 13,13);
			g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 13,55);
			g.drawString("" + player1.getName(), 65, 22);
			g.drawString("" + player2.getName(), 65, 64);
			g.drawString("" + player1.getScore(), 204, 22);
			g.drawString("" + player2.getScore(), 204, 64);
			
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
			
			
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		input = gc.getInput();
		clock2 += delta;
		clock3 += delta;
		timer2 -= delta;
		overallTimer += delta;
		overallState = HRRUClient.cs.getState();
		gameState = HRRUClient.cs.getGameState();
		
		// Connection to server lost
		if(overallState == serverlost)
			sbg.enterState(0);
		
		// Connection to other player lost
		if(overallState == cancelled) {
			if(playerID == 1)
				sbg.enterState(1);
			else sbg.enterState(2);
		}
		
		// Check otherplayerstatus
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
		// Clock ticks
		if(clock2>999)
		{
			timer--;
			timer2=999;
			if(timer<0)
			{					
				time_out = true;
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
		// Check if timed out
		
		if(time_out)
		{
			if(bPlayerProp)
			{
				if(gameState == 0)
				{
					vaProposal.setValue(0);
					emulateYes();
				}
			}
			if(bPlayerDec)
			{
				if(gameState == 2)
				{
					vaProposal.setValue(0);
					emulateYes();
				}
			}
		}
		// Update if player is giving
		if(bPlayerProp)
		{
			int currentValue = vaProposal.getValue();
			if(playerID == 1)
			{
				lblPlayer1.setText(overallPoints-currentValue + " / " + overallPoints);
				lblPlayer2.setText(currentValue + " / " + overallPoints);
			}
			else
			{
				lblPlayer2.setText(overallPoints-currentValue + " / " + overallPoints);
				lblPlayer1.setText(currentValue + " / " + overallPoints);
			}
			if(pShowRollBanner)
			{
				if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
				{
					pShowRollBanner = false;
					lblYourTurn.setVisible(false);
				}
			}
			if(gameState == 1)
			{
				// Player Give Turn, reset timer and wait for other player
				lStatus.setTheme("labelred");
				lStatus.setText("Waiting for " + otherPlayer.getName() + "\n to accept or reject your proposal.");
				lStatus.reapplyTheme();
				overallTimer += timer;
				timer = 40;
				timer2 = 999;
				HRRUClient.cs.setGameState(2);
			}
			else if(gameState == 3)
			{
				timer = 5;
				lStatus.setTheme("labelgreen");
				lStatus.reapplyTheme();
				lStatus.setText(otherPlayer.getName() + " has responded.");
				lblConfirmation.setVisible(false);
				HRRUClient.cs.setGameState(4);
			}
		}
		
		// Update if player is returning
		else if(bPlayerDec)
		{
			if(gameState == 0)
			{
				lStatus.setTheme("labelred");
				lStatus.reapplyTheme();
			}
			if(gameState == 1)
			{
				p1Panel.setPosition(75, 130);
				p2Panel.setPosition(425, 130);
				lStatus.setTheme("labelgreen");
				lStatus.reapplyTheme();
				questionDescription.setVisible(false);
				player1_y -= 140; 
				player2_y -= 140; 
				pShowRollBanner = true;
				lblYourTurn.setVisible(true);
				lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 240);
				lblConfirmation.setVisible(false);
				btnYes.setPosition((gcw/2) - 152, fixed_y + 270);
				btnNo.setPosition((gcw/2) - 152, fixed_y + 305);
				// Player Return turn
				overallTimer += timer;
				timer = 40;
				timer2 = 999;
				enableChoices();
				lStatus.setVisible(false);
				lblConfirmation.setVisible(true);
				time_out = false;
				HRRUClient.cs.setGameState(2);
				playerPropValue = player.getCurrentUltimatumScore().getPlayerPropValue();
				playerDecValue = player.getCurrentUltimatumScore().getPlayerDecValue();
				if(playerID == 1)
				{
					lblPlayer2.setText(overallPoints-playerDecValue + " / " + overallPoints);
					lblPlayer1.setText(playerDecValue + " / " + overallPoints);
				}
				else
				{
					lblPlayer1.setText(overallPoints-playerDecValue + " / " + overallPoints);
					lblPlayer2.setText(playerDecValue + " / " + overallPoints);
				}
				lStatus.setText(otherPlayerName + " has proposed the above split of points, do you accept or reject?");
			}
			if(gameState == 2)
				if(pShowRollBanner)
				{
					if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
					{
						pShowRollBanner = false;
						lblYourTurn.setVisible(false);
					}
				}
			if(gameState == 3)
			{
				timer = 5;
				HRRUClient.cs.setGameState(4);
			}
			
		}
		
		// Timer for end game
		if(gameState == 4)
		{
			if(timer < 0)
			{
			// 	Calculate variables and set new labels
				if(playerProp == playerID)
				{
					if(playerID==1)
					{
						success = HRRUClient.cs.getP1().getCurrentUltimatumScore().isSuccess();
						lblShare.setText(playerPropValue + " / " + overallPoints);
						lblShare2.setText(playerDecValue + " / " + overallPoints);
						lblNew.setText("" + (p1Score));
						lblNew2.setText("" + (p2Score));
						if(success)
						{
							p1ResultPanel.setTheme("correctbid-panel");
							p1ResultPanel.reapplyTheme();
							p2ResultPanel.setTheme("correctbid-panel");
							p2ResultPanel.reapplyTheme();
							lblNew.setText("" + (p1Score+playerPropValue));
							lblNew2.setText("" + (p2Score+playerDecValue));
						}
					}
					if(playerID==2)
					{
						lblNew2.setText("" + (p1Score));
						lblNew.setText("" + (p2Score));
						success = HRRUClient.cs.getP2().getCurrentUltimatumScore().isSuccess();
						lblShare2.setText(playerPropValue + " / " + overallPoints);
						lblShare.setText(playerDecValue + " / " + overallPoints);
						if(success)
						{
							p1ResultPanel.setTheme("correctbid-panel");
							p1ResultPanel.reapplyTheme();
							p2ResultPanel.setTheme("correctbid-panel");
							p2ResultPanel.reapplyTheme();
							lblNew2.setText("" + (p1Score+playerPropValue));
							lblNew.setText("" + (p2Score+playerDecValue));
						}
					}
					
				}
				else
				{
					if(playerID==1)
					{
						lblShare.setText(playerPropValue + " / " + overallPoints);
						lblShare2.setText(playerDecValue + " / " + overallPoints);
						lblNew2.setText("" + (p1Score));
						lblNew.setText("" + (p2Score));
						if(success)
						{
							p1ResultPanel.setTheme("correctbid-panel");
							p1ResultPanel.reapplyTheme();
							p2ResultPanel.setTheme("correctbid-panel");
							p2ResultPanel.reapplyTheme();
							lblShare2.setText(playerPropValue + " / " + overallPoints);
							lblShare.setText(playerDecValue + " / " + overallPoints);
							lblNew2.setText("" + (p1Score+playerPropValue));
							lblNew.setText("" + (p2Score+playerDecValue));
						}
					}
					else
					{
						lblShare.setText(playerPropValue + " / " + overallPoints);
						lblShare2.setText(playerDecValue + " / " + overallPoints);
						lblNew.setText("" + (p1Score));
						lblNew2.setText("" + (p2Score));
						if(success)
						{
							p1ResultPanel.setTheme("correctbid-panel");
							p1ResultPanel.reapplyTheme();
							p2ResultPanel.setTheme("correctbid-panel");
							p2ResultPanel.reapplyTheme();
							lblShare.setText(playerPropValue + " / " + overallPoints);
							lblShare2.setText(playerDecValue + " / " + overallPoints);
							lblNew.setText("" + (p1Score+playerPropValue));
							lblNew2.setText("" + (p2Score+playerDecValue));
						}
					}
				}
				

			// 	Set new variables to finish
				finished = true;
				overallTimer += timer;
				timer = 6;
				timer2 = 999;
				clock2 = 0;
				clock3 = 0;
				full_start_message = "The results are in...";
				full_start_counter = 0;
				ticker = "";
				start_message = "";
				p1Panel.setPosition(75, 130);
				p2Panel.setPosition(425, 130);
				rootPane.removeAllChildren();
				rootPane.add(p1ResultPanel);
				rootPane.add(p2ResultPanel);
			// 	Calculate new scores
				UltimatumScore ultimatumScore = new UltimatumScore(playerProp, playerDec, playerPropValue, playerDecValue, overallPoints, success);
			// 	If Player1, update Player2's new score and add own result to list of results.
				if(playerID == 1)
				{
					HRRUClient.cs.getP1().addUltimatumScore(ultimatumScore);
					if(success)
						if(bPlayerProp)
						{
							HRRUClient.cs.getP1().addScore(playerPropValue);
							HRRUClient.cs.getP2().addScore(playerDecValue);
						}
						else
						{
							HRRUClient.cs.getP1().addScore(playerDecValue);
							HRRUClient.cs.getP2().addScore(playerPropValue);
						}
				}
				else
				{
					HRRUClient.cs.getP2().addUltimatumScore(ultimatumScore);
					if(success)
						if(bPlayerDec)
						{
							HRRUClient.cs.getP2().addScore(playerDecValue);
							HRRUClient.cs.getP1().addScore(playerPropValue);
						}
						else
						{
							HRRUClient.cs.getP2().addScore(playerPropValue);
							HRRUClient.cs.getP1().addScore(playerDecValue);
						}
				}
			// 	Sync message
				syncMessage = new Packet00SyncMessage();
				syncMessage.player = playerID;
				syncMessage.sessionID = HRRUClient.cs.getSessionID();
				client.sendTCP(syncMessage);
				HRRUClient.cs.setGameState(5);
			}
		}
		// End Game
		if(gameState == 5)
		{
			if(timer<0)
			{
				if(HRRUClient.cs.isSync())
				{
					HRRUClient.cs.updateTimer(overallTimer);
					System.out.println("Time Subtract" + (overallTimer));
					HRRUClient.cs.setSync(false);
					HRRUClient.cs.setGameState(0);
					HRRUClient.cs.setState(p1_turn);
					sbg.enterState(play, new FadeOutTransition(), new FadeInTransition());
				}
			}
		}
		
	}

	@Override
	public int getID() {
		return 16;
	}

}