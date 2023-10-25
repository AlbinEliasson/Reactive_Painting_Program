package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import se.miun.dt176g.alel2104.reactive.connect.Client;
import se.miun.dt176g.alel2104.reactive.connect.Server;
import se.miun.dt176g.alel2104.reactive.gui.DrawingPanel;

import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <h1>EventObservable</h1>
 * The event observable component which is utilized for creating observables from events.
 *
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

    /**
     * Method for setting a mouse listener and returning an observable of the events.
     * @param drawingPanel the drawingPanel component.
     * @return an observable of mouse events.
     */
    public static Observable<MouseEvent> getMouseEventsObservable(DrawingPanel drawingPanel) {
        return Observable.create(emitter -> {
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    emitter.onNext(e);
                    super.mousePressed(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    emitter.onNext(e);
                    super.mouseReleased(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
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
    public static Observable<ItemEvent> getItemEventsObservable(JRadioButtonMenuItem menuItem) {
        return Observable.create(emitter -> menuItem.addItemListener(emitter::onNext));
    }

    public static Observable<ActionEvent> getItemActionEventsObservable(JMenuItem menuItem) {
        return Observable.create(emitter -> menuItem.addActionListener(emitter::onNext));
    }

    public static Observable<Server> getCurrentServer() {
        return currentServerSubject;
    }

    public static void setCurrentServerSubject(Server server) {
        currentServerSubject.onNext(server);
    }

    public static Observable<Client> getCurrentClient() {
        return currentClientSubject;
    }

    public static void setCurrentClientSubject(Client client) {
        currentClientSubject.onNext(client);
    }

    public static Observable<Boolean> getIsClientActiveSubject() {
        return isClientActiveSubject;
    }

    public static void setIsClientActiveSubject(boolean active) {
        isClientActiveSubject.onNext(active);
    }

    public static Observable<Boolean> getIsServerActiveSubject() {
        return isServerActiveSubject;
    }

    public static void setIsServerActiveSubject(boolean active) {
        isServerActiveSubject.onNext(active);
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
    public static void setCurrentShapeSubject(Shape shape) {
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
    public static void setCurrentThicknessSubject(long thickness) {
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
    public static void setCurrentColorSubject(Color color) {
        currentColorSubject.onNext(color);
    }
}
