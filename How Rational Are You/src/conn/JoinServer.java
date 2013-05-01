package conn;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import conn.Packet.*;
import main.BasicFont;
import main.HRRUClient;
import main.Player;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import com.esotericsoftware.kryonet.Client;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.*;
import de.matthiasmann.twl.EditField.Callback;

public class JoinServer extends BasicTWLGameState {
	Input input;
	public static boolean p1ready = false;
	public static boolean p2ready = false;
	
	// state variables
	private final int serverlost = -4;
	private final int failed = -3;
	private final int cancelled = -2;
	private final int initial = -1;
	private final int joined = 1;
	private final int ready = 3;
	private final int start = 4;
	private int attempts = 0;
	int clock;
	
	// Ticker variables
	private int titleFontSize = 60;
	private Font loadFont, loadTitleFont;
	private BasicFont titleFont;
	private String start_message = "";
	private String full_start_message = "HOW RATIONAL ARE YOU?";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2 = 0;
	
	// state variables
	public String mouse = "No input yet!";
	private int state;
	private boolean back = false;
	public Client client;
	private int joinSessionID;
	private String joinPassword;
	private String p2name;
	private Player player2;

	int gcw;
	int gch;
	
	// GUI Variables
	DialogLayout joinPanel;
    EditField efName;
    EditField efSessionID;
    EditField efPassword;
    Button btnJoin, btnBack, btnCancel, btnReady;
    Label lName, lSessionID, lPassword, lStatus, lPlayer1, lPlayer2;
    
    
	private Packet2JoinRequest joinRequest;
	private Packet7Ready readyRequest;
	
	public JoinServer(int joinserver) {
		
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		client = HRRUClient.conn.getClient();
		// RESET VARIABLES
		start_message = "";
		full_start_message = "JOINING A GAME...?";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		
		// Set up GUI
		rootPane.removeAllChildren();
		clock = 300;
		
        joinPanel.setTheme("login-panel");
        lName.setLabelFor(efName);
        lName.setLabelFor(efSessionID);
        lPassword.setLabelFor(efPassword);
        disableAllGUI();
        enableGUI();
        btnReady.setVisible(false);
        btnCancel.setEnabled(false);

        rootPane.add(joinPanel);
		rootPane.setTheme("");
		
		joinPanel.adjustSize();
		joinPanel.setPosition(
               (gcw/2 - joinPanel.getWidth()/2),
                (gch/2 - joinPanel.getHeight()/2));
	
	}
	
	void enableGUI() {
		// enable GUI for the menu
		lName.setVisible(true);
	    lSessionID.setVisible(true);
	    lPassword.setVisible(true);
	    efName.setVisible(true);
	    efSessionID.setVisible(true);
	    efPassword.setVisible(true);
	    btnJoin.setVisible(true);
	    btnBack.setVisible(true);
	    
	    lPlayer1.setVisible(false);
	    lPlayer2.setVisible(false);
	    btnReady.setVisible(false);
	    p1ready = false;
	    p2ready = false;
	    lPlayer1.setText(null);
	    lPlayer2.setText(null);
	    
	    resetPosition();
	}
	
	void disableAllGUI() {
		// disable all gui objects
		lName.setVisible(false);
	    lSessionID.setVisible(false);
	    lPassword.setVisible(false);
	    efName.setVisible(false);
	    efSessionID.setVisible(false);
	    efPassword.setVisible(false);
	    btnJoin.setVisible(false);
	    btnBack.setVisible(false);
	    lPlayer1.setVisible(false);
	    lPlayer2.setVisible(false);
	    btnReady.setVisible(false);
		btnCancel.setEnabled(false);
		
		resetPosition();
	}

	void disableGUI() {
		// disable only main gui objects
	    lName.setVisible(false);
	    lSessionID.setVisible(false);
	    lPassword.setVisible(false);
	    efName.setVisible(false);
	    efSessionID.setVisible(false);
	    efPassword.setVisible(false);
	    btnJoin.setVisible(false);
	    btnBack.setVisible(false);

	    btnCancel.setEnabled(true);
	    
	    resetPosition();
	}
	
	void emulateReady() {
		// let other player know that this player is ready
		btnReady.setVisible(false);
		HRRUClient.cs.setState(ready);
		readyRequest = new Packet7Ready();
		readyRequest.sessionID = HRRUClient.cs.getSessionID();
		readyRequest.player = 2;
		client.sendTCP(readyRequest);
		resetPosition();
	}
	
