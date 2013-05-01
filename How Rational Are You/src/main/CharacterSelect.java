package main;

import conn.Packet.*;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import com.esotericsoftware.kryonet.Client;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Label;

public class CharacterSelect extends BasicTWLGameState {
	// initialise character select states
	private final int serverlost = -4;
	private final int cancelled = -2;
	private final int p1_charselect = 5;
	private final int p2_charselect = 6;
	private final int p1_turn = 7;
	// initiate the grid sheet for characters
	SpriteSheet gridSheet;
	public Client client;
	DialogLayout firstPanel;
	// fade transitions used
    FadeOutTransition fadeOutTransition;
    FadeInTransition fadeInTransition;
	// set up ticker variables
	boolean p1ShowRollBanner, p2ShowRollBanner;
	private int gameState;
	int clock, clock2;
	int player;
	boolean picking, selected, p1chosen, p2chosen;
	// set up coordinates for GUI
	int gcw;
	int gch;
	int startx = 25;
	int starty = 100;
	int currentx, selectx;
	int currenty, selecty;
	// check mouse variables
	boolean mouseDown;
	String mouse;
	// setup character GRID images
	private Image emptyGrid, hoverGrid, selectGrid, chosenGrid;
	private Image player_character_bg;
	private Character[] characters;
	private Character selectedCharacter;
	CharacterSheet characterSheet;
	// setup player variables
	int p1charid, p2charid;
	String p1name, p2name;
	Label lblYourTurn;
	Label lTitle, lStatus, lSelCharacter, lP1Name, lP2Name;
	Button btnSelect;
	
	public CharacterSelect(int main) {

	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		// set up player variables
		client = HRRUClient.conn.getClient();
		rootPane.removeAllChildren();
		player = HRRUClient.cs.getPlayer();
		p1name = HRRUClient.cs.getP1().getName();
		p2name = HRRUClient.cs.getP2().getName();
		// show initial gui
        btnSelect.setVisible(false);
		lSelCharacter.setVisible(false);
		// setup mouse
		mouseDown = false;
		mouse = "no input";
		clock = 300;
		clock2 = 0;
		// iniitiate mouse position to top left
		currentx = -1;
		selectx = -1;
		currenty = -1; 
		selecty = -1;
		// reset variables
		p1chosen = false;
		p2chosen = false;
		picking = false;
		selected = false;
		selectedCharacter = null;

		btnSelect.setTheme("importantbutton");
		// set up turn label
		lblYourTurn = new Label();
		lblYourTurn.setSize(800, 600);
		lblYourTurn.setTheme("labelyourturn");
		// check who this player is
		if(player == 1)
			lblYourTurn.setVisible(true);
		else
			lblYourTurn.setVisible(false);
		lblYourTurn.setPosition(0,0);
		// depending on the player, set first or second
		if(player == 1) {
			picking = true;
			p1ShowRollBanner = true;
			lStatus.setText(p1name + " you're first!");
		}
		else {
			player = 2;
			p2ShowRollBanner = true;
			lStatus.setText("Waiting for " + p1name + ".");
		}
		// initiate scores to this value
		HRRUClient.cs.getP1().setScore(1000);
		HRRUClient.cs.getP2().setScore(1000);
		// set up gui for player names
		lP1Name.setText(p1name);
		lP2Name.setText(p2name);
		
		rootPane.add(btnSelect);
		rootPane.add(lTitle);
		rootPane.add(lStatus);
		rootPane.add(lSelCharacter);
		rootPane.add(lP1Name);
		rootPane.add(lP2Name);
		rootPane.add(lblYourTurn);
		rootPane.setTheme("");		
	}

