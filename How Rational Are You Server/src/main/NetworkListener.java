package main;

import java.util.HashMap;

import main.Packet.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class NetworkListener extends Listener{
	
	int sessionID = 0;
	HashMap<Integer, ConnectionObject> connections = new HashMap<Integer, ConnectionObject> ();

	
	public void connected(Connection c) {
		Log.info("[SERVER] Someone has connected.");
	}

	public void disconnected(Connection c) {
		Log.info("[SERVER] Someone has disconnected.");		
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
		
	}
}