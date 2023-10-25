package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.alel2104.reactive.Event;
import se.miun.dt176g.alel2104.reactive.EventObservable;
import se.miun.dt176g.alel2104.reactive.Shape;
import se.miun.dt176g.alel2104.reactive.gui.MainFrame;
import se.miun.dt176g.alel2104.reactive.support.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class Client {
    private final Socket socket;
    private final CompositeDisposable disposables;
    private ObjectOutputStream outputStream;
    private final MainFrame frame;

    public Client(String host, int port, MainFrame frame) throws IOException {
        this.frame = frame;
        disposables = new CompositeDisposable();

        socket = new Socket(host, port);
    }

    public void startClient() {
        System.out.println("Starting client and accessing...");

        EventObservable.setIsClientActiveSubject(true);

        initializeObjectOutputStream(socket);

        setEventListener();

        Disposable disposable = Observable.create(emitter -> getObjectInputStream(socket)
                        .doOnSubscribe(disposables::add)
                .subscribe(objectInputStream -> {
                    while (!emitter.isDisposed()) {
                        // Add if-statement to check if socket is closed
                        try {
                            emitter.onNext(objectInputStream.readObject());
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Server Disconnected");
                            emitter.onComplete();
                        }

                    }
                })).subscribeOn(Schedulers.io())
                .subscribe(object -> {
                    if (object instanceof Shape shape) {
                        System.out.println("Client received shape: " + shape);
                        frame.getDrawingPanel().getDrawing().addShape(shape);
                        frame.getDrawingPanel().redraw();
                    } else if (object instanceof Event event) {
                        System.out.println("Client received event: " + event.getCurrentEvent());
                        handleEvent(event);
                    }
                });
        disposables.add(disposable);
    }

    private void initializeObjectOutputStream(Socket socket) {
        outputStream = Observable.just(socket)
                .subscribeOn(Schedulers.io())
                .map(Socket::getOutputStream)
                .map(ObjectOutputStream::new)
                .blockingFirst();
    }

    private Observable<ObjectInputStream> getObjectInputStream(Socket socket) {
        return Observable.just(socket)
                .subscribeOn(Schedulers.io())
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new);
    }

    public void stopClient() {
        System.out.println("Disconnecting client...!");
        disposables.clear();
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EventObservable.setIsClientActiveSubject(false);
    }

    public void sendObject(Object object) {
        System.out.println("Sending to server...");

        Disposable disposable = Observable.just(object)
                .subscribeOn(Schedulers.io())
                .subscribe(newObject -> {
                    if (!socket.isClosed()) {
                        try {
                            outputStream.writeObject(newObject);
                            System.out.println("Client is sending object: " + newObject.getClass().getName());
                        } catch (IOException e) {
                            System.out.println("Server Disconnected");
                            stopClient();
                        }

                    }
                });

        disposables.add(disposable);
    }

    private void setEventListener() {
        EventObservable.getEventSubject()
                .subscribe(event -> {
                    sendObject(event);
                    handleEvent(event);
                });
    }

    private void handleEvent(Event event) {
        if (Objects.equals(event.getCurrentEvent(), Constants.CLEAR_CANVAS_EVENT)) {
            event.clearCanvasEvent(frame.getMenu());
        }
    }

//    private boolean isSentShapeReceivedShape(Shape sentShape, Shape receivedShape) {
//        if (sentShape != null && receivedShape != null) {
//            if (sentShape.getClass().getName().equals(receivedShape.getClass().getName())) {
//                if (receivedShape instanceof Freehand) {
//                    return sentShape.getFreehandCoordinates().equals(receivedShape.getFreehandCoordinates())
//                            && sentShape.getColor().equals(receivedShape.getColor())
//                            && sentShape.getThickness() == receivedShape.getThickness();
//                }
//                return sentShape.getCoordinates().getX() == receivedShape.getCoordinates().getX()
//                        && sentShape.getCoordinates().getY() == receivedShape.getCoordinates().getY()
//                        && sentShape.getHeight() == receivedShape.getHeight()
//                        && sentShape.getColor().equals(receivedShape.getColor())
//                        && sentShape.getThickness() == receivedShape.getThickness();
//            }
//        }
//        return false;
//    }
}
