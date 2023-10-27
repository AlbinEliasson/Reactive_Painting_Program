package se.miun.dt176g.alel2104.reactive.gui;

import se.miun.dt176g.alel2104.reactive.Drawing;
import se.miun.dt176g.alel2104.reactive.EventShapeHandler;

import javax.swing.JPanel;
import java.awt.Graphics;

/**
 * <h1>DrawingPanel</h1>
 * Creates a Canvas-object for displaying all graphics already drawn.
 *
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */

public class DrawingPanel extends JPanel {
	private Drawing drawing;
	private final EventShapeHandler eventShapeHandler;

	/**
	 * Constructor for initializing the eventShapeHandler which handles mouse/shape events.
	 */
	public DrawingPanel() {
		drawing = new Drawing();
		eventShapeHandler = new EventShapeHandler(this);
		eventShapeHandler.handleMouseEvents();
	}

	/**
	 * Simple setter method for the drawing component.
	 * @param drawing the drawing component.
	 */
	public void setDrawing(final Drawing drawing) {
		this.drawing = drawing;
		repaint();
	}

	/**
	 * Simple getter for the drawing component.
	 * @return the drawing component.
	 */
	public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * Simple getter for the eventShapeHandler component.
	 * @return the eventShapeHandler component.
	 */
	public EventShapeHandler getEventShapeHandler() {
		return eventShapeHandler;
	}

	/**
	 * Method for repainting the canvas.
	 */
	public void redraw() {
		repaint();
	}

	/**
	 * Method for removing stored shapes in the drawing component.
	 */
	public void clearCanvas() {
		getDrawing().clearShapes();
		redraw();
	}

	/**
	 * Method for painting the stored shapes in the drawing component.
	 * @param g the <code>Graphics</code> object to protect
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		drawing.draw(g);
	}
}
