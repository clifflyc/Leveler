import java.util.LinkedList;
import java.util.Queue;

import javafx.concurrent.Task;

/**
 * This class inherits from {@link javafx.concurrent.Task}. This class must be
 * constructed with the a BoardState. Once started, will start filling the
 * {@code playbackQueue} queue with instances Board that represents the states
 * of the board it took to get from the original board to the final BoardState.
 * <p>
 * Identifiers
 * <li>static Queue<Board> playbackQueue - a queue of boards to be printed to
 * the screen
 * <li>static boolean stop - when set to true, the task will stop
 * <li>BoardState finalState - a representation of the final state to trace back
 * from
 * <li>Board board - the board to apply the changes traced from finalState,
 * should be the original board for the current game
 * 
 */
public class Tracer extends Task<Void> {
	static Queue<Board> playbackQueue = new LinkedList<Board>();
	private static boolean stop;

	BoardState finalState;
	Board board;

	/**
	 * constructor for the Tracer class: Sets the {@code startBoard} and
	 * {@code finalState}. Sets {@code stop} to false.
	 * 
	 * @param startBoard
	 *            - the original board at the start of the current game
	 * @param finalState
	 *            - the final state found by the algorithm
	 */
	public Tracer(Board startBoard, BoardState finalState) {
		this.board = startBoard;
		this.finalState = finalState;
		stop = false;
	}// end Tracer constructor

	/**
	 * stop method: Sets {@code stop} to {@code true} which stops the current
	 * task.
	 */
	static void stop() {
		stop = true;
	}// end stop method

	/**
	 * call method: Is called at when the task is started. Calls {@code retrace}
	 * passing {@code finalState} to trace the state it took to get the board
	 * from its initial state to the final state.
	 * 
	 * @return {@link java.lang.Void}
	 */
	@Override
	protected Void call() throws Exception {
		retrace(finalState);
		return null;
	}// end call method

	/**
	 * retrace method: calls itself recursively, passing the previous state of
	 * the parameter {@code b} as parameter. After the recursive call, if
	 * {@code b} has no previous state, add {@code board} to the playback queue.
	 * Otherwise, call {@code applyDiffState} in the board to apply the
	 * differences of {@code b} to it and add the changed {@code board} to the
	 * playback queue. Then, sleep for double the time specified by
	 * {@code Main.speed} before telling Main to read the queue and returning.
	 * 
	 * @param b
	 */
	void retrace(BoardState b) {
		// recursive call until the first state
		if (b.previousState != null) {
			retrace(b.previousState);
		}

		// stop condition
		if (stop) {
			return;
		}

		// apply diff to board and add it to queue
		if (b.moves == 0) {
			playbackQueue.add(board.getDeepClone());
		} else {
			board.applyDiffState(b, b.previousState);
			playbackQueue.add(board.getDeepClone());
		}

		// wait for some time
		try {
			Thread.sleep(Main.speed * 2);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// tell Main to read from queue
		updateMessage("" + b.moves);
	}// end retrace method
}// end Tracer class
