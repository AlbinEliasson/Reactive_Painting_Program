package se.miun.dt176g.alel2104.reactive;

import java.awt.Graphics;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <h1>Drawing</h1> 
 * The drawing component which holds and paints all the shape components.
 *
 * @author  --Albin Eliasson--
 * @version 1.0
 * @since   2023-10-07
 */
public class Drawing implements Drawable {
	private final CopyOnWriteArrayList<Shape> shapes;

	/**
	 * Constructor to initialize the shape container.
	 */
	public Drawing() {
		shapes = new CopyOnWriteArrayList<>();
	}
	
	/**
	 * Method for adding new shapes to the shape container.
	 * @param s the shape to be added.
	 */
	public void addShape(Shape s) {
		shapes.add(s);
		System.out.println("Amount of shapes: " + shapes.size());
	}

	/**
	 * Method for removing all shapes from the shape container.
	 */
	public void clearShapes() {
		shapes.clear();
	}

	/**
	 * Overridden method for painting all shapes from the shape container.
	 * @param g graphics.
	 */
	@Override
	public void draw(Graphics g) {
		shapes.forEach(shape -> shape.draw(g));
	}
}
