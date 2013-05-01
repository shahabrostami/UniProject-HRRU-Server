package conn;

import conn.Packet.*;
import main.HRRUClient;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
// set up connection object for player
public class Connection {
	
	private Client client;
	
	public Connection(String ip) {
		// initaite and start the connection object
		client = new Client(65536, 16384);
		NetworkListener n1 = new NetworkListener();
		n1.init(client);
		client.addListener(n1);
		client.start();
		register();
		// try to connect to the server
		try{
			client.connect(5000, ip, 9991);
			HRRUClient.ConnectionSuccessful = 1;
		} catch (IOException e) {
			e.printStackTrace();
			client.stop();
			HRRUClient.ConnectionSuccessful = 0;
		}
	}
	
	public void reConnect(){
		// try to reconnect to the server
		client.stop();
		client.start();
		try {
            client.reconnect();
            HRRUClient.ConnectionSuccessful = 1;
		} catch (IOException ex) {
            ex.printStackTrace();
			 HRRUClient.ConnectionSuccessful = 0;
		}
	}
	
	private void register(){
		// register necessary network classes
		Kryo kryo = client.getKryo();
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
	
	public Client getClient()
	{
		return client;
	}
}
