import java.util.PriorityQueue;
import javafx.concurrent.Task;

/**
 * This class inherits from {@link javafx.concurrent.Task}. Given a grid, this
 * will find the least amount of moves to solve the grid.
 * <p>
 * <b>Algorithm description:</b> The algorithm used (briefly):
 * <li>checks every single possible way to solve the board
 * <li>starts with the original board, imagine this as a node at the root of a
 * tree
 * <li>for every possible move that can be done on the original board, branch
 * off from the root node into a new node where that move has been done
 * <li>then, for every new node created, branch off again into all of the
 * possible moves that can be taken
 * <li>this creates a tree of all possible moves, where each node has a
 * reference back to the previous node that it branched off of
 * <li>when the least amount of moves is found, only the final node is stored,
 * this node can be traced back through its references to the previous node, all
 * the way back to the root node
 * <li>one instance of the class BoardState represents one node
 * <p>
 * <b>Optimizations:</b>
 * <li>for every node, if there is only one piece on the board that has the
 * highest value on the board (i.e. not two or more pieces having the highest
 * value), that piece can be decreased immediately. There is no need to branch
 * out into all of the possible moves for this node.
 * <li>similarly, if there is only one piece on the board that has the lowest
 * value, that piece can be increased immediately without needing to check any
 * other possible moves.
 * <li>a "possible move" is a move that will join one piece to its next highest
 * neighbor(s) or its next lowest neighbor(s)
 * <li>in a node, the difference between the highest value and the lowest value
 * is the absolute least amount of moves that it can possibly take to complete
 * the game, if the current amount of moves in that node plus that difference
 * exceeds the current record for least amount of moves, then the node can be
 * removed since it cannot finish in less moves than the record
 * <p>
 * Identifiers:
 * <li>static BoardState solution - represents final state of the solution with
 * the least moves
 * <li>static boolean stop - when true, this Thread will stop
 * <li>BoardState board - represents the initial state of the board before
 * solving
 */
public class Algorithm extends Task<Void> {
	static BoardState solution;
	static boolean stop;
	BoardState board;

