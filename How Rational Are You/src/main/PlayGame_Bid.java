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
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.RotateTransition;
import org.newdawn.slick.state.transition.SelectTransition;
import com.esotericsoftware.kryonet.Client;

import conn.Packet.Packet00SyncMessage;
import conn.Packet.Packet16SendBid;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.textarea.HTMLTextAreaModel;
import de.matthiasmann.twl.textarea.SimpleTextAreaModel;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.TextArea;
import de.matthiasmann.twl.ValueAdjusterInt;

public class PlayGame_Bid extends BasicTWLGameState {
	
	// bid game stat variables
	private int gameState;
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int play = 11;
	private final int p1_turn = 7;
	public final int question_points_amount = 100;
	
	public Client client;
	DialogLayout p1ResultPanel, p2ResultPanel;
	Label lBid, lBid2, lblBid, lblBid2;
	Label lAmountWon, lAmountWon2, lblAmountWon, lblAmountWon2;
	
	int gcw;
	int gch;
	
	// Player variables
	BiddingScore biddingResult;
	Player player1, player2, player, otherPlayer;
	int otherPlayerReady;
	int currentBid, amountWon;
	int playerID, otherPlayerID;
	int maximumBid;
	int otherPlayerBid, otherPlayerWon;
	boolean winCheck;
	int playerWon;
	String winString = "The highest bidder wins the value\n of the item in points!";
	
	// Item Panel & Variables
	DialogLayout itemPanel;
	Label lblItemName, lblItemValue;
	Label lItemName, lItemValue, lDescription;
	TextArea itemDescription;
	SimpleTextAreaModel itemDescriptionModel;
	
	ItemList itemList;
	Item items[];
	Item currentItem;
	int itemValue, item_id;
	
	ValueAdjusterInt vaBid;
	Button btnSubmit;
	
	// Confirmation Panel & Variables
	Label lblConfirmation;
	Button btnYes, btnNo;
	TextArea description;
	HTMLTextAreaModel descriptionModel;
	// render variables
	Image scorebackground, background;
	private int header_x = 330;
	private int header_y = 25;
	private int timer_x = 600;
	private int timer_y = 550;
	private int fixed_y = 200;
	// ticker variables
	private String start_message = "";
	private String full_start_message = "MAKE YOUR BID...";
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
	private BasicFont mainFont, titleFont, readyFont, timerFont, timerMFont;
	// clock variables
	private int clock2,clock3,timer,timer2,overallTimer = 0;
	private boolean end, ready, finished, resume = false;
	
	Packet16SendBid playerBid;
	Packet00SyncMessage syncMessage;
	
	EmptyTransition emptyTransition;
    RotateTransition rotateTransition;
    SelectTransition selectTransition;
	
	public PlayGame_Bid(int main) {
	}

