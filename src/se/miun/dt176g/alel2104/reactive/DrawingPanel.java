package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
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
	private Observable<MouseEvent> mouseReleasedEvent;
	private Observable<MouseEvent> mousePressedEvent;
	private Point initialPoint;
	private Shape currentShape;

	public DrawingPanel() {
		drawing = new Drawing();

		setMouseEventsObservable();
	}

	private void InitializeShape(Point shapeSize) {
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
		mousePressedEvent = Observable.create(emitter -> this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				emitter.onNext(e);
				super.mousePressed(e);
			}
		}));

		mouseReleasedEvent = Observable.create(emitter -> this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				emitter.onNext(e);
				super.mouseReleased(e);
			}
		}));

		Observable.zip(mousePressedEvent, mouseReleasedEvent, (p, r) -> {
			setInitialPoint(p.getX(), p.getY(), r.getX(), r.getY());
			return getShapeSize(new Point(p.getX(), p.getY()), new Point(r.getX(), r.getY()));
		}).subscribe(this::InitializeShape);

		// Make 2 zip and merge them later maby?
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		drawing.draw(g);
	}
}
