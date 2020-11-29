package data;

import com.google.gson.JsonObject;

public class StringPacket {
	
	private String str;

	public StringPacket(String str) {
		this.str = str;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("str", str);
		return obj.toString();
	}
	

}
