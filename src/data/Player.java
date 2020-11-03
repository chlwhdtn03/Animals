package data;

import java.io.Serializable;

import com.google.gson.JsonObject;

import io.vertx.core.http.ServerWebSocket;

public class Player implements Serializable {

	private ServerWebSocket ws;
	private String name;
	private int x, y;
	private boolean ready;
	private boolean leaved;
	
	
	
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
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
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", name);
		obj.addProperty("x", x);
		obj.addProperty("y", y);
		obj.addProperty("ready", ready);
		obj.addProperty("leaved", leaved);
		return obj.toString();
	}
	
	
}
