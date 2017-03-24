import javafx.animation.Animation;
import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

//
public class Main extends Application {
	final int SCREENWIDTH = 800;

	Stage stage;
	Group root;

	Label[][] labelsGrid;
	GridPane grid;
	TextField textField;
	Label chat;
	ImageView imageView;

	BoardState board;
	BoardState oriBoard;

	int size = 4;
	int boxSize = 200;
	static int speed = 350;

	boolean autoplay = false;
	boolean checking = false;
	boolean edit = false;
	
	public static void main(String[] args) {
		System.out.println("launching!");
		launch(args);
		System.out.println("exit");
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setTitle("Leveler!");
		setup();
		stage.show();
	}

	void setup() {
		calculateBoxSize();
		initStuff();
		stuff();
		addToRoot();
		stage.setScene(new Scene(root, size * boxSize, size * boxSize + 100));

	}

	void calculateBoxSize() {
		boxSize = SCREENWIDTH / size;

	}

	void initStuff() {
		chat = new Label();
		chat.setPrefSize(size * boxSize, 60);
		chat.setLayoutY(size * boxSize);

		textField = new TextField();
		textField.setPrefSize(size * boxSize, 40);
		textField.setLayoutY(size * boxSize + 60);
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
		board = new BoardState(size);

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

		initNewGame();
	}

	void addToRoot() {
		root = new Group();
		root.getChildren().add(grid);
		root.getChildren().add(imageView);
		root.getChildren().add(textField);
		root.getChildren().add(chat);
	}

	void command(String command) {
		String[] t = command.split(" ");
		String[] split = new String[t.length + 1];
		for (int i = 0; i < t.length; i++) {
			split[i] = t[i];
		}
		split[split.length - 1] = "";

		switch (split[0]) {
		case "new":
			switch (split[1]) {
			case "game":
				initNewGame();
				say("Started new game.");
				break;
			default:
				say("New what?");
				break;
			}
			break;

		case "reset":
			restoreOriginalBoard();
			say("Reset back to original board.");
			break;

		case "size":
			try {
				size = Integer.parseInt(split[1]);
				setup();
				say("Set board size to " + size + "!");
			} catch (NumberFormatException e) {
				say("I don't understand what is '" + split[1] + "'.\nIs it a number?");
			}

			break;

		case "speed":
			try {
				speed = Integer.parseInt(split[1]);
				say("Set animation speed to " + speed + "!");
			} catch (NumberFormatException e) {
				say("I don't understand what is '" + split[1] + "'.\nIs it a number?");
			}

			break;
		case "state":
			say("There is/are currently " + board.pieces + " piece(s) after " + board.getMoves() + " move(s).");
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
				say("Check what?");
				break;
			}
			break;
		case "smart":
			switch (split[1]) {
			case "":
				say("< =_= > working...!");
				algorithm();
				break;
			case "playback":
				playbackSmart();
				break;

			default:
				say("Smart what?");
				break;
			}
			break;
		case "smart2":
			switch (split[1]) {
			case "":
				say("< =_= > working...!");
				algorithm2();
				break;
			case "playback":
				playbackSmart2();
				break;
			default:
				say("Smart2 what?");
				break;
			}
			break;
		case "brute":
			switch (split[1]) {
			case "":
				say("< =_= > working...!");
				bruteForce();
				break;
			case "solutions":
				say("" + BruteForce.solutions.size());
				break;
			case "playback":
				playback();
				break;
			case "autoplay":
				switch (split[2]) {
				case "":
					autoplay = true;
					bruteForce();
					break;
				case "on":
					autoplay = true;
					say("Autoplay turned on.");
					break;
				case "off":
					autoplay = false;
					say("Autoplay turned off.");
					break;
				default:
					say("Autoplay what?");
					break;
				}
				break;

			default:
				say("Brute what?");
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
			break;
		case "pun":
			say("Violinists often fiddle around.");
			break;
		case "up":
			break;
		default:
			say("I don't know what you are saying.");
			break;
		}
	}

	void printBoard(BoardState boardState) {
		printBoard(boardState, false);
	}

