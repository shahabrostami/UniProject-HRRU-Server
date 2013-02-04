package main;

import com.esotericsoftware.kryonet.Connection;

public class ConnectionObject {
	private Connection p1;
	private Connection p2;
	private String p1name;
	private String p2name;
	private String password;
	private int sessionID;
	private boolean established;
	
	public ConnectionObject(Connection p1, String p1name, int sessionID, String password)
	{
		this.p1 = p1;
		this.p2 = null;
		this.p1name = p1name;
		this.p2name = null;
		this.password = password;
		this.sessionID = sessionID;
		this.established = false;
	}
	
	public Connection getP1() {
		return p1;
	}

	public Connection getP2() {
		return p2;
	}

	public void setP2(Connection p2) {
		this.p2 = p2;
	}

	public int getSessionID() {
		return sessionID;
	}

	public String getPassword() {
		return password;
	}
	
	public void setEstablished() {
		this.established = true;
	}

	public boolean isEstablished() {
		return established;
	}
	
	public String getP1name() {
		return p1name;
	}

	public String getP2name() {
		return p2name;
	}

	public void setP2name(String p2name) {
		this.p2name = p2name;
	}

}
