package main;

import java.io.IOException;

import org.newdawn.slick.SlickException;

import main.Packet.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;


public class HRRUServer {
	
	private Server server;
	public static QuestionList question_list;
	
	public HRRUServer() throws IOException {
		try {
			question_list = new QuestionList("Question.txt");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		server = new Server();
		registerPackets();
		server.addListener(new NetworkListener());
		server.start();
		server.bind(9991, 9992);
	}
	
	
	private void registerPackets(){
		Kryo kryo = server.getKryo();
		kryo.register(Packet0CreateRequest.class);
		kryo.register(Packet1CreateAnswer.class);
		kryo.register(Packet2JoinRequest.class);
		kryo.register(Packet3JoinAnswer.class);
		kryo.register(Packet4ConnectionEstablished.class);
		kryo.register(Packet3JoinAnswer.class);
		kryo.register(Packet4ConnectionEstablished.class);
		kryo.register(Packet5CancelRequest.class);
		kryo.register(Packet6CancelRequestResponse.class);
		kryo.register(Packet7Ready.class);
		kryo.register(Packet8Start.class);
		kryo.register(Packet9CharacterSelect.class);
		kryo.register(Packet10ChatMessage.class);
		kryo.register(Packet11TurnMessage.class);
		kryo.register(Packet12PlayReady.class);
		kryo.register(Packet13Play.class);
		kryo.register(Packet14QuestionComplete.class);
		kryo.register(int[].class);
	}
	
	public static void main(String[] args) throws SlickException
	{
		try {
			new HRRUServer();
			Log.set(Log.LEVEL_DEBUG);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
