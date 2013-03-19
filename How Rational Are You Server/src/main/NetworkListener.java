package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import main.Packet.*;
import main.item.Item;
import main.item.ItemList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class NetworkListener extends Listener{
	
	// start the sessionID counter at 0.
	int sessionID = 0;
	
	// server and connections hashmaps
	HashMap<Connection, Integer> connectionSessions = new HashMap<Connection, Integer> ();
	HashMap<Integer, ConnectionObject> connections = new HashMap<Integer, ConnectionObject> ();
	HashMap<Connection, Connection> playerConnections = new HashMap<Connection, Connection> ();
	
	// initiate the scores, questions and items used in the game
	private ArrayList<Score> scores = HRRUServer.scores;
	private final QuestionList question_list = HRRUServer.question_list;
	private final ItemList item_list = HRRUServer.item_list;
	private final Item[] items = item_list.getItems();
	// question variables
	private final int no_of_questions = question_list.getNumberOfQuestions();
	private final int no_of_items = item_list.getSize();
	private final int no_of_games = 4;
	// tile assignment, set this to equal the total tile szie.
	private final int easyTilesMax = 8;
	private final int mediumTilesMax = 8;
	private final int hardTilesMax = 6;
	private final int gameTilesMax = 10;
	// game ids
	private final int bidgame = 1;
	private final int trustgame = 2;
	private final int prisonergame = 3;
	private final int ultgame = 4;
	
	private Random rand = new Random();
	
	public void connected(Connection c) {
		Log.info("[SERVER] Someone has connected.");
		String sendNames[] = new String[10];
		int sendScores[] = new int[10];
		// if a player joins, create scoreboard and send over
		for(int i = 0; i < 10; i++)
		{
			sendNames[i] = scores.get(i).getName();
			sendScores[i] = scores.get(i).getScore();
		}
		Packet25AllScores sendScorePacket = new Packet25AllScores();
		sendScorePacket.names = sendNames;
		sendScorePacket.scores = sendScores;
		c.sendTCP(sendScorePacket);
	}

	public void disconnected(Connection c) {
		Log.info("[SERVER] Someone has disconnected.");
		// if a player leaves, disconnect other player
		Connection otherPlayer = playerConnections.get(c);
		if(!(otherPlayer == null))
		{
			Packet6CancelRequestResponse cancelResponse = new Packet6CancelRequestResponse();
			otherPlayer.sendTCP(cancelResponse);
			connections.remove(connectionSessions.get(c));
		}
	}

	public void received(Connection c, Object o) {
		if(o instanceof Packet00SyncMessage)
		{
			// sync message, ensure both players are ready before continuing
			int sessionID = ((Packet00SyncMessage)o).sessionID;
			int player = ((Packet00SyncMessage)o).player;
			
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			// set player ready to play true
			if(player == 1)
				connection.setP1ReadyToPlay(true);
			else
				connection.setP2ReadyToPlay(true);
			
			if(connection.getP1ReadyToPlay() && (connection.getP2ReadyToPlay()))
			{
				// synchronise for both players
				synchronized(this){
					otherPlayer.sendTCP(o);
					c.sendTCP(o);
					connection.setP1ReadyToPlay(false);
					connection.setP2ReadyToPlay(false);
				}
			}
		}
		if(o instanceof Packet0CreateRequest){
			// Creates server connection between 2 plaeyers.
			Packet1CreateAnswer createAnswer = new Packet1CreateAnswer();
			String player1name = ((Packet0CreateRequest)o).player1Name;
			createAnswer.accepted = true;
			createAnswer.sessionID = sessionID;
			createAnswer.password = ((Packet0CreateRequest)o).password;
			ConnectionObject newconnection = new ConnectionObject(c, player1name, createAnswer.sessionID, createAnswer.password);
			connections.put(sessionID, newconnection);
			connectionSessions.put(c, sessionID);
			Log.info("Created a server connection with sessionID " + sessionID + " with password " + createAnswer.password);
			c.sendTCP(createAnswer);
			sessionID++;
		}
		if(o instanceof Packet2JoinRequest){
			Packet3JoinAnswer joinAnswer = new Packet3JoinAnswer();
			// joins the player connection to the correct connection objective
			joinAnswer.sessionID = ((Packet2JoinRequest)o).sessionID;
			
			if(connections.containsKey(joinAnswer.sessionID)) // does the sessionID exist?
			{
				String password = ((Packet2JoinRequest)o).password;
				ConnectionObject joinServer = connections.get(joinAnswer.sessionID);
				// if the password is correct, initiate the connection object for other player
				if(joinServer.getPassword().equals(password))
				{
					String player2name = ((Packet2JoinRequest)o).player2Name;
					joinServer.setP2name(player2name);
					joinServer.setP2(c);
					joinServer.isEstablished();
					// set up new connections in hashmaps
					connections.put(joinAnswer.sessionID, joinServer);
					connectionSessions.put(c, joinAnswer.sessionID);
					playerConnections.put(connections.get(joinAnswer.sessionID).getP1() , c);
					playerConnections.put(c, connections.get(joinAnswer.sessionID).getP1());
					// set up join packet for other player
					joinAnswer.accepted = true;
					joinAnswer.player1Name = joinServer.getP1name();
					joinAnswer.sessionID =  joinServer.getSessionID();
					joinAnswer.password = password;
					Log.info(joinAnswer.player1Name + " is now connected with " + player2name);
					c.sendTCP(joinAnswer);
					// éstablish connection for first player
					Packet4ConnectionEstablished connectionEstablished = new Packet4ConnectionEstablished();
					connectionEstablished.player2Name = player2name;
					Connection p1 = joinServer.getP1();
					p1.sendTCP(connectionEstablished);
				}
				else
				{
					joinAnswer.accepted = false;
					c.sendTCP(joinAnswer);
				}
			}
			else
			{
				joinAnswer.accepted = false;
				c.sendTCP(joinAnswer);
			}
		}
		if(o instanceof Packet5CancelRequest){
			// cancel request, ensures that when the player disconnects, the other player is notified
			Packet6CancelRequestResponse cancelResponse = new Packet6CancelRequestResponse();
			int sessionID = ((Packet.Packet5CancelRequest)o).sessionID;
			
			if(connections.containsKey(sessionID))
			{
				ConnectionObject connection = connections.get(sessionID);
				connection.getP1().sendTCP(cancelResponse);
				if(connection.getP2() != (null))
					connection.getP2().sendTCP(cancelResponse);
				connections.remove(sessionID);
				connectionSessions.remove(c);
			}
		}
		if(o instanceof Packet7Ready){
			// packet for setting player sync before continuing game
			int sessionID = ((Packet.Packet7Ready)o).sessionID;
			int player = ((Packet.Packet7Ready)o).player;
			ConnectionObject connection = connections.get(sessionID);
			if(player == 1)
				connection.getP2().sendTCP(o);
			else if(player == 2)
				connection.getP1().sendTCP(o);
		}
		if(o instanceof Packet8Start){
			// This initiaties the game connection object between 2 players.
			// Creates the required connection hashmaps and board and
			// sends it to the players involved
			int size = 32;
			int[] tileOrder = new int[size];
			double tile_random_number = 0;
			tileOrder[0] = 0;
			int easyTiles = easyTilesMax;
			int mediumTiles = mediumTilesMax;
			int hardTiles = hardTilesMax;
			int gameTiles = gameTilesMax;
			int counter = 0;
			// calculate order of tiletypes
			while(counter < size)
			{
				tile_random_number = Math.random();
				if(tile_random_number <= 0.25 && easyTiles > 0)
				{
					tileOrder[counter] = 0;
					easyTiles--;
					counter++;
				}
				else if(tile_random_number <= 0.5 && mediumTiles > 0)
				{
					tileOrder[counter] = 1;
					mediumTiles--;
					counter++;
				}
				else if(tile_random_number <= 0.75 && hardTiles > 0)
				{
					tileOrder[counter] = 2;
					hardTiles--;
					counter++;
				}
				else if(tile_random_number <= 1 && gameTiles > 0)
				{
					tileOrder[counter] = 3;
					gameTiles--;
					counter++;
				}
			}
			// send tile assignment to both players
			int sessionID = ((Packet.Packet8Start)o).sessionID;
			ConnectionObject connection = connections.get(sessionID);
			
			Packet8Start startPacket = new Packet8Start();
			startPacket.sessionID = sessionID;
			startPacket.board = tileOrder;
			
			synchronized ( this ) {
			connection.getP1().sendTCP(startPacket);
			connection.getP2().sendTCP(startPacket);
			}
		}
		if(o instanceof Packet9CharacterSelect){
			// handshaking for character selection between 2 players.
			int sessionID = ((Packet.Packet9CharacterSelect)o).sessionID;
			int characterID = ((Packet.Packet9CharacterSelect)o).characterID;
			int player = ((Packet.Packet9CharacterSelect)o).player;
			
			if(player == 1)
			{
				connections.get(sessionID).setP1characterID(characterID);
				connections.get(sessionID).getP2().sendTCP(o);
			}
	        else if(player == 2)
	        {
	        	connections.get(sessionID).setP2characterID(characterID);
				connections.get(sessionID).getP1().sendTCP(o);
	        }
		}
		if(o instanceof Packet10ChatMessage)
		{
			// forwards chat message to correct player.
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet11TurnMessage)
		{
			// return and record the players turn, including their new position
			int player = ((Packet11TurnMessage)o).playerID;
			int moves = ((Packet11TurnMessage)o).moves;
			int sessionID = ((Packet11TurnMessage)o).sessionID;
			int tile = ((Packet11TurnMessage)o).tile;
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			// depending on the player who has had their turn, update their position
			// and update the other player on this new position
			if(player == 1)
			{
				connection.updateP1position(moves);
				connection.setP1tile(tile);
				otherPlayer.sendTCP(o);
			}
			if(player == 2)
			{
				Packet11TurnMessage o_copy = new Packet11TurnMessage();
				((Packet11TurnMessage) o_copy).moves = 0;
				((Packet11TurnMessage) o_copy).playerID = player;
				connection.updateP2position(moves);
				connection.setP2tile(tile);
				synchronized ( this ) {
				otherPlayer.sendTCP(o);
				c.sendTCP(o_copy);
				}
			}
		}
		if(o instanceof Packet12PlayReady)
		{
			// if both players are ready to play, calculate the next activity
			int sessionID = ((Packet12PlayReady)o).sessionID;
			int player = ((Packet12PlayReady)o).player;
			
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			
			int player1tile = connection.getP1tile();
			int player2tile = connection.getP2tile();
			
			Packet13Play playMessage1 = new Packet13Play();
			Packet13Play playMessage2 = new Packet13Play();
			
			if(player == 1)
				connection.setP1ReadyToPlay(true);
			else
				connection.setP2ReadyToPlay(true);
			
			// wait until both players are ready to play
			if(connection.getP1ReadyToPlay() && connection.getP2ReadyToPlay())
			{
				// if both players are on a game tile, calculate a game to play
				if(player1tile == 3 || player2tile == 3)
				{
					boolean gamesDone[] = connection.getGamesDone();
					int activity_id = rand.nextInt(HRRUServer.no_of_games) + 1;
					int counter_check = 0;
					//  make sure a game that hasn't been played yet is played
					while(gamesDone[activity_id] == true)
					{
						counter_check++;
						activity_id = rand.nextInt(HRRUServer.no_of_games) + 1;
						// if all games have been played, reset all to not played and try again
						if(counter_check > (no_of_games*3))
						{
							gamesDone[1] = false;
							gamesDone[2] = false;
							gamesDone[3] = false;
							gamesDone[4] = false;
							counter_check = 0;
						}
					}

					playMessage1.activity = 3;
					playMessage2.activity = 3;
					// if the game is a bidding game, set up variables and send over the item price and multiplier
					if(activity_id == 1)
					{
						int item_id = rand.nextInt(no_of_items);
						Item currentItem = items[item_id];
						int itemValue = rand.nextInt(currentItem.getMaxValue() - currentItem.getMinValue() + 1) + currentItem.getMinValue();
						playMessage1.activity_id = 1;
						playMessage1.secondary_id = item_id;
						playMessage1.secondary_value = itemValue;
						playMessage2.activity_id = 1;
						playMessage2.secondary_id = item_id;
						playMessage2.secondary_value = itemValue;
						gamesDone[bidgame] = true;
					}
					// if the game is a trust game, set up variables and send over the initial value and multiplier
					else if(activity_id == 2)
					{
						int playerCounter = connection.getPlayerTurnCounterTrust();
						int maxValue = rand.nextInt(2) + 3; 
						int multiplier = 3;
						maxValue *= 20;
						
						playMessage1.secondary_id = playerCounter;
						playMessage1.activity_id = 2;
						playMessage1.secondary_value = maxValue;
						playMessage1.third_value = multiplier;

						playMessage2.secondary_id = playerCounter;
						playMessage2.activity_id = 2;
						playMessage2.secondary_value = maxValue;
						playMessage2.third_value = multiplier;
						if(playerCounter == 1)
							connection.setPlayerTurnCounterTrust(2);
						else connection.setPlayerTurnCounterTrust(1);
						gamesDone[trustgame] = true;
					}
					// if the game is a prisoner game, no necessary variables required
					else if(activity_id == 3)
					{
						playMessage1.activity_id = 3;
						playMessage2.activity_id = 3;
						gamesDone[prisonergame] = true;
					}
					// if the game is a dictator game, pick who goes first and send over the playerid
					else if(activity_id == 4)
					{
						playMessage1.activity_id = 4;
						playMessage2.activity_id = 4;
						int playerCounter = connection.getPlayerTurnCounterTrust();
						int overallPoints = rand.nextInt(4) + 2;
						overallPoints*=50;
						
						playMessage1.secondary_id = playerCounter;
						playMessage1.secondary_value = overallPoints;

						playMessage2.secondary_id = playerCounter;
						playMessage2.secondary_value = overallPoints;
						if(playerCounter == 1)
							connection.setPlayerTurnCounterTrust(2);
						else connection.setPlayerTurnCounterTrust(1);
						gamesDone[ultgame] = true;
					}
				}
				// if both players are on the same question tile
				else if(player1tile == player2tile)
				{
					int question_id = rand.nextInt(no_of_questions);
					int question_list_check1[] = connection.getQuestion_list_check1();
					int question_list_check2[] = connection.getQuestion_list_check2();
					int question_check_counter = 0;
					int difficulty =  question_list.getQuestion_list()[question_id].getDifficulty() - 1;
					// pick a question that is equal to the difficulty and both players havent played yet
					while(question_list_check1[question_id] == 1 || question_list_check2[question_id] == 1 || difficulty != player1tile)
					{
						question_id = rand.nextInt(no_of_questions);
						question_check_counter++;
						difficulty =  question_list.getQuestion_list()[question_id].getDifficulty() - 1;
						if(question_check_counter > (no_of_questions*2))
						{
							for(int i = 0; i<no_of_questions; i++)
							{
								connection.getQuestion_list_check1()[question_id] = 0;
								connection.getQuestion_list_check2()[question_id] = 0;
							}
						}
					}
					connection.getQuestion_list_check1()[question_id] = 1;
					connection.getQuestion_list_check2()[question_id] = 1;
					playMessage1.activity = 1;
					playMessage1.activity_id = question_id;
					playMessage2.activity = 1;
					playMessage2.activity_id = question_id;
				}
				// if players are on a different tile
				else
				{
					int question_id = rand.nextInt(no_of_questions);
					int question_list_check1[] = connection.getQuestion_list_check1();
					int question_check_counter = 0;
					int difficulty =  question_list.getQuestion_list()[question_id].getDifficulty() - 1;
					// pick a question that the player hasnt played yet and is equal to their tile difficulty.
					while(question_list_check1[question_id] == 1 || difficulty != player1tile)
					{
						question_id = rand.nextInt(no_of_questions);
						difficulty =  question_list.getQuestion_list()[question_id].getDifficulty() - 1;
						question_check_counter++;
						if(question_check_counter > (no_of_questions*2))
							for(int i = 0; i<no_of_questions; i++)
								connection.getQuestion_list_check1()[question_id] = 0;
					}
					connection.getQuestion_list_check1()[question_id] = 1;
					playMessage1.activity = 1;
					playMessage1.activity_id = question_id;
					// do the same for player 2
					question_id = rand.nextInt(no_of_questions);
					int question_list_check2[] = connection.getQuestion_list_check2();
					question_check_counter = 0;
					difficulty =  question_list.getQuestion_list()[question_id].getDifficulty() - 1;
					// pick a question that player 2 hasnt played yet and is equal to their difficulty
					while(question_list_check2[question_id] == 1 || difficulty != player2tile)
					{
						question_id = rand.nextInt(no_of_questions);
						difficulty =  question_list.getQuestion_list()[question_id].getDifficulty() - 1;
						question_check_counter++;
						if(question_check_counter > (no_of_questions*2))
							for(int i = 0; i<no_of_questions; i++)
								connection.getQuestion_list_check2()[question_id] = 0;
					}
					connection.getQuestion_list_check2()[question_id] = 1;
					playMessage2.activity = 1;
					playMessage2.activity_id = question_id;
				}
				// send the players their packets
				if(player == 1)
				{
					synchronized(this){
						c.sendTCP(playMessage1);
						otherPlayer.sendTCP(playMessage2);
					}
				}
				else if(player == 2)
				{
					synchronized(this){
						c.sendTCP(playMessage2);
						otherPlayer.sendTCP(playMessage1);
					}
				}
				connection.setP1ReadyToPlay(false);
				connection.setP2ReadyToPlay(false);
			}
		}
		if(o instanceof Packet14QuestionComplete)
		{
			// send each players their question results
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet15PuzzleComplete)
		{
			// send each players their puzzle results
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet16SendBid)
		{
			// send each player their bid results
			int sessionID = ((Packet16SendBid)o).sessionID;
			int player = ((Packet16SendBid)o).player;
			int bid = ((Packet16SendBid)o).bid;
			int itemValue = ((Packet16SendBid)o).itemValue;
			
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
			// make sure both players are ready
			if(player == 1)
			{
				connection.setP1ReadyToPlay(true);
				connection.setP1tempvalue(bid);
			}
			else
			{
				connection.setP2ReadyToPlay(true);
				connection.setP2tempvalue(bid);
			}
			// when both players are ready
			if(connection.getP1ReadyToPlay() && (connection.getP2ReadyToPlay()))
			{
				int p1value = connection.getP1tempvalue();
				int p2value = connection.getP2tempvalue();
				// get the player bids
				Packet17EndBid endBidWin = new Packet17EndBid();
				Packet17EndBid endBidLose = new Packet17EndBid();
				
				((Packet17EndBid)endBidWin).itemValue = itemValue;
				((Packet17EndBid)endBidLose).itemValue = itemValue;
				((Packet17EndBid)endBidWin).win = true;
				((Packet17EndBid)endBidLose).win = false;
				// if both bid 0
				if(p1value == 0 && p2value == 0)
				{
					((Packet17EndBid)endBidLose).amountWon = 0;
					((Packet17EndBid)endBidLose).playerWon = 0;
					((Packet17EndBid)endBidLose).otherPlayerBid = 0;
					// both lose
					synchronized (this) {
							c.sendTCP(endBidLose);
							otherPlayer.sendTCP(endBidLose);
					}
					
				}
				// if p1 has a higher bid than p2, p1 wins
				else if(p1value > p2value)
				{
					((Packet17EndBid)endBidWin).amountWon = itemValue - p1value;
					((Packet17EndBid)endBidLose).amountWon = itemValue - p1value;
					((Packet17EndBid)endBidWin).playerWon = 1;
					((Packet17EndBid)endBidLose).playerWon = 1;
					// if player1 sent last packet, they won
					if(player == 1)
					{
						((Packet17EndBid)endBidWin).otherPlayerBid = connection.getP2tempvalue();
						((Packet17EndBid)endBidLose).otherPlayerBid = connection.getP1tempvalue();
						synchronized (this) {
							c.sendTCP(endBidWin);
							otherPlayer.sendTCP(endBidLose);
						}
					}
					// if player2 sent last packet, they lost
					else
					{
						((Packet17EndBid)endBidWin).otherPlayerBid = connection.getP2tempvalue();
						((Packet17EndBid)endBidLose).otherPlayerBid = connection.getP1tempvalue();
						synchronized (this) {
							c.sendTCP(endBidLose);
							otherPlayer.sendTCP(endBidWin);
						}
					}
				}
				// if p2 bids higher than p1, p2 wins
				else if(p2value > p1value)
				{
					((Packet17EndBid)endBidWin).amountWon = itemValue - p2value;
					((Packet17EndBid)endBidLose).amountWon = itemValue - p2value;
					((Packet17EndBid)endBidWin).playerWon = 2;
					((Packet17EndBid)endBidLose).playerWon = 2;
					// if player1 sent last packet, they lost
					if(player == 1)
					{
						((Packet17EndBid)endBidWin).otherPlayerBid = connection.getP1tempvalue();
						((Packet17EndBid)endBidLose).otherPlayerBid = connection.getP2tempvalue();
						synchronized (this) {
							c.sendTCP(endBidLose);
							otherPlayer.sendTCP(endBidWin);
						}
					}
					// if player2 sent last packet, they won
					else
					{
						((Packet17EndBid)endBidWin).otherPlayerBid = connection.getP1tempvalue();
						((Packet17EndBid)endBidLose).otherPlayerBid = connection.getP2tempvalue();
						synchronized (this) {
							c.sendTCP(endBidWin);
							otherPlayer.sendTCP(endBidLose);
						}
					}
				}
				
				// if same value, last player who sent, wins
				else if(p1value == p2value)
				{
					((Packet17EndBid)endBidWin).amountWon = itemValue - p1value;
					((Packet17EndBid)endBidLose).amountWon = itemValue - p1value;
					// if player1 sent last packet, they won
					if(player == 1)
					{
						((Packet17EndBid)endBidWin).playerWon = 1;
						((Packet17EndBid)endBidLose).playerWon = 1;
						((Packet17EndBid)endBidWin).otherPlayerBid = connection.getP2tempvalue();
						((Packet17EndBid)endBidLose).otherPlayerBid = connection.getP1tempvalue();
						synchronized (this) {
							c.sendTCP(endBidLose);
							otherPlayer.sendTCP(endBidWin);
						}
					}
					// if player2 sent last packet, they won
					else
					{
						((Packet17EndBid)endBidWin).playerWon = 2;
						((Packet17EndBid)endBidLose).playerWon = 2;
						((Packet17EndBid)endBidWin).otherPlayerBid = connection.getP1tempvalue();
						((Packet17EndBid)endBidLose).otherPlayerBid = connection.getP2tempvalue();
						synchronized (this) {
							c.sendTCP(endBidLose);
							otherPlayer.sendTCP(endBidWin);
						}
					}
				}
				connection.setP1ReadyToPlay(false);
				connection.setP2ReadyToPlay(false);
			}
		}
		if(o instanceof Packet18TrustFirst)
		{
			// send both players their trust results
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet19TrustSecond)
		{
		    // send both players their trust results
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet20SendPrison)
		{
			// send both players their prison results
			int sessionID = ((Packet20SendPrison)o).sessionID;
			int player = ((Packet20SendPrison)o).player;
			int choice = ((Packet20SendPrison)o).choice;
			
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
			// make sure both players are finished
			if(player == 1)
			{
				connection.setP1ReadyToPlay(true);
				connection.setP1tempvalue(choice);
			}
			else
			{
				connection.setP2ReadyToPlay(true);
				connection.setP2tempvalue(choice);
			}
			// when both players are finished, send results over
			if(connection.getP1ReadyToPlay() && (connection.getP2ReadyToPlay()))
			{
				Packet21EndPrison endPrison = new Packet21EndPrison();
				
				synchronized (this) {
					c.sendTCP(endPrison);
					otherPlayer.sendTCP(endPrison);
					connection.setP1ReadyToPlay(false);
					connection.setP2ReadyToPlay(false);
				}
				
			}
		}
		if(o instanceof Packet22PropUlt)
		{
			// send both players the ultimatum results
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof packet23DecUlt)
		{
			// send both players the ultimatum results
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet24SendScore)
		{
			// setup variables
			String name = ((Packet24SendScore)o).name;
			int score = ((Packet24SendScore)o).score;
			int player = ((Packet24SendScore)o).player;
			int sessionID = ((Packet24SendScore)o).sessionID;
			// set up connection variables
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			// add player score
			Score playerScore = new Score(name, score);
			scores.add(playerScore);
			Collections.sort(scores);
			String textScore = score + "\t" + name;
			
            BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter("res/text/scores.txt", true));
				bw.newLine();
				bw.write(textScore);
	            bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// make sure both players are ready
			if(player == 1)
				connection.setP1ReadyToPlay(true);
			else
				connection.setP2ReadyToPlay(true);
			// when both players are ready, send over the NEW scoreboard
			if(connection.getP1ReadyToPlay() && (connection.getP2ReadyToPlay()))
			{
				String sendNames[] = new String[10];
				int sendScores[] = new int[10];
				for(int i = 0; i < 10; i++)
				{
					sendNames[i] = scores.get(i).getName();
					sendScores[i] = scores.get(i).getScore();
				}
				Packet25AllScores sendScorePacket = new Packet25AllScores();
				sendScorePacket.names = sendNames;
				sendScorePacket.scores = sendScores;
				c.sendTCP(sendScorePacket);
				otherPlayer.sendTCP(sendScorePacket);
			}
		}
		if(o instanceof Packet26Feedback)
		{
			// set up variables to receive player feedback
			int[] feedback = ((Packet26Feedback)o).feedback;
			String otherComments = ((Packet26Feedback)o).otherComments;
			otherComments = otherComments.replace("\n", " ");
			String feedbackLine = feedback[0] + "\t" + feedback[1] + "\t" + feedback[2] 
					+ "\t" + feedback[3] + "\t" + feedback[4] + "\t" + feedback[5] + "\t" + otherComments;
 			BufferedWriter bw;
 			// record the feedback into the feedback text file
				try {
					bw = new BufferedWriter(new FileWriter("res/text/feedback.txt", true));
					bw.newLine();
					bw.write(feedbackLine);
		            bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if(o instanceof Packet27QuestionAnswers)
		{
			// set up variables to receive ALL of the player scores
			int[] answers = ((Packet27QuestionAnswers)o).answers;
			int questionScore = ((Packet27QuestionAnswers)o).questionScore;
			int bidScore = ((Packet27QuestionAnswers)o).bidScore;
			int trustScore = ((Packet27QuestionAnswers)o).trustScore;
			int prisonScore = ((Packet27QuestionAnswers)o).prisonScore;
			int ultScore = ((Packet27QuestionAnswers)o).ultScore;
			int totalScore = ((Packet27QuestionAnswers)o).playerScore;
			
			String answersLine = "" + answers[0];
			
			for(int i = 1; i < answers.length; i++)
				answersLine = answersLine + "\t" + answers[i];

			// record the players answers of questions into the text file for choices 
 			BufferedWriter bw;
				try {
					bw = new BufferedWriter(new FileWriter("res/text/choices.txt", true));
					bw.newLine();
					bw.write(answersLine);
		            bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			// do the same for the points for both players
			String pointsLine = questionScore + "\t" + bidScore + "\t" + trustScore + "\t" + prisonScore + "\t" + ultScore + "\t" + totalScore;
				try {
					bw = new BufferedWriter(new FileWriter("res/text/totalpoints.txt", true));
					bw.newLine();
					bw.write(pointsLine);
		            bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}