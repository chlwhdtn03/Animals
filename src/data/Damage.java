package data;

import com.google.gson.JsonObject;

public class Damage {
	
	String attacker; // 공격자
	String damager; // 피해자
	int damage; // 피해량
	
	public Damage(String attacker, String damager, int damage) {
		this.damage = damage;
		this.attacker = attacker;
		this.damager = damager;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
	public String getAttacker() {
		return attacker;
	}
	
	public void setAttacker(String attacker) {
		this.attacker = attacker;
	}
	
	public String getDamager() {
		return damager;
	}
	
	public void setDamager(String damager) {
		this.damager = damager;
	}
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("damage", damage);
		obj.addProperty("attacker", attacker);
		obj.addProperty("damager", damager);
		
		return obj.toString();
	}

}