	/**
	 * constructor for Algorithm class: Converts the {@code grid} from integer
	 * types to byte types, then instantiates {@code board} with the new byte
	 * type array .
	 * 
	 * @param grid
	 *            2D array of integers containing the values on the original
	 *            board
	 */
	public Algorithm(int[][] grid) {
		byte[][] byteGrid = new byte[grid.length][grid.length];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				byteGrid[i][j] = (byte) grid[i][j];
			}
		}
		this.board = new BoardState(byteGrid);
	}// end Algorithm constructor

	/**
	 * stop method: Sets stop to true.
	 */
	public static void stop() {
		stop = true;
	}// end stop method

	/**
	 * call method: This method is called when the Task starts. Creates a
	 * priority queue of type BoardState and add {@code board} to it. Poll the
	 * queue and store into {@code currentState}. Check if {@code currentState}
	 * only has 1 piece. If so, update {@value leastMoves} to match the moves it
	 * takes to get to {@code currentState}. If {@code currentState} has
	 * multiple pieces, call {@code hasSingleMaxMin} to check for single
	 * maximums and minimums. If the call returns false, then call
	 * {@code branchOff} to add all possible moves that can be taken from
	 * {@code currentState} to the queue, each as a new BoardState. Do this for
	 * as long as there is something in the queue.
	 * <p>
	 * Also, keeps tracks of the total number of states checked and the lowest
	 * size of the queue. These numbers are sent as updated messages once every
	 * 20000 states checked, and they give the user a sense of how long the
	 * program has been working and how long it still needs to work.
	 * <p>
	 * Identifiers:
	 * <li>int statesChecked - the total number of BoardStates processed through
	 * the algorithm
	 * <li>int leastMoves - the least number of moves so far
	 * <li>lowestQueueSize - the lowest size the queue has been
	 * <li>PriorityQueue queue - a priority queue of all the BoardStates that
	 * need to be checked (BoardStates are ordered by number of pieces)
	 */
	@Override
	protected Void call() throws Exception {
		int statesChecked = 0;
		int leastMoves = Integer.MAX_VALUE;
		int lowestQueueSize = Integer.MAX_VALUE;
		PriorityQueue<BoardState> queue = new PriorityQueue<BoardState>();

		stop = false;
		queue.add(board);

		while (!queue.isEmpty()) {
			statesChecked++;

			if (statesChecked % 20000 == 0) {
				if (stop) {
					break;
				}
				updateMessage("< =_= > working...! (" + statesChecked + " states checked; ETA in " + lowestQueueSize
						+ ") \n Is least moves " + leastMoves + "??");
			}

			BoardState currentState = queue.poll();

			if (currentState.pieces.length == 1) {// solved!
				if (currentState.moves < leastMoves) {
					leastMoves = currentState.moves;
					solution = currentState;
				}
				if (queue.size() < lowestQueueSize) {
					lowestQueueSize = queue.size();
				}
			} else if (currentState.moves + currentState.getLeastPossibleMovesLeft() < leastMoves) {
				if (!hasSingleMaxMin(currentState, queue)) {
					// branch fully
					if (currentState.moves + currentState.getLeastPossibleMovesLeft() + 1 < leastMoves) {
						branchOff(currentState, queue);
					}
				}
			} else {
				if (queue.size() < lowestQueueSize) {
					lowestQueueSize = queue.size();
				}
			}
		}

		if (stop) {
			updateMessage("Done! Solve canceled.");
		} else {
			updateMessage("Done! Found least moves is " + solution.moves + "!!!\n" + "(after " + statesChecked
					+ " states checked)");
		}
		return null;
	}// end call method

	/**
	 * hasSingleMaxMin method: Checks if the {@code currentState} has a single
	 * piece that has the highest or lowest value. If it does, then create a
	 * branch of {@code currentState} and in that branch, bring the
	 * highest/lowest value towards the average by 1, and return true. If no
	 * single highest/lowest has been found, then return false.
	 * <p>
	 * Local variables: Piece[] pieces - the {@code pieces} array of
	 * {@code currentState}, which is ordered by each piece's value
	 * 
	 * @param currentState
	 *            the current BoardState to be checked
	 * @param queue
	 *            the queue for instances of BoardState to be added to for later
	 *            checking
	 * @return {@code true} if a single highest/lowest value has been found
	 */
	boolean hasSingleMaxMin(BoardState currentState, PriorityQueue<BoardState> queue) {
		Piece[] pieces = currentState.pieces;

		// check for single max
		if (pieces[0].value != pieces[1].value) {
			BoardState branch = currentState.getNewBranch();
			branch.changeGrid(pieces[0].id, 1);
			queue.add(branch);
			return true;
			// check for single min
		} else if (pieces[pieces.length - 1].value != pieces[pieces.length - 2].value) {
			BoardState branch = currentState.getNewBranch();
			branch.changeGrid(pieces[pieces.length - 1].id, -1);
			queue.add(branch);
			return true;
		}
		return false;
	}// end hasSingleMaxMin method

	/**
	 * branchOff method: Iterate through the pieces in {@code currentState}.
	 * Call {@code getNextHighLow} method in {@code currentState} for each piece
	 * to get the next highest and next lowest neighboring value. Then, create a
	 * new branch changing the piece to the highest, and a new branching
	 * changing the piece to the lowest. Add all branches to the queue. If a
	 * piece is already the highest/lowest out of all of its neighbors, don't
	 * create a branch for that situation.
	 * 
	 * @param currentState
	 *            the current BoardState to check
	 * @param queue
	 *            the queue for instances of BoardState to be added to for later
	 *            checking
	 */
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
	}// end branchOff method
}// end Algorithm class
