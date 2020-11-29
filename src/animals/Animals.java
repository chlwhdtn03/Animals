package animals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import data.MapType;
import data.Player;
import data.PointerBoolean;
import gui.AnimalsGUI;
import server.AnimalServer;
import util.Log;

public class Animals {
	/** 웹 포트 */
	public static final int port = 80;
	
	public static String version = "Final 1.04";
	public static AnimalsGUI gui;
	public static final int MIN_PLAYER = 2;
	public static int startCount = 10;
	
	/** 정책 */
	public static PointerBoolean isAllow_sameIP = new PointerBoolean(false); // 동일 IP 다중접속 허용 여부
	public static PointerBoolean isAllow_longName = new PointerBoolean(false); // 긴 닉네임 허용 여부
	
	/** 중요 데이터 */
	public static List<Player> onlinePlayers = new ArrayList<Player>();
	public static PointerBoolean isStarted = new PointerBoolean(false); // 게임이 시작됬는지
	public static PointerBoolean isIniting = new PointerBoolean(false); // 게임이 시작 준비에 들어갔는지.(게임이 시작될땐 false로 바뀜)
	public static MapType map;

	
	public static void main(String[] args) {
//		loadBuildCount();
		
		Thread Thread_console = new Thread(() -> {
			gui = new AnimalsGUI();
		});
		Thread_console.setName("GUI 쓰레드");
		Thread_console.start();
		
		Thread Thread_server = new Thread(() -> {
			new AnimalServer();
		});
		Thread_server.setName("중앙서버 쓰레드");
		Thread_server.start();
	
		
	}

	private static void loadBuildCount() {
		
//		try {
//			Properties pro = new Properties();
//			File f_build = new File("build.txt");
//			if(f_build.exists() == false) f_build.createNewFile();
//			pro.load(new FileInputStream(f_build));
//			build = Integer.parseInt(pro.getProperty("build", "0"));
//			pro.setProperty("build", Integer.toString(++build));
//			pro.store(new FileOutputStream(f_build), "This is for Build count");
//			Log.info("빌드 버전 : " + build);
//			pro = null;
//			f_build = null;	
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}
