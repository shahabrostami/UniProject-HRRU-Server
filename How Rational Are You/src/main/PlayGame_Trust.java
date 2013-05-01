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
import conn.Packet.Packet18TrustFirst;
import conn.Packet.Packet19TrustSecond;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.ValueAdjusterInt;

public class PlayGame_Trust extends BasicTWLGameState {
	// set up state variables
	Input input;
	private int gameState, overallState, sessionID;
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int play = 11;
	private final int p1_turn = 7;
	public final int question_points_amount = 100;
	// set up GUI variables
	public Client client;
	DialogLayout giveResultPanel, returnResultPanel;
	Label lGive, lblGive, lChoose, lblChoose, lKeep, lblKeep;
	Label lGave, lblGave;
	Label lKeep2, lblKeep2, lReturn, lblReturn;
	Label lKept, lblKept, lReceivedBack, lblReceivedBack;
	Label lReceived, lblReceived, lReturned, lblReturned;
	Label lProfit, lProfit2, lblProfit, lblProfit2;
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
	int otherPlayerReady;
	int player1_x, player1_y, player2_x, player2_y;
	private boolean pShowRollBanner = false;
	
	// Game variable
	private int playerGive, playerReturn;
	private String playerGiveName, playerReturnName;
	private boolean bPlayerGive, bPlayerReturn;
	private int maxToGive, maxToReturn;
	private int giveValue, giveValueFull, returnValue;
	private int giveProfit, returnProfit;
	private int giveScore, returnScore;
	
	// Item Panel & Variables
	DialogLayout firstPanel, secondPanel;
	Label lblFirst, lblSecond;
	TextArea questionDescription;
	HTMLTextAreaModel questionDescriptionModel;
	ValueAdjusterInt vaBid; 
	Button btnSubmit;
	
	// Confirmation Panel & Variables
	Label lblConfirmation;
	Button btnYes, btnNo;

	// Image setup
	Image scorebackground, background, playerbg;
	private int header_x = 330;
	private int header_y = 25;
	private int timer_x = 600;
	private int timer_y = 550;
	private int fixed_y = 150;
	private int firstfixed_x = 132;
	private int firstfixed_y = 292;
	private int secondfixed_x = 132;
	private int secondfixed_y = 402;
	
	// Ticker setup
	private String start_message = "";
	private String full_start_message = "TO TRUST \n ... OR NOT TO TRUST..?";
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
	private int clock2,clock3,clock4,timer,timer2,overallTimer = 0;
	private boolean time_out, finished;
	
	Packet18TrustFirst packetGive;
	Packet19TrustSecond packetReturn;
	
	Packet00SyncMessage syncMessage;
	
	public PlayGame_Trust(int main) {
	}
	
