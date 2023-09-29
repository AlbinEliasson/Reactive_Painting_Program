package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.core.Observable;
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
						getDrawing().addShape(currentShape);

					} else if (mouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
						currentShape = resetShape(currentShape);
					}
				})
				.filter(mouseEvent -> mouseEvent.getID() == MouseEvent.MOUSE_PRESSED)
				.map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
				.switchMap(this::updateShape)
				.subscribe();
	}

	private Observable<Shape> updateShape(Point startPoint) {
		return mouseEventObservable
				.takeUntil(mouseEvent -> mouseEvent.getID() == MouseEvent.MOUSE_RELEASED)
				.map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
				.map(currentPoint -> updateCurrentShape(startPoint, currentPoint));
	}

	private Shape updateCurrentShape(Point startPoint, Point currentPoint) {
		Point initialPoint = getInitialPoint(startPoint.getX(), startPoint.getY(), currentPoint.getX(), currentPoint.getY());
		Point pointSize = getShapeSize(startPoint, new Point(currentPoint.getX(), currentPoint.getY()));

		if (currentShape instanceof Line) {
			currentShape.setCoordinates(new Point(startPoint.getX(), startPoint.getY()));
			currentShape.setSize(new Point(currentPoint.getX(), currentPoint.getY()));
		} else {
			currentShape.setCoordinates(initialPoint);
			currentShape.setSize(pointSize);
		}
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

	public void redraw() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawing.draw(g);
	}
}
