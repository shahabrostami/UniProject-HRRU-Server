package main;

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
import de.matthiasmann.twl.ToggleButton;

public class Tutorial extends BasicTWLGameState {
	// set up state variables and GUI variables
	public Client client;
	private int enterState;
	Button btnBack, btnNext, btnPrevious;
	Image questionbg;
	Image tutorials[];
	Image currentTutorial;
	int tutorial_counter;
	
	int gcw;
	int gch;
	
	// Ticker variables and font variables
	private int titleFontSize = 20;
	private Font loadFont, loadTitleFont;
	private BasicFont titleFont;
	
	private String start_message = "";
	private String full_start_message = "Game Tutorial...";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2 = 0;
	
	public Tutorial(int main) {
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		// RESET VARIABLES
		start_message = "";
		full_start_message = " Game Tutorial...";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		enterState = 0;
		btnPrevious.setVisible(false);
		btnNext.setVisible(true);
		tutorial_counter = 0;
		
		// SETUP GUI
		rootPane.removeAllChildren();
		rootPane.add(btnBack);
		rootPane.add(btnNext);
		rootPane.add(btnPrevious);
		rootPane.setTheme("");
	}
	
	void emulateNext()
	{
		// go to next tutorial image. 
		// if at the end, disable next button
		if(tutorial_counter < (tutorials.length-1))
		{
			tutorial_counter++;
			btnPrevious.setVisible(true);
			currentTutorial = tutorials[tutorial_counter];
		}
		if(tutorial_counter == (tutorials.length-1))
			btnNext.setVisible(false);
	}
	
	void emulatePrevious()
	{
		// go to previous tutorial image
		// if at start, enable next button
		if(tutorial_counter > 0)
		{
			tutorial_counter--;
			btnNext.setVisible(true);
			currentTutorial = tutorials[tutorial_counter];
		}
		if(tutorial_counter == 0)
			btnPrevious.setVisible(false);
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
		tutorials = new Image[12];
		tutorials[0] = new Image("tutorial/summary.png");
		tutorials[1] = new Image("tutorial/char_tutorial_1.png");
		tutorials[2] = new Image("tutorial/char_tutorial_2.png");
		tutorials[3] = new Image("tutorial/game_tutorial_1.png");
		tutorials[4] = new Image("tutorial/game_tutorial_2.png");
		tutorials[5] = new Image("tutorial/question_tutorial_1.png");
		tutorials[6] = new Image("tutorial/bid_tutorial_1.png");
		tutorials[7] = new Image("tutorial/coop_tutorial_1.png");
		tutorials[8] = new Image("tutorial/trust_tutorial_1.png");
		tutorials[9] = new Image("tutorial/trust_tutorial_2.png");
		tutorials[10] = new Image("tutorial/ult_tutorial_1.png");
		tutorials[11] = new Image("tutorial/ult_tutorial_2.png");
		questionbg = new Image("simple/questionbg.png");
		
		currentTutorial = tutorials[0];
		
		// btnnext
		btnNext = new Button("Next");
		btnNext.setSize(340, 30);
		btnNext.setPosition(410,530);
		btnNext.addCallback(new Runnable() {
			@Override
			public void run() {
				emulateNext();
			}
		});
		btnNext.setTheme("menubutton");
		// btn feedback
		btnPrevious = new ToggleButton("Previous");
		btnPrevious.setSize(340, 30);
		btnPrevious.setPosition(50,530);
		btnPrevious.addCallback(new Runnable() {
			@Override
			public void run() {
				emulatePrevious();
			}
		});
		btnPrevious.setTheme("menubutton");
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
		
		//btn back
		btnBack = new Button("Back");
		btnBack.setSize(700, 30);
		btnBack.setPosition(50,565);
		btnBack.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 1;
			}
		});
		btnBack.setTheme("menubutton");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// draw current tutorial and overall images
		g.drawImage(questionbg, 0, 0);
		g.drawImage(currentTutorial, 50, 50);
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
			sbg.enterState(0);
	}

	@Override
	public int getID() {
		return 3;
	}

}