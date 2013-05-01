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

public class HostServer extends BasicTWLGameState {

	Input input;
	
	// static variables
	public static boolean p1ready = false;
	public static boolean p2ready = false;
	
	// states
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int waiting = 0; 
	private final int established = 2;
	private final int ready = 3;
	private final int start = 4;
	
	// Ticker variables
	private int titleFontSize = 60;
	private Font loadFont, loadTitleFont;
	private BasicFont titleFont;
	private String start_message = "";
	private String full_start_message = "HOSTING A GAME...?";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2, clock = 0;
	
	// state variables
	private int state;
	private boolean back = false;
	public Client client;
	private String password;
	private String p1name;
	private Player player1;

	int gcw;
	int gch;
	
	DialogLayout hostPanel;
    EditField efName;
    EditField efPassword;
    Label lName, lPassword, lStatus, lPlayer1, lPlayer2;
    Button btnJoin, btnBack, btnCancel, btnReady, btnStart;
    
	private Packet0CreateRequest createRequest;
	private Packet7Ready readyRequest;
	
	public HostServer(int hostserver) {
	}
	
	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		// RESET VARIABLES
		client = HRRUClient.conn.getClient();
		start_message = "";
		full_start_message = "HOSTING A GAME...?";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		clock = 300;
        rootPane.removeAllChildren();
        hostPanel.setTheme("login-panel");
        rootPane.isFocusKeyEnabled();
        efName.setSize(100,20);
        lName.setLabelFor(efName);

        lPassword.setLabelFor(efPassword);
        disableAllGUI();
        enableGUI();
        btnStart.setVisible(false);
        btnReady.setVisible(false);
        btnCancel.setEnabled(false);

