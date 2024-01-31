package reactive_paint;

/**
 * <h1>Drawable</h1>
 * Functional interface for painting components.
 *
 * @author  --Albin Eliasson--
 * @version 1.0
 * @since   2023-10-07
 */
@FunctionalInterface
interface Drawable {
	/**
	 * Method for painting components.
	 * @param g graphics.
	 */
	void draw(java.awt.Graphics g);
}
