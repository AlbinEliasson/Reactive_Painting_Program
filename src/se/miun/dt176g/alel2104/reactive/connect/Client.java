package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.alel2104.reactive.Shape;
import se.miun.dt176g.alel2104.reactive.gui.DrawingPanel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private final Socket socket;
    private final CompositeDisposable disposables;
    private DrawingPanel drawingPanel;
    private ObjectOutputStream outputStream;

    public Client(String host, int port, DrawingPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        disposables = new CompositeDisposable();

        try {
            socket = new Socket(host, port);
            initializeObjectOutputStream(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void startClient() {
        System.out.println("Starting client and accessing...");

        Observable.create(emitter -> getObjectInputStream(socket)
                .subscribe(objectInputStream -> {
                    while (!emitter.isDisposed()) {
                        // Add if-statement to check if socket is closed
                        emitter.onNext(objectInputStream.readObject());
                    }
                })).subscribeOn(Schedulers.io())
                .map(object -> (Shape) object)
                .subscribe(shape -> {
                    System.out.println("Client received shape: " + shape.getClass().getSimpleName());
                    drawingPanel.getDrawing().addShape(shape);
                    drawingPanel.redraw();
                });
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
        Schedulers.shutdown();
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
//        Disposable sendShapeDisposable = Observable.defer(() -> {
//            try {
//                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//                outputStream.writeObject(shape);
//                return Observable.just(shape);
//            } catch (IOException e) {
//                return Observable.error(new RuntimeException(e));
//            }
//                }).subscribeOn(Schedulers.io())
//                .subscribe(shape1 -> System.out.println("Client is sending shape: " + shape1.getClass()), System.err::println);
//
//            disposables.add(sendShapeDisposable);

//        Observable.just(socket)
//                .subscribeOn(Schedulers.io())
//                .map(Socket::getOutputStream)
//                .map(ObjectOutputStream::new)
//                .map(objectOutputStream -> {
//                    objectOutputStream.writeObject(shape);
//                    return shape;
//                })
//                .subscribe(shape1 -> System.out.println("Client is sending shape: " + shape1.getClass()), System.err::println);

        Observable.just(shape)
                .subscribeOn(Schedulers.io())
                .subscribe(shape1 -> {
                    outputStream.writeObject(shape1);
                    System.out.println("Client is sending shape: " + shape1.getClass().getName());
                });

    }

}
