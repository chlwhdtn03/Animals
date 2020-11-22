package data;

import com.google.gson.JsonObject;

public class Map {
	
	private MapType map;
	
	public Map(MapType map) {
		this.map = map;
	}
	
	public MapType getMap() {
		return map;
	}
	
	public void setMap(MapType map) {
		this.map = map;
	}

	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("map", map.getName());
		return obj.toString();
	}
	
}
