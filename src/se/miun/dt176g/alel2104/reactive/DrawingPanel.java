package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
	private Observable<MouseEvent> mouseReleasedEvent;
	private Observable<MouseEvent> mousePressedEvent;
	private Observable<MouseEvent> mouseEventObservable;
	private Point initialPoint;
	private Point initialPoint2;
	private Shape currentShape;

	public DrawingPanel() {
		drawing = new Drawing();

		setMouseEventsObservable();
	}

	private void InitializeShape(Point shapeSize, MouseEvent mouseEvent) {

		if (currentShape != null) {
			if (currentShape.getClass().equals(Rectangle.class)) {
				Rectangle rectangle = new Rectangle();
				rectangle.setCoordinates(initialPoint);
				rectangle.setSize(shapeSize);
				getDrawing().addShape(rectangle);
				redraw();
			} else if (currentShape.getClass().equals(Oval.class)) {
				Oval oval = new Oval();
				oval.setCoordinates(initialPoint);
				oval.setSize(shapeSize);
				getDrawing().addShape(oval);
				redraw();
			} else if (currentShape.getClass().equals(Line.class)) {
				Line line = new Line();
				line.setCoordinates(initialPoint);
				line.setSize(shapeSize);
				getDrawing().addShape(line);
				redraw();
			}
		}
	}

	private void setInitialPoint(int firstPointX, int firstPointY, int secondPointX, int secondPointY) {
		this.initialPoint = new Point(Math.min(firstPointX, secondPointX), Math.min(firstPointY, secondPointY));
	}

	private Point getInitialPoint(int firstPointX, int firstPointY, int secondPointX, int secondPointY) {
		return new Point(Math.min(firstPointX, secondPointX), Math.min(firstPointY, secondPointY));
	}
	
	public void redraw() {
		repaint();
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

	private Point getShapeSize(Point firstPoint, Point secondPoint) {
		return new Point(Math.abs(secondPoint.getX() - firstPoint.getX()), Math.abs(secondPoint.getY() - firstPoint.getY()));
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

//				@Override
//				public void mouseDragged(MouseEvent e) {
//					emitter.onNext(e);
//					super.mouseDragged(e);
//				}
			};
			this.addMouseListener(mouseAdapter);
			this.addMouseMotionListener(mouseAdapter);

			emitter.setCancellable(() -> {
				removeMouseListener(mouseAdapter);
				removeMouseMotionListener(mouseAdapter);
			});
		});

		mouseEventObservable.filter(e -> e.getID() == MouseEvent.MOUSE_PRESSED)
				.map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
				.flatMap(this::createShape)
				.doOnNext(shape -> {
					getDrawing().addShape(shape);
					redraw();
				})
				.subscribe();

	}

	private Observable<Shape> createShape(Point startPoint) {
		return mouseEventObservable
				.takeUntil(mouseEvent -> mouseEvent.getID() == MouseEvent.MOUSE_RELEASED)
				.map(mouseEvent -> new Point(mouseEvent.getX(), mouseEvent.getY()))
				.map(endPoint -> {
					Point initialPoint = getInitialPoint(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
					Point pointSize = getShapeSize(startPoint, new Point(endPoint.getX(), endPoint.getY()));

					if (currentShape instanceof Rectangle) {
						currentShape = new Rectangle();
						currentShape.setCoordinates(initialPoint);
						currentShape.setSize(pointSize);
					} else if (currentShape instanceof Oval) {
						currentShape = new Oval();
						currentShape.setCoordinates(initialPoint);
						currentShape.setSize(pointSize);
					} else if (currentShape instanceof Line) {
						currentShape = new Line();
						currentShape.setCoordinates(new Point(startPoint.getX(), startPoint.getY()));
						currentShape.setSize(new Point(endPoint.getX(), endPoint.getY()));
					}
					return currentShape;
				});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawing.draw(g);
	}
}
