package se.miun.dt176g.alel2104.reactive;

import java.io.Serializable;

/**
 * <h1>Point</h1> 
 * The point component which represents an x and Y position from the GUI.
 * @author  --Albin Eliasson--
 * @version 1.0
 * @since   2023-10-07
 */
public class Point implements Serializable {
	private int x;
	private int y;

	/**
	 * Constructor to initialize the x and y coordinates.
	 * @param x the x-coordinate.
	 * @param y the y-coordinate.
	 */
	public Point(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Simple getter for the x-coordinate.
	 * @return the x-coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Simple getter for the y-coordinate.
	 * @return the y-coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Method for setting the current x-coordinate.
	 * @param x the x-position.
	 */
	public void setX(final int x) {
		this.x = x;
	}

	/**
	 * Method for setting the current y-coordinate.
	 * @param y the y-coordinate.
	 */
	public void setY(final int y) {
		this.y = y;
	}

	/**
	 * Overridden method for checking if two points have the same x and y-coordinates.
	 * @param o the provided point.
	 * @return true if equal.
	 */
	@Override
	public boolean equals(final Object o) {
		Point p = (Point) o;
		return (x == p.getX() && y == p.getY());
	}

	/**
	 * Overridden method for making printing of the current x and y-coordinates easier.
	 * @return a string of the x and y-coordinates.
	 */
	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
}
