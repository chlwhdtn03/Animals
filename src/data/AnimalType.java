package data;

public enum AnimalType {
	
	치타("chita"), 얼룩말("colored_horse"), 악어("crockdail"), 하마("hama"), 말("horse"), 사슴("noru"), 사자("rion"), 유인원("smart_monkey");
	
	private String name;
	
	 AnimalType(String name) {
		this.name = name;
	}
	 public String getName() {
		 return name;
	 }

}
