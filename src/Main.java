import javafx.animation.Animation;
import javafx.application.*;
import javafx.geometry.Insets;
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

//TODO
//help commands
//no concurrent solves

public class Main extends Application {
	static final int SCREEN_WIDTH = 800;
	static final int COMMAND_LINE_HEIGHT = 50;

	Stage stage;
	Group root;

	Label[][] labelsGrid;
	GridPane grid;

	Label status;
	Label log;
	TextField textField;
	ImageView imageView;
	ScrollPane logPane;

	Board board;
	Board oriBoard;

	String logText;
	int size = 4;
	int boxSize = 200;
	static int speed = 350;

	boolean isSolving = false;
	boolean isPlayingBack = false;
	boolean checking = false;
	boolean edit = false;

	public static void main(String[] args) {
		System.out.println("launching!");
		launch(args);
		System.out.println("exit");
	}

	@Override
	public void start(Stage stage) {
		logText = "";
		this.stage = stage;
		stage.setTitle("Leveler!");

		setup();
		stage.show();
	}

	void setup() {
		calculateBoxSize();
		initStuff();
		stuff();
		initNewGame();
		addToRoot();
		stage.setScene(new Scene(root, SCREEN_WIDTH * 1.5, SCREEN_WIDTH));

	}

	void calculateBoxSize() {
		boxSize = SCREEN_WIDTH / size;

	}

