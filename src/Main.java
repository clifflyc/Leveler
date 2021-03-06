import javafx.animation.Animation;
import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/*=================================================================
Leveler
Cliff Li
Mar 27, 2017
Java 8
=================================================================
Problem Definition 	–  Creates an interactive "Leveler" game, containing an algorithm that can solves for the least amount of moves
Input – user types in commands, user interacts with the game using mouse, loads data from file
Output – shows a visual representation of the game on screen with messages on the side, writes data to file
Process – uses a pruned brute force algorithm to determine the least amount of moves.
*/

/**
 * Main class: Contains the main method which is run first. Inherits a JavaFX
 * applications which is launched at the start of the program.
 * <p>
 * Identifiers:
 * <li>String BG_URL - the URL to the background image
 * <li>int WINDOW_HEIGHT - the height of the window in pixels
 * <li>int COMMAND_LINE_HEIGHT - the height of the command line in pixels
 * <li>int size - the current number of rows and columns in the board
 * <li>int speed - the current time it takes for an animation in milliseconds
 * <li>Stage stage - the instance of the stage created when this application
 * launches
 * <li>GridPane grid - a JavaFX GridPane node which contains the labels
 * representing the grid values
 * <li>Label[][] labelsGrid - a 2D array of labels, these reference the same
 * labels contained by the GridPane
 * <li>Label status - the label displaying the current status
 * <li>Label log - the label displaying a log of messages
 * <li>String logText - represents the String that is to be the text of the log
 * label.
 * <li>Board board - an instance of Board that stores the current state of the
 * board.
 * <li>Board board - an instance of Board that stores the original state of the
 * board at the start of the current game.
 * <li>boolean isSolving - is the program currently running the algorithm to
 * solve the board?
 * <li>boolean isPlayingBack - is the program currently running an instance of
 * Tracer to replay a solution to the game?
 * <li>boolean edit - are we in edit mode?
 */
public class Main extends Application {
	static final String BG_URL = "https://i.imgur.com/rGiw7aW.png";
	static final int WINDOW_HEIGHT = 800;
	static final int COMMAND_LINE_HEIGHT = 50;
	static int size;
	static int speed;

	Stage stage;
	GridPane grid;
	Label[][] labelsGrid;
	Label status;
	Label log;
	String logText;

	Board board;
	Board oriBoard;

	boolean isSolving = false;
	boolean isPlayingBack = false;
	boolean edit = false;

	/**
	 * main method: Runs first, at the start of program. Calls the
	 * <code>launch</code> method inherited from
	 * {@link javafx.application.Application}.
	 * 
	 * @param args
	 *            an array of String arguments (unused in program)
	 */
	public static void main(String[] args) {
		System.out.println("launching!");
		launch(args);
		System.out.println("exit");
	}// end main method

