package se.miun.dt176g.alel2104.reactive;

import se.miun.dt176g.alel2104.reactive.shapes.Line;
import se.miun.dt176g.alel2104.reactive.shapes.Oval;
import se.miun.dt176g.alel2104.reactive.shapes.Rectangle;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.ComponentOrientation;


/**
 * <h1>Menu</h1> 
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */
public class Menu extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private JMenuItem drawRectangle;
	private JMenuItem drawOval;
	private JMenuItem drawLine;
	private JLabel currentShape;

	
	public Menu(MainFrame frame) {
		init(frame);
	}
	
	private void init(MainFrame frame) {
		
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("Some Menu category");
		this.add(menu);


		menuItem = new JMenuItem("Some menu item 1");
		menuItem.addActionListener(e -> anEvent(frame));
		menu.add(menuItem);

		drawRectangle = new JMenuItem("Draw rectangle");
		drawRectangle.addActionListener(e ->  RectangleEvent(frame));
		menu.add(drawRectangle);

		drawOval = new JMenuItem("Draw Oval");
		drawOval.addActionListener(e ->  OvalEvent(frame));
		menu.add(drawOval);

		drawLine = new JMenuItem("Draw Line");
		drawLine.addActionListener(e -> LineEvent(frame));
		menu.add(drawLine);

		currentShape = new JLabel();
		currentShape.setForeground(Color.DARK_GRAY);
		this.add(Box.createHorizontalGlue());
		this.add(currentShape);
	}

	private void anEvent(MainFrame frame) {
	
		String message = (String) JOptionPane.showInputDialog(frame,
				"Send message to everyone:");
		
		if(message != null && !message.isEmpty()) {
			JOptionPane.showMessageDialog(frame, message);
		}
	}
	
	private void RectangleEvent(MainFrame frame) {
		frame.getDrawingPanel().setCurrentShape(new Rectangle());
		drawRectangle.setEnabled(false);
		drawOval.setEnabled(true);
		drawLine.setEnabled(true);
		currentShape.setText("Drawing: Rectangle");
	}

	private void OvalEvent(MainFrame frame) {
		frame.getDrawingPanel().setCurrentShape(new Oval());
		drawOval.setEnabled(false);
		drawRectangle.setEnabled(true);
		drawLine.setEnabled(true);
		currentShape.setText("Drawing: Oval");
	}

	private void LineEvent(MainFrame frame) {
		frame.getDrawingPanel().setCurrentShape(new Line());
		drawLine.setEnabled(false);
		drawRectangle.setEnabled(true);
		drawOval.setEnabled(true);
		currentShape.setText("Drawing: Line");
	}
}
