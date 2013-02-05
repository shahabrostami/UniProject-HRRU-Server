package main;

import java.io.IOException;

import main.Packet.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class HRRUServer {
	
	private Server server;
	
	public HRRUServer() throws IOException {
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
	}
	
	public static void main(String[] args)
	{
		try {
			new HRRUServer();
			Log.set(Log.LEVEL_DEBUG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
