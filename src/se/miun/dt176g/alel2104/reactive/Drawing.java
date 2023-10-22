package se.miun.dt176g.alel2104.reactive;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 * <h1>Drawing</h1> 
 * The drawing component which holds and paints all the shape components.
 *
 * @author  --Albin Eliasson--
 * @version 1.0
 * @since   2023-10-07
 */
public class Drawing implements Drawable {
	private final ArrayList<Shape> shapes;

	/**
	 * Constructor to initialize the shape container.
	 */
	public Drawing() {
		shapes = new ArrayList<>();
	}
	
	/**
	 * Method for adding new shapes to the shape container.
	 * @param s the shape to be added.
	 */
	public void addShape(Shape s) {
		shapes.add(s);
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
		//System.out.println(shapes.size() + " AMOUNT OF SHAPES");
		// iterate over all shapes and draw them using the draw-method found in
		// each concrete subclass.
	}
}
