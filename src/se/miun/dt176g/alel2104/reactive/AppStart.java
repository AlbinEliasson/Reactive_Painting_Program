package se.miun.dt176g.alel2104.reactive;

import se.miun.dt176g.alel2104.reactive.gui.MainFrame;

import javax.swing.SwingUtilities;

/**
* <h1>AppStart</h1>
* The main staring point of the program.
 *
* @author  --Albin Eliasson--
* @version 1.0
* @since   2023-10-07
*/
public class AppStart {
	/**
	 * Starts the program by creating and showing the mainframe component.
	 * @param args args.
	 */
	public static void main(final String[] args) {
		// Make sure GUI is created on the event dispatching thread
		SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
	}
}
