import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import javafx.concurrent.Task;

public class BruteForce extends Task<Void> {
	static Queue<BoardState> solutions = new LinkedList<BoardState>();

	BoardState board;

	int leastMoves = Integer.MAX_VALUE;

	public BruteForce(BoardState board) {
		this.board = board;
	}

	@Override
	protected Void call() throws Exception {
		solutions = new LinkedList<BoardState>();
		Queue<BoardState> queue = new PriorityQueue<BoardState>();
		int ops = 0;

		queue.add(board);
		
		BoardState bran = board.getNewBranch();

		while (!queue.isEmpty()) {

			ops++;
			if (ops % 20000 == 0) {
				updateMessage("< =_= > working...! (" + ops + ")"
						+ ")\n Is least moves " + leastMoves + "??");
			}
			BoardState currentState = queue.poll();
			if (currentState.pieces == 1) {// solved!
				if (currentState.moves < leastMoves) {
					leastMoves = currentState.moves;
					solutions = new LinkedList<BoardState>();
					solutions.add(currentState);
				} else if (currentState.moves == leastMoves) {
					solutions.add(currentState);
				}
			} else if (currentState.moves < leastMoves) {

				branchOff(currentState, queue);

			}

		}

		updateMessage("Done! Found least moves is " + leastMoves + "!!!\n" + "(after " + ops + " ops)");
		return null;
	}

	void branchOff(BoardState currentState, Queue<BoardState> queue) {
		ArrayList<int[]> piecesCoords = currentState.getPieces();
		for (int[] coord : piecesCoords) {
			// how to manage this branch var? TODO
			
			int nextHighest = currentState.getNextHighestNeighbour(coord[0], coord[1]);
			int nextLowest = currentState.getNextLowestNeighbour(coord[0], coord[1]);

			if (nextHighest != Integer.MAX_VALUE) {
				BoardState branch = currentState.getNewBranch();
				branch.changeGrid(coord[0], coord[1], nextHighest - branch.getAt(coord));
				queue.add(branch);
			}
			if (nextLowest != Integer.MIN_VALUE) {
				BoardState branch = currentState.getNewBranch();
				branch.changeGrid(coord[0], coord[1], nextLowest - branch.getAt(coord));
				queue.add(branch);
			}
		}
	}

}
