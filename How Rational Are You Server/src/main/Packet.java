package main;

public class Packet {
	public static class Packet0CreateRequest{ String player1Name; String password; }
	public static class Packet1CreateAnswer{ boolean accepted = false; int sessionID; String password; }
	public static class Packet2JoinRequest{ String player2Name; int sessionID; String password; }
	public static class Packet3JoinAnswer{ String player1Name; boolean accepted = false; int sessionID; String password; }
	public static class Packet4ConnectionEstablished { String player2Name; }
	public static class Packet5CancelRequest { int sessionID; }
	public static class Packet6CancelRequestResponse { }
}
