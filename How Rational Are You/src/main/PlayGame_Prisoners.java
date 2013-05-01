package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import main.item.Item;
import main.item.ItemList;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import com.esotericsoftware.kryonet.Client;

import conn.Packet.Packet00SyncMessage;
import conn.Packet.Packet20SendPrison;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TextArea;

public class PlayGame_Prisoners extends BasicTWLGameState {
	// set up game variables
	private int gameState;
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int play = 11;
	private final int p1_turn = 7;
	public final int question_points_amount = 100;
	// GUI variables
	public Client client;
	DialogLayout p1ResultPanel, p2ResultPanel;
	Label lProfit, lProfit2, lblProfit, lblProfit2;
	Label lNew, lNew2, lblNew, lblNew2;
	Button btnCooperate, btnBetray;
    
	int gcw;
	int gch;
	
	// Player variables
	boolean notMe;
	PrisonScore prisonScore;
	Player player1, player2, player, otherPlayer;
	int playerID, playerReady, otherPlayerID, otherPlayerReady;
	String otherPlayerName;
	
	// Game variables
	private int currentChoice;
	private int otherPlayerChoice;
	private final static int betray = 0;
	private final static int cooperate = 1;
	private final int bothCooperatePoints = 150;
	private final int cooperatePoints = 50;
	private final int betrayPoints = 250;
	private final int bothBetrayPoints = 0;
	
	String winString = "";
	
	// Item Panel & Variables
	DialogLayout descriptionPanel;
	Label lblItemName, lblItemValue;
	Label lItemName, lItemValue, lDescription;
	TextArea description;
	HTMLTextAreaModel descriptionModel;
	
	ItemList itemList;
	Item items[];
	Item currentItem;
	int itemValue, item_id;
	
	// Confirmation Panel & Variables
	Label lblConfirmation;
	Button btnYes, btnNo;
	
	Image scorebackground, background, playerbg, betraySmall, cooperateSmall;
	private int header_x = 330;
	private int header_y = 25;
	private int timer_x = 600;
	private int timer_y = 550;
	private int fixed_y = 150;
	// ticker variables
	private String start_message = "";
	private String full_start_message = "COOPERATE OR BETRAY?";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	// font size variables
	private int mainFontSize = 24;
	private int titleFontSize = 36;
	private int timerFontSize = 40;
	private int timerMFontSize = 18;
	// font variables
	private Font loadFont, loadMainFont, loadTitleFont, loadTimerFont, loadTimerMFont;
	private BasicFont mainFont, titleFont, readyFont, timerFont, timerMFont;;
	
	private int clock2,clock3,timer,timer2,overallTimer,timetaken = 0;
	private boolean timeout = false;
	
	
	Packet20SendPrison playerChoice;
	Packet00SyncMessage syncMessage;
	
	public PlayGame_Prisoners(int main) {
	}

	void disableGUI()
	{
		btnBetray.setVisible(false);
		btnCooperate.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		lblConfirmation.setVisible(true);
		if(currentChoice == betray)
			lblConfirmation.setText("You chose to BETRAY.\nWaiting for " + otherPlayer.getName());
		else
			lblConfirmation.setText("You chose to COOPERATE.\nWaiting for " + otherPlayer.getName());
	}
	
	void disableChoices()
	{
		// disable buttons
		btnBetray.setVisible(false);
		btnCooperate.setVisible(false);
		lblConfirmation.setVisible(true);
		btnYes.setVisible(true);
		btnNo.setVisible(true);
	}
	
	void enableChoices()
	{
		currentChoice = betray;
		btnBetray.setVisible(true);
		btnCooperate.setVisible(true);
		lblConfirmation.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
	}
	
	void emulateYes()
	{
		disableGUI();
		timetaken = timer;
		playerChoice = new Packet20SendPrison();
		playerChoice.choice = currentChoice;
		playerChoice.player = playerID;
		playerChoice.sessionID = HRRUClient.cs.getSessionID();
		playerChoice.elapsedTime = timer;
		client.sendTCP(playerChoice);
		HRRUClient.cs.setGameState(1);
	}
	
	void emulateCooperate()
	{
		disableChoices();
		lblConfirmation.setText("You want to cooperate with " + otherPlayerName + "?");
		currentChoice = cooperate;
	}
	
