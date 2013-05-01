package conn;

import org.newdawn.slick.SlickException;

import conn.Packet.Packet19TrustSecond;
import conn.Packet.*;
import main.ActivityScore;
import main.BiddingScore;
import main.HRRUClient;
import main.Play;
import main.Player;
import main.Score;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
// 
public class NetworkListener extends Listener{
	// set state IDs for the game
	private final int serverlost = -4;
	private final int failed = -3;
	private final int cancelled = -2;
	private final int waiting = 0; 
	private final int joined = 1;
	private final int start = 4;
	private final int player1char = 5;
	private final int player2char = 6;
	private final int p2_turn = 8;
	private final int start_play = 9;
	private final int play = 10;
	
	public void init(Client client) {
	}

	public void connected(Connection c) {
		// player has connected
		Log.info("[SERVER] Someone has connected.");
	}

	public void disconnected(Connection c) {
		// player has disconnected
		Client client = HRRUClient.conn.getClient();
		Log.info("[SERVER] Connection lost!");
		HRRUClient.ConnectionSuccessful = 0;
		client.stop();
		HRRUClient.cs.setState(serverlost);
	}
	
	public void received(Connection c, Object o){
		// sync players to server
		if(o instanceof Packet00SyncMessage)
		{
			HRRUClient.cs.setSync(true);
		}
		// server has established connection
		if(o instanceof Packet1CreateAnswer)
		{
			// if connection successful, update player, if not, try again
			boolean answer = ((Packet1CreateAnswer)o).accepted;
			if(answer)
			{
				int sessionID = ((Packet1CreateAnswer)o).sessionID;
				String password = ((Packet1CreateAnswer)o).password;	
				HRRUClient.cs.init(sessionID, password, 1);
				HRRUClient.cs.setState(waiting);
				Log.info("Connected to session " + sessionID + " with password " + password + " as player 1");
			}
		}
		// if a player has joined successfully, update game state
		if(o instanceof Packet3JoinAnswer)
		{
			boolean answer = ((Packet3JoinAnswer)o).accepted;
			// if received, let other player know that the player has joined
			if(answer)
			{
				int sessionID = ((Packet3JoinAnswer)o).sessionID;
				String password = ((Packet3JoinAnswer)o).password;	
				HRRUClient.cs.init(sessionID, password, 2);
				Player player1;
				// return the player 1 object for player 2 to store
				try {
					player1 = new Player(((Packet3JoinAnswer)o).player1Name);
					HRRUClient.cs.setP1(player1);
				} catch (SlickException e) {
					e.printStackTrace();
				}
				HRRUClient.cs.setState(joined);
				Log.info("Connected to previous session " + sessionID + " with password " + password + " as player 2");
			}
			// if connection failed, update user
			else
			{
				Log.info("Failed to connect.");
				HRRUClient.cs.setState(failed);
			}

		}
		// if both players have joined successfully, update both players
		if(o instanceof Packet4ConnectionEstablished)
		{
			String player2Name = ((Packet4ConnectionEstablished)o).player2Name;
			Log.info("Connection established with " + player2Name);
			Player player2;
			// return the player 2 objects for player 1 to store
			try {
				player2 = new Player(player2Name);
				HRRUClient.cs.setP2(player2);
			} catch (SlickException e) {
				e.printStackTrace();
			}
			HRRUClient.cs.setState(2);
		}
		// if cancelled, update player on cancelled game
		if(o instanceof Packet6CancelRequestResponse)
		{
			Log.info("Cancelled");
			HRRUClient.cs.setState(cancelled);
		}
		// if both players are ready, update states
		if(o instanceof Packet7Ready)
		{
			int player = ((Packet7Ready)o).player;
			if(player == 1)
			{
				HostServer.p1ready = true;
				JoinServer.p1ready = true;
			}
			else if(player == 2)
			{
				HostServer.p2ready = true;
				JoinServer.p2ready = true;
			}
		}
		// if the game has started from server, update states
		if(o instanceof Packet8Start)
		{
			HRRUClient.cs.setBoard(((Packet.Packet8Start)o).board);
			HRRUClient.cs.setState(start);
		}
		// if a character has been selected for the other player
		// update the screen
		if(o instanceof Packet9CharacterSelect)
		{
			int characterID = ((Packet.Packet9CharacterSelect)o).characterID;
			int player = ((Packet.Packet9CharacterSelect)o).player;
			// return player1s character
			if(player == 1)
			{
				HRRUClient.cs.getP1().setPlayerCharacterID(characterID);
				HRRUClient.cs.setState(player1char);
			}
			// return player2s character
			if(player == 2)
			{
				HRRUClient.cs.getP2().setPlayerCharacterID(characterID);
				HRRUClient.cs.setState(player2char);
			}
		}
		// store chat message from other player
		if(o instanceof Packet10ChatMessage)
		{
			Play.chatFrame.appendRowOther("color1", ((Packet10ChatMessage)o).message);
		}
		// update turn from other player and store their new position
		if(o instanceof Packet11TurnMessage)
		{
			int player = ((Packet11TurnMessage)o).playerID;
			int moves = ((Packet11TurnMessage)o).moves;
			// store for player 1
			if(player == 1)
			{
				for(int i = 0; i < moves; i++)
					HRRUClient.cs.getP1().updatePosition();
				HRRUClient.cs.setState(p2_turn);
			}
			// store for player 2
			else if(player == 2)
			{
				for(int i = 0; i < moves; i++)
					HRRUClient.cs.getP2().updatePosition();
				HRRUClient.cs.setState(start_play);
			}
			
		}
		// if an activity is about to start, store required variables
		if(o instanceof Packet13Play)
		{
			HRRUClient.cs.getP1().setReady(0);
			HRRUClient.cs.getP2().setReady(0);
			// return variables for activity
			int activity = ((Packet13Play)o).activity;
			int activity_id = ((Packet13Play)o).activity_id;
			HRRUClient.cs.setActivity(activity);
			HRRUClient.cs.setActivity_id(activity_id);
			// if activity is a bid game, store required variables
			if(activity == 3 && activity_id == 1)
			{
				HRRUClient.cs.setSecondary_id(((Packet13Play)o).secondary_id);
				HRRUClient.cs.setSecondary_value(((Packet13Play)o).secondary_value);
			}
			// if the game is a trust game, store required variables
			else if(activity == 3 && activity_id == 2)
			{
				HRRUClient.cs.setSecondary_id(((Packet13Play)o).secondary_id);
				HRRUClient.cs.setSecondary_value(((Packet13Play)o).secondary_value);
				HRRUClient.cs.setThird_value(((Packet13Play)o).third_value);
			}
			// if the game is a prisoner game, store required variables
			else if(activity == 3 && activity_id == 4)
			{
				HRRUClient.cs.setSecondary_id(((Packet13Play)o).secondary_id);
				HRRUClient.cs.setSecondary_value(((Packet13Play)o).secondary_value);
			}
			HRRUClient.cs.setState(play);
		}
		// if the other player has completed a question
		if(o instanceof Packet14QuestionComplete)
		{
			// store the other players statistics for their question
			int player = ((Packet14QuestionComplete)o).player;
			int choice = ((Packet14QuestionComplete)o).choice;
			int question_difficulty = ((Packet14QuestionComplete)o).difficulty;
			int elapsedtime = ((Packet14QuestionComplete)o).elapsedtime;
			int points = ((Packet14QuestionComplete)o).points;
			int overall = ((Packet14QuestionComplete)o).overall;
			boolean correct = ((Packet14QuestionComplete)o).correct;
			ActivityScore otherPlayerResult = new ActivityScore(0,0,0,0,0, false);
			// create an activity object for their question stats
			otherPlayerResult.setActivity(1);
			otherPlayerResult.setDifficulty(question_difficulty);
			otherPlayerResult.setElapsedtime(elapsedtime);
			otherPlayerResult.setPoints(points);
			otherPlayerResult.setOverall(overall);
			otherPlayerResult.setCorrect(correct);
			otherPlayerResult.setChoice(choice);
			// store the players relevant activity object
			if(player == 1)
			{
				HRRUClient.cs.getP1().setActivityScore(otherPlayerResult);
				HRRUClient.cs.getP1().setReady(1);
			}
			else
			{
				HRRUClient.cs.getP2().setActivityScore(otherPlayerResult);
				HRRUClient.cs.getP2().setReady(1);
			}
		}
		if(o instanceof Packet16SendBid)
		{
			// if the other player has made a bid, store their bid
			int player = ((Packet16SendBid)o).player;
			if(player == 1)
				HRRUClient.cs.getP1().setReady(1);
			else
				HRRUClient.cs.getP2().setReady(1);
		}
		if(o instanceof Packet17EndBid)
		{
			// if the bidding game has ended, return the players bids and calculate winner
			HRRUClient.cs.getP1().setReady(2);
			HRRUClient.cs.getP2().setReady(2);
			int player = HRRUClient.cs.getPlayer();
			int itemValue = ((Packet17EndBid)o).itemValue;
			int otherPlayerBid = ((Packet17EndBid)o).otherPlayerBid;
			int playerWon = ((Packet17EndBid)o).playerWon;
			int amountWon = ((Packet17EndBid)o).amountWon;
			boolean win = ((Packet17EndBid)o).win;
			
			BiddingScore biddingScore;
			// if the player is player 1
			if(player == 1)
			{
				// store the bidding game score for player 1
				biddingScore = HRRUClient.cs.getP1().getCurrentBiddingScore();
				biddingScore.setAmountWon(amountWon);
				biddingScore.setItemValue(itemValue);
				biddingScore.setOtherPlayerBid(otherPlayerBid);
				biddingScore.setPlayerWon(playerWon);
				biddingScore.setWin(win);
				HRRUClient.cs.getP1().setCurrentBiddingScore(biddingScore);
			}
			// if the player is player 2
			else
			{
				// store the bidding game score for player 2
				biddingScore = HRRUClient.cs.getP2().getCurrentBiddingScore();
				biddingScore.setAmountWon(amountWon);
				biddingScore.setItemValue(itemValue);
				biddingScore.setOtherPlayerBid(otherPlayerBid);
				biddingScore.setPlayerWon(playerWon);
				biddingScore.setWin(win);
				HRRUClient.cs.getP2().setCurrentBiddingScore(biddingScore);
			}
		}
		if(o instanceof Packet18TrustFirst)
		{
			// if the trust game first phase is finished, see who is first and store their variables
			int player = HRRUClient.cs.getPlayer();
			int playerGiveValue = ((Packet18TrustFirst)o).playerGiveValue;
			// if player 1 was giving first
			if(player==1)
				HRRUClient.cs.getP1().getCurrentTrustScore().setPlayerGiveValue(playerGiveValue);
			// if player 2 was giving first
			if(player==2)
				HRRUClient.cs.getP2().getCurrentTrustScore().setPlayerGiveValue(playerGiveValue);
			HRRUClient.cs.setGameState(1);
			
		}
		if(o instanceof Packet19TrustSecond)
		{
			// if the trust game second phase is finished, see who is second and store their variables
			int player = HRRUClient.cs.getPlayer();
			int playerReturnValue = ((Packet19TrustSecond)o).playerReturnValue;
			// if player 1 was first
			if(player==1)
				HRRUClient.cs.getP1().getCurrentTrustScore().setPlayerReturnValue(playerReturnValue);
			// if player 2 was first
			if(player==2)
				HRRUClient.cs.getP2().getCurrentTrustScore().setPlayerReturnValue(playerReturnValue);
			HRRUClient.cs.setGameState(3);
		}
		if(o instanceof Packet20SendPrison)
		{
			// if the other player has decided on whether to betray or cooperate
			int player = HRRUClient.cs.getPlayer();
			int otherPlayerChoice = ((Packet20SendPrison)o).choice;
			int otherPlayerTime = ((Packet20SendPrison)o).elapsedTime;
			// store player1s answer
			if(player==1)
			{
				HRRUClient.cs.getP1().getCurrentPrisonScore().setOtherPlayerChoice(otherPlayerChoice);
				HRRUClient.cs.getP1().getCurrentPrisonScore().setOtherPlayerTime(otherPlayerTime);
			}
			// store player2s answer
			if(player==2)
			{
				HRRUClient.cs.getP2().getCurrentPrisonScore().setOtherPlayerChoice(otherPlayerChoice);
				HRRUClient.cs.getP2().getCurrentPrisonScore().setOtherPlayerTime(otherPlayerTime);
			}
			HRRUClient.cs.setGameState(2);
		}
		if(o instanceof Packet21EndPrison)
		{
			// if prison game is finished, set next game state
			HRRUClient.cs.setGameState(3);
		}
		if(o instanceof Packet22PropUlt)
		{
			// if ultimatum game phase 1 finished, see who proposed first
			int player = HRRUClient.cs.getPlayer();
			int playerPropValue = ((Packet22PropUlt)o).playerPropValue;
			int playerDecValue = ((Packet22PropUlt)o).playerDecValue;
			// store p1s proposal
			if(player==1)
			{
				HRRUClient.cs.getP1().getCurrentUltimatumScore().setPlayerPropValue(playerPropValue);
				HRRUClient.cs.getP1().getCurrentUltimatumScore().setPlayerDecValue(playerDecValue);
			}
			// store p2s proposal
			if(player==2)
			{
				HRRUClient.cs.getP2().getCurrentUltimatumScore().setPlayerPropValue(playerPropValue);
				HRRUClient.cs.getP2().getCurrentUltimatumScore().setPlayerDecValue(playerDecValue);
			}
			HRRUClient.cs.setGameState(1);
		}
		if(o instanceof packet23DecUlt)
		{
			// if ultimatum game phase 2 is finished, see who decided
			int player = HRRUClient.cs.getPlayer();
			boolean success = ((packet23DecUlt)o).success;
			// see if successful when p1
			if(player==1)
				HRRUClient.cs.getP1().getCurrentUltimatumScore().setSuccess(success);
			// see if successful when p2
			if(player==2)
				HRRUClient.cs.getP2().getCurrentUltimatumScore().setSuccess(success);
			HRRUClient.cs.setGameState(3);
		}
		if(o instanceof Packet25AllScores)
		{
			// send the players scores along with current scores
			HRRUClient.cs.setState(20);
			String names[] = ((Packet25AllScores)o).names;
			int points[] = ((Packet25AllScores)o).scores;
			Score[] scores = new Score[10];
			for(int i = 0; i < scores.length; i++)
				scores[i] = new Score(names[i], points[i]);
			// send scores to server
			HRRUClient.cs.setScores(scores);
		}
	}

}
