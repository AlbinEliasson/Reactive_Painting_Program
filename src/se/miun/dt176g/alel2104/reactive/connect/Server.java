package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.alel2104.reactive.Shape;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    private boolean acceptConnection = true;
    private static final int SERVER_PORT = 12345;

    public Server() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer() throws Exception {
        System.out.println("Starting server on port: " + SERVER_PORT + " and creating stream of clients");
//        Observable.<Socket>create(e -> {
//                    while (acceptConnection) {
//                        System.out.println("Accepting a connection on " + thread());
//                        e.onNext(serverSocket.accept());
//                    }
//                })
//                // Doing subscribeOn here only determines which thread accepts
//                // but does not prevent reading input to block accepting additional connections.
//                .map(Socket::getInputStream)
//                .map(InputStreamReader::new)
//                .map(BufferedReader::new)
//                .map(BufferedReader::lines)
//                .flatMap(stream -> Observable.fromIterable(() -> stream.iterator()).subscribeOn(Schedulers.io()))
//                // This subscribeOn (in the flatMap) is necessary to allow new connections while one is being processed.
//                // When using subscribeOn outside flatMap, this prevents the main thread from finishing prematurely.
//                // However, it also forces the main thread to be used!
//                //.blockingSubscribe(s -> System.out.println(thread() + ": " + s));
//                .subscribe(s -> System.out.println(thread() + ": " + s));
    }

    public void closeServer() {
        acceptConnection = false;
    }

    public static String thread() {
        Thread current = Thread.currentThread();
        return "(Thread: " + current + " (id = " + current.getId() + "))";
    }

    private static class ClientHandler {
        private final Socket socket;
        private final Server server;

        public ClientHandler(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;

        }

        public void start() {

        }

        public void sendShape(Shape shape) {
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
        }

        private Observable<Shape> createShapeObservable() {
            return Observable.create(emitter -> {
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        Shape shape = (Shape) objectInputStream.readObject();
                        emitter.onNext(shape);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    emitter.onError(e);
                }
            });
        }
    }
}
