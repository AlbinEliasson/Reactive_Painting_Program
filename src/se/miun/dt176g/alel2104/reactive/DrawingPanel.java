package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
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
	private Point firstPoint;

	public DrawingPanel() {
		drawing = new Drawing();

		getFirstPoint().subscribe(point -> getSecondPoint().subscribe(point1 -> {
			Rectangle rectangle = new Rectangle();
			rectangle.setCoordinates(getInitialPoint(point, point1));
			rectangle.setSize(getShapeSize(point, point1));
			getDrawing().addShape(rectangle);
			redraw();
		}));
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

	private Point getInitialPoint(Point firstPoint, Point secondPoint) {
		return new Point(Math.min(firstPoint.getX(), secondPoint.getX()), Math.min(firstPoint.getY(), secondPoint.getY()));
	}

	private Point getShapeSize(Point firstPoint, Point secondPoint) {
		return new Point(Math.abs(secondPoint.getX() - firstPoint.getX()), Math.abs(secondPoint.getY() - firstPoint.getY()));
	}

	public @NonNull Observable<Point> getFirstPoint() {
		return Observable.create(emitter -> {
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					emitter.onNext(new Point(e.getX(), e.getY()));
					//emitter.onComplete();
					super.mousePressed(e);
				}
			});
		});
	}

	public @NonNull Observable<Point> getSecondPoint() {
		return Observable.create(emitter -> {
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					emitter.onNext(new Point(e.getX(), e.getY()));
					//emitter.onComplete();
					super.mouseReleased(e);
				}
			});
		});
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		drawing.draw(g);
	}

//	private @NonNull Observable<Object> mouseListenerObservable() {
//		return Observable.create(emitter -> {
//			this.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mousePressed(MouseEvent e) {
//					emitter.onNext(e.getX());
//					emitter.onNext(e.getY());
//					super.mousePressed(e);
//				}
//
//				@Override
//				public void mouseDragged(MouseEvent e) {
//					super.mouseDragged(e);
//				}
//
//				@Override
//				public void mouseMoved(MouseEvent e) {
//					super.mouseMoved(e);
//				}
//			});
//		});
//	}
}
