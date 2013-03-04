package main;

import java.io.IOException;

import org.newdawn.slick.SlickException;

import main.Packet.*;
import main.item.ItemList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class HRRUServer {
	
	private Server server;
	public static QuestionList question_list;
	public static PuzzleList puzzle_list;
	public static ItemList item_list;
	public static final int no_of_games = 4;
	
	public HRRUServer() throws IOException {
		try {
			question_list = new QuestionList("Question.txt");
			puzzle_list = new PuzzleList("Puzzle.txt");
			item_list = new ItemList();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		server = new Server(131072, 16384);
		server.addListener(new NetworkListener());
		server.start();
		server.bind(9991);
		registerPackets();
	}
	
	private void registerPackets(){
		Kryo kryo = server.getKryo();
		kryo.register(Packet00SyncMessage.class);
		kryo.register(Packet0CreateRequest.class);
		kryo.register(Packet1CreateAnswer.class);
		kryo.register(Packet2JoinRequest.class);
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
		kryo.register(Packet15PuzzleComplete.class);
		kryo.register(Packet16SendBid.class);
		kryo.register(Packet17EndBid.class);
		kryo.register(Packet18TrustFirst.class);
		kryo.register(Packet19TrustSecond.class);
		kryo.register(Packet20SendPrison.class);
		kryo.register(Packet21EndPrison.class);
		kryo.register(Packet22PropUlt.class);
		kryo.register(packet23DecUlt.class);
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