	void printBoard(BoardState boardState, boolean flash) {
		int[][] gameGrid = boardState.getBoardRef();
		for (int i = 0; i < gameGrid.length; i++) {
			for (int j = 0; j < gameGrid[0].length; j++) {

				setLabelText(labelsGrid[i][j], gameGrid[i][j]);

				if (flash || board.grid[i][j] != boardState.grid[i][j]) {

					Color start = getColor(board.grid[i][j]);
					Color end = getColor(boardState.grid[i][j]);

					if (flash) {
						start = start.darker();
					}

					Animation animation = new TileAnimation(labelsGrid[i][j], start, end, Duration.millis(speed));
					animation.play();
				}
			}
		}
		board = boardState;
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

	void say(String text) {
		chat.setText(text);
	}

	void initNewGame() {
		oriBoard = BoardState.generateRandomBoard(size);
		restoreOriginalBoard();
	}

	void restoreOriginalBoard() {
		printBoard(oriBoard.getDeepClone(), true);
	}

	void changeGrid(int row, int column, int change) {

		if (edit) {
			BoardState clone = board.getDeepClone();
			clone.grid[row][column] += change;
			printBoard(clone);
			oriBoard = clone;
		} else {
			BoardState branch = board.getNewBranch();
			branch.changeGrid(row, column, change);
			printBoard(branch);
		}
	}

	void algorithm2() {
		Algorithm2 algorithm = new Algorithm2(oriBoard);
		algorithm.messageProperty().addListener((o, oldmessage, newMessage) -> handleMessageSmart2(newMessage));
		new Thread(algorithm).start();
	}

	void handleMessageSmart2(String message) {
		say(message);
		String[] words = message.split(" ");
		if (words[0].equals("Done!")) {
			if (checking) {
				algorithm();
			}
		}
	}

	void algorithm() {
		Algorithm algorithm = new Algorithm(oriBoard);
		algorithm.messageProperty().addListener((o, oldMessage, newMessage) -> handleMessageSmart(newMessage));
		new Thread(algorithm).start();
	}

	void handleMessageSmart(String message) {
		say(message);
		String[] words = message.split(" ");
		
		if (words[0].equals("Done!")) {
			if (checking) {
				if (Algorithm.solution.moves != Algorithm2.solution.moves) {
					checking = false;
					say("Solution mismatch! Your algorithm sucks!");
				} else {
					initNewGame();
					algorithm2();
				}
				 
			}
		}
		
	}
	void playbackSmart2() {
		//BoardState solvedBoard = Algorithm2.solution;
		//Tracer tracer = new Tracer(solvedBoard);
		//tracer.messageProperty().addListener((o, oldMessage, newMessage) -> printTracerGrid());
		//new Thread(tracer).start();
		//say("Playing smart2 solution...");
	}
	void playbackSmart() {
		BoardState solvedBoard = Algorithm.solution;
		Tracer tracer = new Tracer(solvedBoard);
		tracer.messageProperty().addListener((o, oldMessage, newMessage) -> printTracerGrid());
		new Thread(tracer).start();
		say("Playing smart solution...");
	}

	void bruteForce() {
		BruteForce bForce = new BruteForce(oriBoard);
		bForce.messageProperty().addListener((o, oldMessage, newMessage) -> handleMessage(newMessage));
		new Thread(bForce).start();
	}

	void handleMessage(String message) {
		say(message);
		String[] words = message.split(" ");
		if (words[0].equals("Done!")) {
			if (autoplay) {
				playback();
			}
		}
	}

	void playback() {
		BoardState solvedBoard = BruteForce.solutions.poll();
		Tracer tracer = new Tracer(solvedBoard);
		tracer.messageProperty().addListener((o, oldMessage, newMessage) -> printTracerGrid());
		new Thread(tracer).start();
		say("Playing solutions... (" + BruteForce.solutions.size() + " more remaining)");
	}

	void printTracerGrid() {
		BoardState b = Tracer.playbackQueue.poll();
		printBoard(b, b.moves == 0 ? true : false);

		if (autoplay && b.pieces == 1) {
			if (BruteForce.solutions.size() > 0) {
				playback();
			} else {
				//initNewGame();
				//bruteForce();
			}
		}
	}

	void check() {
		checking = true;
		algorithm2();
	}

}