package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.core.Observable;
import se.miun.dt176g.alel2104.reactive.shapes.Freehand;
import se.miun.dt176g.alel2104.reactive.shapes.Line;
import se.miun.dt176g.alel2104.reactive.shapes.Oval;
import se.miun.dt176g.alel2104.reactive.shapes.Rectangle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * <h1>DrawingPanel</h1> Creates a Canvas-object for displaying all graphics
 * already drawn.
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {
	private Drawing drawing;
	private Observable<MouseEvent> mouseEventObservable;
	private Shape currentShape;
	private float currentThickness;
	private Color currentColor;

	public DrawingPanel() {
		drawing = new Drawing();

		setMouseEventsObservable();
	}

	private void setMouseEventsObservable() {
		mouseEventObservable = Observable.create(emitter -> {
			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					emitter.onNext(e);
					super.mousePressed(e);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					emitter.onNext(e);
					super.mouseReleased(e);
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					emitter.onNext(e);
					super.mouseDragged(e);
				}
			};
			this.addMouseListener(mouseAdapter);
			this.addMouseMotionListener(mouseAdapter);

			emitter.setCancellable(() -> {
				removeMouseListener(mouseAdapter);
				removeMouseMotionListener(mouseAdapter);
			});
		});

		mouseEventObservable
				.doOnNext(mouseEvent -> {
					if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
						currentShape = resetShape(currentShape);
						getDrawing().addShape(currentShape);
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
				System.out.println("EMPTY!");
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
		redraw();
		return currentShape;
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

	private Point getInitialPoint(int firstPointX, int firstPointY, int secondPointX, int secondPointY) {
		return new Point(Math.min(firstPointX, secondPointX), Math.min(firstPointY, secondPointY));
	}

	private Point getShapeSize(Point firstPoint, Point secondPoint) {
		return new Point(Math.abs(secondPoint.getX() - firstPoint.getX()), Math.abs(secondPoint.getY() - firstPoint.getY()));
	}

	public void setDrawing(Drawing d) {
		drawing = d;
		repaint();
	}

	public Drawing getDrawing() {
		return drawing;
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

	public void redraw() {
		repaint();
	}

	public void clearCanvas() {
		getDrawing().clearShapes();
		redraw();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawing.draw(g);
	}
}
