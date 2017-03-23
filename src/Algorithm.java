import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;


import javafx.concurrent.Task;

public class Algorithm extends Task<Void> {
	static BoardState solution;

	BoardState board;

	public Algorithm(BoardState board) {
		this.board = board;
	}

	@Override
	protected Void call() throws Exception {
		int ops = 0;
		solution = board;

		while (solution.pieces != 1) {
			ops++;

			List<int[]> pieces = solution.getPieces();
			int[] values = new int[pieces.size()];
			for (int i = 0; i < values.length; i++) {
				values[i] = solution.getAt(pieces.get(i));
			}

			Arrays.sort(values);
			if (values[0] != values[1]) {

				solution = solution.getNewBranch();

				for (int[] coord : pieces) {
					if (solution.getAt(coord) == values[0]) {
						solution.changeGrid(coord, 1);
					}
				}
			} else if (values[values.length - 1] != values[values.length - 2]) {

				solution = solution.getNewBranch();

				for (int[] coord : pieces) {
					if (solution.getAt(coord) == values[values.length - 1]) {
						solution.changeGrid(coord, -1);
					}
				}
			} else {
				ops += brute();
			}

		}
		updateMessage("Done! Found least moves is " + solution.moves + "!!!\n" + "(after " + ops + " ops)");

		return null;
	}

	int brute() {
		PriorityQueue<BoardState> queue = new PriorityQueue<BoardState>();
		int ops = 0;
		int leastMoves = Integer.MAX_VALUE;
		int current = 0;

		branchOff(solution, queue);

		while (!queue.isEmpty()) {
			current++;

			ops++;
			if (ops % 10000 == 0) {
				updateMessage("< =_= > working...! (" + ops + " ops at part " + current + "/" + (current + queue.size())
						+ ")\n Is least moves " + leastMoves + "??");
			}
			BoardState currentState = queue.poll();
			if (currentState.pieces == 1) {// solved!
				if (currentState.moves < leastMoves) {
					leastMoves = currentState.moves;
					solution = currentState;
				}
			} else if (currentState.moves < leastMoves) {

				PriorityQueue<BoardState> queue2 = new PriorityQueue<BoardState>();
				queue2.add(currentState);

				while (!queue2.isEmpty()) {
					ops++;
					if (ops % 10000 == 0) {
						updateMessage("< =_= > working...! (" + ops + " ops at part " + current + "/"
								+ (current + queue.size()) + ")\n Is least moves " + leastMoves + "??");
					}
					BoardState currentState2 = queue2.poll();
					if (currentState2.pieces == 1) {// solved!
						if (currentState2.moves < leastMoves) {
							leastMoves = currentState2.moves;
							solution = currentState2;
						}
					} else if (currentState2.moves < leastMoves) {

						// +
						List<int[]> pieces = currentState2.getPieces();
						int[] values = new int[pieces.size()];
						for (int i = 0; i < values.length; i++) {
							values[i] = currentState2.getAt(pieces.get(i));
						}

						Arrays.sort(values);
						if (values[0] != values[1]) {

							BoardState branch= currentState2.getNewBranch();

							for (int[] coord : pieces) {
								if (branch.getAt(coord) == values[0]) {
									branch.changeGrid(coord, 1);
								}
							}
							queue2.add(branch);
						} else if (values[values.length - 1] != values[values.length - 2]) {

							BoardState branch= currentState2.getNewBranch();

							for (int[] coord : pieces) {
								if (branch.getAt(coord) == values[values.length - 1]) {
									branch.changeGrid(coord, -1);
								}
							}
							queue2.add(branch);
						} else {

							branchOff(currentState2, queue2);
						}
					}
				}

			}
		}

		return ops;
	}

	void branchOff(BoardState currentState, PriorityQueue<BoardState> queue) {
		ArrayList<int[]> piecesCoords = currentState.getPieces();
		for (int[] coord : piecesCoords) {
			// how to manage this branch var? TODO
			int nextHighest = currentState.getNextHighestNeighbour(coord[0], coord[1]);
			int nextLowest = currentState.getNextLowestNeighbour(coord[0], coord[1]);

			if (nextHighest != Integer.MAX_VALUE) {
				BoardState branch = currentState.getNewBranch();
				branch.changeGrid(coord[0], coord[1], nextHighest - branch.grid[coord[0]][coord[1]]);
				queue.add(branch);
			}
			if (nextLowest != Integer.MIN_VALUE) {
				BoardState branch = currentState.getNewBranch();
				branch.changeGrid(coord[0], coord[1], nextLowest - branch.grid[coord[0]][coord[1]]);
				queue.add(branch);
			}
		}
	}

}