	void disableGUI()
	{
		btnSubmit.setVisible(false);
		vaBid.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 350);
		if(bPlayerGive)
			lblConfirmation.setText("\nYou gave " + giveValue + ".\nWaiting for " + otherPlayer.getName());
		else
			lblConfirmation.setText("\n\nYou returned " + returnValue + " points!");
	}
	
	void disableChoices()
	{
		btnSubmit.setVisible(false);
		vaBid.setVisible(false);
		lblConfirmation.setVisible(true);
		btnYes.setVisible(true);
		btnNo.setVisible(true);
	}
	
	void enableChoices()
	{
		btnSubmit.setVisible(true);
		vaBid.setVisible(true);
		lblConfirmation.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
	}
	
	void emulateChoice()
	{
		disableChoices();
		if(bPlayerGive)
			lblConfirmation.setText("You want to give '" + vaBid.getValue() + "' points?");
		else if(bPlayerReturn)
			lblConfirmation.setText("You want to return '" + vaBid.getValue() + "' points?");
	}
	
	void emulateYes()
	{
		if(bPlayerGive)
		{
			// Player give value
			giveValue = vaBid.getValue();
			giveValueFull = giveValue * multiplier;
			maxToReturn = giveValueFull;
			packetGive.playerGiveValue = giveValue;
			packetGive.player = playerID;
			HRRUClient.cs.setGameState(1);
			client.sendTCP(packetGive);

			lblFirst.setText("\nYou gave " + giveValue + " points!\n\n"
					+	"This gives " + otherPlayer.getName() + " " + giveValueFull + " points!");

			disableGUI();
		}
		if(bPlayerReturn)
		{
			// Player return value
			returnValue = vaBid.getValue();
			packetReturn.playerReturnValue = returnValue;
			packetReturn.player = playerID;
			HRRUClient.cs.setGameState(3);
			client.sendTCP(packetReturn);
			lblSecond.setText("\n\nYou returned " + returnValue + " points!");
			disableGUI();
		}
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
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
		client = HRRUClient.conn.getClient();	
		// Reset main variables
		rootPane.removeAllChildren();
		timer = 70;
		timer2 = 999;
		clock4 = 0;
		overallTimer = 0;
		sessionID = HRRUClient.cs.getSessionID();
		HRRUClient.cs.setGameState(0);
		returnResultPanel.setTheme("incorrecttrust-panel");
		giveResultPanel.setTheme("incorrecttrust-panel");
		
		// Reset packet variables
		packetReturn = new Packet19TrustSecond();
		packetGive = new Packet18TrustFirst();
		packetGive.sessionID = sessionID;
		packetReturn.sessionID = sessionID;
		
		
		// Reset variables
		bPlayerGive = false;
		bPlayerReturn = false;
		pShowRollBanner = false;
		maxToGive = 0;
		maxToReturn = 0;
		giveValue = 0;
		giveValueFull = 0; 
		returnValue = 0;
		giveProfit = 0;
		returnProfit = 0;
		finished = false;
		time_out = false;
		vaBid.setValue(0);
		
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
		}
		else
		{
			otherPlayerID = 1;
			player = player2;
			otherPlayer = player1;
		}
		
		// Retrieve game information
		playerGive = HRRUClient.cs.getSecondary_id();
		if(playerGive == 1)
			playerReturn = 2;
		else playerReturn = 1;
	
		maxToGive = HRRUClient.cs.getSecondary_value();
		multiplier = HRRUClient.cs.getThird_value();
		
		// Set initial game state
		if(playerID == playerGive)
		{
			pShowRollBanner = true;
			lblYourTurn.setVisible(true);
			bPlayerGive = true;
			bPlayerReturn = false;
			giveScore = player.getScore();
			returnScore = otherPlayer.getScore();
			player1_x = firstfixed_x;
			player1_y = firstfixed_y;
			player2_x = secondfixed_x;
			player2_y = secondfixed_y;
			vaBid.setMinMaxValue(0, maxToGive);
			vaBid.setVisible(true);
			btnSubmit.setVisible(true);
			lblFirst.setText("Submit value for " + otherPlayer.getName() + "...");
			lblSecond.setText("");
			playerGiveName = player.getName();
			playerReturnName = otherPlayer.getName();
		}
		else
		{
			bPlayerReturn = true;
			bPlayerGive = false;
			returnScore = player.getScore();
			giveScore = otherPlayer.getScore();
			vaBid.setVisible(false);
			btnSubmit.setVisible(false);
			lblFirst.setText("\n\nWaiting for " + otherPlayer.getName());
			lblSecond.setText("");
			playerReturnName = player.getName();
			playerGiveName = otherPlayer.getName();
		}
		lGive.setText(playerReturnName + " receives: ");
		// Set GUI
		if(playerGive == 1)
		{
			player1_x = firstfixed_x;
			player1_y = firstfixed_y;
			player2_x = secondfixed_x;
			player2_y = secondfixed_y;
		}
		else
		{
			player2_x = firstfixed_x;
			player2_y = firstfixed_y;
			player1_x = secondfixed_x;
			player1_y = secondfixed_y;
		}
		
		// Set up text
		
		questionDescriptionModel.setHtml("<html><body><div>"
				+ "<p style='margin-top: 10px; text-align: center;'><a style='font-family: name;'>" + playerGiveName + "</a> can choose any amount up to <a style='font-family: highlight;'>" + maxToGive + "</a>.</p>"
				+  "<p style='margin-top: 10px; text-align: center;'>The amount chosen is multiplied by "
				+ "<a style='font-family: highlight;'>" + multiplier + "</a> and given to <a style='font-family: name;'>" + playerReturnName + ".</a></p>"
				+ "<p style='margin-top: 10px; text-align: center;'><a style='font-family: name;'>" + playerGiveName + "</a> keeps the rest of the amount not given.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'><a style='font-family: name;'>" + playerReturnName + "</a> can then return any of this amount back to <a style='font-family: name;'>"+ playerGiveName + "</a>.</p>"
				+ "</div></body></html>");
		
		
		// Reset views
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		lblConfirmation.setVisible(false);
		lKeep2.setVisible(false);
		lReturn.setVisible(false);
		lblKeep2.setVisible(false);
		lblReturn.setVisible(false);
		questionDescription.setVisible(true);
		firstPanel.setVisible(true);
		secondPanel.setVisible(true);
		
		// Setup new item variables
		rootPane.add(btnYes);
		rootPane.add(btnNo);
		rootPane.add(lblConfirmation);
		rootPane.add(vaBid);
		rootPane.add(btnSubmit);
		rootPane.add(firstPanel);
		rootPane.add(secondPanel);
		rootPane.add(questionDescription);
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
		questionDescription.setPosition(50, 100);
		questionDescription.setSize(700,300);
		questionDescription.setTheme("trusttextarea");
		
		// Setup Player GUI
		firstPanel = new DialogLayout();
		secondPanel = new DialogLayout();
		firstPanel.setSize(380, 80);
		secondPanel.setSize(380, 80);
		firstPanel.setTheme("firsttrust-panel");
		secondPanel.setTheme("secondtrust-panel");
		firstPanel.setPosition(100, 260);
		secondPanel.setPosition(100, 370);
		firstPanel.setIncludeInvisibleWidgets(false);
		secondPanel.setIncludeInvisibleWidgets(false);
		
		lblFirst = new Label("");
		lblSecond = new Label("");
		lGive = new Label("Give: ");
		lblGive = new Label("");
		lChoose = new Label("Choose: ");
		lblChoose = new Label("");
		lKeep = new Label("Keep: ");
		lblKeep = new Label("");
		
		lGive.setTheme("labelscoretotal");
		lKeep.setTheme("labelscoretotal");
		lChoose.setTheme("labelscoretotal");
		lblGive.setTheme("labelscoretotalright");
		lblKeep.setTheme("labelscoretotalright");
		lblChoose.setTheme("labelscoretotalright");
		
		DialogLayout.Group hGLeft = firstPanel.createParallelGroup(lGive, lKeep, lChoose);
		DialogLayout.Group hGRight = firstPanel.createParallelGroup(lblGive, lblKeep, lblChoose);
		firstPanel.setHorizontalGroup(firstPanel.createParallelGroup()
				.addWidget(lblFirst)
				.addGroup(firstPanel.createSequentialGroup(hGLeft, hGRight)));
		
		firstPanel.setVerticalGroup(firstPanel.createSequentialGroup()
				.addWidget(lblFirst)
				.addGroup(firstPanel.createParallelGroup(lChoose, lblChoose))
				.addGroup(firstPanel.createParallelGroup(lKeep, lblKeep))
				.addGroup(firstPanel.createParallelGroup(lGive, lblGive)));
		
		lKeep2 = new Label("Keep: ");
		lblKeep2 = new Label("");
		lReturn = new Label("Return: ");
		lblReturn = new Label("");
		
		lKeep2.setTheme("labelscoretotal");
		lReturn.setTheme("labelscoretotal");
		lblKeep2.setTheme("labelscoretotalright");
		lblReturn.setTheme("labelscoretotalright");
		
		DialogLayout.Group hRLeft = secondPanel.createParallelGroup(lKeep2, lReturn);
		DialogLayout.Group hRRight = secondPanel.createParallelGroup(lblKeep2, lblReturn);
		
		secondPanel.setHorizontalGroup(secondPanel.createParallelGroup()
				.addWidget(lblSecond)
				.addGroup(secondPanel.createSequentialGroup(hRLeft, hRRight)));
		secondPanel.setVerticalGroup(secondPanel.createSequentialGroup()
				.addWidget(lblSecond)
				.addGroup(secondPanel.createParallelGroup(lKeep2, lblKeep2))
				.addGroup(secondPanel.createParallelGroup(lReturn, lblReturn)));
		
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
		vaBid = new ValueAdjusterInt();
		vaBid.setTooltipContent(null);
		vaBid.setSize(200, 30);
		vaBid.setPosition((gcw/2) - vaBid.getWidth()/2, fixed_y + 340);
		
		btnSubmit = new Button("Submit Offer");
		btnSubmit.setSize(200, 30);
		btnSubmit.setPosition((gcw/2) - btnSubmit.getWidth()/2 - 2, fixed_y + 380);
		btnSubmit.setTheme("choicebutton");
		
		btnSubmit.addCallback(new Runnable() {
            public void run() {
            	emulateChoice();
            }
        });
	
		// Results GUI
		
		giveResultPanel = new DialogLayout();
        giveResultPanel.setTheme("incorrecttrust-panel");
		
		giveResultPanel.setSize(310, 310);
		giveResultPanel.setPosition(
               (gcw/2 - giveResultPanel.getWidth()/2 - 220),
                (gch/2 - giveResultPanel.getHeight()/2));
		
		returnResultPanel = new DialogLayout();
        returnResultPanel.setTheme("incorrecttrust-panel");
		returnResultPanel.setSize(310, 310);
		returnResultPanel.setPosition(
               (gcw/2 - returnResultPanel.getWidth()/2 + 180),
                (gch/2 - returnResultPanel.getHeight()/2));
		
		lGave = new Label("Gave: ");
		lblGave = new Label(giveValue + " out of " + maxToGive);
		lblGave.setTheme("labelright");
		lKept = new Label("Kept: ");
		lblKept = new Label((maxToGive-giveValue)+ " out of " + maxToGive);
		lblKept.setTheme("labelright");
		lReceivedBack = new Label("Received: ");
		lblReceivedBack = new Label(returnValue + " out of " + maxToReturn);
		lblReceivedBack.setTheme("labelright");
		lProfit = new Label("Profit: ");
		lblProfit = new Label("" + giveProfit);
		lblProfit.setTheme("labelright");
		lNew = new Label("New Total:");
		lNew.setTheme("labelscoretotal");
		lblNew = new Label("" + (giveScore+giveProfit));
		lblNew.setTheme("labelscoretotalright");
		
		
		lReceived = new Label("Received: ");
		lblReceived = new Label("" + giveValue);
		lblReceived.setTheme("labelright");
		lReturned = new Label("Returned: ");
		lblReturned = new Label(returnValue + " out of " + maxToReturn);
		lblReturned.setTheme("labelright");
		lProfit2 = new Label("Profit: ");
		lblProfit2 = new Label("" + (returnProfit));
		lblProfit2.setTheme("labelright");
		lNew2 = new Label("New Total:");
		lNew2.setTheme("labelscoretotal");
		lblNew2 = new Label("" + (returnScore+returnProfit));
		lblNew2.setTheme("labelscoretotalright");

		
		DialogLayout.Group hLeftLabel1 = giveResultPanel.createParallelGroup(lGave, lKept, lReceivedBack, lProfit, lNew);
		DialogLayout.Group hRightResult1 = giveResultPanel.createParallelGroup(lblGave, lblKept, lblReceivedBack, lblProfit, lblNew);
		
		giveResultPanel.setHorizontalGroup(giveResultPanel.createParallelGroup()
				.addGroup(giveResultPanel.createSequentialGroup(hLeftLabel1, hRightResult1)));
		
		giveResultPanel.setVerticalGroup(giveResultPanel.createSequentialGroup()
				.addGap(100).addGroup(giveResultPanel.createParallelGroup(lGave, lblGave))
				.addGap(30).addGroup(giveResultPanel.createParallelGroup(lKept, lblKept))
				.addGap(30).addGroup(giveResultPanel.createParallelGroup(lReceivedBack, lblReceivedBack))
				.addGap(30).addGroup(giveResultPanel.createParallelGroup(lProfit, lblProfit))
				.addGap(32).addGroup(giveResultPanel.createParallelGroup(lNew, lblNew)));
		
		
		DialogLayout.Group hLeftLabel2 = returnResultPanel.createParallelGroup(lReceived, lReturned, lProfit2, lNew2);
		DialogLayout.Group hRightResult2 = returnResultPanel.createParallelGroup(lblReceived, lblReturned, lblProfit2, lblNew2);
		
		returnResultPanel.setHorizontalGroup(returnResultPanel.createParallelGroup()
				.addGroup(returnResultPanel.createSequentialGroup(hLeftLabel2, hRightResult2)));
		
		returnResultPanel.setVerticalGroup(returnResultPanel.createSequentialGroup()
				.addGap(100).addGroup(returnResultPanel.createParallelGroup(lReceived, lblReceived))
				.addGap(30).addGroup(returnResultPanel.createParallelGroup(lReturned, lblReturned))
				.addGap(74).addGroup(returnResultPanel.createParallelGroup(lProfit2, lblProfit2))
				.addGap(32).addGroup(returnResultPanel.createParallelGroup(lNew2, lblNew2)));
		
		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
			// Draw main images
			g.drawImage(background, 0, 0);
			g.setFont(titleFont.get());
			g.drawString("> " + start_message + "" + ticker, header_x, header_y);
			g.drawImage(scorebackground, 0,0);

			// Draw player images
			g.drawImage(playerbg, player1_x, player1_y);
			g.drawImage(playerbg, player2_x, player2_y);
			g.drawImage(player1.getPlayerCharacter().getCharacterImage(), player1_x, player1_y);
			g.drawImage(player2.getPlayerCharacter().getCharacterImage(), player2_x, player2_y);
			g.setFont(mainFont.get());
			g.drawString("" + player1.getName(), player1_x+50, player1_y+9);
			g.drawString("" + player2.getName(), player2_x+50, player2_y+9);
			
			
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
			
			if(finished)
			{
				if(bPlayerGive)
				{
					g.drawImage(playerbg, 124, 175);
					g.drawImage(playerbg, 524, 175);
					g.drawImage(player.getPlayerCharacter().getCharacterImage(), 124, 175);
					g.drawImage(otherPlayer.getPlayerCharacter().getCharacterImage(), 524, 175);
					g.setFont(mainFont.get());
					g.drawString("" + player.getName(), 124+50, 175+9);
					g.drawString("" + otherPlayer.getName(), 524+50, 175+9);
				}
				else
				{
					g.drawImage(playerbg, 124, 175);
					g.drawImage(playerbg, 524, 175);
					g.drawImage(otherPlayer.getPlayerCharacter().getCharacterImage(), 124, 175);
					g.drawImage(player.getPlayerCharacter().getCharacterImage(), 524, 175);
					g.setFont(mainFont.get());
					g.drawString("" + otherPlayer.getName(), 124+50, 175+9);
					g.drawString("" + player.getName(), 524+50, 175+9);
				}
			}
			
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		input = gc.getInput();
		clock2 += delta;
		clock3 += delta;
		clock4 += delta;
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
			if(bPlayerGive)
			{
				if(gameState == 0)
				{
					vaBid.setValue(0);
					emulateYes();
				}
			}
			if(bPlayerReturn)
			{
				if(gameState == 2)
				{
					vaBid.setValue(0);
					emulateYes();
				}
			}
		}
		// Update if player is giving
		if(bPlayerGive)
		{
			if(pShowRollBanner)
			{
				if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
				{
					pShowRollBanner = false;
					lblYourTurn.setVisible(false);
				}
			}
			if(gameState == 0)
			{
				lKeep.setVisible(true);
				lGive.setVisible(true);
				lChoose.setVisible(true);
				lblKeep.setVisible(true);
				lblGive.setVisible(true);
				lblChoose.setVisible(true);
				lKeep2.setVisible(false);
				lReturn.setVisible(false);
				lblKeep2.setVisible(false);
				lblReturn.setVisible(false);
				lblKeep.setText((maxToGive - vaBid.getValue()) + " / " + maxToGive);
				lblGive.setText((vaBid.getValue()*multiplier) + " / " + (maxToGive*multiplier) );
				lblChoose.setText((vaBid.getValue()) + " / " + maxToGive);
			}
			if(gameState == 1)
			{
				lKeep.setVisible(false);
				lGive.setVisible(false);
				lChoose.setVisible(false);
				lblKeep.setVisible(false);
				lblGive.setVisible(false);
				lblChoose.setVisible(false);
				lKeep2.setVisible(false);
				lReturn.setVisible(false);
				lblKeep2.setVisible(false);
				lblReturn.setVisible(false);
				// Player Give Turn, reset timer and wait for other player
				lblSecond.setTheme("labelred");
				lblSecond.setText("\n\nWaiting for " + otherPlayer.getName() + "...");
				lblSecond.reapplyTheme();
				overallTimer += timer;
				timer = 70;
				timer2 = 999;
				HRRUClient.cs.setGameState(2);
			}
			else if(gameState == 2)
			{
				if((clock4 % 2000) < 1000)
				{
					lblSecond.setTheme("labelred");
					lblSecond.reapplyTheme();
				}
				else
				{
					lblSecond.setTheme("labelwhite");
					lblSecond.reapplyTheme();
				}
			}
			else if(gameState == 3)
			{
				timer = 6;
				lblSecond.setTheme("label");
				lblSecond.reapplyTheme();
				returnValue = player.getCurrentTrustScore().getPlayerReturnValue();
				lblSecond.setText("\n\n" + playerReturnName + " returned " + returnValue + " points!");
				HRRUClient.cs.setGameState(4);
			}
		}
		// Update if player is returning
		else if(bPlayerReturn)
		{
			lKeep.setVisible(false);
			lGive.setVisible(false);
			lChoose.setVisible(false);
			lblKeep.setVisible(false);
			lblGive.setVisible(false);
			lblChoose.setVisible(false);
			if(gameState == 0)
			{
				lblFirst.setTheme("labelred");
				lblFirst.reapplyTheme();
				if((clock4 % 2000) < 1000)
				{
					lblFirst.setTheme("labelred");
					lblFirst.reapplyTheme();
				}
				else
				{
					lblFirst.setTheme("labelwhite");
					lblFirst.reapplyTheme();
				}
			}
			if(gameState == 1)
			{
				lblFirst.setTheme("label");
				lblFirst.reapplyTheme();
				pShowRollBanner = true;
				lblYourTurn.setVisible(true);
				// Player Return turn
				overallTimer += timer;
				timer = 70;
				timer2 = 999;
				enableChoices();
				time_out = false;
				lKeep2.setVisible(true);
				lReturn.setVisible(true);
				lblKeep2.setVisible(true);
				lblReturn.setVisible(true);
				HRRUClient.cs.setGameState(2);
				giveValue = player.getCurrentTrustScore().getPlayerGiveValue();
				giveValueFull = giveValue * multiplier;
				maxToReturn = giveValueFull;
				vaBid.setMinMaxValue(0, giveValueFull);
				lblFirst.setText(playerGiveName + " gave " + giveValue + " points!\n\n"
						+ "This gives you " + giveValueFull + " points!\n\n" 
						+ "How many points will you return?");
				lblSecond.setText("Value for " + playerGiveName + "...");
				
			}
			if(gameState == 2)
			{
				if(pShowRollBanner)
				{
					if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
					{
						pShowRollBanner = false;
						lblYourTurn.setVisible(false);
					}
				}
				lblKeep2.setText(maxToReturn-vaBid.getValue() + " / " + maxToReturn);
				lblReturn.setText(vaBid.getValue() + " / " + (maxToReturn) );
			}
			if(gameState == 3)
			{
				lKeep2.setVisible(false);
				lReturn.setVisible(false);
				lblKeep2.setVisible(false);
				lblReturn.setVisible(false);
				timer = 6;
				HRRUClient.cs.setGameState(4);
			}
			
		}
		// Timer for end game
		if(gameState == 4)
		{
			if(timer < 0)
			{
			// 	Calculate variables and set new labels
				giveProfit = (returnValue) + (maxToGive - giveValue);
				returnProfit = giveValueFull - returnValue;
				lblGave.setText(giveValue + " / " + maxToGive);
				lblKept.setText(""+ (maxToGive-giveValue) + " / " + maxToGive);
				lblReceivedBack.setText(returnValue + " / " + maxToReturn);
				lblProfit.setText("" + giveProfit);
				lblNew.setText("" + (giveScore+giveProfit));
				lblReceived.setText(giveValueFull + " / " + (maxToGive*multiplier));
				lblReturned.setText(returnValue + " / " + maxToReturn);
				lblProfit2.setText("" + (returnProfit));
				lblNew2.setText("" + (returnScore+returnProfit));
				
				if(returnProfit>0)
				{
					returnResultPanel.setTheme("correcttrust-panel");
					returnResultPanel.reapplyTheme();
				}
				if(giveProfit>0)
				{
					giveResultPanel.setTheme("correcttrust-panel");
					giveResultPanel.reapplyTheme();
				}
			// 	Set new variables to finish
				finished = true;
				overallTimer += timer;
				timer = 9;
				timer2 = 999;
				clock2 = 0;
				clock3 = 0;
				full_start_message = "The results are in...";
				full_start_counter = 0;
				ticker = "";
				start_message = "";
				// 	Calculate new scores
				TrustScore trustScore = new TrustScore(playerGive, playerReturn, maxToGive, maxToReturn, giveValue, returnValue, multiplier, giveProfit, returnProfit);
				// Calculate correct panel to be displayed
				if(playerGive == 1)
				{
					giveResultPanel.setPosition(
				               (gcw/2 - 155 - 220),
				                (gch/2 - 155));
					returnResultPanel.setPosition(
			               (gcw/2 - 155 + 180),
			                (gch/2 - 155));
				}
				else
				{
					giveResultPanel.setPosition(
				               (gcw/2 - 155 + 180),
				                (gch/2 - 155));
					returnResultPanel.setPosition(
			               (gcw/2 - 155 - 220),
			                (gch/2 - 155));
				}
				//	If Player1, update Player2's new score and add own result to list of results.
				if(playerID == 1)
				{
					HRRUClient.cs.getP1().addTrustScore(trustScore);
					if(bPlayerGive)
					{
						HRRUClient.cs.getP1().addScore(giveProfit);
						HRRUClient.cs.getP2().addScore(returnProfit);
					}
					else
					{
						HRRUClient.cs.getP1().addScore(returnProfit);
						HRRUClient.cs.getP2().addScore(giveProfit);
					}
				}
				else
				{
					HRRUClient.cs.getP2().addTrustScore(trustScore);
					if(bPlayerGive)
					{
						HRRUClient.cs.getP1().addScore(returnProfit);
						HRRUClient.cs.getP2().addScore(giveProfit);
					}
					else
					{
						HRRUClient.cs.getP1().addScore(giveProfit);
						HRRUClient.cs.getP2().addScore(returnProfit);
					}
				}
				btnYes.setVisible(false);
				btnNo.setVisible(false);
				lblConfirmation.setVisible(false);
				questionDescription.setVisible(false);
				firstPanel.setVisible(false);
				secondPanel.setVisible(false);
				vaBid.setVisible(false);
				btnSubmit.setVisible(false);
				lblConfirmation.setVisible(false);
				rootPane.add(giveResultPanel);
				rootPane.add(returnResultPanel);
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

	@Override
	public int getID() {
		return 14;
	}

}