	/**
	 * start method: Called at the start of the application for initialization.
	 * Sets the title of the window. Initializes all the UI and display
	 * components and adds it to the window. Shows the window, instantiates
	 * {@code speed} and {@code size} integer fields, instantiate the
	 * {@code logText} String, the {@code board} field, and all boolean fields,
	 * and calls {@code initNewGame} to initiate a new game.
	 * 
	 * @param stage
	 *            the primary instance of {@link javafx.stage.Stage} created
	 *            this Application.
	 */
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setTitle("Leveler!");
		size = 4;
		speed = 350;
		board = new Board(size);
		logText = "";
		isSolving = isPlayingBack = edit = false;
		setup();
		initNewGame();
		status.setText("enter 'help' in the input text field below for a list of commands");
		stage.show();
	}// end start method

	/**
	 * setup method: Initializes the status label, the log label and log scroll
	 * pane, the input text field, the background image, and the game grid. This
	 * is done by calling the methods {@code setupStatusLabel}, {@code setupLog}
	 * , {@code setupTextField} {@code setupBackgroundImage}, and
	 * {@code setupGrid}, each of which sets up one of the components mentioned
	 * in the method name above.
	 * <p>
	 * local variables:
	 * <li>Group root - a JavaFX parent node for all the components to be added
	 * under
	 */
	void setup() {
		Group root = new Group();

		setupStatusLabel(root);
		setupLog(root);
		setupTextField(root);
		setupGrid(root, calculateBoxSize(WINDOW_HEIGHT, size), size);
		setupBackgroundImage(root);
		stage.setScene(new Scene(root, WINDOW_HEIGHT * 1.5, WINDOW_HEIGHT));
	}// end setup method

	/**
	 * calculateBoxSize method: Calculates the width of a single square in the
	 * grid.
	 * 
	 * @param totalWidth
	 *            the total width of the grid
	 * @param numberOfBoxes
	 *            the number of squares to fit inside the grid
	 * @return the most appropriate box size as an integer
	 */
	int calculateBoxSize(int totalWidth, int numberOfBoxes) {
		return totalWidth / numberOfBoxes;
	}// end calculateBoxSize method

	/**
	 * setupStatusLabel method: Instantiates the status label. Sets its height
	 * to be equal to {@code COMMAND_LINE_HEIGHT}, and its width to be half the
	 * width of the grid. Positions it in the upper right corner, to the right
	 * of the grid. Adds the label to the node passed as parameter.
	 * 
	 * @param root
	 *            the parent node for the status label.
	 */
	void setupStatusLabel(Group root) {
		status = new Label();
		status.setPrefSize(WINDOW_HEIGHT / 2, COMMAND_LINE_HEIGHT);
		status.setLayoutX(WINDOW_HEIGHT);
		root.getChildren().add(status);
	}// end setupStatusLabel method

	/**
	 * setupLog method: Instantiates the log label and places it inside a scroll
	 * pane. Sets the log label to have no background and to have text in the
	 * top left. The scroll pane is set to fill the space between the status
	 * label and the input text field, on the right of the grid. Adds the scroll
	 * pane, containing the label, to the node passed as parameter.
	 * <p>
	 * Local variables:
	 * <li>ScrollPane logPane - the scroll pane component created in this method
	 * 
	 * @param root
	 *            the parent node for the status label.
	 */
	void setupLog(Group root) {
		ScrollPane logPane = new ScrollPane();

		log = new Label();
		log.setBackground(Background.EMPTY);
		log.setAlignment(Pos.TOP_LEFT);
		log.maxHeight(Double.POSITIVE_INFINITY);
		log.setText(logText);

		logPane.setContent(log);
		logPane.setPrefSize(WINDOW_HEIGHT / 2, WINDOW_HEIGHT - 2 * COMMAND_LINE_HEIGHT);
		logPane.setLayoutX(WINDOW_HEIGHT);
		logPane.setLayoutY(COMMAND_LINE_HEIGHT);
		logPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		logPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		root.getChildren().add(logPane);
	}// end setupLog method;

	/**
	 * setupTextField method: Instantiates the input text field, with the width
	 * set to half of the width of the grid, and positions it in the lower right
	 * corner, to the right of the grid. Adds an event handler onto the
	 * OnKeyPressed property of the text field, which calls {@code command} and
	 * passes it the text inside the text field when the user presses enter.
	 * Adds the text field to the parent node passed as parameter.
	 * <p>
	 * Local variables:
	 * <li>TextField textField - the text field component created in this
	 * method.
	 * 
	 * 
	 * @param root
	 *            the parent node for the input text field
	 */
	void setupTextField(Group root) {
		TextField textField = new TextField();
		textField.setPrefSize(WINDOW_HEIGHT / 2, COMMAND_LINE_HEIGHT);
		textField.setLayoutX(WINDOW_HEIGHT);
		textField.setLayoutY(WINDOW_HEIGHT - COMMAND_LINE_HEIGHT);
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					command(textField.getText());
					textField.clear();
				}
			}
		});
		root.getChildren().add(textField);
	}// end setupTextField method

	/**
	 * setupBackgroundImage method: Instantiates a, {@code ImageView} object and
	 * adds it to the parent node passed as parameter. Loads the background
	 * image from the URL specified by the static constant {@code BG_URL}. Set
	 * the image to make it 15% transparent, and to not interact with the mouse.
	 * <p>
	 * Local variables:
	 * <li>ImageView imageView - the background image created in this method
	 * 
	 * @param root
	 *            the parent node for the image component
	 */
	void setupBackgroundImage(Group root) {
		ImageView imageView = new ImageView(BG_URL);
		imageView.setPreserveRatio(true);
		imageView.fitHeightProperty().bind(stage.heightProperty());
		imageView.setOpacity(0.15);
		imageView.setMouseTransparent(true);
		root.getChildren().add(imageView);
	} // end setupBackgroundImage method

	/**
	 * setupGrid method: Instantiates {@code grid} and {@code labelsGrid}. A
	 * label will be created for each square in the grid, and the label will be
	 * added to the grid pane, and stored in its respective coordinate in the
	 * {@code labelsGrid} array. Each label will have a centered text with a
	 * font size that is 3 times smaller than the width of the grid.
	 * <p>
	 * Each label will have a LevelerEventHandler, which will contain the
	 * label's coordinate. When the label is clicked on by the mouse, the
	 * handler call {@link changeGrid} passing the coordinate and a value 1 or
	 * -1 (based on left or right click) to change the value at that coordinate
	 * on the grid.
	 * <p>
	 * The grid pane, containing the labels, is added to the parent node passed
	 * as parameter.T
	 * 
	 * @param root
	 *            the parent node for the grid pane @
	 * 
	 */
	void setupGrid(Group root, int boxSize, int size) {
		grid = new GridPane();
		labelsGrid = new Label[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				labelsGrid[i][j] = new Label("");
				labelsGrid[i][j].setPrefSize(boxSize, boxSize);
				labelsGrid[i][j].setAlignment(Pos.CENTER);
				labelsGrid[i][j].setTextAlignment(TextAlignment.CENTER);
				labelsGrid[i][j].setFont(new Font(boxSize / 3));

				LevelerEventHandler handler = new LevelerEventHandler() {
					@Override
					public void handle(MouseEvent event) {
						int change;
						if (event.getButton() == MouseButton.PRIMARY)
							change = 1;
						else
							change = -1;
						changeGrid(coord, change);
					}
				};

				handler.coord = new Pair((short) i, (short) j);
				labelsGrid[i][j].setOnMousePressed(handler);
				grid.add(labelsGrid[i][j], j, i);
			}
		}
		root.getChildren().add(grid);
	} // end setupGrid method

	/**
	 * command method: Does an action or calls a method to do an action based on
	 * the String passed as parameter. Splits the String into an array, then
	 * adds a blank String at the end of the array. Then, uses multiple levels
	 * of switch statements on the Strings in the String array to decide which
	 * actions to take. If {@code isSolving} or {@code isPlayingBack} is true,
	 * blocks the user from using any command except for the "stop" command. If
	 * a command passed in does not satisfy the case for any action to be taken,
	 * a message will be sent to the user using addLog method
	 * 
	 * @param command
	 *            a String to determine which actions will be done.
	 */
	void command(String command) {
		String[] t = command.split(" ");
		String[] split = new String[t.length + 1];
		// copy t into split, an identical array except with an extra blank
		// String at the end.
		for (int i = 0; i < t.length; i++) {
			split[i] = t[i];
		}
		split[split.length - 1] = "";

		if (isSolving || isPlayingBack) {
			if (split[0].equals("stop")) {
				stopAll();
			} else {
				pleaseWaitMessage();
			}
		} else {
			switch (split[0]) {
			case "stop": // stops playback/solving
				stopAll();
				break;

			case "new": // new game
				switch (split[1]) {
				case "game":
					initNewGame();
					addLog("Started new game.");
					break;
				default:
					addLog("New what?");
					break;
				}
				break;

			case "reset": // reset board to original
				restoreOriginalBoard();
				addLog("Reset back to original board.");
				break;

			case "size": // set the size
				try {
					setSize(Integer.parseInt(split[1]));
				} catch (NumberFormatException e) {
					addLog("I don't understand what is '" + split[1] + "'.\nIs it a number?");
				}
				break;

			case "speed": // set animation and playback speed
				try {
					speed = Integer.parseInt(split[1]);
					addLog("Set animation speed to " + speed + "!");
				} catch (NumberFormatException e) {
					addLog("I don't understand what is '" + split[1] + "'.\nIs it a number?");
				}
				break;

			case "solve": // finds least moves
				setStatus("< =_= > working...!");
				algorithm();
				break;
			case "playback": // playback solution
				playback();
				break;

			case "edit": // manually change individual squares on the grid
				switch (split[1]) {
				case "":
					edit = true;
					addLog("Editing turned on");
					break;
				case "off":
					edit = false;
					addLog("Editing turned off");
					break;
				}
				break;
			case "clear": // clear log
				logText = "";
				addLog("log cleared");
				break;
			case "undo": // undo one step
				undo();
				break;
			case "read": // read from file
				importBoard(split[1]);
				break;
			case "write": // write to file
				exportBoard(split[1]);
				break;
			case "moves": // log current number of moves
				addLog("Current moves = " + board.moves);
				break;
			case "help": // log current number of moves
				logHelp();
				break;
			case "info": // log current number of moves
				logInfo();
				break;

			default:
				addLog("I don't know what you are saying.");
				break;
			}
		}

	}// end command method

	/**
	 * stopAll method: calls the stop method in Algorithm and Tracer, to stop
	 * them from running.
	 */
	void stopAll() {
		Algorithm.stop();
		Tracer.stop();
	}// end stopAll method

	/**
	 * pleaseWaitMessage method: Sends a message telling the user to wait for
	 * solving or playing-back of solution to end.
	 */
	void pleaseWaitMessage() {
		addLog("Please wait until " + (isSolving ? "solving" : "playback") + " is finished.");
		addLog("You can enter 'stop' to cancel the " + (isSolving ? "solving" : "playback."));
	}// end pleaseWaitMessage method

	/**
	 * printBoard method: Updates the labels in {@code labelsGrid} to match the
	 * Board passed in as parameter. The color of the label will also be updated
	 * and an animation will be created and applied to the label fading from its
	 * current color to its new color.
	 * <p>
	 * Overloaded method for printBoard - this one supplies false for the
	 * {@code flash} parameter.
	 * 
	 * @param Board
	 *            the board to display on screen
	 */
	void printBoard(Board Board) {
		printBoard(Board, false);
	}// end printBoard method

	/**
	 * printBoard method: Updates the labels in {@code labelsGrid} to match the
	 * Board passed in as parameter. The color of the label will also be updated
	 * and an animation will be created and applied to the label fading from its
	 * current color to its new color.
	 * 
	 * @param Board
	 *            the board to display on screen
	 * @param flash
	 *            if {@code true}, creates a flash effect at the start of the
	 *            animation by darkening the current color of the label.
	 */
	void printBoard(Board Board, boolean flash) {
		int[][] gameGrid = Board.grid;
		for (int i = 0; i < gameGrid.length; i++) {
			for (int j = 0; j < gameGrid[0].length; j++) {
				labelsGrid[i][j].setText(gameGrid[i][j] + "");
				if (flash || board.grid[i][j] != Board.grid[i][j]) {
					Color start = getColor(board.grid[i][j]);
					Color end = getColor(Board.grid[i][j]);
					if (flash) {
						start = start.darker();
					}
					Animation animation = new TileAnimation(labelsGrid[i][j], start, end, Duration.millis(speed));
					animation.play();
				}
			}
		}
	}// end printBoard method

	/**
	 * getColor method: Returns a color specific for a number. For numbers below
	 * and including 0, {@code Color.WHEAT} is returned. For positive numbers, a
	 * color is decided based on the number mod by 7, and gets more desaturated
	 * as the number increases.
	 * 
	 * @param number
	 *            the number to base the color on
	 * @return a color specific to the number passed as parameter
	 */
	Color getColor(int number) {
		Color color;
		switch (number % 7) {
		case 1:
			color = Color.MEDIUMPURPLE;
			break;
		case 2:
			color = Color.CORNFLOWERBLUE;
			break;
		case 3:
			color = Color.LIGHTGREEN;
			break;
		case 4:
			color = Color.ORANGE;
			break;
		case 5:
			color = Color.LIGHTPINK;
			break;
		case 6:
			color = Color.ORANGERED;
			break;
		case 7:
			color = Color.MEDIUMVIOLETRED;
			break;
		default:
			color = Color.WHEAT;
		}

		for (int k = 0; k < number / 7; k++) {
			color = color.desaturate();
		}
		return color;
	}// end getColor method

	/**
	 * logHelp method: Displays a list of commands and what each command does in
	 * the log.
	 */
	void logHelp() {
		addLog("\nList of commands:" + "\ninfo - displays some information about the game"
				+ "\nmoves - displays current number of moves"
				+ "\nnew game - starts a new game, with a new grid of numbers"
				+ "\nreset - restore the grid of numbers back to the state at the"
				+ "\n		start of the current game."
				+ "\nsize [integer] - sets the size of the game grid to a number representing"
				+ "\n		number of squares in a row(will start a new game when used) "
				+ "\nspeed [integer] - sets the speed of animations and the solution playback"
				+ "\n		speed to a number in ms" + "\nundo - undoes the most recent move"
				+ "\nclear - clear this console" + "\n"
				+ "\nsolve - finds the least amount of moves possible for the current game"
				+ "\nplayback - plays back the solution after using the solve command"
				+ "\nstop - stops the process of solving or playing-back a solution" + "\n"
				+ "\nedit - this command allows you to manually change the values in the"
				+ "\n		grid without counting it as a move (this will start a new game)"
				+ "\nedit off - turns off editing" + "\nread [filepath] - start a new games with values from a file"
				+ "\nwrite [filepath] - saves the current grid values to a file");
	}// end logHelp method

	/**
	 * logInfo method: Displays to the log an description of the game.
	 */
	void logInfo() {
		addLog("\nThis is a game called leveler." + "\nTo the left are tiles on a n by n grid."
				+ "\nYou can left click a tile to increment it." + "\nYou can right click a tile to decrement it."
				+ "\nWhen a tile changes, all tiles touching it" + "\n  with the same value changes as well"
				+ "\nThe goal is to make all of the tiles the same"
				+ "\n  value in the least amount of moves possible.\n");
	}// end logInfo method

	/**
	 * setSize method: Sets the size of the grid to an integer passed as
	 * parameter. If the size is not between 1 and 127, reject the number and
	 * tell the user. Otherwise, set the size and call {@code setup} again to
	 * remake the grid with appropriate size.
	 * 
	 * @param s
	 */
	void setSize(int s) {
		if (s > 0 && s <= Byte.MAX_VALUE) {
			size = s;
			board = new Board(size);
			setup();
			initNewGame();
			addLog("Set board size to " + size + "!");
		} else {
			addLog("Make sure the number is between 1 and " + Byte.MAX_VALUE + "!");
		}
	}// end setSize method

	/**
	 * undo method: Set the current board to {@code board.previousState). This
	 * is done by calling {@code setCurrentBoard} and passing the previous state
	 * as parameter.
	 */
	void undo() {
		if (board.previousState != null) {
			printBoard(board.previousState);
			setCurrentBoard(board.previousState);
			addLog("undo successful");
		} else {
			addLog("nothing to undo");
		}
	}// end undo method

	/**
	 * setStaus method: Sets the text of the status label.
	 * 
	 * @param text
	 *            the String to set the text of the status label to
	 */
	void setStatus(String text) {
		status.setText(text);
	}// end setStatus

	/**
	 * addLog method: Adds a line in the log label. This is done by adding a new
	 * line of text to {@code logText} and then setting the text of the log
	 * label to {@code logText}.
	 * 
	 * @param text
	 *            the String to set the text of the log label to
	 */
	void addLog(String text) {
		logText += "\n" + text;
		log.setText(logText);
	} // end addLog method

	/**
	 * initNewGame method: Starts a new game, by generating a random board and
	 * setting it as {@code oriBoard}, and then calling
	 * {@code restoreOriginalBoard} restore the game to {@code oriBoard}. Also
	 * sets solution field of Algorithm to null, since there is no found
	 * solution for the new game yet.
	 */
	void initNewGame() {
		oriBoard = Board.generateRandomBoard(size);
		restoreOriginalBoard();
		Algorithm.solution = null;
	}// end initNewGame method

	/**
	 * importBoard method: Starts a new game with the values of the board
	 * imported from a file. Calls {@code FileIO.read} to get a 2D integer array
	 * read from the file. Then, resize the grid to match the arrayLength by
	 * calling {@code setSize}. After, create a new Board, set its grid to the
	 * integer array and starts a new game using this new Board as
	 * {@code oriBoard}.
	 * 
	 * @param filePath
	 *            the path for the file, if blank, reads from default path
	 */
	void importBoard(String filePath) {
		int[][] grid = FileIO.read(filePath);
		if (grid != null) {
			setSize(grid.length);
			oriBoard = new Board(grid.length);
			oriBoard.grid = grid;
			restoreOriginalBoard();
			Algorithm.solution = null;
			addLog("Successfully read from file!");
		} else {
			addLog("Error reading from file!");
		}
	} // end importBoard method

	/**
	 * exportBoard method: Saves the current values of the grid to a file by
	 * calling {@code FileIO.write} and passing the integer array
	 * {@code board.grid} (which hold the values of the current grid) as
	 * parameter.
	 * 
	 * 
	 * @param filePath
	 *            the path for the file, if blank, writes to default path
	 */
	void exportBoard(String filePath) {
		FileIO.write(board.grid, filePath);
		addLog("Finished exporting to file!");
	}// end exportBoard method

	/**
	 * setCurrentBoard method: sets {@code board} to the Board passed as
	 * parameter.
	 * 
	 * @param board
	 *            the Board object to set {@code board} as
	 */
	void setCurrentBoard(Board board) {
		this.board = board;
	}// end setCurrentBoard method

	/**
	 * restoreOriginalBoard method: sets the current board back to
	 * {@code oriBoard}, the initial state at the start of the game. When
	 * setting, a deep clone of {@code oriBoard} is created to avoid mutating
	 * it.
	 * 
	 */
	void restoreOriginalBoard() {
		Board oriClone = oriBoard.getDeepClone();
		printBoard(oriClone, true);
		setCurrentBoard(oriClone);
	}// end restoreOriginalBoard method

	/**
	 * changeGrid method: Changes the value of a piece at a coordinate. If
	 * currently solving or playing-back a solution, the request is blocked. If
	 * editing, only a single square is changed by the value specified in
	 * {@code change} parameter. Otherwise, the entire piece connected to the
	 * coordinate is changed by the value specified in {@code change} parameter.
	 * A branch of the current board is created and its reference replaces the
	 * current board, the branch will hold reference to the current board if the
	 * user wishes to undo.
	 * <p>
	 * Afterwards, calls {@code checkWin} method to tell the player if they have
	 * won yet.
	 * 
	 * 
	 * @param coord
	 *            the coordinate the change is to be applied to
	 * @param change
	 *            the value to add to the current value at the coordinate
	 *            specified
	 */
	void changeGrid(Pair coord, int change) {
		if (isSolving || isPlayingBack) {
			pleaseWaitMessage();
			return;
		}

		if (edit) {
			Board clone = board.getDeepClone();
			clone.grid[coord.i1][coord.i2] += change;
			oriBoard = clone;
			oriBoard.moves = 0;
			restoreOriginalBoard();
			Algorithm.solution = null;
		} else {
			Board branch = board.getNewBranch();
			branch.changeGrid(coord, change);
			printBoard(branch);
			setCurrentBoard(branch);
		}

		checkWin();
	}// end changeGrid method

	/**
	 * checkWin method: Checks if the current board satisfies win condition by
	 * calling the {@code hasWon} method of the board. If the player has won,
	 * tell the player in the log.
	 */
	void checkWin() {
		if (board.hasWon()) {
			addLog("Game won after " + board.moves + " moves!");
		}
	}// end checkWin method

	/**
	 * algorithm method: Sets {@code isSolving} to true. Starts a new Thread
	 * containing a new instance of the Algorithm class, which is constructed
	 * with the initial grid values for the current game. Adds a listener to the
	 * {@code messageProperty} of the instance, which passes any messages to the
	 * {@code handleMessage} method.
	 */
	void algorithm() {
		isSolving = true;
		Algorithm algorithm = new Algorithm(oriBoard.grid);
		algorithm.messageProperty().addListener((o, oldMessage, newMessage) -> handleMessage(newMessage));
		new Thread(algorithm).start();
	}// end algorithm method

	/**
	 * handleMessage method: Handles the messages from the Algorithm instances.
	 * Updates the status label with the message, If the message starts with
	 * "Done!" as the first word, then assume that the algorithm is done
	 * solving, and set {@code isSolving} to false.
	 * 
	 * @param message
	 *            the message update from the Algorithm instance
	 */
	void handleMessage(String message) {
		setStatus(message);
		String[] words = message.split(" ");

		if (words[0].equals("Done!")) {
			isSolving = false;
		}

	}// end handleMessage method

	/**
	 * playback method: Plays back the most recent solution found for the
	 * current game. If there is a solution found, set {@code isPlayingBack} to
	 * true, and start a new Thread containing an instance of the Tracer class.
	 * The instance will have an added listener which calls
	 * {@code printTracerGrid} method whenever an update message is received.
	 */
	void playback() {
		if (Algorithm.solution != null) {
			isPlayingBack = true;
			Tracer tracer = new Tracer(oriBoard.getDeepClone(), Algorithm.solution);
			tracer.messageProperty().addListener((o, oldMessage, newMessage) -> printTracerGrid());
			new Thread(tracer).start();
			setStatus("Playing solution...");
		} else {
			addLog("No solutions to play for the current game (did you use 'solve' already?)");
		}
	}// end playback method;

	/**
	 * printTracerGrid method: Polls the playback queue from the Tracer class
	 * for a Board, and prints that board to the screen using {@code printBoard}
	 * method. If this is called and the playback queue is empty, assume that
	 * playback has ended, and set {@code isPlayingBack} to false.
	 */
	void printTracerGrid() {
		Board b = Tracer.playbackQueue.poll();
		printBoard(b);
		setCurrentBoard(b);
		if (Tracer.playbackQueue.isEmpty()) {
			setStatus("Done playing solution...");
			isPlayingBack = false;
		}
	}// end printTracerGrid
}// end Main method