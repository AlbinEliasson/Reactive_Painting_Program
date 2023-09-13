package se.miun.dt176g.alel2104.reactive;

import javax.swing.SwingUtilities;


/**
* <h1>AppStart</h1>
*
* @author  --Albin Eliasson--
* @version 1.0
* @since   2022-09-08
*/
public class AppStart {

	public static void main(String[] args) {
		
		// Make sure GUI is created on the event dispatching thread
		SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
	}
}
