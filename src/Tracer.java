import java.util.LinkedList;
import java.util.Queue;

import javafx.concurrent.Task;

public class Tracer extends Task<Void> {
	static Queue<BoardState> playbackQueue = new LinkedList<BoardState>();

	BoardState finalState;

	public Tracer(BoardState finalState) {
		this.finalState = finalState;
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

		try {
			Thread.sleep(Main.speed*2);
		} catch (Exception e) {
			System.out.println("WTFF!!!!???????????????????? " + e.getMessage());
		}
		playbackQueue.add(b); 
		updateMessage("hi"+b.moves);
	}
}
