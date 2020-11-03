package data;

import com.google.gson.JsonObject;

public class Ready {
	
	private String readyer;

	public Ready(String readyer) {
		this.readyer = readyer;
	}
	public String getReadyer() {
		return readyer;
	}

	public void setReadyer(String readyer) {
		this.readyer = readyer;
	}
	
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("readyer", readyer);
		return obj.toString();
	}

}
