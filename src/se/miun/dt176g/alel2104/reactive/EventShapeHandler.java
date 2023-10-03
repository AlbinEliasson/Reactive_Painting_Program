package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.core.Observable;
import se.miun.dt176g.alel2104.reactive.shapes.Freehand;
import se.miun.dt176g.alel2104.reactive.shapes.Line;
import se.miun.dt176g.alel2104.reactive.shapes.Oval;
import se.miun.dt176g.alel2104.reactive.shapes.Rectangle;

import java.awt.Color;
import java.awt.event.MouseEvent;

public class EventShapeHandler {
    private DrawingPanel drawingPanel;
    private Observable<MouseEvent> mouseEventObservable;
    private Shape currentShape;
    private float currentThickness;
    private Color currentColor;

    public EventShapeHandler(DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        mouseEventObservable = EventObservable.getMouseEventsObservable(drawingPanel);
    }

    public void handleMouseEvents() {
        mouseEventObservable
                .doOnNext(mouseEvent -> {
                    if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
                        currentShape = resetShape(currentShape);
                        drawingPanel.getDrawing().addShape(currentShape);
                    }
                })
                .filter(mouseEvent -> mouseEvent.getID() == MouseEvent.MOUSE_PRESSED)
                .map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
                .switchMap(this::getShape)
                .subscribe();
    }

    private Observable<Shape> getShape(Point startPoint) {
        return mouseEventObservable
                .takeUntil(mouseEvent -> mouseEvent.getID() == MouseEvent.MOUSE_RELEASED)
                .map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
                .map(currentPoint -> getUpdatedShape(startPoint, currentPoint));
    }

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
            Point pointSize = getShapeSize(startPoint, new Point(currentPoint.getX(), currentPoint.getY()));
            currentShape.setCoordinates(initialPoint);
            currentShape.setSize(pointSize);
        }
        currentShape.setThickness(currentThickness);
        currentShape.setColor(currentColor);
        drawingPanel.redraw();
        return currentShape;
    }

    private Point getInitialPoint(int firstPointX, int firstPointY, int secondPointX, int secondPointY) {
        return new Point(Math.min(firstPointX, secondPointX), Math.min(firstPointY, secondPointY));
    }

    private Point getShapeSize(Point firstPoint, Point secondPoint) {
        return new Point(Math.abs(secondPoint.getX() - firstPoint.getX()), Math.abs(secondPoint.getY() - firstPoint.getY()));
    }

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

    public void setCurrentShape(Shape shape) {
        this.currentShape = shape;
    }

    public void setCurrentThickness(float thickness) {
        this.currentThickness = thickness;
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }
}

