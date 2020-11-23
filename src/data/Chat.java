package data;

import com.google.gson.JsonObject;

public class Chat {

	private String name;
	private String message;
	
	public Chat() {}
	public Chat(String message) {
		this.message = message;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", name);
		obj.addProperty("message", message);
		return obj.toString();
	}
	
	
}
