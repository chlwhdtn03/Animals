package data;

public class MovePacket {
	private int dx,dy;
	private String direction;
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public int getDx() {
		return dx;
	}
	public int getDy() {
		return dy;
	}
	public void setDx(int dx) {
		this.dx = dx;
	}
	public void setDy(int dy) {
		this.dy = dy;
	}
}
