package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import se.miun.dt176g.alel2104.reactive.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final ServerSocket serverSocket;
    private final ConcurrentHashMap<Socket, CompositeDisposable> clientDisposables;
    private boolean acceptConnection = true;
    private Observable<Socket> clientConnections;
    private List<Socket> clientSockets;
    private Subject<Socket> clientConnectionss;
    private Subject<Shape> shapeStream;

    public Server(int serverPort) {
        clientDisposables = new ConcurrentHashMap<>();
        clientSockets = new ArrayList<>();
        clientConnectionss = PublishSubject.create();
        shapeStream = ReplaySubject.create();

        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startServer() {
        System.out.println("Starting server and waiting for client connections!");

        Completable.create(emitter -> getClientConnection())
                .subscribeOn(Schedulers.single())
                .subscribe();

        clientConnectionss
                .doOnNext(s -> System.out.println("tcp connection accepted..."))
                .subscribe(this::handleClient);

//        clientConnections = getClientConnection();
//
//        clientConnections.subscribeOn(Schedulers.io())
//                .subscribe(this::handleClient, e -> {
//                    System.err.println("Error accepting client connection: " + e.getMessage());
//                    //closeServer();
//                }, () -> {
////                    System.out.println("No clients connected, closing server");
////                    closeServer();
//                });
    }

    private void getClientConnection() {
        Observable.<Socket>create(emitter -> {
            while (!emitter.isDisposed()) {
                try {
                    emitter.onNext(serverSocket.accept());
                } catch (IOException e) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                    break;
                }
            }
        }).observeOn(Schedulers.io())
                .subscribe(clientConnectionss);
    }

    private void handleClient(Socket clientSocket) {
        System.out.println("Client connected with IP: " + clientSocket.getInetAddress());

        if (!clientSockets.contains(clientSocket)) {
            clientSockets.add(clientSocket);
        }
        Observable.create(emitter -> getObjectInputStream(clientSocket)
                .subscribe(objectInputStream -> {
                    while (!emitter.isDisposed()) {
                        // Add if-statement to check if socket is closed
                        emitter.onNext(objectInputStream.readObject());
                    }
                })).subscribeOn(Schedulers.io())
                .map(o -> (Shape) o)
                .doOnNext(shape -> System.out.println("Server received shape: " + shape.getClass().getName()
                        + "from client: " + clientSocket.getInetAddress()))
                .subscribe(shapeStream::onNext
                        , err -> System.err.println(err.getMessage())
                        , () -> System.out.println("Socket closed"));

        shapeStream.subscribeOn(Schedulers.io())
                .withLatestFrom(getObjectOutputStream(clientSocket), (shape, outputStream) -> {
                    System.out.println("Server is trying to send shape: " + shape + "to client: "
                            + clientSocket.getInetAddress());

                    outputStream.writeObject(shape);
                    return true;
                })
                .subscribe();

//        CompositeDisposable disposables = new CompositeDisposable();
//
//        Disposable disposable = getShapeObservable(clientSocket)
//                .observeOn(Schedulers.io())
//                .subscribe(shape -> {
//                    System.out.println("Server received shape: " + shape.getClass());
//                    clientSockets.forEach(socket -> {
////                        if (!socket.equals(clientSocket)) {
////                            sendClientShape(shape, socket);
////                        }
//                        sendClientShape(shape, socket);
//                    });
//
////                    clientDisposables.forEach((socket, compositeDisposable) -> {
////                        if (!socket.equals(clientSocket)) {
////                            System.out.println("Server is sending shape: " + shape.getClass() + "to client: "
////                                    + clientSocket.getInetAddress());
////
////                            sendClientShape(shape, socket);
////                        }
////                    });
//                        },
//                        e -> {
//                    System.out.println("Error receiving shape: " + e.getMessage());
//                    //disposeClientResources(clientSocket);
//                }, () -> {
//
//                        });
//        disposables.add(disposable);
//        clientDisposables.put(clientSocket, disposables);
    }

    public void closeServer() {
        System.out.println("Shutting down server!");
        boolean isClientsConnected = !clientDisposables.isEmpty();

        if (isClientsConnected) {
            clientDisposables.forEach((socket, compositeDisposable) -> {
                disposeClientResources(socket);
                compositeDisposable.clear();
            });
            clientDisposables.clear();
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (isClientsConnected) {
            Schedulers.shutdown();
        }
    }

    private void disposeClientResources(Socket clientSocket) {
        CompositeDisposable disposable = clientDisposables.remove(clientSocket);
        if (disposable != null) {
            disposable.clear();
        }
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Observable<ObjectInputStream> getObjectInputStream(Socket socket) {
        return Observable.just(socket)
                .subscribeOn(Schedulers.io())
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new);
    }

    private Observable<ObjectOutputStream> getObjectOutputStream(Socket socket) {
        return Observable.just(socket)
                .subscribeOn(Schedulers.io())
                .map(Socket::getOutputStream)
                .map(ObjectOutputStream::new);
    }

    private Observable<Shape> getShapeObservable(Socket clientSocket) {
        return Observable.just(clientSocket)
                .subscribeOn(Schedulers.io())
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new)
                .map(objectInputStream -> (Shape) objectInputStream.readObject());
//        return Observable.create(emitter -> {
//            try {
//                System.out.println("Trying to get shape from client: " + clientSocket.getInetAddress());
//                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
//               // while (!emitter.isDisposed()) {
//                    Object receivedObject = objectInputStream.readObject();
//                    if (receivedObject instanceof Shape) {
//                        emitter.onNext((Shape) receivedObject);
//                    }
//               // }
//            } catch (IOException | ClassNotFoundException e) {
//                emitter.onError(new RuntimeException(e));
//            }
//        });
//        return Observable.defer(() -> {
//            try {
//                System.out.println("Trying to get shape from client: " + clientSocket.getInetAddress());
//                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
//                return Observable.just((Shape) objectInputStream.readObject());
//            } catch (IOException | ClassNotFoundException e) {
//                return Observable.error(new RuntimeException(e));
//            }
//        }).subscribeOn(Schedulers.io());
    }

    private void sendClientShape(Shape shape, Socket clientSocket) {
        Disposable clientDisposable = Observable.defer(() -> {
            try {
                System.out.println("Server is trying to send shape: " + shape.getClass()
                        + " to client: " + clientSocket.getInetAddress());
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                outputStream.writeObject(shape);
                return Observable.just(shape);
            } catch (IOException e) {
                return Observable.error(new RuntimeException(e));
            }
        }).subscribeOn(Schedulers.io()).subscribe();

        //clientDisposables.get(clientSocket).add(clientDisposable);
    }

    public static String thread() {
        Thread current = Thread.currentThread();
        return "(Thread: " + current + " (id = " + current.getId() + "))";
    }
}
