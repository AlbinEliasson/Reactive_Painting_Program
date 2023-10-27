package se.miun.dt176g.alel2104.reactive.gui;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import se.miun.dt176g.alel2104.reactive.Event;
import se.miun.dt176g.alel2104.reactive.EventObservable;
import se.miun.dt176g.alel2104.reactive.connect.Client;
import se.miun.dt176g.alel2104.reactive.connect.Server;
import se.miun.dt176g.alel2104.reactive.shapes.Freehand;
import se.miun.dt176g.alel2104.reactive.shapes.Line;
import se.miun.dt176g.alel2104.reactive.shapes.Oval;
import se.miun.dt176g.alel2104.reactive.shapes.Rectangle;
import se.miun.dt176g.alel2104.reactive.support.Constants;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.Serial;

/**
 * <h1>Menu</h1> 
 * The menu of the program.
 * @author 	--Albin Eliasson--
 * @version 1.0
 * @since 	2023-10-07
 */
public class Menu extends JMenuBar {
	@Serial
	private static final long serialVersionUID = 1L;
	private JLabel currentShapeLabel;
	private Server server;
	private Client client;
	private final MainFrame frame;
	private Disposable clientDisposable;
	private Disposable serverDisposable;
	private JMenuItem hostServer;
	private JMenuItem disconnectServer;
	private JMenuItem disconnectFromServer;
	private JMenuItem connectToSever;

	/**
	 * Constructor to initialize the menu swing components.
	 * @param frame the window frame.
	 */
	public Menu(final MainFrame frame) {
		this.frame = frame;
		init();
	}

	/**
	 * Method for initializing all the menu swing components and server/client active listeners.
	 */
	private void init() {
		currentShapeLabel = new JLabel();
		currentShapeLabel.setForeground(Color.DARK_GRAY);

		initOptionMenu();
		initDrawMenu();
		initThicknessMenu();
		initColorMenu();
		isClientActiveListener();
		isServerActiveListener();

		this.add(Box.createHorizontalGlue());
		this.add(currentShapeLabel);
	}

	/**
	 * Method for initializing the general option menu.
	 */
	private void initOptionMenu() {
		JMenu optionsMenu = new JMenu("General options");
		JMenuItem clearCanvas = new JMenuItem("Clear canvas");
		hostServer = new JMenuItem("Host server");
		disconnectServer = new JMenuItem("Stop hosting server");
		disconnectFromServer = new JMenuItem("Disconnect from server");
		connectToSever = new JMenuItem("Connect to server");

		EventObservable.getItemActionEventsObservable(clearCanvas)
				.subscribe(event -> {
					if (shouldClearCanvas()) {
						if (client == null && server == null) {
							clearCanvasEvent();
						} else {
							EventObservable.setCurrentEventSubject(new Event(Constants.CLEAR_CANVAS_EVENT));
						}
					}
				});

		EventObservable.getItemActionEventsObservable(hostServer)
				.subscribe(event -> {
					try {
						server = new Server(Constants.SERVER_PORT, frame);
						serverDisposable = Observable.just(server)
								.subscribeOn(Schedulers.single())
								.doOnNext(Server::startServer)
								.doOnDispose(server::closeServer)
								.subscribe(newServer -> {
									EventObservable.setCurrentServerSubject(newServer);
									disconnectServer.setEnabled(true);
									hostServer.setEnabled(false);
									connectToSever.setEnabled(false);
								});
					} catch (IOException e) {
						System.out.println("Could not host server! Error: " + e.getMessage());
						connectServerEventError(e, Constants.HOST_SERVER_ERROR_MESSAGE);
                    }
                });

		EventObservable.getItemActionEventsObservable(disconnectServer)
				.doOnSubscribe(disposable -> disconnectServer.setEnabled(false))
				.subscribe(event -> server.closeServer());

		EventObservable.getItemActionEventsObservable(connectToSever)
				.subscribe(event -> {
					try {
						client = new Client(Constants.HOST, Constants.SERVER_PORT, frame);

						clientDisposable = Observable.just(client)
								.subscribeOn(Schedulers.single())
								.doOnNext(Client::startClient)
								.doOnDispose(client::stopClient)
								.subscribe(newClient -> {
									EventObservable.setCurrentClientSubject(newClient);
									hostServer.setEnabled(false);
									connectToSever.setEnabled(false);
									disconnectFromServer.setEnabled(true);
								});
					} catch (IOException e) {
						System.out.println("Could not connect to server! Error: " + e.getMessage());
						connectServerEventError(e, Constants.CONNECT_TO_SERVER_ERROR_MESSAGE);
                    }
				});

		EventObservable.getItemActionEventsObservable(disconnectFromServer)
				.doOnSubscribe(disposable -> disconnectFromServer.setEnabled(false))
				.subscribe(event -> client.stopClient());

		optionsMenu.add(hostServer);
		optionsMenu.add(disconnectServer);
		optionsMenu.add(connectToSever);
		optionsMenu.add(disconnectFromServer);
		optionsMenu.add(clearCanvas);
		this.add(optionsMenu);
	}

