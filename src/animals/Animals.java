package animals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import server.AnimalServer;
import util.Log;

public class Animals {
	/** 웹 포트 */
	public static final int port = 80;
	public static int build;
	
	public static void main(String[] args) {
		loadBuildCount();
		new AnimalServer();
	}

	private static void loadBuildCount() {
		
		try {
			Properties pro = new Properties();
			File f_build = new File("build.properties");
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
