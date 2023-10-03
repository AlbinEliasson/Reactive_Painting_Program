package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.alel2104.reactive.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private final Socket socket;
    private final CompositeDisposable disposables;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        disposables = new CompositeDisposable();
    }

    public void startClient() {
        System.out.println("Starting client and accessing...");
        Disposable getShapeDisposable = getShapeObservable()
                .observeOn(Schedulers.io())
                .doOnDispose(socket::close)
                .subscribe(shape -> System.out.println(shape.getClass()), System.err::println);
        // Store the shapes later on
        disposables.add(getShapeDisposable);
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
        Disposable sendShapeDisposable = Observable.defer(() -> {
            try {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(shape);
                return Observable.just(shape);
            } catch (IOException e) {
                return Observable.error(new RuntimeException(e));
            }
                }).subscribeOn(Schedulers.io())
                .subscribe(shape1 -> System.out.println(shape1.getClass()), System.err::println);

            disposables.add(sendShapeDisposable);
//        Observable.just(socket)
//                .subscribeOn(Schedulers.io())
//                .map(Socket::getOutputStream)
//                .map(ObjectOutputStream::new)
//                .map(objectOutputStream -> {
//                    objectOutputStream.writeObject(shape);
//                    return shape;
//                })
//                .subscribe(shape1 -> System.out.println(shape1.getClass()), System.err::println);
    }

}