	void emulateBetray()
	{
		disableChoices();
		lblConfirmation.setText("You want to betray " + otherPlayerName + "?");
		currentChoice = betray;
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		client = HRRUClient.conn.getClient();
		rootPane.removeAllChildren();
		
		// Reset variables
		timeout = false;
		clock2 = 0; clock3 = 0; timetaken = 0;
		timer = 50;
		timer2 = 999;
		overallTimer = 0;
		playerReady = 0;
		otherPlayerReady = 0;
		HRRUClient.cs.setGameState(0);
		
		// Reset player variables
		currentChoice = betray;
		otherPlayerChoice = betray;
		
		// Reset ticker and text variables
		start_message = "";
		full_start_message = "COOPERATE OR BETRAY?";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		winString = "Choose wisely...";
		
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
				otherPlayerName = otherPlayer.getName();
		
		// description
		descriptionModel.setHtml("<html><body><div><p style='text-align: center;'>"
				+ "You and " + otherPlayerName + " can either "
				+ "<a style='font-family: name;'>cooperate</a> or <a style='font-family: name;'>betray</a>.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'>If you both <a style='font-family: name;'>cooperate</a> you both receive <a style='font-family: highlight;'>" + bothCooperatePoints + "</a> each.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'>If one player chooses to <a style='font-family: name;'>cooperate</a> and the other <a style='font-family: name;'>betrays</a> " 
				+ "then the player who <a style='font-family: name;'>cooperates</a> only receives <a style='font-family: highlight;'>" + cooperatePoints + "</a>"
				+ " with the <a style='font-family: name;'>betrayer</a> receiving <a style='font-family: highlight;'>" + betrayPoints + "</a>.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'>If you both <a style='font-family: name;'>betray</a> you both receive <a style='font-family: highlight;'>" + bothBetrayPoints + "</a>.</p>"
				+ "</div></body></html>");
				
		// Testing
		/*
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
		
		// Setup new item variables
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		lblConfirmation.setVisible(false);
		description.setVisible(true);
		btnBetray.setVisible(true);
		btnCooperate.setVisible(true);
		rootPane.add(btnCooperate);
		rootPane.add(btnBetray);
		rootPane.add(description);
		rootPane.add(btnYes);
		rootPane.add(btnNo);
		rootPane.add(lblConfirmation);
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
		
		// Set up images
		scorebackground = new Image("simple/playerscorebackground.png");
		background = new Image("simple/questionbg.png");
		playerbg = new Image("simple/playerbg.png");
		betraySmall = new Image("simple/betraysmall2.png");
		cooperateSmall = new Image("simple/cooperatesmall2.png");
		// Set up item panel
		descriptionModel = new HTMLTextAreaModel();
		description = new TextArea(descriptionModel);
		description.setTheme("trusttextarea");
		description.setPosition(50,150);
		description.setSize(700,100);
		
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
		readyFont = new BasicFont(loadTitleFont, Color.red);
		
		loadTimerFont = loadFont.deriveFont(Font.BOLD,timerFontSize);
		loadTimerMFont = loadFont.deriveFont(Font.BOLD,timerMFontSize);
		timerFont = new BasicFont(loadTimerFont);
		timerMFont = new BasicFont(loadTimerMFont);
		
		// Confirmation GUI
		lblConfirmation = new Label("");
		btnYes = new Button("Yes");
		btnNo = new Button("No");
		
		lblConfirmation.setTheme("labelscoretotal");
		btnYes.setTheme("choicebutton");
		btnNo.setTheme("choicebutton");
		
		lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 200);
		btnYes.setPosition((gcw/2) - 252, fixed_y + 230);
		btnNo.setPosition((gcw/2) - 252, fixed_y + 295);
		btnYes.setSize(500, 60);
		btnNo.setSize(500, 60);
		
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
		
		// Set up buttons
		btnBetray = new Button();
		btnCooperate = new Button();
		btnBetray.setSize(319, 239);
		btnCooperate.setSize(319, 239);
		btnBetray.setTheme("betraybutton");
		btnCooperate.setTheme("cooperatebutton");
		btnCooperate.setPosition(74, 300);
		btnBetray.setPosition(397, 300);
		
		btnBetray.addCallback(new Runnable() {
            public void run() {
            	emulateBetray();
            }
        });
		
		btnCooperate.addCallback(new Runnable() {
            public void run() {
            	emulateCooperate();
            }
        });
		
		// Results GUI
		p1ResultPanel = new DialogLayout();
        p1ResultPanel.setTheme("incorrectprison-panel");
		p1ResultPanel.setSize(310, 90);
		p1ResultPanel.setPosition(
               (gcw/2 - p1ResultPanel.getWidth()/2 - 200), 150);
		
		p2ResultPanel = new DialogLayout();
        p2ResultPanel.setTheme("correctprison-panel");
		p2ResultPanel.setSize(310, 90);
		p2ResultPanel.setPosition(
               (gcw/2 - p2ResultPanel.getWidth()/2 + 160), 150);
		
		lProfit = new Label("Profit:");
		lProfit2 = new Label("Profit: ");
		lblProfit = new Label("1");
		lblProfit2 = new Label("1");
		
		lNew = new Label("New Total: ");
		lNew2 = new Label("New Total: ");
		lblNew = new Label("2");
		lblNew2 = new Label("2");
		
		lblProfit.setTheme("labelright");
		lblProfit2.setTheme("labelright");
		lblNew.setTheme("labelscoretotalright");
		lblNew2.setTheme("labelscoretotalright");
		
		DialogLayout.Group hLeftLabel1 = p1ResultPanel.createParallelGroup(lProfit, lNew);
		DialogLayout.Group hRightResult1 = p1ResultPanel.createParallelGroup(lblProfit, lblNew);
		
		p1ResultPanel.setHorizontalGroup(p1ResultPanel.createParallelGroup()
				.addGap(120).addGroup(p1ResultPanel.createSequentialGroup(hLeftLabel1, hRightResult1)));
		
		p1ResultPanel.setVerticalGroup(p1ResultPanel.createSequentialGroup()
				.addGap(30).addGroup(p1ResultPanel.createParallelGroup(lProfit, lblProfit))
				.addGroup(p1ResultPanel.createParallelGroup(lNew, lblNew)));
		
		
		DialogLayout.Group hLeftLabel2 = p2ResultPanel.createParallelGroup(lProfit2, lNew2);
		DialogLayout.Group hRightResult2 = p2ResultPanel.createParallelGroup(lblProfit2, lblNew2);
		
		p2ResultPanel.setHorizontalGroup(p2ResultPanel.createParallelGroup()
				.addGroup(p2ResultPanel.createSequentialGroup(hLeftLabel2, hRightResult2)));
		
		p2ResultPanel.setVerticalGroup(p2ResultPanel.createSequentialGroup()
				.addGap(30).addGroup(p2ResultPanel.createParallelGroup(lProfit2, lblProfit2))
				.addGroup(p2ResultPanel.createParallelGroup(lNew2, lblNew2)));
		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
			if(gameState < 3)
			{
				g.drawImage(background, 0, 0);
				g.setFont(titleFont.get());
				g.drawString("> " + start_message + "" + ticker, header_x, header_y);
				g.drawImage(scorebackground, 0,0);
				g.setFont(mainFont.get());
				g.drawString("" + winString, header_x, header_y+30);
				g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 13, 13);
				g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 13, 55);
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
				
				g.setFont(readyFont.get());
				if(playerReady == 1)
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
			}
			else if(gameState>=3)
			{
				g.drawImage(background, 0, 0);
				g.setFont(titleFont.get());
				g.drawString("> " + start_message + "" + ticker, 50, header_y);
				g.setFont(mainFont.get());
				g.drawString("" + winString, 50, header_y+30);
				
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
				
				g.drawImage(playerbg, 144, 180);
				g.drawImage(playerbg, 504, 180);
				g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 144, 180);
				g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 504, 180);
				g.setFont(mainFont.get());
				g.drawString("" + player1.getName(), 144+50, 180+9);
				g.drawString("" + player2.getName(), 504+50, 180+9);
				if(playerID == 1)
				{
					if(currentChoice == 0)
						g.drawImage(betraySmall, 141, 230);
					else
						g.drawImage(cooperateSmall, 141, 230);
					if(otherPlayerChoice == 0)
						g.drawImage(betraySmall, 501, 230);
					else
						g.drawImage(cooperateSmall, 501, 230);
				}
				else
				{
					if(otherPlayerChoice == 0)
						g.drawImage(betraySmall, 141, 230);
					else
						g.drawImage(cooperateSmall, 141, 230);
					if(currentChoice == 0)
						g.drawImage(betraySmall, 501, 230);
					else
						g.drawImage(cooperateSmall, 501, 230);
				}

			}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		clock2 += delta;
		clock3 += delta;
		timer2 -= delta;
		overallTimer += delta;
		gameState = HRRUClient.cs.getGameState();
		
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
		// Clock ticks
		if(clock2>999)
		{
			timer--;
			timer2=999;
			if(timer<=0)
				timeout = true;
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
		if(timeout)
		{
			if(gameState == 0 || gameState == 2)
			{
				currentChoice = betray;
				emulateYes();
			}
		}
		if(gameState == 1)
		{
			playerReady = 1;
		}
		// Other player ready
		else if(gameState == 2)
		{
			otherPlayerReady = 1;
		}
		// Game finished
		else if(gameState == 3)
		{
			timer = 7;
			timer2 = 999;
			clock2 = 0;
			clock3 = 0;
			full_start_message = "The results are in...";
			full_start_counter = 0;
			ticker = "";
			start_message = "";
			
			prisonScore = player.getCurrentPrisonScore();
			otherPlayerChoice = prisonScore.getOtherPlayerChoice();
			prisonScore.setPlayerChoice(currentChoice);
			prisonScore.setPlayerTime(timetaken);
			p1ResultPanel.setTheme("incorrectprison-panel");
			p2ResultPanel.setTheme("incorrectprison-panel");
			// if player betrayed
			if(currentChoice == betray)
			{
				// both player betrayed
				if(otherPlayerChoice == betray)
				{
					winString = "\nYou failed to betray " + otherPlayerName + "!";
					prisonScore.setbOtherPlayerProfit(false);
					prisonScore.setbPlayerProfit(false);
					prisonScore.setPlayerProfit(bothBetrayPoints);
					prisonScore.setOtherPlayerProfit(bothBetrayPoints);
				}
				// player betrayed
				else if(otherPlayerChoice == cooperate)
				{
					winString = "\nYou successfully betrayed " + otherPlayerName + "!";
					prisonScore.setbOtherPlayerProfit(false);
					prisonScore.setbPlayerProfit(true);
					prisonScore.setPlayerProfit(betrayPoints);
					prisonScore.setOtherPlayerProfit(cooperatePoints);
				}
			}
			// if player cooperated
			else if(currentChoice == cooperate)
			{
				// both player cooperated
				if(otherPlayerChoice == cooperate)
				{
					winString = "\nYou both successfully cooperated. Good job!";
					prisonScore.setbOtherPlayerProfit(true);
					prisonScore.setbPlayerProfit(true);
					prisonScore.setPlayerProfit(bothCooperatePoints);
					prisonScore.setOtherPlayerProfit(bothCooperatePoints);
				}
				// player cooperated
				else if(otherPlayerChoice == betray)
				{
					winString = "\nYou were betrayed by " + otherPlayerName + "!";
					prisonScore.setbOtherPlayerProfit(true);
					prisonScore.setbPlayerProfit(false);
					prisonScore.setPlayerProfit(cooperatePoints);
					prisonScore.setOtherPlayerProfit(betrayPoints);
				}
			}
			// Update results
			if(playerID == 1)
			{
				if(prisonScore.isbPlayerProfit())
					p1ResultPanel.setTheme("correctprison-panel");	
				if(prisonScore.isbOtherPlayerProfit())
					p2ResultPanel.setTheme("correctprison-panel");
				HRRUClient.cs.getP1().addPrisonScore(prisonScore);
				HRRUClient.cs.getP1().addScore(prisonScore.getPlayerProfit());
				HRRUClient.cs.getP2().addScore(prisonScore.getOtherPlayerProfit());
				lblProfit.setText("" + prisonScore.getPlayerProfit());
				lblProfit2.setText("" + prisonScore.getOtherPlayerProfit());
			}
			else
			{
				if(prisonScore.isbPlayerProfit())
					p2ResultPanel.setTheme("correctprison-panel");	
				if(prisonScore.isbOtherPlayerProfit())
					p1ResultPanel.setTheme("correctprison-panel");
				HRRUClient.cs.getP2().addPrisonScore(prisonScore);
				HRRUClient.cs.getP1().addScore(prisonScore.getOtherPlayerProfit());
				HRRUClient.cs.getP2().addScore(prisonScore.getPlayerProfit());
				lblProfit2.setText("" + prisonScore.getPlayerProfit());
				lblProfit.setText("" + prisonScore.getOtherPlayerProfit());
				
			}
			lblNew.setText("" + HRRUClient.cs.getP1().getScore());
			lblNew2.setText("" + HRRUClient.cs.getP2().getScore());
			p1ResultPanel.reapplyTheme();
			p2ResultPanel.reapplyTheme();
			rootPane.removeAllChildren();
			rootPane.add(p1ResultPanel);
			rootPane.add(p2ResultPanel);

			// Sync message
			syncMessage = new Packet00SyncMessage();
			syncMessage.player = playerID;
			syncMessage.sessionID = HRRUClient.cs.getSessionID();
			client.sendTCP(syncMessage);
			HRRUClient.cs.setGameState(4);
		}
		else if(gameState == 4)
		{
			if(timer<=0)
			{
				HRRUClient.cs.setGameState(5);
			}
		}
		else if(gameState == 5)
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
		return 15;
	}

}