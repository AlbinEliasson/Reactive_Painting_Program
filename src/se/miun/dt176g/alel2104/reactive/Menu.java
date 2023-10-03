package se.miun.dt176g.alel2104.reactive;

import se.miun.dt176g.alel2104.reactive.shapes.Freehand;
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
import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
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
		currentShape = new JLabel();
		currentShape.setForeground(Color.DARK_GRAY);

		initOptionMenu(frame);
		initDrawMenu(frame);
		initThicknessMenu(frame);
		initColorMenu(frame);

		this.add(Box.createHorizontalGlue());
		this.add(currentShape);
	}

	private void initOptionMenu(MainFrame frame) {
		JMenu optionsMenu = new JMenu("General options");

		JMenuItem clearCanvas = new JMenuItem("Clear canvas");
		clearCanvas.addActionListener(e -> clearCanvasEvent(frame));

		JMenuItem hostServer = new JMenuItem("Host server");
		hostServer.addActionListener(e -> System.out.println("Hosting server"));

		JMenuItem connectToSever = new JMenuItem("Connect to server");
		connectToSever.addActionListener(e -> System.out.println("Connect to server"));

		optionsMenu.add(hostServer);
		optionsMenu.add(connectToSever);
		optionsMenu.add(clearCanvas);
		this.add(optionsMenu);
	}

	private void initThicknessMenu(MainFrame frame) {
		JMenu thicknessMenu = new JMenu("Thickness");
		ButtonGroup thicknessButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem thin = new JRadioButtonMenuItem("Thin");
		thin.setSelected(true);
		thin.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				frame.getDrawingPanel().getEventShapeHandler().setCurrentThickness(thinLine);
			}
		});

		JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
		medium.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				frame.getDrawingPanel().getEventShapeHandler().setCurrentThickness(mediumLine);
			}
		});

		JRadioButtonMenuItem thick = new JRadioButtonMenuItem("Thick");
		thick.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				frame.getDrawingPanel().getEventShapeHandler().setCurrentThickness(thickLine);
			}
		});

		thicknessMenu.add(thin);
		thicknessMenu.add(medium);
		thicknessMenu.add(thick);

		thicknessButtonGroup.add(thin);
		thicknessButtonGroup.add(medium);
		thicknessButtonGroup.add(thick);

		this.add(thicknessMenu);
	}

	private void initDrawMenu(MainFrame frame) {
		JMenu drawMenu = new JMenu("Painting options");
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

		JRadioButtonMenuItem drawFreehand = new JRadioButtonMenuItem("Freehand");
		drawFreehand.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				freehandEvent(frame);
			}
		});

		drawMenu.add(drawRectangle);
		drawMenu.add(drawOval);
		drawMenu.add(drawLine);
		drawMenu.add(drawFreehand);

		drawButtonGroup.add(drawRectangle);
		drawButtonGroup.add(drawOval);
		drawButtonGroup.add(drawLine);
		drawButtonGroup.add(drawFreehand);

		this.add(drawMenu);
	}

	private void initColorMenu(MainFrame frame) {
		JMenu colorMenu = new JMenu("Color");
		ButtonGroup colorButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem colorBlack = new JRadioButtonMenuItem("Black");
		colorBlack.setSelected(true);
		colorBlack.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				colorEvent(frame, "Black");
			}
		});
		colorEvent(frame, "Black");

		JRadioButtonMenuItem colorRed = new JRadioButtonMenuItem("Red");
		colorRed.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				colorEvent(frame, "Red");
			}
		});

		JRadioButtonMenuItem colorBlue = new JRadioButtonMenuItem("Blue");
		colorBlue.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				colorEvent(frame, "Blue");
			}
		});

		colorMenu.add(colorBlack);
		colorMenu.add(colorRed);
		colorMenu.add(colorBlue);

		colorButtonGroup.add(colorBlack);
		colorButtonGroup.add(colorRed);
		colorButtonGroup.add(colorBlue);

		this.add(colorMenu);
	}
	
	private void rectangleEvent(MainFrame frame) {
		frame.getDrawingPanel().getEventShapeHandler().setCurrentShape(new Rectangle());
		currentShape.setText("Drawing: Rectangle");
	}

	private void ovalEvent(MainFrame frame) {
		frame.getDrawingPanel().getEventShapeHandler().setCurrentShape(new Oval());
		currentShape.setText("Drawing: Oval");
	}

	private void lineEvent(MainFrame frame) {
		frame.getDrawingPanel().getEventShapeHandler().setCurrentShape(new Line());
		currentShape.setText("Drawing: Line");
	}

	private void freehandEvent(MainFrame frame) {
		frame.getDrawingPanel().getEventShapeHandler().setCurrentShape(new Freehand());
		currentShape.setText("Drawing: Freehand");
	}

	private void colorEvent(MainFrame frame, String color) {
		switch (color) {
			case "Black" -> frame.getDrawingPanel().getEventShapeHandler().setCurrentColor(Color.BLACK);
			case "Red" -> frame.getDrawingPanel().getEventShapeHandler().setCurrentColor(Color.RED);
			case "Blue" -> frame.getDrawingPanel().getEventShapeHandler().setCurrentColor(Color.BLUE);
		}
	}

	private void clearCanvasEvent(MainFrame frame) {
		int dialogResult = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear the canvas?",
				"Reactive Paint" ,JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (dialogResult == JOptionPane.YES_OPTION) {
			frame.getDrawingPanel().clearCanvas();
		}
	}
}
