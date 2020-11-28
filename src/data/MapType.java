package data;

public enum MapType {
	
	Field("field",5333,3333), Desert("desert",5002,3002);
	
	private String name;
	private int width, height;
	
	MapType(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}
	 public String getName() {
		 return name;
	 }

	 public int getHeight() {
		return height;
	}
	 
	 public int getWidth() {
		return width;
	}
}
