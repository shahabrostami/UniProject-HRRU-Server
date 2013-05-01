package conn;

import main.Player;
import main.Score;
// store the state of the game
public class ConnectionState {
	// initiate required variables for the game
	private final int initial = -1;
	private int timer = 480000;	// start the games timer at this many ms
	private int player;
	private Player p1;
	private Player p2;
	private int state = initial;
	private int gameState = 0; 
	private int activity;
	private int activity_id;
	private int secondary_id;
	private int secondary_value;
	private int third_value;
	private int sessionID;
	private String password;
	private int[] board;
	private boolean sync;
	private Score[] scores;
	public int no_of_questions; 
	
	
	public ConnectionState(){
	}
	// initialise the games connection state
	public void init(int sessionID, String password, int player){
		this.player = player;
		this.sessionID = sessionID;
		this.password = password;
		this.state = 0;
	}
	
	// get and set functions for the connection state
	public int getPlayer()
	{
		return player;
	}
	
	public void setPlayer(int player)
	{
		this.player = player;
	}
	
	public int getSessionID()
	{
		return sessionID;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setState(int state){
		this.state = state;
	}
	
	public int getState(){
		return state;
	}

	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}

	public Player getP2() {
		return p2;
	}

	public void setP2(Player p2) {
		this.p2 = p2;
	}

	public int[] getBoard(){
		return board;
	}
	
	public void setBoard(int[] board) {
		this.board = board;
	}

	public int getTimer() {
		return timer;
	}
	
	public void setTimer(int timer) {
		this.timer = timer;
	}
	
	public void updateTimer(int timer) {
		this.timer -= timer;
	}
	
	public int getActivity() {
		return this.activity;
	}
	
	public void setActivity(int activity) {
		this.activity = activity;
	}

	public int getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(int activity_id) {
		this.activity_id = activity_id;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public int getSecondary_id() {
		return secondary_id;
	}

	public void setSecondary_id(int secondary_id) {
		this.secondary_id = secondary_id;
	}

	public int getSecondary_value() {
		return secondary_value;
	}

	public void setSecondary_value(int secondary_value) {
		this.secondary_value = secondary_value;
	}

	public int getThird_value() {
		return third_value;
	}

	public void setThird_value(int third_value) {
		this.third_value = third_value;
	}

	public int getGameState() {
		return gameState;
	}

	public void setGameState(int gameState) {
		this.gameState = gameState;
	}

	public Score[] getScores() {
		return scores;
	}

	public void setScores(Score[] scores) {
		this.scores = scores;
	}

	public int getNo_of_questions(){
		return no_of_questions;
	}
	
	public void setNo_of_questions(int no_of_questions){
		this.no_of_questions = no_of_questions;
	}
}
