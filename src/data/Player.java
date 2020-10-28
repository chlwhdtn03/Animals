package data;

import io.vertx.core.http.ServerWebSocket;

public class Player {

	private ServerWebSocket ws;
	private String name;
	private int x, y;
	private boolean leaved;
	
	
	
	public ServerWebSocket getWs() {
		return ws;
	}
	public void setWs(ServerWebSocket ws) {
		this.ws = ws;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public boolean isLeaved() {
		return leaved;
	}
	public void setLeaved(boolean leaved) {
		this.leaved = leaved;
	}
	
	
}
