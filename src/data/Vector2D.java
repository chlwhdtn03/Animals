package data;

public class Vector2D {
	
	private int x;
	private int y;
	private int dx;
	private int dy;
	
	private AnimalType animal;
	
	
	public Vector2D(int x, int y, int dx, int dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	public Vector2D(Player p) {
		this.x = p.getX();
		this.y = p.getY();
		this.dx = p.getX() + p.getAnimal().getWidth();
		this.dy = p.getY() + p.getAnimal().getHeight();
	}
	
	public static boolean isCoveredWithVector2D(Vector2D a, Vector2D b) {
		return (a.x < b.dx && a.dx > b.x && a.y < b.dy && a.dy > b.y);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	
	public int getDx() {
		return dx;
	}

	public void setDx(int dx) {
		this.dx = dx;
	}

	public int getDy() {
		return dy;
	}

	public void setDy(int dy) {
		this.dy = dy;
	}

	public AnimalType getAnimal() {
		return animal;
	}

	public void setAnimal(AnimalType animal) {
		this.animal = animal;
	}



}
