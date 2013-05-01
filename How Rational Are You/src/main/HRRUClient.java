package main;

import java.net.URL;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import main.Main;
import conn.*;

import TWLSlick.TWLStateBasedGame;
// set up overall game variables
public class HRRUClient extends TWLStateBasedGame {
	// set up connection variables
	public static ConnectionState cs;
	public static Connection conn;
	public static int ConnectionSuccessful = -1;
	public static String IP = "2.27.19.165";
	// set up game variables
	public static final int main = 0;
	public static final int host = 1;
	public static final int join = 2;
	public static final int tutorial = 3;
	public static final int scoreboardmenu = 4;
	public static final int about = 5;
	public static final int characterselect  = 10;
	public static final int play = 11;
	
	
	public static final int resX = 800;
	public static final int resY = 600;
	
 public HRRUClient() {
	 	// initialise states
		super("How Rational Are You");
		cs = new ConnectionState();
		this.addState(new Main(main));
		this.addState(new HostServer(host));
		this.addState(new JoinServer(join));
		this.addState(new Tutorial(tutorial));
		this.addState(new ScoreboardMenu(scoreboardmenu));
		this.addState(new About(about));
		this.addState(new CharacterSelect(characterselect));
		this.addState(new Play(play));
		this.enterState(main);
	}

	public static void main(String[] args) {
		try {
			// set up container variables
			AppGameContainer app = new AppGameContainer(new HRRUClient());
			app.setTargetFrameRate(60);
			app.setMaximumLogicUpdateInterval(16);
			app.setMinimumLogicUpdateInterval(16);
			app.setDisplayMode(800, 600, false);
			app.setUpdateOnlyWhenVisible(false);
			app.setAlwaysRender(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	@Override
	public URL getThemeURL() {
		String fileName = "/simple/simple2.xml";
		System.out.println("Loading file: " + fileName);
		return HRRUClient.class.getResource(fileName);
	}

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
	}
} 