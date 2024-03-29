package reactive_paint;

import io.reactivex.rxjava3.core.Observable;
import reactive_paint.connect.Client;
import reactive_paint.connect.Server;
import reactive_paint.gui.DrawingPanel;
import reactive_paint.shapes.Freehand;
import reactive_paint.shapes.Line;
import reactive_paint.shapes.Oval;
import reactive_paint.shapes.Rectangle;

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
    private Server currentServer;
    private Client currentClient;
    private boolean isClientActive = false;
    private boolean isServerActive = false;

    /**
     * Constructor to initialize the drawing panel, mouse event, and other listeners.
     * @param drawingPanel the drawing panel component.
     */
    public EventShapeHandler(final DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        mouseEventObservable = EventObservable.getMouseEventsObservable(drawingPanel);

        setShapeListener();
        setThicknessListener();
        setColorListener();
        setServerListener();
        setClientListener();
        setIsClientActiveListener();
        setIsServerActiveListener();
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
                        if (currentClient != null && isClientActive) {
                            currentClient.sendObject(currentShape);
                        } else if (currentServer != null && isServerActive) {
                            currentServer.sendServerObject(currentShape);
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
    private Observable<Shape> getShape(final Point startPoint) {
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
    private Shape getUpdatedShape(final Point startPoint, final Point currentPoint) {
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
    private Point getInitialPoint(final int firstPointX, final int firstPointY,
                                  final int secondPointX, final int secondPointY) {
        return new Point(Math.min(firstPointX, secondPointX), Math.min(firstPointY, secondPointY));
    }

    /**
     * Helper method for calculating the shape size point when painting shapes like ovals and rectangles.
     * @param firstPoint the start point, which is the first point where the user clicked.
     * @param secondPoint the current position point of the mouse.
     * @return the new shape size point.
     */
    private Point getShapeSize(final Point firstPoint, final Point secondPoint) {
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
     * Method for setting the current color from the current color subject.
     */
    private void setColorListener() {
        EventObservable.getCurrentColor()
                .subscribe(this::setCurrentColor);
    }

    /**
     * Method for setting the current server from the current server subject.
     */
    private void setServerListener() {
        EventObservable.getCurrentServer()
                .subscribe(this::setCurrentServer);
    }

    /**
     * Method for setting the current client from the current client subject.
     */
    private void setClientListener() {
        EventObservable.getCurrentClient()
                .subscribe(this::setCurrentClient);
    }

    /**
     * Method for setting if the current client is active from the is client
     * active subject.
     */
    private void setIsClientActiveListener() {
        EventObservable.getIsClientActiveSubject()
                .subscribe(this::setIsClientActive);
    }

    /**
     * Method for setting if the current server is active from the is server
     * active subject.
     */
    private void setIsServerActiveListener() {
        EventObservable.getIsServerActiveSubject()
                .subscribe(this::setIsServerActive);
    }

    /**
     * Method for setting the current shape.
     * @param shape the current shape.
     */
    private void setCurrentShape(final Shape shape) {
        this.currentShape = shape;
    }

    /**
     * Method for setting the current thickness.
     * @param thickness the current thickness.
     */
    private void setCurrentThickness(final float thickness) {
        this.currentThickness = thickness;
    }

    /**
     * Method for setting the current color.
     * @param currentColor the current color.
     */
    private void setCurrentColor(final Color currentColor) {
        this.currentColor = currentColor;
    }

    /**
     * Method for setting the current server.
     * @param server the current server.
     */
    private void setCurrentServer(final Server server) {
        this.currentServer = server;
    }

    /**
     * Method for setting the current client.
     * @param client the current client.
     */
    private void setCurrentClient(final Client client) {
        this.currentClient = client;
    }

    /**
     * Method for setting if the current client is active.
     * @param active true if active.
     */
    private void setIsClientActive(final boolean active) {
        this.isClientActive = active;
    }

    /**
     * Method for setting if the current server is active.
     * @param active true if active.
     */
    private void setIsServerActive(final boolean active) {
        this.isServerActive = active;
    }
}

