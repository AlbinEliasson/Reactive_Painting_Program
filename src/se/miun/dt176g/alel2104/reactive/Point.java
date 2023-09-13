package se.miun.dt176g.alel2104.reactive;

/**
 * <h1>Point</h1> 
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

public class Point {

	private int x, y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object o) {
		Point p = (Point) o;
		return (x == p.getX() && y == p.getY());
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+"]";
	}

}
