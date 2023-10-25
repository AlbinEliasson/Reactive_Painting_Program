package se.miun.dt176g.alel2104.reactive;

import se.miun.dt176g.alel2104.reactive.gui.Menu;

import java.io.Serializable;

public class Event implements Serializable {
    private final String currentEvent;
    private int clientHashCode = 0;

    public Event(String event) {
        this.currentEvent = event;
    }

    public String getCurrentEvent() {
        return currentEvent;
    }

    public void clearCanvasEvent(Menu menu) {
        menu.clearCanvasEvent();
    }

    public int getClientHashCode() {
        return clientHashCode;
    }

    public void setClientHashCode(int clientHashCode) {
        this.clientHashCode = clientHashCode;
    }
}
