package main;

import java.util.HashMap;
import java.util.Random;

import main.Packet.Packet11TurnMessage;
import main.Packet.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class NetworkListener extends Listener{
	
	int sessionID = 0;
	HashMap<Integer, ConnectionObject> connections = new HashMap<Integer, ConnectionObject> ();
	HashMap<Connection, Connection> playerConnections = new HashMap<Connection, Connection> ();
	
	private final QuestionList question_list = HRRUServer.question_list;
	private final Question[] questions = question_list.getQuestion_list();
	
	private final PuzzleList puzzle_list = HRRUServer.puzzle_list;
	private final Puzzle[] puzzles = puzzle_list.getPuzzle_list();
	
	private final int no_of_questions = question_list.getNumberOfQuestions();
	private final int no_of_puzzles = puzzle_list.getNumberOfPuzzles();
	
	private Random rand = new Random();
	
	public void connected(Connection c) {
		Log.info("[SERVER] Someone has connected.");
	}

	public void disconnected(Connection c) {
		Log.info("[SERVER] Someone has disconnected.");
		Connection otherPlayer = playerConnections.get(c);
		if(!(otherPlayer == null))
		{
			Packet6CancelRequestResponse cancelResponse = new Packet6CancelRequestResponse();
			otherPlayer.sendTCP(cancelResponse);
		}
	}

	public void received(Connection c, Object o) {
		if(o instanceof Packet0CreateRequest){
			Packet1CreateAnswer createAnswer = new Packet1CreateAnswer();
			String player1name = ((Packet0CreateRequest)o).player1Name;
			createAnswer.accepted = true;
			createAnswer.sessionID = sessionID;
			createAnswer.password = ((Packet0CreateRequest)o).password;
			ConnectionObject newconnection = new ConnectionObject(c, player1name, createAnswer.sessionID, createAnswer.password);
			connections.put(sessionID, newconnection);
			Log.info("Created a server connection with sessionID " + sessionID + " with password " + createAnswer.password);
			c.sendTCP(createAnswer);
			sessionID++;
		}
		if(o instanceof Packet2JoinRequest){
			Packet3JoinAnswer joinAnswer = new Packet3JoinAnswer();
			// TO DO, make sure connection is correct, right sessionID and right password etc.
			joinAnswer.sessionID = ((Packet2JoinRequest)o).sessionID;
			
			if(connections.containsKey(joinAnswer.sessionID)) // does the sessionID exist?
			{
				String password = ((Packet2JoinRequest)o).password;
				ConnectionObject joinServer = connections.get(joinAnswer.sessionID);
				if(joinServer.getPassword().equals(password))
				{
					String player2name = ((Packet2JoinRequest)o).player2Name;
					joinServer.setP2name(player2name);
					joinServer.setP2(c);
					joinServer.isEstablished();
					connections.put(joinAnswer.sessionID, joinServer);
					playerConnections.put(connections.get(joinAnswer.sessionID).getP1() , c);
					playerConnections.put(c, connections.get(joinAnswer.sessionID).getP1());
					joinAnswer.accepted = true;
					joinAnswer.player1Name = joinServer.getP1name();
					joinAnswer.sessionID =  joinServer.getSessionID();
					joinAnswer.password = password;
					Log.info(joinAnswer.player1Name + " is now connected with " + player2name);
					c.sendTCP(joinAnswer);
					
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
			Packet6CancelRequestResponse cancelResponse = new Packet6CancelRequestResponse();
			int sessionID = ((Packet.Packet5CancelRequest)o).sessionID;
			
			if(connections.containsKey(sessionID))
			{
				ConnectionObject connection = connections.get(sessionID);
				connection.getP1().sendTCP(cancelResponse);
				if(connection.getP2() != (null))
					connection.getP2().sendTCP(cancelResponse);
				connections.remove(sessionID);
			}
		}
		if(o instanceof Packet7Ready){
			int sessionID = ((Packet.Packet7Ready)o).sessionID;
			int player = ((Packet.Packet7Ready)o).player;
			ConnectionObject connection = connections.get(sessionID);
			if(player == 1)
			{
				connection.getP2().sendTCP(o);
			}
			else if(player == 2)
			{
				connection.getP1().sendTCP(o);
			}
		}
		if(o instanceof Packet8Start){
			int size = 36;
			int[] tileOrder = new int[size];
			double tile_random_number = 0;
			tileOrder[0] = 0;
			
			// calculate order of tiletypes
			for(int i = 0; i < size; i++)
			{
				tile_random_number = Math.random();
				if(tile_random_number <= 1)
					tileOrder[i] = 1;
				else if(tile_random_number <= 1)
					tileOrder[i] = 2;
				else if(tile_random_number <= 0.6)
					tileOrder[i] = 3;
				else tileOrder[i] = 0;
			}
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
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet11TurnMessage)
		{
			int player = ((Packet11TurnMessage)o).playerID;
			int moves = ((Packet11TurnMessage)o).moves;
			int sessionID = ((Packet11TurnMessage)o).sessionID;
			int tile = ((Packet11TurnMessage)o).tile;
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			
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
			int sessionID = ((Packet12PlayReady)o).sessionID;
			int player = ((Packet12PlayReady)o).player;
			
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			
			int player1tile = connection.getP1tile();
			int player2tile = connection.getP2tile();
			
			Packet13Play playMessage1 = new Packet13Play();
			Packet13Play playMessage2 = new Packet13Play();
			
			System.out.println(player);
			
			if(player == 1)
				connection.setP1ReadyToPlay(true);
			else
				connection.setP2ReadyToPlay(true);
			
			System.out.println(connection.getP1ReadyToPlay() + "---" + connection.getP2ReadyToPlay());
			
			if(connection.getP1ReadyToPlay() && connection.getP2ReadyToPlay())
			{
				// P1 and P2 have same tile, calculate difference!
				if(player1tile == player2tile)
				{
					if(player1tile == 1)
					{
						int question_id = rand.nextInt(no_of_questions);
						int question_list_check1[] = connection.getQuestion_list_check1();
						int question_list_check2[] = connection.getQuestion_list_check2();
						int question_check_counter = 0;
						while(question_list_check1[question_id] == 1 && question_list_check2[question_id] == 1)
						{
							question_id = rand.nextInt(no_of_questions);
							question_check_counter++;
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
						System.out.println(question_id);
					}
					else if(player1tile == 2)
					{
						int puzzle_id = rand.nextInt(no_of_puzzles);
						int puzzle_list_check1[] = connection.getPuzzle_list_check1();
						int puzzle_list_check2[] = connection.getPuzzle_list_check2();
						int puzzle_check_counter = 0;
						while(puzzle_list_check1[puzzle_id] == 1 && puzzle_list_check2[puzzle_id] == 1)
						{
							puzzle_id = rand.nextInt(no_of_puzzles);
							puzzle_check_counter++;
							System.out.println("puzzletoomany!");
							if(puzzle_check_counter > (no_of_puzzles*2))
							{
								for(int i = 0; i<no_of_puzzles; i++)
								{
									connection.getPuzzle_list_check1()[puzzle_id] = 0;
									connection.getPuzzle_list_check2()[puzzle_id] = 0;
								}
							}
						}
						connection.getPuzzle_list_check1()[puzzle_id] = 1;
						connection.getPuzzle_list_check2()[puzzle_id] = 1;
						playMessage1.activity = 2;
						playMessage1.activity_id = puzzle_id;
						playMessage2.activity = 2;
						playMessage2.activity_id = puzzle_id;
					}
				}
				// P1's tile
				else if(player1tile == 1)
				{
					int question_id = rand.nextInt(no_of_questions);
					int question_list_check1[] = connection.getQuestion_list_check1();
					int question_check_counter = 0;
					while(question_list_check1[question_id] == 1)
					{
						question_id = rand.nextInt(no_of_questions);
						question_check_counter++;
						if(question_check_counter > (no_of_questions*2))
							for(int i = 0; i<no_of_questions; i++)
							{
								connection.getQuestion_list_check1()[question_id] = 0;
							}
					}
					connection.getQuestion_list_check1()[question_id] = 1;
					playMessage1.activity = 1;
					playMessage1.activity_id = question_id;
					System.out.println("P1" + question_id);
				}
				else if(player1tile == 2)
				{
					int puzzle_id = rand.nextInt(no_of_puzzles);
					int puzzle_list_check1[] = connection.getPuzzle_list_check1();
					int puzzle_check_counter = 0;
					while(puzzle_list_check1[puzzle_id] == 1)
					{
						puzzle_id = rand.nextInt(no_of_puzzles);
						puzzle_check_counter++;
						if(puzzle_check_counter > (no_of_puzzles*2))
							for(int i = 0; i<no_of_puzzles; i++)
							{
								connection.getPuzzle_list_check1()[puzzle_id] = 0;
							}
					}
					connection.getPuzzle_list_check1()[puzzle_id] = 1;
					playMessage1.activity = 2;
					playMessage1.activity_id = puzzle_id;
					System.out.println("P1" + puzzle_id);
				}
				// P2's tile
				else if(player2tile == 1)
				{
					int question_id = rand.nextInt(no_of_questions);
					int question_list_check2[] = connection.getQuestion_list_check2();
					int question_check_counter = 0;
					while(question_list_check2[question_id] == 1)
					{
						question_id = rand.nextInt(no_of_questions);
						question_check_counter++;
						if(question_check_counter > (no_of_questions*2))
							for(int i = 0; i<no_of_questions; i++)
							{
								connection.getQuestion_list_check2()[question_id] = 0;
							}
					}
					connection.getQuestion_list_check2()[question_id] = 1;
					playMessage2.activity = 2;
					playMessage2.activity_id = question_id;
					System.out.println("P2" + question_id);
				}
				else if(player2tile == 2)
				{
					int puzzle_id = rand.nextInt(no_of_puzzles);
					int puzzle_list_check2[] = connection.getPuzzle_list_check2();
					int puzzle_check_counter = 0;
					while(puzzle_list_check2[puzzle_id] == 1)
					{
						puzzle_id = rand.nextInt(no_of_puzzles);
						puzzle_check_counter++;
						if(puzzle_check_counter > (no_of_puzzles*2))
							for(int i = 0; i<no_of_puzzles; i++)
							{
								connection.getPuzzle_list_check2()[puzzle_id] = 0;
							}
					}
					connection.getPuzzle_list_check2()[puzzle_id] = 1;
					playMessage2.activity = 2;
					playMessage2.activity_id = puzzle_id;
					System.out.println("P2" + puzzle_id);
				}
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
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet15PuzzleComplete)
		{
			Connection otherPlayer = playerConnections.get(c);
			otherPlayer.sendTCP(o);
		}
		if(o instanceof Packet00SyncMessage)
		{
			int sessionID = ((Packet00SyncMessage)o).sessionID;
			int player = ((Packet00SyncMessage)o).player;
			
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			
			if(player == 1)
				connection.setP1ReadyToPlay(true);
			else
				connection.setP2ReadyToPlay(true);
			
			if(connection.getP1ReadyToPlay() && (connection.getP2ReadyToPlay()))
			{
				synchronized(this){
					otherPlayer.sendTCP(o);
					c.sendTCP(o);
					connection.setP1ReadyToPlay(false);
					connection.setP2ReadyToPlay(false);
				}
			}
		}
		
	}
}