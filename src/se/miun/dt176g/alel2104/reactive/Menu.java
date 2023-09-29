package se.miun.dt176g.alel2104.reactive;

import se.miun.dt176g.alel2104.reactive.shapes.Line;
import se.miun.dt176g.alel2104.reactive.shapes.Oval;
import se.miun.dt176g.alel2104.reactive.shapes.Rectangle;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.event.ItemEvent;


/**
 * <h1>Menu</h1> 
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */
public class Menu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private JLabel currentShape;
	private final long thinLine = 1;
	private final long mediumLine = 4;
	private final long thickLine = 8;

	public Menu(MainFrame frame) {
		init(frame);
	}
	
	private void init(MainFrame frame) {
		JMenu drawMenu = new JMenu("Choose painting option");
		this.add(drawMenu);

		JMenu thicknessMenu = new JMenu("Choose thickness");
		this.add(thicknessMenu);

		currentShape = new JLabel();
		currentShape.setForeground(Color.DARK_GRAY);

		ButtonGroup thicknessButtonGroup = new ButtonGroup();
		JRadioButtonMenuItem thin = new JRadioButtonMenuItem("Thin");
		thin.setSelected(true);
		thin.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				frame.getDrawingPanel().setCurrentThickness(thinLine);
			}
		});

		JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
		medium.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				frame.getDrawingPanel().setCurrentThickness(mediumLine);
			}
		});

		JRadioButtonMenuItem thick = new JRadioButtonMenuItem("Thick");
		thick.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				frame.getDrawingPanel().setCurrentThickness(thickLine);
			}
		});

		thicknessMenu.add(thin);
		thicknessMenu.add(medium);
		thicknessMenu.add(thick);

		thicknessButtonGroup.add(thin);
		thicknessButtonGroup.add(medium);
		thicknessButtonGroup.add(thick);

		ButtonGroup drawButtonGroup = new ButtonGroup();
		JRadioButtonMenuItem drawRectangle = new JRadioButtonMenuItem("Rectangle");
		drawRectangle.setSelected(true);

		drawRectangle.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				rectangleEvent(frame);
			}
		});
		rectangleEvent(frame);

		JRadioButtonMenuItem drawOval = new JRadioButtonMenuItem("Oval");
		drawOval.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				ovalEvent(frame);
			}
		});

		JRadioButtonMenuItem drawLine = new JRadioButtonMenuItem("Line");
		drawLine.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				lineEvent(frame);
			}
		});

		drawMenu.add(drawRectangle);
		drawMenu.add(drawOval);
		drawMenu.add(drawLine);
		drawButtonGroup.add(drawRectangle);
		drawButtonGroup.add(drawOval);
		drawButtonGroup.add(drawLine);

		this.add(Box.createHorizontalGlue());
		this.add(currentShape);
	}
	
	private void rectangleEvent(MainFrame frame) {
		frame.getDrawingPanel().setCurrentShape(new Rectangle());
		currentShape.setText("Drawing: Rectangle");
	}

	private void ovalEvent(MainFrame frame) {
		frame.getDrawingPanel().setCurrentShape(new Oval());
		currentShape.setText("Drawing: Oval");
	}

	private void lineEvent(MainFrame frame) {
		frame.getDrawingPanel().setCurrentShape(new Line());
		currentShape.setText("Drawing: Line");
	}
}
