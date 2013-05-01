package main;

import java.util.ArrayList;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
// set up player variables
public class Player {
	// initial player variables for characters, names, scores and so on.
	SpriteSheet sheet;
	private String name;
	private int score;
	private int position;
	private int totalRolled;
	private int ready;
	private int playerCharacterID;
	private Character playerCharacter;
	CharacterSheet characterSheet = new CharacterSheet();
	Character[] characters = characterSheet.getCharacters();
	
	// set up question score, bidding score, trust score, ult score and prisoner score lists
	// this allows us to score every result of every game and question for each player
	private ActivityScore currentActivityScore;
	private ArrayList<ActivityScore> activityScores;
	private BiddingScore currentBiddingScore;
	private ArrayList<BiddingScore> biddingScores;
	private TrustScore currentTrustScore;
	private ArrayList<TrustScore> trustScores;
	private PrisonScore currentPrisonScore;
	private ArrayList<PrisonScore> prisonScores;
	private UltimatumScore currentUltimatumScore;
	private ArrayList<UltimatumScore> ultimatumScores;
	// this is the overall scores for each game and question accumlated statistics
	private BiddingScoreResult biddingScoreResult;
	private PrisonerScoreResult prisonerScoreResult;
	private TrustScoreResult trustScoreResult;
	private UltScoreResult ultScoreResult;
	private QuestionScoreResult questionScoreResult;
	
	public Player(String name) throws SlickException
	{
		// initialise all variables to 0 to ensure no null error exceptions from sync problems
		this.setName(name);
		this.setScore(0);
		this.setReady(0);
		this.setPosition(0);
		this.setTotalRolled(0);
		this.currentActivityScore = new ActivityScore(0,0,0,0,0, false);
		this.activityScores = new ArrayList<ActivityScore>();
		this.currentBiddingScore = new BiddingScore(0,0,0, false);
		this.biddingScores = new ArrayList<BiddingScore>();
		this.setCurrentTrustScore(new TrustScore(0,0,0,0,0,0));
		this.trustScores = new ArrayList<TrustScore>();
		this.setCurrentPrisonScore(new PrisonScore(0,0,0,0,false,false));
		this.prisonScores = new ArrayList<PrisonScore>();
		this.setCurrentUltimatumScore(new UltimatumScore(0,0,0,0,0, false));
		this.ultimatumScores = new ArrayList<UltimatumScore>();
		this.biddingScoreResult = new BiddingScoreResult(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
		this.prisonerScoreResult = new PrisonerScoreResult(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
		this.ultScoreResult = new UltScoreResult(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
	}

	// get and set functions for players
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getPosition() {
		return position;
	}
	
	public void updatePosition(){
		this.position++;
		this.totalRolled++;
		if(this.position == 32)
			this.position = 0;
	}

	public void setPosition(int position) {
		this.totalRolled += position;
		this.position += position;
	}

	public Character getPlayerCharacter() {
		return playerCharacter;
	}
	
	public void setPlayerCharacter(Character playerCharacter) {
		this.playerCharacter = playerCharacter;
	}
	
	public int getPlayerCharacterID() {
		return playerCharacterID;
	}
	
	public void setPlayerCharacterID(int playerCharacterID) {
		this.playerCharacterID = playerCharacterID;
		setPlayerCharacter(characters[playerCharacterID]);
	}
	
	public void addScore(int i) {
		this.score += i;
	}

	public int getReady() {
		return ready;
	}

	public void setReady(int ready) {
		this.ready = ready;
	}

	public ActivityScore getActivityScore(){
		return currentActivityScore;
	}
	
	public void setActivityScore(ActivityScore activityScore){
		this.currentActivityScore = activityScore;
	}

	public ArrayList<ActivityScore> getActivityScores() {
		return activityScores;
	}

	public void setActivityScores(ArrayList<ActivityScore> activityScores) {
		this.activityScores = activityScores;
	}
	
	public void addActivityScore(ActivityScore activityScore) {
		this.activityScores.add(activityScore);
	}

	public BiddingScore getCurrentBiddingScore() {
		return currentBiddingScore;
	}

	public void setCurrentBiddingScore(BiddingScore currentBiddingScore) {
		this.currentBiddingScore = currentBiddingScore;
	}

	public ArrayList<BiddingScore> getBiddingScores() {
		return biddingScores;
	}

	public void setBiddingScores(ArrayList<BiddingScore> biddingScores) {
		this.biddingScores = biddingScores;
	}

	public void addBiddingScore(BiddingScore biddingScore) {
		this.biddingScores.add(biddingScore);
	}

	public ArrayList<TrustScore> getTrustScores() {
		return trustScores;
	}

	public void setTrustScores(ArrayList<TrustScore> trustScores) {
		this.trustScores = trustScores;
	}

	public void addTrustScore(TrustScore trustScore) {
		this.trustScores.add(trustScore);
	}

	public TrustScore getCurrentTrustScore() {
		return currentTrustScore;
	}

	public void setCurrentTrustScore(TrustScore currentTrustScore) {
		this.currentTrustScore = currentTrustScore;
	}

	public PrisonScore getCurrentPrisonScore() {
		return currentPrisonScore;
	}

	public void setCurrentPrisonScore(PrisonScore currentPrisonScore) {
		this.currentPrisonScore = currentPrisonScore;
	}

	public ArrayList<PrisonScore> getPrisonScores() {
		return prisonScores;
	}

	public void setPrisonScores(ArrayList<PrisonScore> prisonScores) {
		this.prisonScores = prisonScores;
	}
	
	public void addPrisonScore(PrisonScore prisonScore) {
		this.prisonScores.add(prisonScore);
	}

	public UltimatumScore getCurrentUltimatumScore() {
		return currentUltimatumScore;
	}

	public void setCurrentUltimatumScore(UltimatumScore currentUltimatumScore) {
		this.currentUltimatumScore = currentUltimatumScore;
	}

	public ArrayList<UltimatumScore> getUltimatumScores() {
		return ultimatumScores;
	}

	public void setUltimatumScores(ArrayList<UltimatumScore> ultimatumScores) {
		this.ultimatumScores = ultimatumScores;
	}
	
	public void addUltimatumScore(UltimatumScore ultimatumScore) {
		this.ultimatumScores.add(ultimatumScore);
	}

	public BiddingScoreResult getBiddingScoreResult() {
		return biddingScoreResult;
	}

	public void setBiddingScoreResult(BiddingScoreResult biddingScoreResult) {
		this.biddingScoreResult = biddingScoreResult;
	}

	public PrisonerScoreResult getPrisonerScoreResult() {
		return prisonerScoreResult;
	}

	public void setPrisonerScoreResult(PrisonerScoreResult prisonerScoreResult) {
		this.prisonerScoreResult = prisonerScoreResult;
	}

	public TrustScoreResult getTrustScoreResult() {
		return trustScoreResult;
	}

	public void setTrustScoreResult(TrustScoreResult trustScoreResult) {
		this.trustScoreResult = trustScoreResult;
	}

	public UltScoreResult getUltScoreResult() {
		return ultScoreResult;
	}

	public void setUltScoreResult(UltScoreResult ultScoreResult) {
		this.ultScoreResult = ultScoreResult;
	}
	
	public QuestionScoreResult getQuestionScoreResult() {
		return questionScoreResult;
	}

	public void setQuestionScoreResult(QuestionScoreResult questionScoreResult) {
		this.questionScoreResult = questionScoreResult;
	}

	public int getTotalRolled() {
		return totalRolled;
	}

	public void setTotalRolled(int totalRolled) {
		this.totalRolled = totalRolled;
	}
	

}
