package se.miun.dt176g.alel2104.reactive;

import io.reactivex.rxjava3.core.Observable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EventObservable {

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
}
