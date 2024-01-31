package reactive_paint.support;

/**
 * <h1>Constants</h1>
 * Utility interface used to define constant values.
 *
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public interface Constants {
    long MENU_THIN_THICKNESS = 1;
    long MENU_MEDIUM_THICKNESS = 4;
    long MENU_THICK_THICKNESS = 8;
    int DEFAULT_WINDOW_WIDTH = 1200;
    int DEFAULT_WINDOW_HEIGHT = 900;
    int SERVER_PORT = 12345;
    String HOST = "localhost";
    String HEADER = "Reactive Paint";
    String CLEAR_CANVAS_MESSAGE = "Are you sure you want to clear the canvas?\n"
            + "If you are connected to/or hosting a server it will clear all canvases!";
    String CONNECT_TO_SERVER_ERROR_MESSAGE = "Could not connect to server: ";
    String HOST_SERVER_ERROR_MESSAGE = "Could not host server: ";
    String CONNECTION_LOST_MESSAGE = "Connection lost to server!";
    String CLEAR_CANVAS_EVENT = "CLEAR_CANVAS";
}