	void emulateCancel() {
		// let the other player know that the game has been cancelled
		Packet5CancelRequest cancelRequest = new Packet5CancelRequest();
	    cancelRequest.sessionID = HRRUClient.cs.getSessionID();
	    HRRUClient.cs.setState(cancelled);
	    client.sendTCP(cancelRequest);
	    // update the prompts
	    lStatus.setText("Enter your name and game details.");
	    enableGUI();
	}
	
	void emulateLogin() {
		// if the player has joined, let other player know that the game has started
		try {
			// make sure a name ahs been entered
			if(efName.getText().isEmpty())
				lStatus.setText("Please enter a name.");
			else
			{
				// reset prompts for next game state
				disableGUI();
				String.valueOf(joinSessionID);
				joinSessionID = Integer.parseInt(efSessionID.getText());
				joinPassword = efPassword.getText();
				p2name = efName.getText();
				attempts++;
				joinRequest = new Packet2JoinRequest();
				joinRequest.sessionID = joinSessionID;
				joinRequest.password = joinPassword;
				joinRequest.player2Name = p2name;
				client.sendTCP(joinRequest);
			}
	
		 } catch (NumberFormatException e) {
			 // make sure a numerical sessionID has been entered
			 	HRRUClient.cs.setState(initial);
		        lStatus.setText("Please enter numbers only for the Session ID.");
		        enableGUI();
		        resetPosition();
		 }
	}
	
	void resetPosition() {
		// reset GUI positions
		joinPanel.adjustSize();
		joinPanel.setPosition(
	               (gcw/2 - joinPanel.getWidth()/2),
	                (gch/2 - joinPanel.getHeight()/2));
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
		gc.setShowFPS(false);
	    
	    //setup font variables
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
		
		//setup gui variables
		lStatus = new Label("Enter your name and game details.");
		lPlayer1 = new Label();
		lPlayer2 = new Label();
        joinPanel = new DialogLayout();
        efName = new EditField();
        efSessionID = new EditField();
        efPassword = new EditField();
        lName = new Label("Your Name");
        lSessionID = new Label("Session ID");
        lPassword = new Label("Password");
        btnReady = new Button("Ready");
        btnJoin = new Button("Join");
        btnBack = new Button("Back");
        btnCancel= new Button("Cancel");
        
        btnReady.setTheme("menubutton");
        btnJoin.setTheme("menubutton");
        btnBack.setTheme("menubutton");
        btnCancel.setTheme("menubutton");
        
        efName.addCallback(new Callback() {
            public void callback(int key) {
                if(key == Event.KEY_RETURN || key == Event.KEY_TAB) {
                    efSessionID.requestKeyboardFocus();
                }
            }
        });
        efSessionID.addCallback(new Callback() {
            public void callback(int key) {
            	if(key == Event.KEY_RETURN || key == Event.KEY_TAB) {
                    efPassword.requestKeyboardFocus();
                }
            }
        });
        efPassword.addCallback(new Callback() {
            public void callback(int key) {
                if(key == Event.KEY_RETURN) {
                    emulateLogin();
                }
                else if(key == Event.KEY_TAB) {
                	btnReady.requestKeyboardFocus();
                }
            }
        });
        btnReady.addCallback(new Runnable() {
            public void run() {
                emulateReady();
            }
        });
        btnJoin.addCallback(new Runnable() {
            public void run() {
                emulateLogin();
            }
        });
        btnBack.addCallback(new Runnable() {
            public void run() {
                back = true;
            }
        });
        btnCancel.addCallback(new Runnable() {
            public void run() {
                emulateCancel();
            }
        });

        efName.setMaxTextLength(8);
        efSessionID.setMaxTextLength(5);
        efPassword.setMaxTextLength(8);
        
        //setup gui layouts
        DialogLayout.Group hLabels = joinPanel.createParallelGroup(lName, lSessionID, lPassword);
        DialogLayout.Group hFields = joinPanel.createParallelGroup(efName, efSessionID, efPassword);
        DialogLayout.Group hBtn = joinPanel.createSequentialGroup().addWidget(btnJoin);
        DialogLayout.Group hBtn2 = joinPanel.createSequentialGroup().addWidget(btnBack);
        DialogLayout.Group hBtnCancel = joinPanel.createSequentialGroup().addWidget(btnCancel);
        DialogLayout.Group hBtnReady = joinPanel.createSequentialGroup().addWidget(btnReady);
        DialogLayout.Group hStatus = joinPanel.createSequentialGroup().addWidget(lStatus);
        DialogLayout.Group hPlayer1Ready = joinPanel.createSequentialGroup().addWidget(lPlayer1);
        DialogLayout.Group hPlayer2Ready = joinPanel.createSequentialGroup().addWidget(lPlayer2);
        joinPanel.setIncludeInvisibleWidgets(false);
        
        joinPanel.setHorizontalGroup(joinPanel.createParallelGroup()
        		.addGroup(hStatus)
        		.addGroup(hPlayer1Ready)
        		.addGroup(hPlayer2Ready)
        		.addGroup(hBtnReady)
                .addGroup(joinPanel.createSequentialGroup(hLabels, hFields))
                .addGroup(hBtn)
                .addGroup(hBtn2)
                .addGroup(hBtnCancel));
        
        joinPanel.setVerticalGroup(joinPanel.createSequentialGroup()
        		.addWidget(lStatus)
        		.addWidget(lPlayer1)
        		.addWidget(lPlayer2).addGap(10)
                .addGroup(joinPanel.createParallelGroup(lName, efName))
                .addGroup(joinPanel.createParallelGroup(lSessionID, efSessionID))
                .addGroup(joinPanel.createParallelGroup(lPassword, efPassword))
                .addWidget(btnJoin)
        		.addWidget(btnBack)
        		.addWidget(btnReady)
        		.addWidget(btnCancel));
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// draw main graphics for title
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, 50, 50);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		input = gc.getInput();
		// if tab is pressed, tab into relevant GUI
		if(input.isKeyPressed(Input.KEY_TAB))
		{
			if (efName.hasKeyboardFocus())
				efSessionID.requestKeyboardFocus();
			else if(efSessionID.hasKeyboardFocus())
				efPassword.requestKeyboardFocus();
			else if(efPassword.hasKeyboardFocus())
				efName.requestKeyboardFocus();
		}
		// if back pressed, reset variables and go backt o main menu
		if(back)
		{
			lStatus.setText("Enter your name and game details.");
			sbg.enterState(0);
			back = false;
		}
		
