package se.miun.dt176g.alel2104.reactive.shapes;

import se.miun.dt176g.alel2104.reactive.Shape;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * <h1>Line</h1>
 * Component representing the line shape.
 *
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Line extends Shape {

    /**
     * Overridden draw method for painting the line shape.
     * @param g graphics.
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        g2.setStroke(new BasicStroke(getThickness()));
        g2.setColor(getColor());
        g2.drawLine(getCoordinates().getX(), getCoordinates().getY(), getWidth(), getHeight());
        System.out.println(getCoordinates().getX() + " " + getCoordinates().getY());
    }
}
