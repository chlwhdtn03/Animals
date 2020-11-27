package data;

public class PointerBoolean {
	
	private boolean value;
	
	public PointerBoolean(boolean V) {
		this.value=V;
	}
	
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return Boolean.toString(value);
	}
	

}
