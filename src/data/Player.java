package data;

import com.google.gson.JsonObject;

import io.vertx.core.http.ServerWebSocket;

public class Player {

	private ServerWebSocket ws;
	private String name;
	private AnimalType animal; // 동물 이름
	private String direction;
	private int x, y;
	private int health;
	private boolean ready;
	private boolean leaved;
	@SuppressWarnings("unused")
	private Vector2D vector2d;
	
	public void makeSpectator() {
		x = 0;
		y = 0;
		health = 0;
		direction = "right";
		animal = null;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	public int getHealth() {
		return health;
	}
	public int getMaxhealth() {
		return animal.getMaxhealth();
	}
	public AnimalType getAnimal() {
		return animal;
	}	
	public void setAnimal(AnimalType animal) {
		this.animal = animal;
	}
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
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Vector2D getVector2D() {
		return vector2d = new Vector2D(this);
	}
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", name);
		obj.addProperty("health", health);
		try {
			obj.addProperty("maxhealth", animal.getMaxhealth());
		} catch(Exception e) {
			obj.addProperty("maxhealth", "0");
		}
		obj.addProperty("x", x);
		obj.addProperty("y", y);
		obj.addProperty("direction", direction);
		obj.addProperty("ready", ready);
		try {
			obj.addProperty("animal", animal.getName());
		} catch(Exception e) {
			obj.addProperty("animal", "");
		}
		obj.addProperty("leaved", leaved);
		return obj.toString();
	}
	
	
}
