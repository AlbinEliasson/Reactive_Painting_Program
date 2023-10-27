package se.miun.dt176g.alel2104.reactive.connect;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import io.reactivex.rxjava3.subjects.Subject;
import se.miun.dt176g.alel2104.reactive.Event;
import se.miun.dt176g.alel2104.reactive.EventObservable;
import se.miun.dt176g.alel2104.reactive.Shape;
import se.miun.dt176g.alel2104.reactive.gui.MainFrame;
import se.miun.dt176g.alel2104.reactive.support.Constants;

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
import java.util.Objects;

/**
 * <h1>Server</h1>
 * The server of the program which users can host and connect to.
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Server {
    private final ServerSocket serverSocket;
    private boolean acceptConnection = true;
    private final List<Socket> clientSockets;
    private final Subject<Socket> clientConnections;
    private final Subject<Object> objectStream;
    private final MainFrame frame;
    private final Map<Integer, Disposable> disposableMap;
    private final Map<Integer, Disposable> objectDisposableMap;

    /**
     * Constructor to initialize the server socket, main frame and connection/stream related variables.
     * @param serverPort the server sockets port number.
     * @param frame the main frame.
     * @throws IOException IOException.
     */
    public Server(final int serverPort, final MainFrame frame) throws IOException {
        this.frame = frame;
        clientSockets = new ArrayList<>();
        clientConnections = PublishSubject.create();
        objectStream = ReplaySubject.create();
        disposableMap = new HashMap<>();
        objectDisposableMap = new HashMap<>();

        serverSocket = new ServerSocket(serverPort);
    }

    /**
     * Method for starting the server by listening and handling client connections.
     */
    public void startServer() {
        System.out.println("Starting server and waiting for client connections!");

        EventObservable.setIsServerActiveSubject(true);

        setEventListener();

        Completable.create(emitter -> getClientConnection())
                .subscribeOn(Schedulers.single())
                .subscribe();

        clientConnections
                .doOnNext(s -> System.out.println("tcp connection accepted..."))
                .subscribe(this::handleClient);
    }

    /**
     * Method for listening for client connections and adding them to the client connection subject.
     */
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

    /**
     * Method for handling the clients by listening for/sending data to/from client sockets.
     * @param clientSocket the client socket.
     */
    private void handleClient(final Socket clientSocket) {
        System.out.println("Client connected with IP: " + clientSocket.getInetAddress());

        if (!clientSockets.contains(clientSocket)) {
            clientSockets.add(clientSocket);
            System.out.println("Amount of connected clients: " + clientSockets.size());
        }
        // Observable which listens for client objects, adds them to server user and finally to the object stream.
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
                .doOnNext(object -> {
                    if (object instanceof Shape shape) {
                        System.out.println("Server received shape: " + shape.getClass().getName()
                                + "from client: " + clientSocket.getInetAddress());

                        // Set client hashCode to the shape to prevent sending the shape back to the client
                        shape.setClientHashCode(clientSocket.hashCode());

                        // Adding and drawing the shape to the server user
                        frame.getDrawingPanel().getDrawing().addShape(shape);
                        frame.getDrawingPanel().redraw();

                    } else if (object instanceof Event event) {
                        System.out.println("Server received an event from client: " + clientSocket.getInetAddress());
                        //Set client hashCode to the event to prevent sending the event back to the client
                        event.setClientHashCode(clientSocket.hashCode());
                        // Handling the event for the server user
                        handleEvent(event);
                    }
                })
                .subscribe(objectStream::onNext,
                        err -> System.err.println(err.getMessage()),
                        () -> System.out.println("Socket closed"));

        // Subject stream which sends the received objects to clients
        objectStream.subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> objectDisposableMap.put(clientSocket.hashCode(), disposable))
                .withLatestFrom(getObjectOutputStream(clientSocket), (object, outputStream) -> {
                    if (object instanceof Shape shape) {
                        // Check if client hashCode is 0 (the server user sent the shape) or if the shape is
                        // connected to the client to prevent sending the shape back to the client.
                        if (shape.getClientHashCode() == 0 || shape.getClientHashCode() != clientSocket.hashCode()) {
                            System.out.println("Server is trying to send shape: " + shape + "to client: "
                                    + clientSocket.getInetAddress());
                            outputStream.writeObject(shape);
                        }
                    } else if (object instanceof Event event) {
                        if (event.getClientHashCode() == 0 || event.getClientHashCode() != clientSocket.hashCode()) {
                            outputStream.writeObject(event);
                        }
                    }
                    return true;
                })
                .subscribe();
    }

    /**
     * Method for the server user to send objects to clients connected.
     * @param object the object to be sent.
     */
    public void sendServerObject(final Object object) {
        Observable.just(object)
                .subscribeOn(Schedulers.io())
                .subscribe(objectStream::onNext);
    }

    /**
     * Method for closing the server and disposing of client resources.
     */
    public void closeServer() {
        System.out.println("Shutting down server!");
        acceptConnection = false;

        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        clientSockets.forEach(this::disposeClientResources);
        clientSockets.clear();
        EventObservable.setIsServerActiveSubject(false);
    }

    /**
     * Method for disposing resources and closing client sockets.
     * @param clientSocket the client socket.
     */
    private void disposeClientResources(final Socket clientSocket) {
        Disposable disposable = disposableMap.get(clientSocket.hashCode());
        if (disposable != null) {
            disposable.dispose();
        }
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method for creating an ObjectInputStream from the provided sockets InputStream.
     * @param socket the client socket.
     * @return an observable of the ObjectInputStream.
     */
    private Observable<ObjectInputStream> getObjectInputStream(final Socket socket) {
        return Observable.just(socket)
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new);
    }

    /**
     * Method for creating an ObjectOutputStream from the provided sockets OutPutStream.
     * @param socket the client socket.
     * @return an observable of the ObjectOutputStream.
     */
    private Observable<ObjectOutputStream> getObjectOutputStream(final Socket socket) {
        return Observable.just(socket)
                .map(Socket::getOutputStream)
                .map(ObjectOutputStream::new);
    }

    /**
     * Method for handling client connection errors by disposing the client resources.
     * @param throwableError throwable error.
     */
    private void handleError(final Throwable throwableError) {
        if (throwableError instanceof ConnectionError) {
            Socket socket = ((ConnectionError) throwableError).getSocket();
            disposableMap.get(socket.hashCode()).dispose();
            disposableMap.remove(socket.hashCode());
            objectDisposableMap.get(socket.hashCode()).dispose();
            objectDisposableMap.remove(socket.hashCode());
        }
    }

    /**
     * Method for setting the event listener and handle/send the incoming events to clients.
     */
    private void setEventListener() {
        EventObservable.getEventSubject()
                .subscribe(event -> {
                    sendServerObject(event);
                    handleEvent(event);
                });
    }

    /**
     * Method for handling and executing the event.
     * @param event the event object.
     */
    private void handleEvent(final Event event) {
        if (Objects.equals(event.getCurrentEvent(), Constants.CLEAR_CANVAS_EVENT)) {
            event.clearCanvasEvent(frame.getMenu());
        }
    }

    /**
     * Innerclass to help with client connection errors.
     */
    public static class ConnectionError extends Throwable {
        @Serial
        private static final long serialVersionUID = 1L;
        private final Socket socket;

        /**
         * Constructor to initialize the client socket that had a connection error.
         * @param socket the client socket.
         */
        public ConnectionError(final Socket socket) {
            this.socket = socket;
        }

        /**
         * Simple getter for the client socket that had a connection error.
         * @return the client socket.
         */
        public Socket getSocket() {
            return socket;
        }
    }
}
