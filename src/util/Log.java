package util;

import animals.Animals;

public class Log {

	public static void info(String msg) {
		System.out.println("[정보] " + msg);
		if(Animals.gui != null) {
			Animals.gui.textarea.append("[정보] " + msg + "\n");
			Animals.gui.textarea.setCaretPosition(Animals.gui.textarea.getDocument().getLength());
		}
	}
	
	public static void warning(String msg) {
		System.out.println("[주의] " + msg);
		if(Animals.gui != null) {
			Animals.gui.textarea.append("[주의] " + msg + "\n");
			Animals.gui.textarea.setCaretPosition(Animals.gui.textarea.getDocument().getLength());
		}
	}
	
	public static void error(String msg) {
		System.err.println("[에러] " + msg);
		if(Animals.gui != null) {
			Animals.gui.textarea.append("[에러] " + msg + "\n");
			Animals.gui.textarea.setCaretPosition(Animals.gui.textarea.getDocument().getLength());
		}
	}
	
	public static void error(Exception e) {
		Log.error(e.getLocalizedMessage());
		for(StackTraceElement s : e.getStackTrace()) {
			Log.error("위치 : " + s.getFileName() + ":" + s.getLineNumber());
		}
	}
	
}
