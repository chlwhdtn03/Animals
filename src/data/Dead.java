package data;

import com.google.gson.JsonObject;

public class Dead {
	
	private Player killer;
	private Player dead;
	private int left; // 방금 얘가 죽고 남은 플레이어
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
	public Dead(Player killer, Player dead, int left) {
		this.killer = killer;
		this.dead = dead;
		this.left = left;
	}
	
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}

	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("killer", killer.toString());
		obj.addProperty("dead", dead.toString());
		obj.addProperty("left", left);
		return obj.toString();
	}
	

}