	public void chooseCharacter()
	{
		// if the player has chosen their character, send the information over to the other player
		if(player == 1) 
		{
			lStatus.setText("Waiting for " + p2name + "."); // reset status text
			picking = false;
			HRRUClient.cs.getP1().setPlayerCharacterID(selectedCharacter.getId());
			HRRUClient.cs.setState(p1_charselect);
			p1charid = HRRUClient.cs.getP1().getPlayerCharacterID();
			
			Packet9CharacterSelect characterRequest = new Packet9CharacterSelect();
			characterRequest.sessionID = HRRUClient.cs.getSessionID();
			characterRequest.player = player;
			characterRequest.characterID = HRRUClient.cs.getP1().getPlayerCharacterID();
			client.sendTCP(characterRequest);
			
			btnSelect.setTheme("choicebutton");
			btnSelect.reapplyTheme();
			btnSelect.setEnabled(false);
			p1chosen = true;
		}
		// if player 2, send over their variables to player 1 and initiate gui
		else if(player == 2)
		{
			picking = false;
			HRRUClient.cs.getP2().setPlayerCharacterID(selectedCharacter.getId());
			HRRUClient.cs.setState(p2_charselect);
			p2charid = HRRUClient.cs.getP2().getPlayerCharacterID();
			
			Packet9CharacterSelect characterRequest = new Packet9CharacterSelect();
			characterRequest.sessionID = HRRUClient.cs.getSessionID();
			characterRequest.player = player;
			characterRequest.characterID = HRRUClient.cs.getP2().getPlayerCharacterID();
			client.sendTCP(characterRequest);
			
			btnSelect.setTheme("choicebutton");
			btnSelect.reapplyTheme();
			btnSelect.setEnabled(false);
			p2chosen = true;
		}
	}
	// set up root pane
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
		// inititae GUI variables
		gcw = gc.getWidth();
		gch = gc.getHeight();
		fadeOutTransition = new FadeOutTransition();
		fadeInTransition  = new FadeInTransition();
		// initiate character GUI variables
		player_character_bg = new Image("simple/player_character_bg.png");
		
		gridSheet = new SpriteSheet("simple/character_select.png", 36,36);
		emptyGrid = gridSheet.getSprite(0,0);
		hoverGrid = gridSheet.getSprite(1,0);
		selectGrid = gridSheet.getSprite(2,0);
		chosenGrid = gridSheet.getSprite(3,0);
		
		characterSheet = new CharacterSheet();
		characters = characterSheet.getCharacters();
		// initiate overall GUI variables
		lTitle = new Label("Choose Your Character!");
		lStatus = new Label("");
		lSelCharacter = new Label("");
		
		lP1Name = new Label("");
		lP2Name= new Label("");
		// initiate select button GUI
		btnSelect = new Button("Click to Select Character");
		btnSelect.setTheme("importantbutton");
		btnSelect.setPosition(450, 280);
		btnSelect.setSize(300, 40);
        btnSelect.addCallback(new Runnable() {
            public void run() {
            	chooseCharacter();
            }
        });

