package se.miun.dt176g.alel2104.reactive.gui;

import se.miun.dt176g.alel2104.reactive.EventObservable;
import se.miun.dt176g.alel2104.reactive.shapes.Freehand;
import se.miun.dt176g.alel2104.reactive.shapes.Line;
import se.miun.dt176g.alel2104.reactive.shapes.Oval;
import se.miun.dt176g.alel2104.reactive.shapes.Rectangle;
import se.miun.dt176g.alel2104.reactive.support.Constants;

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
 * The menu of the program.
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Menu extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private JLabel currentShapeLabel;

	/**
	 * Constructor to initialize the menu swing components.
	 * @param frame the window frame.
	 */
	public Menu(MainFrame frame) {
		init(frame);
	}

	/**
	 * Method for initializing all the menu swing components.
	 * @param frame the window frame.
	 */
	private void init(MainFrame frame) {
		currentShapeLabel = new JLabel();
		currentShapeLabel.setForeground(Color.DARK_GRAY);

		initOptionMenu(frame);
		initDrawMenu();
		initThicknessMenu();
		initColorMenu();

		this.add(Box.createHorizontalGlue());
		this.add(currentShapeLabel);
	}

	/**
	 * Method for initializing the general option menu.
	 * @param frame the window frame.
	 */
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

	/**
	 * Method for initializing the thickness options menu.
	 */
	private void initThicknessMenu() {
		JMenu thicknessMenu = new JMenu("Thickness");
		ButtonGroup thicknessButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem thin = new JRadioButtonMenuItem("Thin");
		JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
		JRadioButtonMenuItem thick = new JRadioButtonMenuItem("Thick");

		// Set thin option as initial selected
		thin.setSelected(true);
		EventObservable.setCurrentThicknessSubject(Constants.menuThinThickness);

		EventObservable.getItemEventsObservable(thin)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentThicknessSubject(Constants.menuThinThickness));

		EventObservable.getItemEventsObservable(medium)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentThicknessSubject(Constants.menuMediumThickness));

		EventObservable.getItemEventsObservable(thick)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentThicknessSubject(Constants.menuThickThickness));

		thicknessMenu.add(thin);
		thicknessMenu.add(medium);
		thicknessMenu.add(thick);

		thicknessButtonGroup.add(thin);
		thicknessButtonGroup.add(medium);
		thicknessButtonGroup.add(thick);

		this.add(thicknessMenu);
	}

	/**
	 * Method for initializing the shape options menu.
	 */
	private void initDrawMenu() {
		JMenu drawMenu = new JMenu("Painting options");
		ButtonGroup drawButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem drawRectangle = new JRadioButtonMenuItem("Rectangle");
		JRadioButtonMenuItem drawOval = new JRadioButtonMenuItem("Oval");
		JRadioButtonMenuItem drawLine = new JRadioButtonMenuItem("Line");
		JRadioButtonMenuItem drawFreehand = new JRadioButtonMenuItem("Freehand");

		// Set Rectangle option as initial selected
		drawRectangle.setSelected(true);
		setCurrentShapeText("Drawing: Rectangle");
		EventObservable.setCurrentShapeSubject(new Rectangle());

		EventObservable.getItemEventsObservable(drawRectangle)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.map(itemEvent -> new Rectangle())
				.subscribe(rectangle -> {
					EventObservable.setCurrentShapeSubject(rectangle);
					setCurrentShapeText("Drawing: Rectangle");
				});

		EventObservable.getItemEventsObservable(drawOval)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.map(integer -> new Oval())
				.subscribe(oval -> {
					EventObservable.setCurrentShapeSubject(oval);
					setCurrentShapeText("Drawing: Oval");
				});

		EventObservable.getItemEventsObservable(drawLine)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.map(integer -> new Line())
				.subscribe(line -> {
					EventObservable.setCurrentShapeSubject(line);
					setCurrentShapeText("Drawing: Line");
				});

		EventObservable.getItemEventsObservable(drawFreehand)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.map(integer -> new Freehand())
				.subscribe(freehand -> {
					EventObservable.setCurrentShapeSubject(freehand);
					setCurrentShapeText("Drawing: Freehand");
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

	/**
	 * Method for initializing the color options' menu.
	 */
	private void initColorMenu() {
		JMenu colorMenu = new JMenu("Color");
		ButtonGroup colorButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem colorBlack = new JRadioButtonMenuItem("Black");
		JRadioButtonMenuItem colorRed = new JRadioButtonMenuItem("Red");
		JRadioButtonMenuItem colorBlue = new JRadioButtonMenuItem("Blue");

		// Set black color as initial color
		colorBlack.setSelected(true);
		EventObservable.setCurrentColorSubject(Color.BLACK);

		EventObservable.getItemEventsObservable(colorBlack)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentColorSubject(Color.BLACK));


		EventObservable.getItemEventsObservable(colorRed)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentColorSubject(Color.RED));


		EventObservable.getItemEventsObservable(colorBlue)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentColorSubject(Color.BLUE));

		colorMenu.add(colorBlack);
		colorMenu.add(colorRed);
		colorMenu.add(colorBlue);

		colorButtonGroup.add(colorBlack);
		colorButtonGroup.add(colorRed);
		colorButtonGroup.add(colorBlue);

		this.add(colorMenu);
	}

	/**
	 * Method for creating and executing a clear canvas event with an option dialog.
	 * @param frame the window frame.
	 */
	private void clearCanvasEvent(MainFrame frame) {
		int dialogResult = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear the canvas?",
				"Reactive Paint" ,JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (dialogResult == JOptionPane.YES_OPTION) {
			frame.getDrawingPanel().clearCanvas();
		}
	}

	/**
	 * Method for setting the current shape JLabel text.
	 * @param text the current shape selected.
	 */
	private void setCurrentShapeText(String text) {
		currentShapeLabel.setText(text);
	}
}
