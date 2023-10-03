package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EventObservable {
    private static final PublishSubject<Shape> currentShapeSubject = PublishSubject.create();
    private static final PublishSubject<Long> currentThicknessSubject = PublishSubject.create();
    private static final PublishSubject<Color> currentColorSubject = PublishSubject.create();

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

    public static Observable<ItemEvent> getItemEventsObservable(JRadioButtonMenuItem menuItem) {
        return Observable.create(emitter -> menuItem.addItemListener(emitter::onNext));
    }

    public static Observable<Shape> getCurrentShape() {
        return currentShapeSubject;
    }

    public static void setCurrentShapeSubject(Shape shape) {
        currentShapeSubject.onNext(shape);
    }

    public static Observable<Long> getCurrentThickness() {
        return currentThicknessSubject;
    }

    public static void setCurrentThicknessSubject(long thickness) {
        currentThicknessSubject.onNext(thickness);
    }

    public static Observable<Color> getCurrentColor() {
        return currentColorSubject;
    }

    public static void setCurrentColorSubject(Color color) {
        currentColorSubject.onNext(color);
    }
}
