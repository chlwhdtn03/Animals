package data;

import com.google.gson.JsonObject;

public class Dead {
	
	private Player killer;
	private Player dead;
	public Player getKiller() {
		return killer;
	}
	public void setKiller(Player killer) {
		this.killer = killer;
	}
	public Player getDead() {
		return dead;
	}
	public void setDead(Player dead) {
		this.dead = dead;
	}
	public Dead(Player killer, Player dead) {
		this.killer = killer;
		this.dead = dead;
	}

	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("killer", killer.toString());
		obj.addProperty("dead", dead.toString());
		return obj.toString();
	}
	

}