	void initStuff() {
		status = new Label();
		status.setPrefSize(SCREEN_WIDTH / 2, COMMAND_LINE_HEIGHT);
		status.setLayoutX(SCREEN_WIDTH);

		log = new Label();
		log.setBackground(Background.EMPTY);
		log.setAlignment(Pos.TOP_LEFT);
		log.maxHeight(Double.POSITIVE_INFINITY);
		log.setText(logText);
		logPane = new ScrollPane();
		logPane.setPrefSize(SCREEN_WIDTH / 2, SCREEN_WIDTH - 2 * COMMAND_LINE_HEIGHT);
		logPane.setContent(log);
		logPane.setLayoutX(SCREEN_WIDTH);
		logPane.setLayoutY(COMMAND_LINE_HEIGHT);
		logPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		logPane.setHbarPolicy(ScrollBarPolicy.NEVER);

		textField = new TextField();
		textField.setPrefSize(SCREEN_WIDTH / 2, COMMAND_LINE_HEIGHT);
		textField.setLayoutX(SCREEN_WIDTH);
		textField.setLayoutY(SCREEN_WIDTH - COMMAND_LINE_HEIGHT);
		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					command(textField.getText());
					textField.clear();
				}
			}
		});

		imageView = new ImageView(
				"https://s-media-cache-ak0.pinimg.com/originals/f4/54/b9/f454b97c3820bfb6412f4ca1567d80e9.jpg");
		imageView.setPreserveRatio(true);
		imageView.fitHeightProperty().bind(stage.heightProperty());
		imageView.setOpacity(0.15);
		imageView.setMouseTransparent(true);
	}

	void stuff() {
		grid = new GridPane();
		labelsGrid = new Label[size][size];
		board = new Board(size);

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
						changeGrid(row, column, change);
					}

				};
				handler.row = i;
				handler.column = j;
				labelsGrid[i][j].setOnMousePressed(handler);
				grid.add(labelsGrid[i][j], j, i);
			}
		}
	}

	void addToRoot() {
		root = new Group();
		root.getChildren().add(grid);
		root.getChildren().add(logPane);
		root.getChildren().add(textField);
		root.getChildren().add(status);
		root.getChildren().add(imageView);

	}

	// TODO HOW TO DO THIS giant switch case?
	void command(String command) {
		String[] t = command.split(" ");
		String[] split = new String[t.length + 1];
		for (int i = 0; i < t.length; i++) {
			split[i] = t[i];
		}
		split[split.length - 1] = "";

		if (isSolving|| isPlayingBack) {
			if(split[0].equals("stop")){
				stopAll();
			}else{
				pleaseWaitMessage();
			}
		}else{
			switch (split[0]) {
			case "stop":
				stopAll();
				break;
			case "new":
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

			case "reset":
				restoreOriginalBoard();
				addLog("Reset back to original board.");
				break;

			case "size":
				try {
					size = Integer.parseInt(split[1]);
					setup();
					addLog("Set board size to " + size + "!");
				} catch (NumberFormatException e) {
					addLog("I don't understand what is '" + split[1] + "'.\nIs it a number?");
				}

				break;

			case "speed":
				try {
					speed = Integer.parseInt(split[1]);
					addLog("Set animation speed to " + speed + "!");
				} catch (NumberFormatException e) {
					addLog("I don't understand what is '" + split[1] + "'.\nIs it a number?");
				}

				break;
			case "check":
				switch (split[1]) {
				case "stop":
					checking = false;
					break;
				case "":
					check();
					break;
				default:
					addLog("Check what?");
					break;
				}
				break;
			case "smart":
				switch (split[1]) {
				case "":
					setStatus("< =_= > working...!");
					algorithm();
					break;
				case "playback":
					playbackSmart();
					break;
				case "stop":
					Algorithm.stop();
					break;
				default:
					addLog("Smart what?");
					break;
				}
				break;
			case "smart2":
				switch (split[1]) {
				case "":
					setStatus("< =_= > working...!");
					algorithm2();
					break;
				case "playback":
					playbackSmart2();
					break;
				case "stop":
					Algorithm2.stop();
					break;
				default:
					addLog("Smart2 what?");
					break;
				}
				break;
			case "edit":
				switch (split[1]) {
				case "":
					edit = true;
					break;
				case "off":
					edit = false;
					break;
				}
				break;

			case "undo":
				printBoard(board.previousState);
				setCurrentBoard(board.previousState);
				break;
			case "read":
				importBoard();
				break;
			case "write":
				exportBoard();
				break;
			default:
				addLog("I don't know what you are saying.");
				break;
			}
		}
	}

	void stopAll(){
		Algorithm.stop();
		Algorithm2.stop();
		Tracer.stop();
		checking=false;
	}
	
	void pleaseWaitMessage(){
		addLog("Please wait until "+ (isSolving? "solving" : "playback") + "is finished.");
		addLog("You can enter 'stop' to cancel the " +(isSolving? "solving" : "playback."));
	}
	void printBoard(Board Board) {
		printBoard(Board, false);
	}

	void printBoard(Board Board, boolean flash) {
		int[][] gameGrid = Board.grid;
		for (int i = 0; i < gameGrid.length; i++) {
			for (int j = 0; j < gameGrid[0].length; j++) {

				setLabelText(labelsGrid[i][j], gameGrid[i][j]);

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
	}

	void setLabelText(Label label, int number) {
		String text = null;
		switch (number) {
		case 0:
			text = "零";
			break;
		case 1:
			text = "一";
			break;
		case 2:
			text = "二";
			break;
		case 3:
			text = "三";
			break;
		case 4:
			text = "四";
			break;
		case 5:
			text = "五";
			break;
		case 6:
			text = "六";
			break;
		case 7:
			text = "七";
			break;
		case 8:
			text = "八";
			break;
		case 9:
			text = "九";
			break;
		case 10:
			text = "十";
			break;
		default:
			text = number + "";
			break;
		}
		label.setText(text);
	}

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
	}

	void setStatus(String text) {
		status.setText(text);
	}

	void addLog(String text) {
		logText += "\n" + text;
		log.setText(logText);
	}

	void initNewGame() {
		oriBoard = Board.generateRandomBoard(size);
		restoreOriginalBoard();
	}

	void importBoard() {
		int[][] grid = FileIO.read();
		if (grid != null) {
			size = grid.length;
			setup();
			oriBoard = new Board(grid.length);
			oriBoard.grid = grid;
			restoreOriginalBoard();
			addLog("Successfully read from file!");
		} else {
			addLog("Error reading from file!");
		}
	}

	void exportBoard() {
		FileIO.write(board.grid);
		addLog("Successfully wrote to file!");

	}

	void setCurrentBoard(Board board) {
		this.board = board;
	}

	void restoreOriginalBoard() {
		Board oriClone = oriBoard.getDeepClone();
		printBoard(oriClone, true);
		setCurrentBoard(oriClone);
	}

	void changeGrid(int row, int column, int change) {
		if (isSolving|| isPlayingBack) {
			pleaseWaitMessage();
			return;
		}
		
		if (edit) {
			Board clone = board.getDeepClone();
			clone.grid[row][column] += change;
			oriBoard = clone;
			restoreOriginalBoard();
		} else {
			Board branch = board.getNewBranch();
			branch.changeGrid(row, column, change);
			printBoard(branch);
			setCurrentBoard(branch);
		}
	}

	void algorithm2() {
		Algorithm2 algorithm = new Algorithm2(oriBoard.grid);
		algorithm.messageProperty().addListener((o, oldmessage, newMessage) -> handleMessageSmart2(newMessage));
		new Thread(algorithm).start();
	}

	void handleMessageSmart2(String message) {
		setStatus(message);
		String[] words = message.split(" ");
		if (words[0].equals("Done!")) {
			if (checking) {
				algorithm();
			}
		}
	}

	void algorithm() {
		isSolving = true;
		Algorithm algorithm = new Algorithm(oriBoard.grid);
		algorithm.messageProperty().addListener((o, oldMessage, newMessage) -> handleMessageSmart(newMessage));
		new Thread(algorithm).start();
	}

	void handleMessageSmart(String message) {
		setStatus(message);
		String[] words = message.split(" ");

		if (words[0].equals("Done!")) {
			isSolving = false;
			if (checking) {
				if (Algorithm.solution.moves != Algorithm2.solution.moves) {
					checking = false;
					setStatus("Solution mismatch! Your algorithm sucks!");
				} else {
					initNewGame();
					algorithm2();
				}
			}
		}

	}

	void playbackSmart() {
		isPlayingBack = true;
		Tracer tracer = new Tracer(oriBoard.getDeepClone(), Algorithm.solution);
		tracer.messageProperty().addListener((o, oldMessage, newMessage) -> printTracerGrid());
		new Thread(tracer).start();
		setStatus("Playing smart solution...");
	}

	void playbackSmart2() {
		Tracer tracer = new Tracer(oriBoard.getDeepClone(), Algorithm2.solution);
		tracer.messageProperty().addListener((o, oldMessage, newMessage) -> printTracerGrid());
		new Thread(tracer).start();
		setStatus("Playing smart2 solution...");
	}

	void printTracerGrid() {
		Board b = Tracer.playbackQueue.poll();
		printBoard(b);
		setCurrentBoard(b);
		if (Tracer.playbackQueue.isEmpty()) {
			setStatus("Done playing solution...");
			isPlayingBack = false;
		}
	}

	void check() {
		checking = true;
		algorithm2();
	}

}