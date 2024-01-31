package reactive_paint;

import reactive_paint.gui.Menu;

import java.io.Serializable;

/**
 * <h1>Event</h1>
 * Represents the events of the application.
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Event implements Serializable {
    private final String currentEvent;
    private int clientHashCode = 0;

    /**
     * Constructor to initialize the current event.
     * @param event the current event.
     */
    public Event(final String event) {
        this.currentEvent = event;
    }

    /**
     * Simple getter for the current event.
     * @return the current event.
     */
    public String getCurrentEvent() {
        return currentEvent;
    }

    /**
     * Method for executing the clear canvas event.
     * @param menu the menu.
     */
    public void clearCanvasEvent(final Menu menu) {
        menu.clearCanvasEvent();
    }

    /**
     * Simple getter for the client hashCode, used by server to prevent
     * sending event back to the sending client.
     * @return the client hashCode.
     */
    public int getClientHashCode() {
        return clientHashCode;
    }

    /**
     * Simple setter for the client hashCode.
     * @param clientHashCode the client hashCode.
     */
    public void setClientHashCode(final int clientHashCode) {
        this.clientHashCode = clientHashCode;
    }
}
