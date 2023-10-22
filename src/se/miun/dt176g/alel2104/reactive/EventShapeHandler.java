package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.core.Observable;
import se.miun.dt176g.alel2104.reactive.gui.DrawingPanel;
import se.miun.dt176g.alel2104.reactive.shapes.Freehand;
import se.miun.dt176g.alel2104.reactive.shapes.Line;
import se.miun.dt176g.alel2104.reactive.shapes.Oval;
import se.miun.dt176g.alel2104.reactive.shapes.Rectangle;

import java.awt.Color;
import java.awt.event.MouseEvent;

/**
 * <h1>EventShapeHandler</h1>
 * The event shape handler component which utilizes and handles mouse, shape, color and thickness events.
 *
 * @author  --Albin Eliasson--
 * @version 1.0
 * @since   2023-10-07
 */
public class EventShapeHandler {
    private final DrawingPanel drawingPanel;
    private final Observable<MouseEvent> mouseEventObservable;
    private Shape currentShape;
    private float currentThickness;
    private Color currentColor;

    /**
     * Constructor which initialized the drawing panel,
     * as well as the mouse event, shape event and color event listeners.
     * @param drawingPanel the drawing panel component.
     */
    public EventShapeHandler(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        mouseEventObservable = EventObservable.getMouseEventsObservable(drawingPanel);

        setShapeListener();
        setThicknessListener();
        setColorListener();
    }

    /**
     * Method for handling and utilizing the mouse events to update and store the current shape.
     */
    public void handleMouseEvents() {
        mouseEventObservable
                .doOnNext(mouseEvent -> {
                    if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                        currentShape = resetShape(currentShape);
                        drawingPanel.getDrawing().addShape(currentShape);
                    } else if (mouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
                        if (drawingPanel.getClient() != null) {
                            drawingPanel.getClient().sendShape(currentShape);
                        }
                    }
                })
                .filter(mouseEvent -> mouseEvent.getID() == MouseEvent.MOUSE_PRESSED)
                .map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
                .switchMap(this::getShape)
                .subscribe();
    }

    /**
     * Method for accessing the update shape from the starting and current point.
     * @param startPoint the start point, which is the first point where the user clicked.
     * @return an observable containing the updated shape.
     */
    private Observable<Shape> getShape(Point startPoint) {
        return mouseEventObservable
                .takeUntil(mouseEvent -> mouseEvent.getID() == MouseEvent.MOUSE_RELEASED)
                .map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
                .map(currentPoint -> getUpdatedShape(startPoint, currentPoint));
    }

    /**
     * Method for updating and redrawing the current shape with the starting and current point.
     * @param startPoint the start point, which is the first point where the user clicked.
     * @param currentPoint the current position point of the mouse.
     * @return an updated shape with new coordinates and size.
     */
    private Shape getUpdatedShape(Point startPoint, Point currentPoint) {
        if (currentShape instanceof Line) {
            currentShape.setCoordinates(startPoint);
            currentShape.setSize(currentPoint);

        } else if (currentShape instanceof Freehand) {
            if (currentShape.getFreehandCoordinates().isEmpty()) {
                currentShape.setFreehandCoordinates(startPoint);
            } else {
                currentShape.setFreehandCoordinates(currentPoint);
            }

        } else {
            Point initialPoint = getInitialPoint(startPoint.getX(), startPoint.getY(), currentPoint.getX(), currentPoint.getY());
            Point pointSize = getShapeSize(startPoint, currentPoint);
            currentShape.setCoordinates(initialPoint);
            currentShape.setSize(pointSize);
        }
        currentShape.setThickness(currentThickness);
        currentShape.setColor(currentColor);
        drawingPanel.redraw();
        return currentShape;
    }

    /**
     * Helper method for calculating the initial point when painting shapes like ovals and rectangles.
     * @param firstPointX the first points x-coordinate.
     * @param firstPointY the first points y-coordinate.
     * @param secondPointX the current points x-coordinate.
     * @param secondPointY the current points y-coordinate.
     * @return the new initial point.
     */
    private Point getInitialPoint(int firstPointX, int firstPointY, int secondPointX, int secondPointY) {
        return new Point(Math.min(firstPointX, secondPointX), Math.min(firstPointY, secondPointY));
    }

    /**
     * Helper method for calculating the shape size point when painting shapes like ovals and rectangles.
     * @param firstPoint the start point, which is the first point where the user clicked.
     * @param secondPoint the current position point of the mouse.
     * @return the new shape size point.
     */
    private Point getShapeSize(Point firstPoint, Point secondPoint) {
        return new Point(Math.abs(secondPoint.getX() - firstPoint.getX()), Math.abs(secondPoint.getY() - firstPoint.getY()));
    }

    /**
     * Method for resetting the current shape for the next drawing event.
     * @param currentShape the current shape.
     * @return new instance of the current shape.
     */
    private Shape resetShape(Shape currentShape) {
        if (currentShape instanceof Rectangle) {
            currentShape = new Rectangle();

        } else if (currentShape instanceof Oval) {
            currentShape = new Oval();

        } else if (currentShape instanceof Line) {
            currentShape = new Line();

        } else if (currentShape instanceof Freehand) {
            currentShape = new Freehand();
        }
        return currentShape;
    }

    /**
     * Method for setting the current shape from the shape subject.
     */
    private void setShapeListener() {
        EventObservable.getCurrentShape()
                .subscribe(this::setCurrentShape);
    }

    /**
     * Method for setting the current thickness from the thickness subject.
     */
    private void setThicknessListener() {
        EventObservable.getCurrentThickness()
                .subscribe(this::setCurrentThickness);
    }

    /**
     * Method for setting the current color from the color subject.
     */
    private void setColorListener() {
        EventObservable.getCurrentColor()
                .subscribe(this::setCurrentColor);
    }

    /**
     * Method for setting the current shape.
     * @param shape the current shape.
     */
    private void setCurrentShape(Shape shape) {
        this.currentShape = shape;
    }

    /**
     * Method for setting the current thickness.
     * @param thickness the current thickness.
     */
    private void setCurrentThickness(float thickness) {
        this.currentThickness = thickness;
    }

    /**
     * Method for setting the current color.
     * @param currentColor the current color.
     */
    private void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }
}

