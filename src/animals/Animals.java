package animals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import data.Player;
import gui.AnimalsGUI;
import server.AnimalServer;
import util.Log;

public class Animals {
	/** 웹 포트 */
	public static final int port = 80;
	public static int build;
	public static AnimalsGUI gui;
	
	/** 중요 데이터 */
	public static List<Player> onlinePlayers = new ArrayList<Player>();
	
	public static void main(String[] args) {
		loadBuildCount();
		
		Thread Thread_server = new Thread(() -> {
			new AnimalServer();
		});
		Thread_server.setName("중앙서버 쓰레드");
		Thread_server.start();
	
		Thread Thread_console = new Thread(() -> {
			gui = new AnimalsGUI();
		});
		Thread_console.setName("GUI 쓰레드");
		Thread_console.start();
	}

	private static void loadBuildCount() {
		
		try {
			Properties pro = new Properties();
			File f_build = new File("build.txt");
			if(f_build.exists() == false) f_build.createNewFile();
			pro.load(new FileInputStream(f_build));
			build = Integer.parseInt(pro.getProperty("build", "0"));
			pro.setProperty("build", Integer.toString(++build));
			pro.store(new FileOutputStream(f_build), "This is for Build count");
			Log.info("빌드 버전 : " + build);
			pro = null;
			f_build = null;	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
