package data;

public enum MapType {
	
	Field("field"), Desert("desert");
	
	private String name;
	
	MapType(String name) {
		this.name = name;
	}
	 public String getName() {
		 return name;
	 }

}
