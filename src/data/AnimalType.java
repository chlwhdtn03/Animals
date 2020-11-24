package data;

import com.google.gson.annotations.SerializedName;

public enum AnimalType {
	
	@SerializedName("chita")
	치타("chita", 150, 125, 100),
	@SerializedName("colored_horse")
	얼룩말("colored_horse",150, 150, 100),
	@SerializedName("crockdail")
	악어("crockdail",150,150, 100),
	@SerializedName("hama")
	하마("hama",150,150, 100),
	@SerializedName("horse")
	말("horse",150,150, 100),
	@SerializedName("noru")
	사슴("noru",150,150, 100),
	@SerializedName("rion")
	사자("rion",150,150, 100),
	@SerializedName("smart_monkey")
	유인원("smart_monkey",150,225, 100);
	
	private String name;
	private int width, height;
	private int maxhealth;
	
	 AnimalType(String name, int width, int height, int maxhealth) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.maxhealth = maxhealth;
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
	 public int getMaxhealth() {
		return maxhealth;
	}

}