	/**
	 * Method for initializing the thickness options menu.
	 */
	private void initThicknessMenu() {
		JMenu thicknessMenu = new JMenu("Thickness");
		ButtonGroup thicknessButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem thin = new JRadioButtonMenuItem("Thin");
		JRadioButtonMenuItem medium = new JRadioButtonMenuItem("Medium");
		JRadioButtonMenuItem thick = new JRadioButtonMenuItem("Thick");

		EventObservable.getItemEventsObservable(thin)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.doOnSubscribe(disposable -> {
					// Set thin option as initial selected
					thin.setSelected(true);
					EventObservable.setCurrentThicknessSubject(Constants.MENU_THIN_THICKNESS);
				})
				.subscribe(event -> EventObservable.setCurrentThicknessSubject(Constants.MENU_THIN_THICKNESS));

		EventObservable.getItemEventsObservable(medium)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentThicknessSubject(Constants.MENU_MEDIUM_THICKNESS));

		EventObservable.getItemEventsObservable(thick)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentThicknessSubject(Constants.MENU_THICK_THICKNESS));

		thicknessMenu.add(thin);
		thicknessMenu.add(medium);
		thicknessMenu.add(thick);

		thicknessButtonGroup.add(thin);
		thicknessButtonGroup.add(medium);
		thicknessButtonGroup.add(thick);

		this.add(thicknessMenu);
	}

	/**
	 * Method for initializing the shape options menu.
	 */
	private void initDrawMenu() {
		JMenu drawMenu = new JMenu("Painting options");
		ButtonGroup drawButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem drawRectangle = new JRadioButtonMenuItem("Rectangle");
		JRadioButtonMenuItem drawOval = new JRadioButtonMenuItem("Oval");
		JRadioButtonMenuItem drawLine = new JRadioButtonMenuItem("Line");
		JRadioButtonMenuItem drawFreehand = new JRadioButtonMenuItem("Freehand");

		EventObservable.getItemEventsObservable(drawRectangle)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.map(itemEvent -> new Rectangle())
				.doOnSubscribe(disposable -> {
					// Set Rectangle option as initial selected
					drawRectangle.setSelected(true);
					setCurrentShapeText("Drawing: Rectangle");
					EventObservable.setCurrentShapeSubject(new Rectangle());
				})
				.subscribe(rectangle -> {
					EventObservable.setCurrentShapeSubject(rectangle);
					setCurrentShapeText("Drawing: Rectangle");
				});

		EventObservable.getItemEventsObservable(drawOval)
				.filter(stateChange -> stateChange.getStateChange() == ItemEvent.SELECTED)
				.map(integer -> new Oval())
				.subscribe(oval -> {
					EventObservable.setCurrentShapeSubject(oval);
					setCurrentShapeText("Drawing: Oval");
				});

		EventObservable.getItemEventsObservable(drawLine)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.map(integer -> new Line())
				.subscribe(line -> {
					EventObservable.setCurrentShapeSubject(line);
					setCurrentShapeText("Drawing: Line");
				});

		EventObservable.getItemEventsObservable(drawFreehand)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.map(integer -> new Freehand())
				.subscribe(freehand -> {
					EventObservable.setCurrentShapeSubject(freehand);
					setCurrentShapeText("Drawing: Freehand");
				});

		drawMenu.add(drawRectangle);
		drawMenu.add(drawOval);
		drawMenu.add(drawLine);
		drawMenu.add(drawFreehand);

		drawButtonGroup.add(drawRectangle);
		drawButtonGroup.add(drawOval);
		drawButtonGroup.add(drawLine);
		drawButtonGroup.add(drawFreehand);

		this.add(drawMenu);
	}

	/**
	 * Method for initializing the color options' menu.
	 */
	private void initColorMenu() {
		JMenu colorMenu = new JMenu("Color");
		ButtonGroup colorButtonGroup = new ButtonGroup();

		JRadioButtonMenuItem colorBlack = new JRadioButtonMenuItem("Black");
		JRadioButtonMenuItem colorRed = new JRadioButtonMenuItem("Red");
		JRadioButtonMenuItem colorBlue = new JRadioButtonMenuItem("Blue");

		EventObservable.getItemEventsObservable(colorBlack)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.doOnSubscribe(disposable -> {
					// Set black color as initial color
					colorBlack.setSelected(true);
					EventObservable.setCurrentColorSubject(Color.BLACK);
				})
				.subscribe(event -> EventObservable.setCurrentColorSubject(Color.BLACK));


		EventObservable.getItemEventsObservable(colorRed)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentColorSubject(Color.RED));


		EventObservable.getItemEventsObservable(colorBlue)
				.filter(itemEvent -> itemEvent.getStateChange() == ItemEvent.SELECTED)
				.subscribe(event -> EventObservable.setCurrentColorSubject(Color.BLUE));

		colorMenu.add(colorBlack);
		colorMenu.add(colorRed);
		colorMenu.add(colorBlue);

		colorButtonGroup.add(colorBlack);
		colorButtonGroup.add(colorRed);
		colorButtonGroup.add(colorBlue);

		this.add(colorMenu);
	}

	/**
	 * Method for executing the client disconnect event by disposing resources, activating/disabling
	 * menu options and displaying a disconnect message.
	 */
	private void disconnectFromServerEvent() {
		if (clientDisposable != null) {
			clientDisposable.dispose();
		}
		client = null;
		disconnectFromServer.setEnabled(false);
		hostServer.setEnabled(true);
		connectToSever.setEnabled(true);
		connectServerEventError(null, Constants.CONNECTION_LOST_MESSAGE);
	}

	/**
	 * Method for executing the server disconnect event by disposing resources and
	 * activating/disabling menu options.
	 */
	private void disconnectServerEvent() {
		if (serverDisposable != null) {
			serverDisposable.dispose();
		}
		server = null;
		disconnectServer.setEnabled(false);
		hostServer.setEnabled(true);
		connectToSever.setEnabled(true);
	}

	/**
	 * Method for listening for a client inactive call to execute the client disconnect event.
	 */
	private void isClientActiveListener() {
		EventObservable.getIsClientActiveSubject()
				.subscribe(active -> {
					if (!active) {
						disconnectFromServerEvent();
					}
				});
	}

	/**
	 * Method for listening for a server inactive call to execute the server disconnect event.
	 */
	private void isServerActiveListener() {
		EventObservable.getIsServerActiveSubject()
				.subscribe(active -> {
					if (!active) {
						disconnectServerEvent();
					}
				});
	}

	/**
	 * Method for creating a confirmation dialog for the clear canvas event.
	 * @return true if "yes" is chosen.
	 */
	private boolean shouldClearCanvas() {
		int dialogResult = JOptionPane.showConfirmDialog(
				frame, Constants.CLEAR_CANVAS_MESSAGE, Constants.HEADER, JOptionPane.YES_NO_OPTION);

        return dialogResult == JOptionPane.YES_OPTION;
	}

	/**
	 * Method for executing a clear canvas event.
	 */
	public void clearCanvasEvent() {
		frame.getDrawingPanel().clearCanvas();
	}

	/**
	 * Method for creating a message dialog for connection/disconnection errors.
	 * @param e IOException.
	 * @param message the displayed dialog message.
	 */
	public void connectServerEventError(final IOException e, final String message) {
		if (e != null) {
			JOptionPane.showMessageDialog(frame, message + e.getMessage());
		} else {
			JOptionPane.showMessageDialog(frame, message);
		}
	}

	/**
	 * Method for setting the current shape JLabel text.
	 * @param text the current shape selected.
	 */
	private void setCurrentShapeText(final String text) {
		currentShapeLabel.setText(text);
	}
}
