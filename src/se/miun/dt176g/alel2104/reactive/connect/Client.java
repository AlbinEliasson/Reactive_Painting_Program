package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.alel2104.reactive.EventObservable;
import se.miun.dt176g.alel2104.reactive.Shape;
import se.miun.dt176g.alel2104.reactive.gui.DrawingPanel;
import se.miun.dt176g.alel2104.reactive.gui.MainFrame;
import se.miun.dt176g.alel2104.reactive.gui.Menu;
import se.miun.dt176g.alel2104.reactive.shapes.Freehand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;
    private final CompositeDisposable disposables;
    private ObjectOutputStream outputStream;
    private Shape sentShape;
    private MainFrame frame;

    public Client(String host, int port, MainFrame frame) throws IOException {
        this.frame = frame;
        disposables = new CompositeDisposable();

        socket = new Socket(host, port);

//        Observable.create(emitter -> {
//            try {
//                socket = new Socket(host, port);
//                emitter.onNext(socket);
//                initializeObjectOutputStream(socket);
//            } catch (IOException e) {
//
//                emitter.onError(e);
//            }
//        }).subscribe();

//        try {
//            socket = new Socket(host, port);
//            initializeObjectOutputStream(socket);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    public void startClient() {
        System.out.println("Starting client and accessing...");

        EventObservable.setIsClientActiveSubject(true);

        initializeObjectOutputStream(socket);

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
                .map(object -> (Shape) object)
                .subscribe(shape -> {
                    System.out.println("Client received shape: " + shape);
                    if (!isSentShapeReceivedShape(sentShape, shape)) {
                        frame.getDrawingPanel().getDrawing().addShape(shape);
                        frame.getDrawingPanel().redraw();
                    }
                });
        disposables.add(disposable);
//        Disposable getShapeDisposable = getShapeObservable()
//                .observeOn(Schedulers.io())
//                .doOnDispose(socket::close)
//                .subscribe(shape -> {
//                    System.out.println("Client received shape: " + shape.getClass());
//                    drawingPanel.getDrawing().addShape(shape);
//                    drawingPanel.redraw();
//
//                }, System.err::println);
//
//        disposables.add(getShapeDisposable);
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

    public Observable<Shape> getShapeObservable() {
        return Observable.defer(() -> {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                return Observable.just((Shape) objectInputStream.readObject());
            } catch (IOException | ClassNotFoundException e) {
                return Observable.error(new RuntimeException(e));
            }
        }).subscribeOn(Schedulers.io());

//        return Observable.just(socket)
//                .subscribeOn(Schedulers.io())
//                .map(Socket::getInputStream)
//                .map(ObjectInputStream::new)
//                .map(objectInputStream -> (Shape) objectInputStream.readObject());
    }

    public void sendShape(Shape shape) {
        System.out.println("Sending to server...");
        sentShape = shape;

        Disposable disposable = Observable.just(shape)
                .subscribeOn(Schedulers.io())
                .subscribe(shape1 -> {
                    if (!socket.isClosed()) {
                        try {
                            outputStream.writeObject(shape1);
                            System.out.println("Client is sending shape: " + shape1.getClass().getName());
                        } catch (IOException e) {
                            System.out.println("Server Disconnected");
                            stopClient();
                        }

                    }
                });

        disposables.add(disposable);
    }

    private boolean isSentShapeReceivedShape(Shape sentShape, Shape receivedShape) {
        if (sentShape != null && receivedShape != null) {
            if (sentShape.getClass().getName().equals(receivedShape.getClass().getName())) {
                if (receivedShape instanceof Freehand) {
                    return sentShape.getFreehandCoordinates().equals(receivedShape.getFreehandCoordinates())
                            && sentShape.getColor().equals(receivedShape.getColor())
                            && sentShape.getThickness() == receivedShape.getThickness();
                }
                return sentShape.getCoordinates().getX() == receivedShape.getCoordinates().getX()
                        && sentShape.getCoordinates().getY() == receivedShape.getCoordinates().getY()
                        && sentShape.getHeight() == receivedShape.getHeight()
                        && sentShape.getColor().equals(receivedShape.getColor())
                        && sentShape.getThickness() == receivedShape.getThickness();
            }
        }
        return false;
    }
}
