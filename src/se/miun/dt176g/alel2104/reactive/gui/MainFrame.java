package se.miun.dt176g.alel2104.reactive.gui;

import se.miun.dt176g.alel2104.reactive.support.Constants;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame which is the window frame of the program.
 *
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */

public class MainFrame extends JFrame {
	private final DrawingPanel drawingPanel;
	private final Menu menu;

	/**
	 * Constructor to set window preferences and adding swing components.
	 */
	public MainFrame() {

		// default window-size.
		this.setSize(Constants.DEFAULT_WINDOW_WIDTH, Constants.DEFAULT_WINDOW_HEIGHT);
		// application closes when the "x" in the upper-right corner is clicked.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Makes sure the window starts at the center of the screen
		this.setLocationRelativeTo(null);

		this.setTitle(Constants.HEADER);

		// Changes layout from default to BorderLayout
		this.setLayout(new BorderLayout());

		// Creates all necessary objects and adds them to the MainFrame (just one object right now)
		drawingPanel = new DrawingPanel();
		drawingPanel.setBounds(0, 0, getWidth(), getHeight());
		this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

		menu = new Menu(this);
		this.setJMenuBar(menu);
	}

	/**
	 * Simple getter for the drawing JPanel component.
	 * @return the drawing JPanel component.
	 */
	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}

	/**
	 * Simple getter for the menu JMenuBar component.
	 * @return the menu JMenuBar component.
	 */
	public Menu getMenu() {
		return menu;
	}
}
