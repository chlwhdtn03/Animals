package util;

public class Log {

	public static void info(String msg) {
		System.out.println("[정보] " + msg);
	}
	
	public static void warning(String msg) {
		System.out.println("[주의] " + msg);
	}
	
	public static void error(String msg) {
		System.err.println("[에러] " + msg);
	}
	
}
