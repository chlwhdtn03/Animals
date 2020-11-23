package data;

import com.google.gson.annotations.SerializedName;

public enum AnimalType {
	
	@SerializedName("chita")
	치타("chita", 150, 125),
	@SerializedName("colored_horse")
	얼룩말("colored_horse",150, 150),
	@SerializedName("crockdail")
	악어("crockdail",150,150),
	@SerializedName("hama")
	하마("hama",150,150),
	@SerializedName("horse")
	말("horse",150,150),
	@SerializedName("noru")
	사슴("noru",150,150),
	@SerializedName("rion")
	사자("rion",150,150),
	@SerializedName("smart_monkey")
	유인원("smart_monkey",150,225);
	
	private String name;
	private int width, height;
	
	 AnimalType(String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}
	 public String getName() {
		 return name;
	 }
	 public int getWidth() {
		return width;
	 }
	 public int getHeight() {
		return height;
	 }
	 

}
