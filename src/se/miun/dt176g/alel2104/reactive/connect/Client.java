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
    private DrawingPanel drawingPanel;

    public Client(String host, int port, DrawingPanel drawingPanel) throws IOException {
        this.drawingPanel = drawingPanel;
        disposables = new CompositeDisposable();

        socket = new Socket(host, port);
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
                    drawingPanel.getDrawing().addShape(shape);
                    drawingPanel.redraw();
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

    public void sendShape(Shape shape) {
        System.out.println("Sending to server...");

        Disposable disposable = Observable.just(shape)
                .subscribeOn(Schedulers.io())
                .subscribe(newShape -> {
                    if (!socket.isClosed()) {
                        try {
                            outputStream.writeObject(newShape);
                            System.out.println("Client is sending shape: " + newShape.getClass().getName());
                        } catch (IOException e) {
                            System.out.println("Server Disconnected");
                            stopClient();
                        }

                    }
                });

        disposables.add(disposable);
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
