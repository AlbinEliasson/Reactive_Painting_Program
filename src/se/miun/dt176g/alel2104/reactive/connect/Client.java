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

/**
 * <h1>Client</h1>
 * The client of the program which users can use to connect to the hosted server.
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Client {
    private final Socket socket;
    private final CompositeDisposable disposables;
    private ObjectOutputStream outputStream;
    private final MainFrame frame;

    /**
     * Constructor to initialize the client socket, mainframe and the CompositeDisposable.
     * @param host the host name for the socket.
     * @param port the port of the socket.
     * @param frame the main frame.
     * @throws IOException IOException.
     */
    public Client(final String host, final int port, final MainFrame frame) throws IOException {
        this.frame = frame;
        disposables = new CompositeDisposable();

        socket = new Socket(host, port);
    }

    /**
     * Method for starting the client by listening to the socket for incoming objects.
     */
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

    /**
     * Method for initializing an ObjectOutputStream from the provided sockets OutPutStream.
     * @param socket the client socket.
     */
    private void initializeObjectOutputStream(final Socket socket) {
        outputStream = Observable.just(socket)
                .subscribeOn(Schedulers.io())
                .map(Socket::getOutputStream)
                .map(ObjectOutputStream::new)
                .blockingFirst();
    }

    /**
     * Method for creating an ObjectInputStream from the provided sockets InputStream.
     * @param socket the client socket.
     * @return an observable of the ObjectInputStream.
     */
    private Observable<ObjectInputStream> getObjectInputStream(final Socket socket) {
        return Observable.just(socket)
                .subscribeOn(Schedulers.io())
                .map(Socket::getInputStream)
                .map(ObjectInputStream::new);
    }

    /**
     * Method for stopping the client by closing the socket and disposing of resources.
     */
    public void stopClient() {
        System.out.println("Disconnecting client...!");
        disposables.clear();
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        EventObservable.setIsClientActiveSubject(false);
    }

    /**
     * Method for sending objects through the socket.
     * @param object the object to be sent.
     */
    public void sendObject(final Object object) {
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

    /**
     * Method for setting the event listener and handle/send the incoming events to server.
     */
    private void setEventListener() {
        EventObservable.getEventSubject()
                .subscribe(event -> {
                    sendObject(event);
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
}
