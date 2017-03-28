import java.util.PriorityQueue;

import javafx.concurrent.Task;

public class Algorithm extends Task<Void> {
	static BoardState solution;
	static boolean stop;
	BoardState board;

	public Algorithm(int[][] grid) {
		byte size = (byte) grid.length;
		byte[][] byteGrid = new byte[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				byteGrid[i][j] = (byte) grid[i][j];
			}
		}
		this.board = new BoardState(byteGrid);
	}

	public static void stop() {
		stop = true;
	}

	@Override
	protected Void call() throws Exception {
		stop = false;
		int ops = 0;
		solution = board;

		while (solution.pieces.length != 1) {
			ops++;

			Piece[] orderedPieces = solution.pieces;

			if (orderedPieces[0].value != orderedPieces[1].value) {

				solution = solution.getNewBranch();
				solution.changeGrid(orderedPieces[0].id, 1);

			} else if (orderedPieces[orderedPieces.length - 1].value != orderedPieces[orderedPieces.length - 2].value) {

				solution = solution.getNewBranch();
				solution.changeGrid(orderedPieces[orderedPieces.length - 1].id, -1);
			} else {

				ops += brute();
			}
		}

		if (stop) {
			updateMessage("Done! Solve canceled.");
		} else {
			updateMessage("Done! Found least moves is " + solution.moves + "!!!\n" + "(after " + ops + " ops)");
		}
		return null;
	}

	int brute() {
		PriorityQueue<BoardState> queue = new PriorityQueue<BoardState>();
		int ops = 0;
		int leastMoves = Integer.MAX_VALUE;
		int current = 0;

		branchOff(solution, queue);

		a: while (!queue.isEmpty()) {
			current++;
			ops++;

			BoardState currentState = queue.poll();

			updateMessage("< =_= > working...! (" + ops + " ops at part " + current + "/" + (current + queue.size())
					+ ")\n Is least moves " + leastMoves + "??");

			if (currentState.pieces.length == 1) {// solved!
				if (currentState.moves < leastMoves) {
					leastMoves = currentState.moves;
					solution = currentState;
				}
			} else if (currentState.moves + currentState.getLeastPossibleMovesLeft() < leastMoves) {

				PriorityQueue<BoardState> queue2 = new PriorityQueue<BoardState>();
				queue2.add(currentState);

				while (!queue2.isEmpty()) {
					ops++;

					if (ops % 10000 == 0) {
						if (stop) {
							break a;
						}
						updateMessage("< =_= > working...! (" + ops + " ops at part " + current + "/"
								+ (current + queue.size()) + ")\n Is least moves " + leastMoves + "??");
					}

					BoardState currentState2 = queue2.poll();

					if (currentState2.pieces.length == 1) {// solved!
						if (currentState2.moves < leastMoves) {
							leastMoves = currentState2.moves;
							solution = currentState2;
						}
					} else if (currentState2.moves + currentState2.getLeastPossibleMovesLeft() < leastMoves) {
						Piece[] orderedPieces = currentState2.pieces;

						if (orderedPieces[0].value != orderedPieces[1].value) {
							BoardState branch = currentState2.getNewBranch();
							branch.changeGrid(orderedPieces[0].id, 1);
							queue2.add(branch);
						} else if (orderedPieces[orderedPieces.length - 1].value != orderedPieces[orderedPieces.length
								- 2].value) {

							BoardState branch = currentState2.getNewBranch();
							branch.changeGrid(orderedPieces[orderedPieces.length - 1].id, -1);
							queue2.add(branch);
						} else if (currentState2.moves + currentState2.getLeastPossibleMovesLeft() + 1 < leastMoves) {

							branchOff(currentState2, queue2);
						}
					}
				}
			}
		}

		return ops;
	}

	// 4343
	// 2132
	// 3423
	// 1342
	void branchOff(BoardState currentState, PriorityQueue<BoardState> queue) {
		for (Piece piece : currentState.pieces) {
			Pair nextHighLow = currentState.getNextHighLow(piece);
			if (nextHighLow.i1 != Byte.MAX_VALUE) {
				BoardState branch = currentState.getNewBranch();
				branch.changeGrid(piece.id, nextHighLow.i1 - piece.value);
				queue.add(branch);
			}
			if (nextHighLow.i2 != Byte.MIN_VALUE) {
				BoardState branch = currentState.getNewBranch();
				branch.changeGrid(piece.id, nextHighLow.i2 - piece.value);
				queue.add(branch);
			}
		}

	}

}
