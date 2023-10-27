package se.miun.dt176g.alel2104.reactive.shapes;

import se.miun.dt176g.alel2104.reactive.Shape;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * <h1>FreeHand</h1>
 * Component representing the freehand shape.
 *
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Freehand extends Shape implements Serializable {

    /**
     * Overridden draw method for painting the freehand shape.
     * @param g graphics.
     */
    @Override
    public void draw(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.
        // Draw using g2.
        g2.setStroke(new BasicStroke(getThickness()));
        g2.setColor(getColor());

        if (getFreehandCoordinates() != null) {
            getFreehandCoordinates().forEach(point ->
                    g2.drawLine(point.getX(), point.getY(), point.getX(), point.getY()));
        }
    }
}
