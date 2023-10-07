package se.miun.dt176g.alel2104.reactive;

import java.awt.Color;
import java.util.ArrayList;

/**
 * <h1>Shape</h1>
 * The abstract Shape class which derived shape classes builds on.
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 *
 * @author  --Albin Eliasson--
 * @version 1.0
 * @since   2023-10-07
 */
public abstract class Shape implements Drawable {
    // private member : some container storing coordinates
    private Point coordinates;
    private Point size;
    private final ArrayList<Point> freehandCoordinates = new ArrayList<>();
    private float thickness;
    private Color color;

    /**
     * Simple getter for the shape point coordinates.
     * @return the shape point coordinates.
     */
    public Point getCoordinates() {
        return coordinates;
    }

    /**
     * Method for setting the shape point coordinates.
     * @param coordinates the point coordinates.
     */
    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Method utilized for the freehand shape, which sets a point coordinate in the freehand
     * point container.
     * @param coordinates the point coordinates.
     */
    public void setFreehandCoordinates(Point coordinates) {
        this.freehandCoordinates.add(coordinates);
    }

    /**
     * Simple getter of the freehand point container.
     * @return the freehand point container.
     */
    public ArrayList<Point> getFreehandCoordinates() {
        return this.freehandCoordinates;
    }

    /**
     * Simple getter for the current width of the shape.
     * @return the current shape width.
     */
    public int getWidth() {
        return size.getX();
    }

    /**
     * Simple getter for the current height of the shape.
     * @return the current shape height.
     */
    public int getHeight() {
        return size.getY();
    }

    /**
     * Method for setting the current size of the shape, (height and width).
     * @param size the current shape size.
     */
    public void setSize(Point size) {
        this.size = size;
    }

    /**
     * Method for setting the current thickness of the shape.
     * @param thickness the current thickness.
     */
    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    /**
     * Simple getter for the current thickness of the shape.
     * @return the shape thickness.
     */
    public float getThickness() {
        return thickness;
    }

    /**
     * Method for setting the current color of the shape.
     * @param color the shape color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Simple getter for the current color of the shape.
     * @return the shape color.
     */
    public Color getColor() {
        return color;
    }
}