	void disableGUI()
	{
		btnSubmit.setVisible(false);
		vaBid.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 350);
		lblConfirmation.setText("You bid " + currentBid + ".\nWaiting for " + otherPlayer.getName());
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
		currentBid = 0;
		btnSubmit.setVisible(true);
		vaBid.setVisible(true);
		lblConfirmation.setVisible(false);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
	}
	
	void emulateChoice()
	{
		disableChoices();
		lblConfirmation.setText("Is your bid: '" + vaBid.getValue() + "' ?");
		currentBid = vaBid.getValue();
	}
	
	void emulateYes()
	{
		disableGUI();
		playerBid = new Packet16SendBid();
		playerBid.player = playerID;
		playerBid.bid = currentBid;
		playerBid.itemValue = itemValue;
		playerBid.sessionID = HRRUClient.cs.getSessionID();
		client.sendTCP(playerBid);
		ready = true;
		end = true;
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		client = HRRUClient.conn.getClient();
		rootPane.removeAllChildren();
		itemPanel.removeAllChildren();
		
		end = false; finished = false; resume = false; ready = false;
		otherPlayerReady = 0;
		currentBid = 0;
		amountWon = 0;
		maximumBid = 0;
		clock2 = 0; clock3 = 0;
		timer = 60;
		timer2 = 999;
		overallTimer = 0;
		otherPlayerBid = 0;
		otherPlayerWon = 0;
		winCheck = false;
		
		start_message = "";
		full_start_message = "MAKE YOUR BID...";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		winString = "Think carefully.";
		
				
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
		
		// Retrieve player information
		player1 = HRRUClient.cs.getP1();
		player2 = HRRUClient.cs.getP2();
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			player =  player1;
			otherPlayer = player2;
			otherPlayerID = 2;
			maximumBid = player.getScore();
		}
		else 
		{
			player =  player2;
			otherPlayer = player1;
			otherPlayerID = 1;
			maximumBid = player.getScore();
		}

		// setup description
		descriptionModel.setHtml("<html><body><div><p style='text-align: center;'>"
				+ "You and " + otherPlayer.getName() + " are both "
				+ "<a style='font-family: name;'>bidding</a> for the item using your <a style='font-family: name;'>points</a>.</p>"
				+ "<p style='margin-top: 10px; text-align: center;'>The player with the <a style='font-family: name;'>highest bid</a> wins the item for the <a style='font-family: name;'>cost of their winning bid.</a></p>"
				+ "<p style='margin-top: 10px; text-align: center;'>The highest bidder will then win the <a style='font-family: name;'>value</a> of the item in <a style='font-family: name;'>points!</a></p>" 
				+ "</div></body></html>");
		
		// Setup new item variables
		item_id = HRRUClient.cs.getSecondary_id();
		currentItem = items[item_id];
        itemValue = HRRUClient.cs.getSecondary_value();
        
        itemPanel.setPosition(50, fixed_y);
        lblItemName = new Label(currentItem.getName());
        lblItemValue = new Label("" + itemValue);
        itemDescriptionModel.setText(currentItem.getDescription());
        lblItemName.setTheme("itematari16");
        lblItemValue.setTheme("itematari16");
        itemDescription.setTheme("questiontextarea");
        
        DialogLayout.Group hItemLabel = itemPanel.createSequentialGroup(lItemName);
        DialogLayout.Group hItemResult = itemPanel.createSequentialGroup(lblItemName);
        DialogLayout.Group hDescriptionLabel = itemPanel.createSequentialGroup(lDescription);
        DialogLayout.Group hDescriptionResult = itemPanel.createSequentialGroup(itemDescription);
        DialogLayout.Group hItemValueLabel = itemPanel.createSequentialGroup(lItemValue);
        DialogLayout.Group hItemValueResult = itemPanel.createSequentialGroup(lblItemValue);
        
        itemPanel.setHorizontalGroup(itemPanel.createParallelGroup()
        		.addGroup(hItemLabel)
        		.addGroup(hItemResult)
        		.addGroup(hDescriptionLabel)
        		.addGroup(hDescriptionResult)
        		.addGroup(hItemValueLabel)
        		.addGroup(hItemValueResult));
        
        itemPanel.setVerticalGroup(itemPanel.createSequentialGroup()
        		.addWidget(lItemName)
        		.addWidget(lblItemName)
        		.addGap(30).addWidget(lDescription)
        		.addWidget(itemDescription)
        		.addGap(60).addWidget(lItemValue)
        		.addWidget(lblItemValue));
        
        lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 320);
        // Setup bidding GUI
		vaBid.setMinMaxValue(0, maximumBid);
		vaBid.setValue(0);
		vaBid.setVisible(true);
		btnSubmit.setVisible(true);
		btnYes.setVisible(false);
		btnNo.setVisible(false);
		lblConfirmation.setVisible(false);
		p1ResultPanel.setVisible(false);
		p2ResultPanel.setVisible(false);
		description.setVisible(true);
		rootPane.add(description);
		rootPane.add(p1ResultPanel);
		rootPane.add(p2ResultPanel);
		rootPane.add(btnYes);
		rootPane.add(btnNo);
		rootPane.add(lblConfirmation);
		rootPane.add(btnSubmit);
		rootPane.add(vaBid);
		rootPane.add(itemPanel);
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
		
		// Set up item lists
		itemList = new ItemList();
		items = itemList.getItems();
		
		// Set up item panel
		itemPanel = new DialogLayout();
		itemPanel.setTheme("item-panel");
		itemPanel.setSize(400, 220);
		itemPanel.setPosition(50, fixed_y);
		itemDescriptionModel = new SimpleTextAreaModel();
		itemDescription = new TextArea(itemDescriptionModel);
		descriptionModel = new HTMLTextAreaModel();
		description = new TextArea(descriptionModel);
		description.setTheme("trusttextarea");
		description.setPosition(10,110);
		description.setSize(780,100);
		
		lItemName = new Label("Name: ");
		lDescription = new Label("Description: ");
		lItemValue = new Label("Value: ");
		lItemName.setTheme("questionatari16lbl");
		lDescription.setTheme("questionatari16lbl");
		lItemValue.setTheme("questionatari16lbl");
		
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
		
		lblConfirmation.setPosition((gcw/2) - lblConfirmation.getWidth()/2, fixed_y + 320);
		btnYes.setPosition((gcw/2) - 152, fixed_y + 340);
		btnNo.setPosition((gcw/2) - 152, fixed_y + 370);
		btnYes.setSize(300, 25);
		btnNo.setSize(300, 25);
		
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
		vaBid.setSize(200, 30);
		vaBid.setPosition((gcw/2) - vaBid.getWidth()/2, fixed_y + 320);
		
		btnSubmit = new Button("Submit Bid");
		btnSubmit.setSize(200, 30);
		btnSubmit.setPosition((gcw/2) - btnSubmit.getWidth()/2 - 2, fixed_y + 360);
		btnSubmit.setTheme("choicebutton");
		
		btnSubmit.addCallback(new Runnable() {
            public void run() {
            	emulateChoice();
            }
        });
	
		// Results GUI
		p1ResultPanel = new DialogLayout();
        p1ResultPanel.setTheme("incorrectbid-panel");
		p1ResultPanel.setSize(300, 80);
		p1ResultPanel.setPosition(
               (gcw/2 - p1ResultPanel.getWidth()/2 - 200), 420);
		
		p2ResultPanel = new DialogLayout();
        p2ResultPanel.setTheme("incorrectbid-panel");
		p2ResultPanel.setSize(300, 80);
		p2ResultPanel.setPosition(
               (gcw/2 - p2ResultPanel.getWidth()/2 + 160), 420);
		
		lBid = new Label("Bid:");
		lBid2 = new Label("Bid: ");
		lAmountWon = new Label("Amount Won: ");
		lblAmountWon = new Label("");
		
		lblBid = new Label("");
		lblBid2 = new Label("");
		lAmountWon2 = new Label("Amount Won: ");
		lblAmountWon2 = new Label("");
		
		lblBid.setTheme("labelscoretotalright");
		lblBid2.setTheme("labelscoretotalright");
		lblAmountWon.setTheme("labelscoretotalright");
		lblAmountWon2.setTheme("labelscoretotalright");
		
		DialogLayout.Group hLeftLabel1 = p1ResultPanel.createParallelGroup(lBid, lAmountWon);
		DialogLayout.Group hRightResult1 = p1ResultPanel.createParallelGroup(lblBid, lblAmountWon);
		
		p1ResultPanel.setHorizontalGroup(p1ResultPanel.createParallelGroup()
				.addGap(120).addGroup(p1ResultPanel.createSequentialGroup(hLeftLabel1, hRightResult1)));
		
		p1ResultPanel.setVerticalGroup(p1ResultPanel.createSequentialGroup()
				.addGap(30).addGroup(p1ResultPanel.createParallelGroup(lBid, lblBid))
				.addGroup(p1ResultPanel.createParallelGroup(lAmountWon, lblAmountWon)));
		
		
		DialogLayout.Group hLeftLabel2 = p2ResultPanel.createParallelGroup(lBid2, lAmountWon2);
		DialogLayout.Group hRightResult2 = p2ResultPanel.createParallelGroup(lblBid2, lblAmountWon2);
		
		p2ResultPanel.setHorizontalGroup(p2ResultPanel.createParallelGroup()
				.addGroup(p2ResultPanel.createSequentialGroup(hLeftLabel2, hRightResult2)));
		
		p2ResultPanel.setVerticalGroup(p2ResultPanel.createSequentialGroup()
				.addGap(30).addGroup(p2ResultPanel.createParallelGroup(lBid2, lblBid2))
				.addGroup(p2ResultPanel.createParallelGroup(lAmountWon2, lblAmountWon2)));
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
			if(!end)
			{
				g.drawImage(background, 0, 0);
				g.drawImage(currentItem.getItemImage(), 75, fixed_y + 25);
				g.setFont(titleFont.get());
				g.drawString("> " + start_message + "" + ticker, header_x, header_y);
				g.drawImage(scorebackground, 0,0);
				g.setFont(mainFont.get());
				g.drawString("" + winString, header_x, header_y+30);
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
			}
			else if(end)
			{
				g.drawImage(background, 0, 0);
				g.setFont(titleFont.get());
				g.drawString("> " + start_message + "" + ticker, header_x, header_y);
				g.setFont(mainFont.get());
				g.drawString("" + winString, header_x, header_y+30);
				g.drawImage(currentItem.getItemImage(), 75, fixed_y + 25);
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
					g.drawImage(background, 0, 0);
					g.setFont(titleFont.get());
					g.drawString("> " + start_message + "" + ticker, 25, 25);
					g.drawString("" + timer, 750, 50);
					g.drawImage(currentItem.getItemImage(), 75, 125);
					g.drawImage(new Image("simple/playerbg.png"), 146, 435);
					g.drawImage(new Image("simple/playerbg.png"), 506, 435);
					g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 146, 435);
					g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 506, 435);
					g.setFont(mainFont.get());
					g.drawString(winString, 50, 60);
					g.drawString("" + player1.getName(), 198, 444);
					g.drawString("" + player2.getName(), 558, 444);
					
					
				}
			}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
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
					playerBid = new Packet16SendBid();
					playerBid.player = playerID;
					playerBid.bid = currentBid;
					playerBid.itemValue = itemValue;
					playerBid.sessionID = HRRUClient.cs.getSessionID();
					client.sendTCP(playerBid);
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
			if(otherPlayerReady == 2)
			{
				// Setup new UI
				timer = 10; // should be 10
				timer2 = 999;
				clock2 = 0;
				clock3 = 0;
				lblConfirmation.setVisible(false);
				description.setVisible(false);
				full_start_message = "The results are in...";
				full_start_counter = 0;
				ticker = "";
				start_message = "";
				tickerBoolean = true;
				itemPanel.setPosition(50, 100);
				p1ResultPanel.setVisible(true);
				p2ResultPanel.setVisible(true);

				lblBid.setText("0");
				lblBid2.setText("0");
				lblAmountWon.setText("0");
				lblAmountWon2.setText("0");
				p1ResultPanel.setTheme("incorrectbid-panel");
				p2ResultPanel.setTheme("incorrectbid-panel");
				p1ResultPanel.reapplyTheme();
				p2ResultPanel.reapplyTheme();
				// Setup players results
				if(playerID == 1)
				{
					// results view when player 1
					BiddingScore biddingScore = HRRUClient.cs.getP1().getCurrentBiddingScore();
					otherPlayerBid = biddingScore.getOtherPlayerBid();
					winCheck = biddingScore.isWin();
					playerWon = biddingScore.getPlayerWon();
					amountWon = biddingScore.getAmountWon();
					winString = "No one won the item!";
					if(winCheck)
					{
						winString = "You won " + amountWon + " points!";
						p1ResultPanel.setTheme("correctbid-panel");
						p2ResultPanel.setTheme("incorrectbid-panel");
						lblBid.setText("" + currentBid);
						lblBid2.setText("" + otherPlayerBid);
						lblAmountWon.setText("" + amountWon);
						lblAmountWon2.setText("0");
						p1ResultPanel.reapplyTheme();
						p2ResultPanel.reapplyTheme();
					}
					else if(!winCheck && otherPlayerBid != 0)
					{
						winString = otherPlayer.getName() + " wins " + amountWon + "!";
						p1ResultPanel.setTheme("incorrectbid-panel");
						p2ResultPanel.setTheme("correctbid-panel");
						lblBid.setText("" + currentBid);
						lblBid2.setText("" + otherPlayerBid);
						lblAmountWon.setText("0");
						lblAmountWon2.setText("" + amountWon);
						p1ResultPanel.reapplyTheme();
						p2ResultPanel.reapplyTheme();

					}
					finished = true;
				}
				else
				{
					// results view when player 2
					BiddingScore biddingScore = HRRUClient.cs.getP2().getCurrentBiddingScore();
					otherPlayerBid = biddingScore.getOtherPlayerBid();
					winCheck = biddingScore.isWin();
					playerWon = biddingScore.getPlayerWon();
					amountWon = biddingScore.getAmountWon();
					winString = "No one won the item!";
					if(winCheck)
					{
						winString = "You won " + amountWon + "!";
						p1ResultPanel.setTheme("incorrectbid-panel");
						p2ResultPanel.setTheme("correctbid-panel");
						lblBid2.setText("" + currentBid);
						lblBid.setText("" + otherPlayerBid);
						lblAmountWon2.setText("" + amountWon);
						lblAmountWon.setText("0");
						p1ResultPanel.reapplyTheme();
						p2ResultPanel.reapplyTheme();
					}
					else if(!winCheck && otherPlayerBid != 0)
					{
						winString = otherPlayer.getName() + " wins " + amountWon + "!";
						p1ResultPanel.setTheme("correctbid-panel");
						p2ResultPanel.setTheme("incorrectbid-panel");
						lblBid2.setText("" + currentBid);
						lblBid.setText("" + otherPlayerBid);
						lblAmountWon2.setText("0");
						lblAmountWon.setText("" + amountWon);
						p1ResultPanel.reapplyTheme();
						p2ResultPanel.reapplyTheme();

					}
					finished = true;
				}
			}
			else if(otherPlayerReady == 0)
			{
				lblConfirmation.setText("You bid " + currentBid + ".\nWaiting for " + otherPlayer.getName());
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
					if(winCheck)
					{
						if(playerID == 1)
							HRRUClient.cs.getP1().addScore(amountWon);
						else
							HRRUClient.cs.getP2().addScore(amountWon);
						biddingResult = new BiddingScore(item_id, itemValue, currentBid, otherPlayerBid, playerWon, amountWon, winCheck);	
					}
					else
					{
						if(playerID == 1)
							HRRUClient.cs.getP2().addScore(amountWon);
						else
							HRRUClient.cs.getP1().addScore(amountWon);
						biddingResult = new BiddingScore(item_id, itemValue, currentBid, otherPlayerBid, playerWon, 0, winCheck);
					}
					if(playerID == 1)
						HRRUClient.cs.getP1().addBiddingScore(biddingResult);
					else
						HRRUClient.cs.getP2().addBiddingScore(biddingResult);
			
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
		return 13;
	}

}