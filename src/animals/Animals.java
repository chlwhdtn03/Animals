package animals;

import server.AnimalServer;

public class Animals {
	
	/** 웹 포트 */
	public static final int port = 80;
	
	public static void main(String[] args) {
		new AnimalServer();
	}

}
