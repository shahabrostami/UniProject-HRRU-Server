package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import conn.Packet.*;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import com.esotericsoftware.kryonet.Client;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.ResizableFrame.ResizableAxis;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;
import main.Chat.ChatFrame;
import main.board.*;

public class Play extends BasicTWLGameState {
	// set up client variables
	public Client client;
	DialogLayout firstPanel;
	
	Input input;
	int gcw;
	int gch;
	// set up the main board variables
	private Board board;
	private Dice dice;
	private QuestionList question_list;
	private int playerID;
	private int sessionID;
	private boolean currentPlayer;
	private Player player;
	private Player player1;
	private Player player2;
	private boolean p1ShowRollBanner;
	private boolean p2ShowRollBanner;
	private boolean moving;
	private int glowTimer;
	Animation glowAnimation;
	// set up state variables
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int p1_turn = 7;
	private final int p2_turn = 8;
	private final int start_play = 9;
	private final int play = 10;
	private final int play_question = 12;
	private final int play_bidgame = 13;
    private final int play_trustgame = 14;
    private final int play_prisongame = 15;
    private final int play_ultgame = 16;
	private final int verdict = 17;
	
	// states: 0 = idle, 1 = rolling, 2 = navigate board
	private int state, gameState;
	private int dice_counter, dice_counter_copy = 0;
	private int clock, timer;
	
	String mouse = "no input";
	// set up font variables
	private int mainFontSize = 24;
	private int timerFontSize = 30;
	private Font loadFont, loadMainFont, loadTimerFont;
	private BasicFont mainFont, timerFont;
	// set up GUI variables
	DialogLayout playerPanel;
	Label lStatus, lPlayer1, lPlayer2, lPlayer1Score, lPlayer2Score;
	Label player1turn, lblYourTurn, lblYourWait;
	Label lImgPlayer1, lImgPlayer2; 
	ToggleButton btnRoll;
	DialogLayout rollPanel, backgroundLayout;
	BasicFont header; 
	DialogLayout backgroundChoices;
	Image background0, background1, background2, background3, background4, background5;
	ToggleButton tBackground0, tBackground1, tBackground2, tBackground3, tBackground4, tBackground5;
	// chat frame variables
	public static ChatFrame chatFrame;
	
	Image scorebackground, background, yourTurnBG, yourWaitBG;
	// packet variables
	Packet00SyncMessage syncMessage;
	Packet11TurnMessage turnMessage;
	Packet12PlayReady readyMessage;
	
	public Play(int main) {
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		client = HRRUClient.conn.getClient();
		rootPane.removeAllChildren();
		state = 0;
		clock = 0;
		moving = false;
		glowTimer = 0;
		timer = HRRUClient.cs.getTimer();
		
		sessionID = HRRUClient.cs.getSessionID();
		playerID = HRRUClient.cs.getPlayer();
		player1 = HRRUClient.cs.getP1();
		player2 = HRRUClient.cs.getP2();
				
		syncMessage = new Packet00SyncMessage(); 
		syncMessage.sessionID = sessionID;
		syncMessage.player = playerID;
		
		board = new Board(9);
		System.out.println("Time After: " + timer);
		
		if(playerID == 1)
		{
			btnRoll.setVisible(true);
			currentPlayer = true;
			player = HRRUClient.cs.getP1();
			p1ShowRollBanner = true;
			// p1ShowWaitBanner = false;
			/*
			ActivityScore list;
			if(!(player.getActivityScores().isEmpty()))
			{
				for(int i = 0; i < player.getActivityScores().size(); i++)
				{
					list = player.getActivityScores().get(i);
					System.out.println(list.getActivity());
					System.out.println(list.getActivity_id());
					System.out.println(list.getCorrect());
					System.out.println(list.getDifficulty());
					System.out.println(list.getElapsedtime());
					System.out.println(list.getOverall());
					System.out.println(list.getPoints());
					System.out.println(list.getChoice());
				}
			}
			
			/*
			BiddingScore list;
			if(!(player.getBiddingScores().isEmpty()))
			{
				for(int i = 0; i < player.getBiddingScores().size(); i++)
				{
					list = player.getBiddingScores().get(i);
					System.out.println(list.getAmountWon());
				}
			}
			
			 */
		}
		else {
			p2ShowRollBanner = true;
			// p2ShowWaitBanner = true;
			btnRoll.setVisible(true);
			currentPlayer = false;
			player = HRRUClient.cs.getP2();
		}
		btnRoll.setActive(false);
		
		if(playerID == 1)
		{
			lStatus.setTheme("statusgreen");
			lblYourTurn.setVisible(true);
			// lblYourWait.setVisible(false);
		}
		else
		{
			lStatus.setTheme("statuswhite");
			lblYourTurn.setVisible(false);
			// lblYourWait.setVisible(true);
		}

		lStatus.reapplyTheme();
		lblYourTurn.setPosition(0,0);
		
		chatFrame = new ChatFrame();
        chatFrame.setSize(296, 200);
        chatFrame.setDraggable(false);
        chatFrame.setResizableAxis(ResizableAxis.NONE);
        chatFrame.requestKeyboardFocus();
		
        if(playerID == 1)
        	lStatus.setText(player1.getName() + ", it's your turn!");
        else
        	lStatus.setText("Waiting for " + player1.getName() + "...");
		
		rootPane.add(rollPanel);
		rootPane.add(lStatus);
		rootPane.add(chatFrame);
		rootPane.add(lblYourTurn);
		rootPane.add(backgroundLayout);
		// rootPane.add(lblYourWait);
		resetPosition();
		
		turnMessage = new Packet11TurnMessage();
		readyMessage = new Packet12PlayReady();
		readyMessage.sessionID = HRRUClient.cs.getSessionID();
		readyMessage.player = playerID;
	}
	
