package se.miun.dt176g.alel2104.reactive.shapes;

import se.miun.dt176g.alel2104.reactive.Shape;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * <h1>Oval</h1>
 * Component representing the oval shape.
 *
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Oval extends Shape implements Serializable {

    /**
     * Overridden draw method for painting the oval shape.
     * @param g graphics.
     */
    @Override
    public void draw(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        g2.setStroke(new BasicStroke(getThickness()));
        g2.setColor(getColor());

        if (getCoordinates() != null) {
            g2.drawOval(getCoordinates().getX(), getCoordinates().getY(), getWidth(), getHeight());
        }
    }
}
