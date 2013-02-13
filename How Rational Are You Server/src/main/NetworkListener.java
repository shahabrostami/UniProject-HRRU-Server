package main;

import java.util.HashMap;

import main.Packet.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class NetworkListener extends Listener{
	
	int sessionID = 0;
	HashMap<Integer, ConnectionObject> connections = new HashMap<Integer, ConnectionObject> ();
	HashMap<Connection, Connection> playerConnections = new HashMap<Connection, Connection> ();

	
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
				if(tile_random_number <= 0.2)
					tileOrder[i] = 1;
				else if(tile_random_number <= 0.4)
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
			
			connection.getP1().sendTCP(startPacket);
			connection.getP2().sendTCP(startPacket);
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
			ConnectionObject connection = connections.get(sessionID);
			Connection otherPlayer = playerConnections.get(c);
			if(player == 1)
			{
				connection.updateP1position(moves);
				otherPlayer.sendTCP(o);
			}
			if(player == 2)
			{
				connection.updateP2position(moves);
				otherPlayer.sendTCP(o);
			}
		}
		
	}
}