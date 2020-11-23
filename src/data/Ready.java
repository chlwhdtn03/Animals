package data;

import com.google.gson.JsonObject;

public class Ready {
	
	private String readyer;
	private boolean ready;

	public Ready(String readyer, boolean ready) {
		this.readyer = readyer;
		this.ready = ready;
	}
	public String getReadyer() {
		return readyer;
	}

	public void setReadyer(String readyer) {
		this.readyer = readyer;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	@Override
	public String toString() {
		JsonObject obj = new JsonObject();
		obj.addProperty("readyer", readyer);
		obj.addProperty("ready", ready);
		return obj.toString();
	}

}
