import java.util.LinkedList;
import java.util.Queue;

import javafx.concurrent.Task;

public class Tracer extends Task<Void> {
	static Queue<Board> playbackQueue = new LinkedList<Board>();

	BoardState finalState;
	Board board;
	static boolean stop;

	public Tracer(Board startBoard, BoardState finalState) {
		this.board = startBoard;
		this.finalState = finalState;
		stop=false;
	}

	static void stop() {
		stop=true;
	}

	@Override
	protected Void call() throws Exception {
		retrace(finalState);
		return null;
	}

	void retrace(BoardState b) {
		if (b.previousState != null) {
			retrace(b.previousState);
		}

		if(stop){
			return;
		}
		if (b.moves == 0) {
			playbackQueue.add(board.getDeepClone());
		} else {
			board.applyDiffState(b, b.previousState);
			playbackQueue.add(board.getDeepClone());
		}

		try {
			Thread.sleep(Main.speed * 2);
		} catch (Exception e) {
			System.out.println("WTFF!!!!???????????????????? " + e.getMessage());
		}

		updateMessage("hi" + b.moves);
	}
}