	    hostPanel.setIncludeInvisibleWidgets(false);
	    rootPane.add(hostPanel);
		rootPane.setTheme("");		
		resetPosition();
	}
	
	void enableGUI() {
		// enable the initial GUI 
		lName.setVisible(true);
	    lPassword.setVisible(true);
	    efName.setVisible(true);
	    efPassword.setVisible(true);
	    btnJoin.setVisible(true);
	    btnBack.setVisible(true);
	    
	    lPlayer1.setVisible(false);
	    lPlayer2.setVisible(false);
	    btnReady.setVisible(false);
	    btnStart.setVisible(false);
	    p1ready = false;
	    p2ready = false;
	    lPlayer1.setText(null);
	    lPlayer2.setText(null);
	    
	    resetPosition();
	}
	
	void disableAllGUI() {
		// disable ALL GUI
		lName.setVisible(false);
	    lPassword.setVisible(false);
	    efName.setVisible(false);
	    efPassword.setVisible(false);
	    btnJoin.setVisible(false);
	    btnBack.setVisible(false);
	    lPlayer1.setVisible(false);
	    lPlayer2.setVisible(false);
	    btnReady.setVisible(false);
	    btnStart.setVisible(false);
		btnCancel.setEnabled(false);
		
		resetPosition();
	}
	
	void disableGUI() {
		// disbable the main GUI
	    lName.setVisible(false);
	    lPassword.setVisible(false);
	    efName.setVisible(false);
	    efPassword.setVisible(false);
	    btnJoin.setVisible(false);
	    btnBack.setVisible(false);

	    btnCancel.setEnabled(true);
	    
	    resetPosition();
	}
	
	void emulateStart() {
		// send the start packet to other player
		Packet8Start startRequest = new Packet8Start();
		startRequest.sessionID = HRRUClient.cs.getSessionID();
		client.sendTCP(startRequest);
		HRRUClient.cs.setState(start);
	}
	
	void emulateReady() {
		// send the ready packet to the other player
		// this lets the other player know that this player is ready
		btnReady.setVisible(false);
		HRRUClient.cs.setState(ready);
		readyRequest = new Packet7Ready();
		readyRequest.sessionID = HRRUClient.cs.getSessionID();
		readyRequest.player = 1;
		client.sendTCP(readyRequest);
		p1ready = true;
		lPlayer1.setText("Player 1: \t" + player1.getName() + " is ready!");
		resetPosition();
	}
	
	void emulateCancel() {
		// send the cancel packet, letting the other know that the game has been cancelled
		Packet5CancelRequest cancelRequest = new Packet5CancelRequest();
	    cancelRequest.sessionID = HRRUClient.cs.getSessionID();
	    HRRUClient.cs.setState(cancelled);
	    client.sendTCP(cancelRequest);
	    // set up a notification
	    lStatus.setText("Enter your name and a password for your game.");
	    enableGUI();
	}
	
	void emulateLogin() {
		// send the login packet, letting the server know that you have created a game
		// if no name entered, prompt for a name
		if(efName.getText().isEmpty())
			lStatus.setText("Please enter a name.");
		else
		{
			disableGUI();
			p1name = efName.getText();
			password = efPassword.getText();
			createRequest = new Packet0CreateRequest();
			createRequest.password = password;
			createRequest.player1Name = p1name;
			client.sendTCP(createRequest);
		}

	}
	
	void resetPosition() {
		// reset GUI position
		hostPanel.adjustSize();
		hostPanel.setPosition(
	               (gcw/2 - hostPanel.getWidth()/2),
	                (gch/2 - hostPanel.getHeight()/2));
	}

	@Override
	protected RootPane createRootPane() {
		assert rootPane == null : "RootPane already created";

		RootPane rp = new RootPane(this);
		rp.setFocusKeyEnabled(true);
		rp.setTheme("");
		rp.getOrCreateActionMap().addMapping(this);
		return rp;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		// game properties
		gcw = gc.getWidth();
		gch = gc.getHeight();
		gc.setShowFPS(false);
		
		// font properties
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
		
		// GUI properties
		lStatus = new Label("Enter your name and a password for your game.");
		lPlayer1 = new Label();
		lPlayer2 = new Label();
		
        hostPanel = new DialogLayout();
        efName = new EditField();
        efPassword = new EditField();
        lName = new Label("Your Name");
        lPassword = new Label("Password");
        btnStart = new Button("Start");
        btnReady = new Button("Ready");
        btnJoin = new Button("Host");
        btnBack = new Button("Back");
        btnCancel= new Button("Cancel");
        
        efName.setMaxTextLength(8);
        efPassword.setMaxTextLength(8);
        
        efName.addCallback(new Callback() {
            public void callback(int key) {
            	if(key == Event.KEY_RETURN) {
                    efPassword.requestKeyboardFocus();
                }
            }
        });
        efPassword.addCallback(new Callback() {
        	public void callback(int key) {
                if(key == Event.KEY_RETURN ) {
                    emulateLogin();
                }
            }
        });
        btnStart.addCallback(new Runnable() {
            public void run() {
                emulateStart();
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
        
        btnStart.setTheme("menubutton");
        btnReady.setTheme("menubutton");
        btnJoin.setTheme("menubutton");
        btnBack.setTheme("menubutton");
        btnCancel.setTheme("menubutton");

	    hostPanel.setIncludeInvisibleWidgets(false);
        DialogLayout.Group hLabels = hostPanel.createParallelGroup(lName, lPassword);
        DialogLayout.Group hFields = hostPanel.createParallelGroup(efName, efPassword);
        DialogLayout.Group hBtnJoin = hostPanel.createSequentialGroup().addWidget(btnJoin);
        DialogLayout.Group hBtnBack = hostPanel.createSequentialGroup().addWidget(btnBack);
        DialogLayout.Group hBtnCancel = hostPanel.createSequentialGroup().addWidget(btnCancel);
        DialogLayout.Group hBtnReady = hostPanel.createSequentialGroup().addWidget(btnReady);
        DialogLayout.Group hBtnStart = hostPanel.createSequentialGroup().addWidget(btnStart);
        
        DialogLayout.Group hStatus = hostPanel.createSequentialGroup().addWidget(lStatus);
        DialogLayout.Group hPlayer1Ready = hostPanel.createSequentialGroup().addWidget(lPlayer1);
        DialogLayout.Group hPlayer2Ready = hostPanel.createSequentialGroup().addWidget(lPlayer2);
        
        hostPanel.setHorizontalGroup(hostPanel.createParallelGroup()
        		.addGroup(hStatus)
        		.addGroup(hPlayer1Ready)
        		.addGroup(hPlayer2Ready)
                .addGroup(hostPanel.createSequentialGroup(hLabels, hFields))
                .addGroup(hBtnJoin)
                .addGroup(hBtnBack)
                .addGroup(hBtnReady)
                .addGroup(hBtnStart)
                .addGroup(hBtnCancel));
        
        hostPanel.setVerticalGroup(hostPanel.createSequentialGroup()
        		.addWidget(lStatus)
        		.addWidget(lPlayer1)
        		.addWidget(lPlayer2).addGap(10)
        		.addGroup(hostPanel.createParallelGroup(lName, efName))
                .addGroup(hostPanel.createParallelGroup(lPassword, efPassword))
                .addWidget(btnJoin)
        		.addWidget(btnBack)
        		.addWidget(btnReady)
        		.addWidget(btnStart)
        		.addWidget(btnCancel));
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// draw the game graphics
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, 50, 50);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		// Back to main menu
		clock3 += delta;
		clock2 += delta;
		input = gc.getInput();
		
		// check whether player has pressed tab, tab into relevant GUI if so
		if(input.isKeyPressed(Input.KEY_TAB))
		{
			if(efName.hasKeyboardFocus())
				efPassword.requestKeyboardFocus();
			else if(efPassword.hasKeyboardFocus())
				efName.requestKeyboardFocus();
		}
		
		// reset the variabels if player has clicked back into main menu
		if(back) {
			lStatus.setText("Enter your name and a password for your game.");
			sbg.enterState(0);
			back = false;
		}
		state = HRRUClient.cs.getState();
		
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
			lStatus.setText("Session cancelled.\nEnter your name and a password for your game.");
			enableGUI();
		}
		// Waiting for player 2.
		else if(state == waiting) {
			lStatus.setText("Hosting Game...\n" +
					"\nSession ID: \t" + HRRUClient.cs.getSessionID() +
					"\nPassword: \t" + HRRUClient.cs.getPassword() + 
					"\n\nWaiting for Player 2.");
			player1 = new Player(p1name);
			HRRUClient.cs.setP1(player1);
			disableGUI();
		}
		// Connection established with player 2.
		else if(state == established)
		{
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
		// if both playres are ready, give start prompt
		else if(state == ready)
		{
			if((p1ready == true) && (p2ready == true))
			{
				btnStart.setVisible(true);
				resetPosition();
				state = start;
			}
		}
		// if player has started, initiate next game state
		else if(state == start)
		{
			HRRUClient.cs.setP1(player1);
			clock--;
			disableAllGUI();
			lStatus.setText("Game Starting in " + (clock/100+1) + "...");
			if(clock<0)
			{
				sbg.enterState(10, new FadeOutTransition(), new FadeInTransition());
			}
		}
		if(p2ready == true)
		{
			lPlayer2.setText("Player 2: \t" + HRRUClient.cs.getP2().getName() + " is ready!");
			resetPosition();
		}
	}

	public int getID() {
		return 1;
	}
}