		state = HRRUClient.cs.getState();
		
		
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
		
		// Connection to server lost.
		if(state == serverlost)
			sbg.enterState(0);
		
		// Connection cancelled.
		if(state == cancelled) {
			lStatus.setText("Session cancelled.\nEnter your name and game details.");
			enableGUI();
		}
		// Connection failed.
		else if(state == failed)
		{
			lStatus.setText("Connection failed.\nSession ID or Password incorrect.\nAttempts: " + attempts + "\n\nEnter your name and game details.");
			enableGUI();
		}
		// Connected to player1.
		else if(state == joined)
		{
			player2 = new Player(p2name);
			HRRUClient.cs.setP2(player2);
			lStatus.setText("Connection Established!\n" +
					"\nSession ID: \t" + HRRUClient.cs.getSessionID() +
					"\n\nReady?");
			lPlayer1.setText("Player 1: \t" + HRRUClient.cs.getP1().getName());
			lPlayer2.setText("Player 2 : \t" + HRRUClient.cs.getP2().getName());
		    lPlayer1.setVisible(true);
		    lPlayer2.setVisible(true);
			btnReady.setVisible(true);
			disableGUI();
		}
		// Player 2 is ready
		else if(state == ready)
		{
			lPlayer2.setText("Player 2: \t" + HRRUClient.cs.getP2().getName() + " is ready!");
			resetPosition();
		}
		// if game started, go into next game state
		else if(state == start)
		{
			HRRUClient.cs.setP2(player2);
			clock--;
			disableAllGUI();
			lStatus.setText("Game Starting in " + (clock/100+1) + "...");
			if(clock<0)
			{
				sbg.enterState(10, new FadeOutTransition(), new FadeInTransition());
			}
		}
		// Player 1 is ready
		if(p1ready == true)
		{
			lPlayer1.setText("Player 1: \t" + HRRUClient.cs.getP1().getName() + " is ready!");
			resetPosition();
		}
		
	}

	public int getID() {
		return 2;
	}

}