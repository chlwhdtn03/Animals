package packet;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AnimalsPacket implements Serializable {
	
	private String type;
	private Object data;
	
	public AnimalsPacket(String type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("type", type);
		obj.addProperty("data", data.toString());
		return obj.toString();
	}
	
}