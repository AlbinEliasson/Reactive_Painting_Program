package reactive_paint;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import reactive_paint.connect.Client;
import reactive_paint.connect.Server;
import reactive_paint.gui.DrawingPanel;

import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <h1>EventObservable</h1>
 * The event observable utility component which is utilized for
 * creating observables from events.
 * @author  --Albin Eliasson--
 * @version 1.0
 * @since   2023-10-07
 */
public class EventObservable {
    private static final PublishSubject<Shape> currentShapeSubject = PublishSubject.create();
    private static final PublishSubject<Long> currentThicknessSubject = PublishSubject.create();
    private static final PublishSubject<Color> currentColorSubject = PublishSubject.create();
    private static final PublishSubject<Server> currentServerSubject = PublishSubject.create();
    private static final PublishSubject<Client> currentClientSubject = PublishSubject.create();
    private static final PublishSubject<Boolean> isClientActiveSubject = PublishSubject.create();
    private static final PublishSubject<Boolean> isServerActiveSubject = PublishSubject.create();
    private static final PublishSubject<Event> currentEventSubject = PublishSubject.create();

    /**
     * Method for setting a mouse listener and returning an observable of the events.
     * @param drawingPanel the drawingPanel component.
     * @return an observable of mouse events.
     */
    public static Observable<MouseEvent> getMouseEventsObservable(final DrawingPanel drawingPanel) {
        return Observable.create(emitter -> {
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    emitter.onNext(e);
                    super.mousePressed(e);
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    emitter.onNext(e);
                    super.mouseReleased(e);
                }

                @Override
                public void mouseDragged(final MouseEvent e) {
                    emitter.onNext(e);
                    super.mouseDragged(e);
                }
            };
            drawingPanel.addMouseListener(mouseAdapter);
            drawingPanel.addMouseMotionListener(mouseAdapter);

            emitter.setCancellable(() -> {
                drawingPanel.removeMouseListener(mouseAdapter);
                drawingPanel.removeMouseMotionListener(mouseAdapter);
            });
        });
    }

    /**
     * Method for setting an item listener and returning an observable of the events.
     * @param menuItem a radio menu button item.
     * @return an observable of item events.
     */
    public static Observable<ItemEvent> getItemEventsObservable(final JRadioButtonMenuItem menuItem) {
        return Observable.create(emitter -> menuItem.addItemListener(emitter::onNext));
    }

    /**
     * Method for setting an action listener and returning an observable of the events.
     * @param menuItem a menu item.
     * @return an observable of item events.
     */
    public static Observable<ActionEvent> getItemActionEventsObservable(final JMenuItem menuItem) {
        return Observable.create(emitter -> menuItem.addActionListener(emitter::onNext));
    }

    /**
     * Simple getter of the current server subject containing the current server.
     * @return an observable of the current server.
     */
    public static Observable<Server> getCurrentServer() {
        return currentServerSubject;
    }

    /**
     * Method for adding the current server to the current server subject.
     * @param server the current server to be added.
     */
    public static void setCurrentServerSubject(final Server server) {
        currentServerSubject.onNext(server);
    }

    /**
     * Simple getter for the current client subject containing the current client.
     * @return an observable of the current client.
     */
    public static Observable<Client> getCurrentClient() {
        return currentClientSubject;
    }

    /**
     * Method for adding the current client to the current client subject.
     * @param client the current client to be added.
     */
    public static void setCurrentClientSubject(final Client client) {
        currentClientSubject.onNext(client);
    }

    /**
     * Simple getter for if the client is active, as connection errors can occur.
     * @return an observable of the boolean value if the client is active.
     */
    public static Observable<Boolean> getIsClientActiveSubject() {
        return isClientActiveSubject;
    }

    /**
     * Method for adding if the client is active.
     * @param active true if active.
     */
    public static void setIsClientActiveSubject(final boolean active) {
        isClientActiveSubject.onNext(active);
    }

    /**
     * Simple getter for if the server is active, as connection errors can occur.
     * @return an observable of the boolean value if the server is active.
     */
    public static Observable<Boolean> getIsServerActiveSubject() {
        return isServerActiveSubject;
    }

    /**
     * Method for adding if the server is active.
     * @param active true if active.
     */
    public static void setIsServerActiveSubject(final boolean active) {
        isServerActiveSubject.onNext(active);
    }

    /**
     * Simple getter for the current event subject containing the current event.
     * @return an observable of the current event.
     */
    public static Observable<Event> getEventSubject() {
        return currentEventSubject;
    }

    /**
     * Method for adding the current event to the current event subject.
     * @param currentEvent the current event to be added.
     */
    public static void setCurrentEventSubject(final Event currentEvent) {
        currentEventSubject.onNext(currentEvent);
    }

    /**
     * Simple getter of the current shape subject containing the current shape.
     * @return an observable of the current shape.
     */
    public static Observable<Shape> getCurrentShape() {
        return currentShapeSubject;
    }

    /**
     * Method for adding the current shape to the current shape subject.
     * @param shape the current shape to be added.
     */
    public static void setCurrentShapeSubject(final Shape shape) {
        currentShapeSubject.onNext(shape);
    }

    /**
     * Simple getter for the current thickness subject containing the current thickness.
     * @return an observable of the current thickness.
     */
    public static Observable<Long> getCurrentThickness() {
        return currentThicknessSubject;
    }

    /**
     * Method for adding the current thickness to the current thickness subject.
     * @param thickness the current thickness.
     */
    public static void setCurrentThicknessSubject(final long thickness) {
        currentThicknessSubject.onNext(thickness);
    }

    /**
     * Simple getter for the current color subject containing the current color.
     * @return an observable of the current color.
     */
    public static Observable<Color> getCurrentColor() {
        return currentColorSubject;
    }

    /**
     * Method for adding the current color to the current color subject.
     * @param color the current color.
     */
    public static void setCurrentColorSubject(final Color color) {
        currentColorSubject.onNext(color);
    }
}
