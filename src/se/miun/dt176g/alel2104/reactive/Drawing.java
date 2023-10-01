package se.miun.dt176g.alel2104.reactive;


import java.awt.Graphics;
import java.util.ArrayList;


/**
 * <h1>Drawing</h1> 
 * Let this class store an arbitrary number of AbstractShape-objects in
 * some kind of container. 
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */


public class Drawing implements Drawable {
	// private SomeContainer shapes;
	private ArrayList<Shape> shapes;

	public Drawing() {
		shapes = new ArrayList<>();
	}
	
	/**
	 * <h1>addShape</h1> add a shape to the "SomeContainer shapes"
	 * 
	 * @param s a {@link Shape} object.
	 */
	public void addShape(Shape s) {
		shapes.add(s);
	}

	public void clearShapes() {
		shapes.clear();
	}

	@Override
	public void draw(Graphics g) {
		shapes.forEach(shape -> shape.draw(g));
		System.out.println(shapes.size() + " AMOUNT OF SHAPES");
		// iterate over all shapes and draw them using the draw-method found in
		// each concrete subclass.
	}

}
