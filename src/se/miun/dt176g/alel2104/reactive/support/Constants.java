package se.miun.dt176g.alel2104.reactive.support;

/**
 * <h1>Constants</h1>
 * Utility interface used to define constant values.
 *
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public interface Constants {
    long menuThinThickness = 1;
    long menuMediumThickness = 4;
    long menuThickThickness = 8;
    int defaultWindowWidth = 1200;
    int defaultWindowHeight = 900;
    int serverPort = 12345;
    String host = "localhost";
    String CONNECT_TO_SERVER_ERROR_MESSAGE = "Could not connect to server: ";
    String HOST_SERVER_ERROR_MESSAGE = "Could not host server: ";
    String CONNECTION_LOST_MESSAGE = "Connection lost to server!";
}
