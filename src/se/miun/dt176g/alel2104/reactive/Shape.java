package se.miun.dt176g.alel2104.reactive;

/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * <p>
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 * 
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

public abstract class Shape implements Drawable {
    // private member : some container storing coordinates
    private Point coordinates;
    private Point size;
    private float thickness;

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }

    public int getWidth() {
        return size.getX();
    }

    public int getHeight() {
        return size.getY();
    }

    public void setSize(Point size) {
        this.size = size;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public float getThickness() {
        return thickness;
    }
}
