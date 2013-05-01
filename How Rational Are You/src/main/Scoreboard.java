package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.Button;

public class Scoreboard extends BasicTWLGameState {

	// set up initial vairables for client and GUI
	public Client client;
	private int enterState;
	Button btnBack;
	Image scorebg, questionbg;
	
	int gcw;
	int gch;
	
	// Ticker variables and font variables
	private int mainFontSize = 14;
	private int titleFontSize = 20;
	// font variables
	private Font loadFont, loadMainFont, loadTitleFont;
	private BasicFont mainFont, titleFont;
	// ticker variables
	private String start_message = "";
	private String full_start_message = "TOP 10 SCOREBOARD...";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2 = 0;
	// the scors and player score
	private Score[] scores;
	private int playerScore;
	private int playerID;
	private String playerName;
	
	public Scoreboard(int main) {
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
		
		// SETUP GUI
		rootPane.removeAllChildren();
		rootPane.add(btnBack);
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
		scorebg = new Image("simple/scorebg.png");
		questionbg = new Image("simple/questionbg.png");
		
		// set up font variables
		try {
			loadFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
					      org.newdawn.slick.util.ResourceLoader.getResourceAsStream("font/atari.ttf"));
		} catch (FontFormatException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
		loadTitleFont = loadFont.deriveFont(Font.BOLD,titleFontSize);
		titleFont = new BasicFont(loadTitleFont);
		
		loadMainFont = loadFont.deriveFont(Font.BOLD,mainFontSize);
		mainFont = new BasicFont(loadMainFont, Color.white);
		
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
		
		// set player variable
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			playerScore = HRRUClient.cs.getP1().getScore();
			playerName = HRRUClient.cs.getP1().getName();
		}
		else
		{
			playerScore = HRRUClient.cs.getP2().getScore();
			playerName = HRRUClient.cs.getP2().getName();
		}
		scores = HRRUClient.cs.getScores();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// darw the scoreboard
		g.drawImage(questionbg, 0, 0);
		g.drawImage(scorebg, 0, 0);
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, 50, 25);
		g.setFont(mainFont.get());
		for(int i = 0; i < scores.length; i++)
		{
			// if the score is belonging to the current player, highlight it
			if(scores[i].getName().equals(playerName) && scores[i].getScore() == playerScore)
			{
				g.drawString(">> " + (i+1), 95, (i*40)+145);
				g.drawString("" + scores[i].getName(), 220, (i*40)+145);
				g.drawString(scores[i].getScore() + " <<", 510, (i*40)+145);
			}
			else
			{
				g.drawString("" + (i+1), 100, (i*40)+145);
				g.drawString("" + scores[i].getName(), 220, (i*40)+145);
				g.drawString(scores[i].getScore() + "", 510, (i*40)+145);
			}

		}
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
	}

	@Override
	public int getID() {
		return 25;
	}

}