import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import javafx.concurrent.Task;

public class Algorithm2 extends Task<Void> {
	static BoardState2 solution;

	BoardState2 board;

	public Algorithm2(BoardState board) {
		this.board = new BoardState2();
		this.board.setup(board.size, board.grid);
	}

	@Override
	protected Void call() throws Exception {
		int ops = 0;
		solution = board;

		while (solution.pieceCount() != 1) {
			ops++;

			Piece[] orderedPieces = solution.getOrderedPieces();
			
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

		updateMessage("Done! Found least moves is " + solution.moves + "!!!\n" + "(after " + ops + " ops)");

		return null;
	}

	int brute() {
		PriorityQueue<BoardState2> queue = new PriorityQueue<BoardState2>();
		int ops = 0;
		int leastMoves = Integer.MAX_VALUE;
		int current = 0;

		branchOff(solution, queue);

		while (!queue.isEmpty()) {
			current++;
			ops++;

			BoardState2 currentState = queue.poll();

			updateMessage("< =_= > working...! (" + ops + " ops at part " + current + "/" + (current + queue.size())
					+ ")\n Is least moves " + leastMoves + "??");

			if (currentState.pieceCount() == 1) {// solved!
				if (currentState.moves < leastMoves) {
					leastMoves = currentState.moves;
					solution = currentState;
				}
			} else if (currentState.moves+currentState.getLeastPossibleMovesLeft() < leastMoves) {

				PriorityQueue<BoardState2> queue2 = new PriorityQueue<BoardState2>();
				queue2.add(currentState);

				while (!queue2.isEmpty()) {
					ops++;

					if (ops % 10000 == 0) {
						updateMessage("< =_= > working...! (" + ops + " ops at part " + current + "/"
								+ (current + queue.size()) + ")\n Is least moves " + leastMoves + "??");
					}

					BoardState2 currentState2 = queue2.poll();

					if (currentState2.pieceCount() == 1) {// solved!
						if (currentState2.moves < leastMoves) {
							leastMoves = currentState2.moves;
							solution = currentState2;
						}
					} else if (currentState2.moves < leastMoves) {
						Piece[] orderedPieces = currentState2.getOrderedPieces();

						if (orderedPieces[0].value != orderedPieces[1].value) {
							BoardState2 branch = currentState2.getNewBranch();
							branch.changeGrid(orderedPieces[0].id, 1);
							queue2.add(branch);
						} else if (orderedPieces[orderedPieces.length - 1].value != orderedPieces[orderedPieces.length
								- 2].value) {

							BoardState2 branch = currentState2.getNewBranch();
							branch.changeGrid(orderedPieces[orderedPieces.length - 1].id, -1);
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

	void branchOff(BoardState2 currentState, PriorityQueue<BoardState2> queue) {
		for (Piece piece : currentState.pieces) {
			// int nextHighest = currentState.getNextHighestNeighbour(piece);
			// int nextLowest = currentState.getNextLowestNeighbour(piece);

			Pair nextHighLow = currentState.getNextHighLow(piece);
			if (nextHighLow.i1 != Integer.MAX_VALUE) {
				BoardState2 branch = currentState.getNewBranch();
				branch.changeGrid(piece.id, nextHighLow.i1 - piece.value);
				queue.add(branch);
			}
			if (nextHighLow.i2 != Integer.MIN_VALUE) {
				BoardState2 branch = currentState.getNewBranch();
				branch.changeGrid(piece.id, nextHighLow.i2 - piece.value);
				queue.add(branch);
			}
		}

	}

}
