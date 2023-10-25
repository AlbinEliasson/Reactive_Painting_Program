package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import se.miun.dt176g.alel2104.reactive.EventObservable;
import se.miun.dt176g.alel2104.reactive.Shape;
import se.miun.dt176g.alel2104.reactive.gui.DrawingPanel;
import se.miun.dt176g.alel2104.reactive.shapes.Freehand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private boolean acceptConnection = true;
    private final List<Socket> clientSockets;
    private final Subject<Socket> clientConnections;
    private final Subject<Shape> shapeStream;
    private final DrawingPanel drawingPanel;
    private final Map<Integer, Disposable> disposableMap;
    private final Map<Integer, Disposable> shapeDisposableMap;

    public Server(int serverPort, DrawingPanel drawingPanel) throws IOException {
        this.drawingPanel = drawingPanel;
        clientSockets = new ArrayList<>();
        clientConnections = PublishSubject.create();
        shapeStream = ReplaySubject.create();
        disposableMap = new HashMap<>();
        shapeDisposableMap = new HashMap<>();

        serverSocket = new ServerSocket(serverPort);
    }

    public void startServer() {
        System.out.println("Starting server and waiting for client connections!");

        EventObservable.setIsServerActiveSubject(true);

        Completable.create(emitter -> getClientConnection())
                .subscribeOn(Schedulers.single())
                .subscribe();

        clientConnections
                .doOnNext(s -> System.out.println("tcp connection accepted..."))
                .subscribe(this::handleClient);
    }

    private void getClientConnection() {
        Observable.<Socket>create(emitter -> {
            while (acceptConnection) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    emitter.onNext(clientSocket);
                } catch (IOException e) {
                    if (!acceptConnection) {
                        break;
                    }
                    emitter.onError(e);
                }
            }
            emitter.onComplete();
        }).observeOn(Schedulers.io())
                .subscribe(clientConnections);
    }

    private void handleClient(Socket clientSocket) {
        System.out.println("Client connected with IP: " + clientSocket.getInetAddress());

        if (!clientSockets.contains(clientSocket)) {
            clientSockets.add(clientSocket);
            System.out.println("Amount of connected clients: " + clientSockets.size());
        }
        Observable.create(emitter -> getObjectInputStream(clientSocket)
                        .doOnSubscribe(disposable -> disposableMap.put(clientSocket.hashCode(), disposable))
                        .doOnError(this::handleError)
                .subscribe(objectInputStream -> {
                    while (!emitter.isDisposed()) {
                        try {
                            emitter.onNext(objectInputStream.readObject());
                        } catch (IOException | ClassNotFoundException e) {
                            emitter.onError(new ConnectionError(clientSocket));
                        }
                    }
                }))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> disposableMap.put(clientSocket.hashCode(), disposable))
                .doOnError(this::handleError)
                .onErrorComplete(err -> err instanceof ConnectionError)
                .map(o -> (Shape) o)
                .doOnNext(shape -> {
                    System.out.println("Server received shape: " + shape.getClass().getName()
                            + "from client: " + clientSocket.getInetAddress());

                    // Set client hashCode to the shape to prevent sending the shape back to client
                    shape.setClientHashCode(clientSocket.hashCode());

                    // Adding and drawing the shape to the server user
                    drawingPanel.getDrawing().addShape(shape);
                    drawingPanel.redraw();
                })
                .subscribe(shapeStream::onNext
                        , err -> System.err.println(err.getMessage())
                        , () -> System.out.println("Socket closed"));

        shapeStream.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> shapeDisposableMap.put(clientSocket.hashCode(), disposable))
                .withLatestFrom(getObjectOutputStream(clientSocket), (shape, outputStream) -> {
                    System.out.println("Server is trying to send shape: " + shape + "to client: "
                            + clientSocket.getInetAddress());

                    // Check if client hashCode is 0 (the server user sent the shape) or if the shape is
                    // connected to the client to prevent sending the shape back to the client.
                    if (shape.getClientHashCode() == 0 || shape.getClientHashCode() != clientSocket.hashCode()) {
                        outputStream.writeObject(shape);
                    }
                    return true;
                })
                .subscribe();
    }

    public void sendServerShape(Shape sentShape) {
        Observable.just(sentShape)
                .subscribeOn(Schedulers.io())
                .subscribe(shapeStream::onNext);
    }

    public void closeServer() {
        System.out.println("Shutting down server!");
        acceptConnection = false;

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        clientSockets.forEach(this::disposeClientResources);
        clientSockets.clear();
        EventObservable.setIsServerActiveSubject(false);
    }

    private void disposeClientResources(Socket clientSocket) {
        Disposable disposable = disposableMap.get(clientSocket.hashCode());
        if (disposable != null) {
            disposable.dispose();
        }
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Observable<ObjectInputStream> getObjectInputStream(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new);
    }

    private Observable<ObjectOutputStream> getObjectOutputStream(Socket socket) {
        return Observable.just(socket)
                .map(Socket::getOutputStream)
                .map(ObjectOutputStream::new);
    }

    private void handleError(Throwable throwableError) {
        if (throwableError instanceof ConnectionError) {
            Socket socket = ((ConnectionError) throwableError).getSocket();
            disposableMap.get(socket.hashCode()).dispose();
            disposableMap.remove(socket.hashCode());
            shapeDisposableMap.get(socket.hashCode()).dispose();
            shapeDisposableMap.remove(socket.hashCode());
        }
    }

    public static class ConnectionError extends Throwable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final Socket socket;

        public ConnectionError(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }
    }
}
