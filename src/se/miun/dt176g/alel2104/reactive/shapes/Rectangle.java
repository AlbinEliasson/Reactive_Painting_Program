package se.miun.dt176g.alel2104.reactive.shapes;

import se.miun.dt176g.alel2104.reactive.Shape;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * <h1>ConcreteShape</h1> Creates a Circle-object.
 * Concrete class which extends Shape.
 * In other words, this class represents ONE type of shape
 * i.e. a circle, rectangle, n-sided regular polygon (if that's your thing)
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

public class Rectangle extends Shape {

	@Override
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.
		// Draw using g2.
		g2.setStroke(new BasicStroke(getThickness()));
		g2.drawRect(getCoordinates().getX(), getCoordinates().getY(), getWidth(), getHeight());
		System.out.println(getCoordinates().getX() + " " + getCoordinates().getY());
	}

}
