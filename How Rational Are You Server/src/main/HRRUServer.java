package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

import main.Packet.*;
import main.item.ItemList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class HRRUServer {

	// Server variables
	private Server server;
	public static QuestionList question_list;
	public static ItemList item_list;
	public static ArrayList<Score> scores = new ArrayList<Score>();
	public static final int no_of_games = 4;
	
	public HRRUServer() throws IOException {
		try {
			// set up question and items list
			question_list = new QuestionList("Question.txt");
			item_list = new ItemList();
			
			// set up input reader for scores
			InputStream file_stream = ResourceLoader.getResourceAsStream("text/scores.txt");
			DataInputStream in = new DataInputStream(file_stream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// score list creator
			Score score;
			String[] values;
			String name;
			int points;
			while((strLine = br.readLine()) != null)
			{
				// read and separate by tabs, score and name read in and stored in list
				values = strLine.split("\\t", -1);
				name = values[1];
				points = Integer.parseInt(values[0]);
				score = new Score(name, points);
				scores.add(score);
			}
			Collections.sort(scores);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SlickException e) {
			e.printStackTrace();
		}
		// initiate server and start
		server = new Server(131072, 16384);
		server.addListener(new NetworkListener());
		server.start();
		server.bind(9991);
		registerPackets();
	}
	
	private void registerPackets(){
		// register network classes for packets
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
		kryo.register(Packet24SendScore.class);
		kryo.register(Packet25AllScores.class);
		kryo.register(Packet26Feedback.class);
		kryo.register(Packet27QuestionAnswers.class);
		kryo.register(String[].class);
		kryo.register(int[].class);
	}
	
	public static void main(String[] args) throws SlickException
	{
		// Create server object
		try {
			new HRRUServer();
			Log.set(Log.LEVEL_DEBUG);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}