		// initiate title gui
        lTitle.setPosition(30,40);
		lTitle.setTheme("atarititle");
		// initiate status gui
		lStatus.setPosition(0,70);
		lStatus.setTheme("atarisubtitle");
		lStatus.setSize(800, 100);
		// initiate character prompt label
		lSelCharacter.setPosition(540, 250);
		lSelCharacter.setTheme("atarisubtitle");
		// place player variables on screen
		lP1Name.setPosition(540, 385);
		lP2Name.setPosition(540, 455);
		lP1Name.setTheme("atarisubtitle");
		lP2Name.setTheme("atarisubtitle");
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// draw the character images on screen scaled by 2
		g.scale(2f, 2f);
		gridSheet.setFilter(Image.FILTER_NEAREST); // don't use antialisaing
		player_character_bg.setFilter(Image.FILTER_NEAREST);
		g.drawImage(player_character_bg, 225, 175);
		// draw a 5 by 5 grid for the images
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++)
				g.drawImage(emptyGrid, startx+i*37, starty+j*37);
		// if player is picking, show hover image over their mouse on character
		if(picking)
		{
			if(currentx>=0 && currenty>=0)
				g.drawImage(hoverGrid, startx+currentx*37, starty+currenty*37);
		}
		// if the player has clicked a character, show a clicked image on the character
		if(selected)
		{
			g.drawImage(selectGrid, startx+selectx*37, starty+selecty*37);
			g.drawImage(selectedCharacter.getCharacterImage(), 225, 100);
		}
		
		// draw the characters over each grid image 5 by 5  
		for(int i = 0; i < 5; i++)
			for(int j = 0; j < 5; j++)
				g.drawImage(characters[(i)+(j*5)].getCharacterImage(), startx+i*37, starty+j*37);
		
		// depending on whether the other player has chosen their character, show their chosen character
		if(p1chosen)
		{
			g.drawImage(chosenGrid, startx+(characters[p1charid].getPositionx()*37), starty+(characters[p1charid].getPositiony()*37));
			g.drawImage(characters[p1charid].getCharacterImage(), startx+characters[p1charid].getPositionx()*37, starty+characters[p1charid].getPositiony()*37);
			g.drawImage(characters[p1charid].getCharacterImage(), 225, 175);
		}
		if(p2chosen)
		{
			g.drawImage(chosenGrid, startx+(characters[p2charid].getPositionx()*37), starty+(characters[p2charid].getPositiony()*37));
			g.drawImage(characters[p2charid].getCharacterImage(), startx+characters[p2charid].getPositionx()*37, starty+characters[p2charid].getPositiony()*37);
			g.drawImage(characters[p2charid].getCharacterImage(), 225, 212);
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		Input input = gc.getInput();
		int xpos = Mouse.getX();
		int ypos= Mouse.getY();
		mouse = "xpos: " + xpos + "\nypos: " + ypos;
		gameState = HRRUClient.cs.getState();
		clock2 += delta;
		// has the server closed down? update states if so
		if(gameState == serverlost)
			sbg.enterState(0);
		// has the other player closed down? update states if so
		if(gameState == cancelled) {
			if(player == 1)
				sbg.enterState(1);
			else
				sbg.enterState(2);
		}
		// if player 1 is going first. let them choose their character
		if(player == 1) {
			if(p1ShowRollBanner)
			{
				lStatus.setTheme("statusgreen");
				lStatus.reapplyTheme();
				lblYourTurn.setVisible(true);
				if(input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
				{
					p1ShowRollBanner = false;
					lblYourTurn.setVisible(false);
				}
			}
			if(gameState == p2_charselect) {
				p2charid = HRRUClient.cs.getP2().getPlayerCharacterID();
				p2chosen = true;
			}
		}
		// if player 2 is next, let them choose their character
		else if(player == 2) {
			if(gameState == p1_charselect) {
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
				p1charid = HRRUClient.cs.getP1().getPlayerCharacterID();
				p1chosen = true;
				lStatus.setText(p2name + ", it's your turn!");
				picking = true;
			}
		}
		
		// ensure if player is picking, record their mouse position
		if(picking) 
		{
			lStatus.setTheme("statusgreen");
			lStatus.reapplyTheme();
			btnSelect.setEnabled(true);
			// make sure to only update if their mouse is over the character
			if((xpos>startx*2 && xpos < startx*2+(74*5)) && (ypos>35 && ypos<gch-starty*2)) {
				currentx = (xpos-startx*2)/74;
				currenty = ((gch-ypos)-starty*2)/74;
				if(input.isMouseButtonDown(0)) {
					selectx = currentx;
					selecty = currenty;
					selected = true;
					selectedCharacter = characters[(selectx)+(selecty*5)];
					lSelCharacter.setVisible(true);
					lSelCharacter.setText(selectedCharacter.getName());
					btnSelect.setVisible(true);
				}
			}
			else {
				currentx = -1;
				currenty = -1;
			}
			// if both players have chosen, disable the select button
			if(player == 2 && selected) {
				if(selectedCharacter.getId() == HRRUClient.cs.getP1().getPlayerCharacterID())
					btnSelect.setEnabled(false);
			}
		}
		// create flashing text for the label
		else
		{
			if((clock2 % 2000) < 1000)
			{
				lStatus.setTheme("statuswhite");
				lStatus.reapplyTheme();
			}
			else
			{
				lStatus.setTheme("statusred");
				lStatus.reapplyTheme();
			}
		}
		// if both players have chosen
		if(gameState == p2_charselect)
		{
			clock--;
			// count down from 3
			lStatus.setText("Game Starting in " + (clock/100+1) + "...");
			// start the game to next state
			if(clock<0)
			{
				HRRUClient.cs.setState(p1_turn);
				sbg.enterState(11, new FadeOutTransition(), new FadeInTransition());
			}
		}
	}

	@Override
	public int getID() {
		return 10;
	}

}