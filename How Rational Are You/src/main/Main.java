package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;
import org.newdawn.slick.state.transition.SelectTransition;
import com.esotericsoftware.kryonet.Client;

import conn.Connection;
import conn.Packet.Packet0CreateRequest;
import conn.Packet.Packet2JoinRequest;
import conn.Packet.Packet8Start;
import conn.Packet.Packet9CharacterSelect;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;

public class Main extends BasicTWLGameState {

	public Main(int main) {
		// TODO Auto-generated constructor stub
	}

	Client client;
	boolean read = false;
	
	// GUI variables
	ToggleButton btnFullscreen;
	boolean isFullscreen;
	int connectionSuccessful;
	int enterState = 0;
	int attempts = 0;
	Label lConnection;
	Button btnHost, btnJoin, btnTutorial, btnSummary, btnScoreboard, btnAbout, btnExit;
	Button btnHT1, btnHT2, btnHT3;
	Button btnTestStart;
	EditField efSessionID;
	EditField efIP;
	Button btnConnect;
	Button btnJT1, btnJT2;
	Button btnRetry;
	HorizontalSplitTransition horizontalSplitTransition;
    EmptyTransition emptyTransition;
    SelectTransition selectTransition;
    
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
	private Image summaryImg;
	
	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		attempts = 0;
		enterState = 0;
		rootPane.removeAllChildren();
		HRRUClient.cs.setTimer(480000);
		// btnRetry.setEnabled(false);
		btnHT2.setEnabled(false);
		btnHT3.setEnabled(false);
		btnJT2.setEnabled(false);
		btnTestStart.setEnabled(false);
		
		/*
		rootPane.add(btnHT1);
		rootPane.add(efSessionID);
		rootPane.add(btnJT1);
		rootPane.add(btnHT2);
		rootPane.add(btnJT2);
		rootPane.add(btnHT3);
		rootPane.add(btnTestStart);
		*/
		rootPane.add(btnJoin);
		rootPane.add(btnHost);
		rootPane.add(btnScoreboard);
		rootPane.add(lConnection);
		rootPane.add(efIP);
		rootPane.add(btnConnect);
		rootPane.add(btnTutorial);
		rootPane.add(btnFullscreen);
		rootPane.add(btnAbout);
		rootPane.add(btnRetry);
		rootPane.add(btnExit);
		if(read)
		{
			if(HRRUClient.ConnectionSuccessful == 1)
			{
				lConnection.setVisible(true);
				btnJoin.setVisible(true);
				btnHost.setVisible(true);
				btnScoreboard.setVisible(true);
				btnTutorial.setVisible(true);
				btnExit.setVisible(true);
				btnAbout.setVisible(true);
				btnRetry.setVisible(false);
				efIP.setVisible(false);
				btnConnect.setVisible(false);
			}
			else {
				btnRetry.setVisible(true);
				efIP.setVisible(true);
				btnConnect.setVisible(true);
				lConnection.setVisible(true);
				btnTutorial.setVisible(true);
				btnExit.setVisible(true);
				btnAbout.setVisible(true);
				btnJoin.setVisible(false);
				btnHost.setVisible(false);
				btnScoreboard.setVisible(false);
			}
		}
		rootPane.add(btnSummary);

		rootPane.setTheme("");
		
