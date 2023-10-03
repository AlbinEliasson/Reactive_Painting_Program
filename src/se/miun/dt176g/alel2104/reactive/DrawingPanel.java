package se.miun.dt176g.alel2104.reactive;

import java.awt.*;
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
	private final EventShapeHandler eventShapeHandler;

	public DrawingPanel() {
		drawing = new Drawing();
		eventShapeHandler = new EventShapeHandler(this);
		eventShapeHandler.handleMouseEvents();
	}

	public void setDrawing(Drawing d) {
		drawing = d;
		repaint();
	}

	public Drawing getDrawing() {
		return drawing;
	}

	public EventShapeHandler getEventShapeHandler() {
		return eventShapeHandler;
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
