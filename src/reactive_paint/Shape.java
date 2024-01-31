package reactive_paint;

import java.awt.Color;
import java.io.Serializable;
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
public abstract class Shape implements Drawable, Serializable {
    // private member : some container storing coordinates
    private Point coordinates;
    private Point size;
    private final ArrayList<Point> freehandCoordinates = new ArrayList<>();
    private float thickness;
    private Color color;
    private int clientHashCode = 0;

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
    public void setCoordinates(final Point coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Method utilized for the freehand shape, which sets a point coordinate in the freehand
     * point container.
     * @param coordinates the point coordinates.
     */
    public void setFreehandCoordinates(final Point coordinates) {
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
    public void setSize(final Point size) {
        this.size = size;
    }

    /**
     * Method for setting the current thickness of the shape.
     * @param thickness the current thickness.
     */
    public void setThickness(final float thickness) {
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
    public void setColor(final Color color) {
        this.color = color;
    }

    /**
     * Simple getter for the current color of the shape.
     * @return the shape color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Simple getter for the client hashCode, used by server to prevent sending the shape back to
     * the sending client.
     * @return the client hashCode.
     */
    public int getClientHashCode() {
        return clientHashCode;
    }

    /**
     * Simple setter for the client hashCode.
     * @param clientHashCode the client hashCode.
     */
    public void setClientHashCode(final int clientHashCode) {
        this.clientHashCode = clientHashCode;
    }
}