	void resetPosition() {
        chatFrame.setPosition(0, 397);
	}

	@Override
	protected RootPane createRootPane() {
		assert rootPane == null : "RootPane already created";

		RootPane rp = new RootPane(this);
		rp.setTheme("");
		rp.getOrCreateActionMap().addMapping(this);
		return rp;
	}

	void emulateRoll() {
		state = 1;
	}
	
	void setBackgroundsTogglesFalse(){
	    tBackground0.setActive(false);	
	    tBackground1.setActive(false);	
	    tBackground2.setActive(false);	
	    tBackground3.setActive(false);	
	    tBackground4.setActive(false);	
	    tBackground5.setActive(false);	
	}
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		// Game 
		/*
		Player player1 = new Player("player1");
		Player player2 = new Player("player2");
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
		Image[] glow = {new Image("simple/glowing0.png"), new Image("simple/glowing1.png"), new Image("simple/glowing2.png"),  
				new Image("simple/glowing3.png"), new Image("simple/glowing4.png"), new Image("simple/glowing5.png")};
		int[] duration = {100,100,100,100,100,100};
		glowAnimation = new Animation(glow,duration,true);
		moving = false;
		gcw = gc.getWidth();
		gch = gc.getHeight();
		scorebackground = new Image("simple/playerscorebackground.png");
		background = new Image("simple/background.png");
		background0 = new Image("simple/background.png");
		background1 = new Image("simple/backgroundblue2.png");
		background2 = new Image("simple/backgroundgreen.png");
		background3 = new Image("simple/backgroundgreen2.png");
		background4 = new Image("simple/backgroundgreen3.png");
		background5 = new Image("simple/backgroundblue.png");

		yourTurnBG = new Image("simple/yourturn.png");
		yourWaitBG = new Image("simple/yourwait.png");
		
		header = new BasicFont("Atari Font Full Version", Font.PLAIN, 12);
		// Create custom font for question
		try {
			loadFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
			        org.newdawn.slick.util.ResourceLoader.getResourceAsStream("font/visitor2.ttf"));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
				}
		
		loadMainFont = loadFont.deriveFont(Font.BOLD,mainFontSize);
		loadTimerFont = loadFont.deriveFont(Font.BOLD,timerFontSize);
		mainFont = new BasicFont(loadMainFont);
		timerFont = new BasicFont(loadTimerFont);
		try {
			question_list = new QuestionList("Question.txt");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lblYourTurn = new Label();
		lblYourTurn.setSize(800, 600);
		lblYourTurn.setTheme("labelyourturn");
		lblYourWait = new Label();
		lblYourWait.setSize(800, 600);
		lblYourWait.setTheme("labelyourwait");
		
		dice = new Dice(1);
		
		rollPanel = new DialogLayout();
		lStatus = new Label("");
		btnRoll = new ToggleButton("ROLL");
		btnRoll.addCallback(new Runnable() {
            public void run() {
                emulateRoll();
            }
        });
		
        DialogLayout.Group hBtnRoll = rollPanel.createSequentialGroup(btnRoll);
         
        
        rollPanel.setHorizontalGroup(rollPanel.createParallelGroup()
                .addGroup(hBtnRoll));
        
        rollPanel.setVerticalGroup(rollPanel.createSequentialGroup()
        		.addGap(180).addWidget(btnRoll));
		
        // Background layout
        backgroundLayout = new DialogLayout();
        backgroundLayout.setSize(400,80);
        backgroundLayout.setPosition(325,500);
        tBackground0 = new ToggleButton("0");
        tBackground1 = new ToggleButton("1");
        tBackground2 = new ToggleButton("2");
        tBackground3 = new ToggleButton("3");
        tBackground4 = new ToggleButton("4");
        tBackground5 = new ToggleButton("5");
        tBackground0.setActive(true);
        tBackground0.setSize(10, 10);
        tBackground1.setSize(10, 10);
        tBackground2.setSize(10, 10);
        tBackground3.setSize(10, 10);
        tBackground4.setSize(10, 10);
        tBackground5.setSize(10, 10);
        
        tBackground0.addCallback(new Runnable() {
            public void run() {
                background = background0;
                setBackgroundsTogglesFalse();
                tBackground0.setActive(true);
            }
        });
        tBackground1.addCallback(new Runnable() {
            public void run() {
                background = background1;
                setBackgroundsTogglesFalse();
                tBackground1.setActive(true);
            }
        });
        tBackground2.addCallback(new Runnable() {
            public void run() {
                background = background2;
                setBackgroundsTogglesFalse();
                tBackground2.setActive(true);
            }
        });
        tBackground3.addCallback(new Runnable() {
            public void run() {
                background = background3;
                setBackgroundsTogglesFalse();
                tBackground3.setActive(true);
            }
        });
        tBackground4.addCallback(new Runnable() {
            public void run() {
                background = background4;
                setBackgroundsTogglesFalse();
                tBackground4.setActive(true);
            }
        });
        tBackground5.addCallback(new Runnable() {
            public void run() {
                background = background5;
                setBackgroundsTogglesFalse();
                tBackground5.setActive(true);
            }
        });
        
        Label lblBackground = new Label("Background: ");
        DialogLayout.Group hTB = backgroundLayout.createSequentialGroup(lblBackground, tBackground0, tBackground1,tBackground2,tBackground3,tBackground4,tBackground5);
        
        backgroundLayout.setHorizontalGroup(backgroundLayout.createSequentialGroup().addGroup(hTB));
        
        backgroundLayout.setVerticalGroup(backgroundLayout.createSequentialGroup()
				.addGap(60).addGroup(backgroundLayout.createParallelGroup(lblBackground, tBackground0, tBackground1,tBackground2,tBackground3,tBackground4,tBackground5)));
        
        // set up states
		sbg.addState(new PlayQuestion(play_question, question_list));
		sbg.addState(new PlayGame_Bid(play_bidgame));
		sbg.addState(new PlayGame_Trust(play_trustgame));
		sbg.addState(new PlayGame_Prisoners(play_prisongame));
		sbg.addState(new PlayGame_Ultimatum(play_ultgame));
		
		sbg.getState(play_question).init(gc, sbg);
		sbg.getState(play_bidgame).init(gc, sbg);
		sbg.getState(play_trustgame).init(gc, sbg);
		sbg.getState(play_prisongame).init(gc, sbg);
		sbg.getState(play_ultgame).init(gc, sbg);
		
			
		lStatus.setPosition(305, 10);
        lStatus.setTheme("statuswhite");
        lStatus.setSize(495, 107);
        
		rollPanel.setTheme("roll-panel");
		rollPanel.setPosition(0,105);
		rollPanel.setSize(304, 270);
		btnRoll.setTheme("importantrollbutton");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawImage(background, 0,0);		
		g.drawImage(scorebackground, 0,0);
		g.setFont(mainFont.get());
		g.drawImage(player1.getPlayerCharacter().getCharacterImage(), 13,13);
		g.drawImage(player2.getPlayerCharacter().getCharacterImage(), 13,55);
		g.drawString("" + player1.getName(), 65, 22);
		g.drawString("" + player2.getName(), 65, 64);
		g.drawString("" + player1.getScore(), 204, 22);
		g.drawString("" + player2.getScore(), 204, 64);
		timerFont.get();
		g.drawString("TIME: " + ((timer/1000)+1), 650, 560);
		
		g.scale(1.25f, 1.25f);
		for(int i = 0; i < board.getScale()*3-3; i++)
			g.drawImage(board.gridSquares[i].gridSquare.getImage(), board.gridSquares[i].getx(), board.gridSquares[i].gety());
		if(state>0 && state <= 3)
		{
			dice.dice.draw(105, 97+dice.getY());
		}
		if(moving)
			glowAnimation.draw(board.gridSquares[player.getPosition()].getx()-56, board.gridSquares[player.getPosition()].gety()-54);
		for(int j = board.getSize()-1; j >= board.getScale()*3-3; j--)
			g.drawImage(board.gridSquares[j].gridSquare.getImage(), board.gridSquares[j].getx(), board.gridSquares[j].gety());
		g.drawImage(player1.getPlayerCharacter().getCharacterImage(), board.gridSquares[player1.getPosition()].getx(), board.gridSquares[player1.getPosition()].gety());
		g.drawImage(player2.getPlayerCharacter().getCharacterImage(), board.gridSquares[player2.getPosition()].getx(), board.gridSquares[player2.getPosition()].gety());
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		input = gc.getInput();
		Mouse.getX();
		Mouse.getY();
		gameState = HRRUClient.cs.getState();
		timer -= delta;
		if(gameState == serverlost)
			sbg.enterState(0);
		
		if(gameState == cancelled) {
			if(playerID == 1)
				sbg.enterState(1);
			else sbg.enterState(2);
		}

		else 
		
		if(timer <0)
		{
			Packet24SendScore sendScore = new Packet24SendScore();
			sendScore.name = player.getName();
			sendScore.score = player.getScore();
			sendScore.sessionID = sessionID;
			sendScore.player = playerID;
			client.sendTCP(sendScore);
			sbg.addState(new Verdict(verdict));
			sbg.getState(verdict).init(gc, sbg);
			sbg.enterState(verdict, new FadeOutTransition(), new FadeInTransition());
		}
		
		if(state == 0)
		{
			if(playerID == 1)
			{
				if(gameState == p1_turn)
				{
					// if p1 turn, show turn banner
					if(p1ShowRollBanner)
					{
						if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
						{
							p1ShowRollBanner = false;
							lblYourTurn.setVisible(false);
						}
					}
					lStatus.setText(player1.getName() + ", it's your turn!");
					currentPlayer = true;
					btnRoll.setText("CLICK TO ROLL");
					btnRoll.setVisible(true);
				}
				else
				{
					lStatus.setText("It's " + player2.getName() + "'s turn.");
					currentPlayer = false;
					btnRoll.setText("Waiting for " + player2.getName() + "...");
					btnRoll.setVisible(false);
				}
			}
			else if(playerID == 2)
			{
				if(gameState == p2_turn)
				{
					// if p2 turn, show turn banner
					lStatus.setTheme("statusgreen");
					lStatus.reapplyTheme();
					if(p2ShowRollBanner)
					{
						lblYourTurn.setVisible(true);
						if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
						{
							p2ShowRollBanner = false;
							lblYourTurn.setVisible(false);
						}
					}
					btnRoll.setText("CLICK TO ROLL");
					lStatus.setText(player2.getName() + ", it's your turn!");
					currentPlayer = true;
					btnRoll.setVisible(true);
				}
				else
				{
					// WAIT BANNER if p2 waitng, show wait banner
					/*
					if(p2ShowWaitBanner)
					{
						lblYourWait.setVisible(true);
						if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
						{
							p2ShowWaitBanner = false;
							lblYourWait.setVisible(false);
						}
					}*/
					if((timer % 2000) < 1000)
					{
						lStatus.setTheme("statuswhite");
						lStatus.reapplyTheme();
					}
					else
					{
						lStatus.setTheme("statusred");
						lStatus.reapplyTheme();
					}
					lStatus.setText("It's " + player1.getName() + "'s turn.");
					currentPlayer = false;
					btnRoll.setText("WAITING FOR " + player1.getName());
					btnRoll.setVisible(false);
				}
			}
		}
		
		if(currentPlayer)
		{
			//roll dice
			moving = true;
			lblYourWait.setVisible(false);
			if(state==1)
			{
				clock += delta;
				if(clock>=60) // should be 60
				{
					dice.rollDice();
					clock-=60; // should be 60
					if(dice.getPosition()==0)
					{
						dice_counter = dice.getCurrentNumber();
						dice_counter_copy = dice.getCurrentNumber();
						state = 2;
						btnRoll.setVisible(false);
					}
				}
			}
			
			//navigating board
			if(state == 2)
			{
				clock += delta;
				if(clock>=200) // should be 200
				{
					player.updatePosition();
					clock-=200;
					dice_counter--;
					if(dice_counter==0)
					{
						dice.reset();
						state = 3;
					}
				}
			}
			if(state == 3)
			{
				turnMessage.sessionID = HRRUClient.cs.getSessionID();
				turnMessage.playerID = playerID;
				turnMessage.moves = dice_counter_copy;
				turnMessage.tile = board.gridSquares[player.getPosition()].getTileType();
				client.sendTCP(turnMessage);
				
				if(playerID == 1)
				{
					HRRUClient.cs.setState(p2_turn);
					lStatus.setText("Waiting for " + player2.getName() + "...");
					btnRoll.setText("Waiting for " + player2.getName() + "...");
				}
				else
				{
					lStatus.setText("Waiting for " + player1.getName() + "...");
					btnRoll.setText("Waiting for " + player1.getName() + "...");
				}
				btnRoll.setVisible(false);
				state = 4;
				clock = 200; // should be 200
			}
		}
	
		if(state == 4)
		{
			glowTimer+=delta;
			// WAIT BANNER if p1 is waiting
			/*
			if(p1ShowWaitBanner && playerID == 1)
			{
				rootPane.removeChild(lblYourWait);
				rootPane.add(lblYourWait);
				lblYourWait.setVisible(true);
				if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
				{
					p1ShowWaitBanner = false;
					lblYourWait.setVisible(false);
				}
			}
			*/
			if(glowTimer>1000)
				moving = false;
			if((timer % 2000) < 1000)
			{
				lStatus.setTheme("statuswhite");
				lStatus.reapplyTheme();
			}
			else
			{
				lStatus.setTheme("statusred");
				lStatus.reapplyTheme();
			}
			if(gameState == start_play)
			{
				// lblYourWait.setVisible(false);
				lStatus.setTheme("statusgreen");
				lStatus.reapplyTheme();
				clock--;
				lStatus.setText("Starting in " + (clock/100+1) + "...");
				if(clock<=0)
				{
					clock=0;
					client.sendTCP(readyMessage);
					state = 5;
				}
			}
		}
		else if(state == 5)
		{
			state = 6;
		}
		
		if(state == 6)
		{
			if(gameState == play)
			{
				int currentTile = board.gridSquares[player.getPosition()].getTileType();
				int otherPlayerTile;
				HRRUClient.cs.setTimer(timer);
				if(HRRUClient.cs.getPlayer() == 1)
					otherPlayerTile = board.gridSquares[HRRUClient.cs.getP2().getPosition()].getTileType();
				else
					otherPlayerTile = board.gridSquares[HRRUClient.cs.getP1().getPosition()].getTileType();
					
				int activity_id = HRRUClient.cs.getActivity_id();
				
				if(otherPlayerTile == 3 || currentTile == 3)
				{
					if(activity_id == 1)
						sbg.enterState(play_bidgame, new FadeOutTransition(), new EmptyTransition());
					if(activity_id == 2)
						sbg.enterState(play_trustgame, new FadeOutTransition(), new EmptyTransition());
					if(activity_id == 3)
						sbg.enterState(play_prisongame, new FadeOutTransition(), new EmptyTransition());
					if(activity_id == 4)
						sbg.enterState(play_ultgame, new FadeOutTransition(), new EmptyTransition());
				}
				else
					sbg.enterState(play_question, new FadeOutTransition(), new EmptyTransition());
			}
		}
	}

	@Override
	public int getID() {
		return 11;
	}

}