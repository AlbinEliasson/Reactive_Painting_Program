package se.miun.dt176g.alel2104.reactive;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame to contain the rest
 *
 * @author 	--YOUR NAME HERE--
 * @version 1.0
 * @since 	2022-09-08
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private String header;
	private DrawingPanel drawingPanel;

	public MainFrame() {

		// default window-size.
		this.setSize(1200, 900);
		// application closes when the "x" in the upper-right corner is clicked.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Makes sure the window starts at the center of the screen
		this.setLocationRelativeTo(null);

		this.header = "Reactive Paint";
		this.setTitle(header);

		// Changes layout from default to BorderLayout
		this.setLayout(new BorderLayout());

		// Creates all necessary objects and adds them to the MainFrame (just one object right now)
		drawingPanel = new DrawingPanel();
		drawingPanel.setBounds(0, 0, getWidth(), getHeight());
		this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

		this.setJMenuBar(new Menu(this));

	}

	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}
}