		// RESET VARIABLES
		start_message = "";
		full_start_message = "HOW RATIONAL ARE YOU?";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
	}

	@Override
	protected RootPane createRootPane() {
		assert rootPane == null : "RootPane already created";

		RootPane rp = new RootPane(this);
		rp.setTheme("");
		rp.getOrCreateActionMap().addMapping(this);
		return rp;
	}

	void emulateRetry() {
		HRRUClient.conn.reConnect();
		attempts++;
	}
	
	void emulateConnect() {
		HRRUClient.conn = new Connection(efIP.getText());
		client = HRRUClient.conn.getClient();
		if(HRRUClient.ConnectionSuccessful == 1)
		{
			efIP.setVisible(false);
			btnConnect.setVisible(false);
			btnJoin.setVisible(true);
			btnHost.setVisible(true);
			btnRetry.setVisible(false);
			btnScoreboard.setVisible(true);
		}
			
	}
	
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		// setup font variables
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
		
		lConnection = new Label("");
		lConnection.setPosition(gc.getWidth()/2-100, gc.getHeight()/2-125);
		horizontalSplitTransition = new HorizontalSplitTransition();
	    emptyTransition = new EmptyTransition();
	    selectTransition = new SelectTransition();
		
		btnRetry = new Button("Retry Server");
		
		btnRetry.setSize(400, 30);
		btnRetry.setPosition(gc.getWidth()/2-200, gc.getHeight()/2-25);
		btnRetry.addCallback(new Runnable() {
			@Override
			public void run() {
				emulateRetry();
			}
		});
		btnRetry.setTheme("menubutton");
		btnRetry.setVisible(false);
		
		btnConnect = new Button("Connect");
		btnConnect.setSize(400, 30);
		btnConnect.setPosition(gc.getWidth()/2-200, gc.getHeight()/2-60);
		btnConnect.addCallback(new Runnable() {
			@Override
			public void run() {
				emulateConnect();
				
			}
		});
		btnConnect.setTheme("menubutton");
		
		efIP = new EditField();
		efIP.setSize(200,30);
		efIP.setPosition(gc.getWidth()/2-100, gc.getHeight()/2-95);
		efIP.setText(HRRUClient.IP);
		
	    // setup fullscreen button
	    btnFullscreen = new ToggleButton("");
	    btnFullscreen.setSize(25, 25);
	    btnFullscreen.setPosition(775,0);
	    btnFullscreen.addCallback(new Runnable() {
			@Override
			public void run() {
					if(isFullscreen)
						isFullscreen = false;
					else
						isFullscreen = true;
			}
		});
	    btnFullscreen.setTheme("fullscreenbutton");
	    
		// setup gui variables
		btnHost = new Button("Host Game");
		btnHost.setSize(400, 30);
		btnHost.setPosition(gc.getWidth()/2-200, gc.getHeight()/2-95);
		btnHost.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 1;
				
			}
		});
		btnHost.setTheme("menubutton");
		btnJoin = new Button("Join Game");
		btnJoin.setSize(400, 30);
		btnJoin.setPosition(gc.getWidth()/2-200, gc.getHeight()/2-60);
		btnJoin.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 2;
			}
		});
		btnJoin.setTheme("menubutton");
		
		btnTutorial = new Button("Tutorial");
		btnTutorial.setSize(400, 30);
		btnTutorial.setPosition(gc.getWidth()/2-200, gc.getHeight()/2+20);
		btnTutorial.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 5;
			}
		});
		btnTutorial.setTheme("menubutton");
		
		btnScoreboard = new Button("Scoreboard");
		btnScoreboard.setSize(400, 30);
		btnScoreboard.setPosition(gc.getWidth()/2-200, gc.getHeight()/2+55);
		btnScoreboard.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 6;
			}
		});
		btnScoreboard.setTheme("menubutton");
		
		btnSummary = new Button("CONTINUE");
		btnSummary.setSize(400, 50);
		btnSummary.setPosition(gc.getWidth()/2-200, gc.getHeight()/2+250);
		btnSummary.addCallback(new Runnable() {
			@Override
			public void run() {
				btnSummary.setVisible(false);
				lConnection.setVisible(true);
				btnTutorial.setVisible(true);
				efIP.setVisible(true);
				btnConnect.setVisible(true);
				btnFullscreen.setVisible(true);
				btnAbout.setVisible(true);
				btnExit.setVisible(true);
				read = true;
			}
		});
		btnSummary.setTheme("menubutton");
		
		btnAbout = new Button("About");
		btnAbout.setSize(400, 30);
		btnAbout.setPosition(gc.getWidth()/2-200, gc.getHeight()/2+90);
		btnAbout.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 7;
			}
		});
		btnAbout.setTheme("menubutton");
		
		btnExit = new Button("Exit");
		btnExit.setSize(400, 30);
		btnExit.setPosition(gc.getWidth()/2-200, gc.getHeight()/2+125);
		btnExit.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 8;
			}
		});
		btnExit.setTheme("menubutton");
		
		
		btnHT1 = new Button("1) Host Server Create");
		btnHT1.setSize(200, 20);
		btnHT1.setPosition(gc.getWidth()/2-300, gc.getHeight()/2+15);
		btnHT1.addCallback(new Runnable() {
			@Override
			public void run() {
				Packet0CreateRequest createRequest = new Packet0CreateRequest();
				createRequest.password = "";
				createRequest.player1Name = "Alex";
				Player player1;
				try {
					player1 = new Player("Alex");
					HRRUClient.cs.setP1(player1);
				} catch (SlickException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.sendTCP(createRequest);
				btnHT2.setEnabled(true);
				btnHT1.setEnabled(false);
				btnJT1.setEnabled(false);
				btnJT2.setEnabled(false);
				enterState = 3;
			}
		});
		
		efSessionID = new EditField();
		efSessionID.setPosition(gc.getWidth()/2-250, gc.getHeight()/2+45);
		efSessionID.setSize(100,20);
		btnJT1 = new Button("2) Join Server Request");
		btnJT1.setSize(200, 20);
		btnJT1.setPosition(gc.getWidth()/2-300, gc.getHeight()/2+75);
		btnJT1.addCallback(new Runnable() {
			@Override
			public void run() {
				Packet2JoinRequest joinRequest = new Packet2JoinRequest();
				joinRequest.sessionID = Integer.parseInt(efSessionID.getText());
				joinRequest.password = "";
				joinRequest.player2Name = "Samantha";
				Player player2;
				try {
					player2 = new Player("Samantha");
					HRRUClient.cs.setP2(player2);
				} catch (SlickException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				btnJT2.setEnabled(true);
				btnJT1.setEnabled(false);
				btnHT1.setEnabled(false);
				btnHT2.setEnabled(false);
				btnHT3.setEnabled(false);
				client.sendTCP(joinRequest);
			}
		});
		
		btnHT2 = new Button("3) Host Server Start");
		btnHT2.setSize(200, 20);
		btnHT2.setPosition(gc.getWidth()/2-300, gc.getHeight()/2+105);
		btnHT2.addCallback(new Runnable() {
			@Override
			public void run() {
				Packet8Start startRequest = new Packet8Start();
				startRequest.sessionID = HRRUClient.cs.getSessionID();
				client.sendTCP(startRequest);
				btnHT2.setEnabled(false);
				btnHT3.setEnabled(true);
			}
		});
		
		btnHT3 = new Button("4) Host Server Character");
		btnHT3.setSize(200, 20);
		btnHT3.setPosition(gc.getWidth()/2-300, gc.getHeight()/2+135);
		btnHT3.addCallback(new Runnable() {
			@Override
			public void run() {
				Packet9CharacterSelect characterRequest = new Packet9CharacterSelect();
				characterRequest.sessionID = HRRUClient.cs.getSessionID();
				characterRequest.player = 1;
				characterRequest.characterID = 3;
				HRRUClient.cs.getP1().setPlayerCharacterID(3);
				client.sendTCP(characterRequest);
				btnHT3.setEnabled(false);
				btnTestStart.setEnabled(true);
			}
		});
		
		btnJT2 = new Button("5) Join Server Character");
		btnJT2.setSize(200, 20);
		btnJT2.setPosition(gc.getWidth()/2-300, gc.getHeight()/2+165);
		btnJT2.addCallback(new Runnable() {
			@Override
			public void run() {
				Packet9CharacterSelect characterRequest = new Packet9CharacterSelect();
				characterRequest.sessionID = HRRUClient.cs.getSessionID();
				characterRequest.player = 2;
				characterRequest.characterID = 4;
				HRRUClient.cs.getP2().setPlayerCharacterID(4);
				client.sendTCP(characterRequest);
				btnJT2.setEnabled(false);
				btnTestStart.setEnabled(true);
			}
		});
		
		btnTestStart = new Button("6) Start Test Game");
		btnTestStart.setSize(200, 20);
		btnTestStart.setPosition(gc.getWidth()/2-300, gc.getHeight()/2+195);
		btnTestStart.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 4;
			}
		});
		// default to false for GUI
		btnRetry.setVisible(false);
		efIP.setVisible(false);
		btnConnect.setVisible(false);
		lConnection.setVisible(false);
		btnTutorial.setVisible(false);
		btnExit.setVisible(false);
		btnAbout.setVisible(false);
		btnJoin.setVisible(false);
		btnHost.setVisible(false);
		btnScoreboard.setVisible(false);
		read = false;
		summaryImg = new Image("tutorial/summary2.png");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, 50, 50);
		if(!read)
			g.drawImage(summaryImg, 0, 0);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		if(read)
		{
		clock3 += delta;
		clock2 += delta;
		// full message ticker
		if(isFullscreen && !(gc.isFullscreen()))
			gc.setFullscreen(true);
		else if(!(isFullscreen) && gc.isFullscreen())
			gc.setFullscreen(false);
		
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
		// Is connection successful?
		connectionSuccessful = HRRUClient.ConnectionSuccessful;
		if(connectionSuccessful == 1)
		{
			btnJoin.setEnabled(true);
			btnHost.setEnabled(true);
			// btnRetry.setVisible(false);
			lConnection.setText("Connection Successful");
		}
		else if(connectionSuccessful == 0)
		{
			btnJoin.setEnabled(false);
			btnHost.setEnabled(false);
			btnRetry.setVisible(true);
			lConnection.setText("Connection Failed...\nPlease restart application.\nAttemtps: " + attempts);
		}
		else
		{
			lConnection.setText("Please enter IP address:");
		}
		if (enterState == 1)
		{
			sbg.enterState(1);
			enterState = 0;
		}
		else if (enterState == 2)
		{
			sbg.enterState(2);
			enterState = 0;
		}
		else if (enterState == 3)
		{
			efSessionID.setText("" + HRRUClient.cs.getSessionID());
		}
		else if(enterState == 4)
		{
			HRRUClient.cs.getP1().setScore(1000);
			HRRUClient.cs.getP2().setScore(1000);
			HRRUClient.cs.setState(7);
			sbg.enterState(11);
			enterState = 0;
		}
		else if(enterState == 5)
			sbg.enterState(3);
		else if(enterState == 6)
			sbg.enterState(4);
		else if(enterState == 7)
			sbg.enterState(5);
		else if(enterState == 8)
			gc.exit();
		}
	}

	@Override
	public int getID() {
		return 0;
	}

}