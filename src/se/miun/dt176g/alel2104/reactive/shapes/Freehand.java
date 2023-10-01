package se.miun.dt176g.alel2104.reactive.shapes;

import se.miun.dt176g.alel2104.reactive.Point;
import se.miun.dt176g.alel2104.reactive.Shape;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Freehand extends Shape {
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.
        // Draw using g2.
        g2.setStroke(new BasicStroke(getThickness()));
        g2.setColor(getColor());

        getFreehandCoordinates().forEach(point -> {
            g2.drawLine(point.getX(), point.getY(), point.getX(), point.getY());
        });
    }
}
