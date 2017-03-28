
/**
 * A concrete representation of one state of the game grid. Each instance
 * contains the values of each square in the grid, the size of the grid, the
 * number of moves it took to achieve this state, and a reference to the
 * previous state.
 * <p>
 * Identifiers:
 * <li>static int GENERATED_RANDOM_VALUE_MAX - when generating random values for
 * a board, this is the max number
 * <li>int size - the number of rows/columns in this board
 * <li>int[][] grid - a 2D array holding the values at each coordinate in the
 * grid
 * <li>int moves - the number of moves it took to achieve this state
 * <li>Board previousState - the previous state from which this state originated
 * from
 * 
 */
public class Board {
	static int GENERATED_RANDOM_VALUE_MAX = 5;

	int size;
	int[][] grid;
	int moves = 0;
	Board previousState;

	/**
	 * generateRandomBoard method: Returns a new Board with the grid of a given
	 * size. The grid will be filled with random numbers from 1 to
	 * {@code GENERATED_RANDOM_VALUE_MAX}.
	 * 
	 * @param size
	 *            how many rows/columns in the grid?
	 * @return a new instance of Board class with randomized grid values
	 */
	static Board generateRandomBoard(int size) {
		Board newBoard = new Board(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				newBoard.grid[i][j] = (int) (Math.random() * GENERATED_RANDOM_VALUE_MAX + 1);
			}
		}
		return newBoard;
	}// end generateRandomBoard method

	/**
	 * constructor for Board class: Initializes the {@code grid} array with a
	 * give size.
	 * 
	 * @param size
	 *            how many rows/columns in this grid?
	 */
	public Board(int size) {
		this.size = size;
		grid = new int[size][size];
	}// end Board constructor

	/**
	 * changeGrid method: Changes the value at a coordinate in the grid. Calls
	 * {@code affectNeighbours} to change all the surrounding squares in the
	 * grid with the same values. Increases {@code moves} by an appropriate
	 * value based on how the amount of change applied.
	 * 
	 * @param coord
	 *            a Pair which contains the coordinates of the square in the
	 *            grid (format: row,column)
	 * @param change
	 *            how much to change the square by? (positive adds, negative
	 *            subtracts)
	 */
	public void changeGrid(Pair coord, int change) {
		changeGrid(coord.i1, coord.i2, change);
	}// end changeGrid method

	/**
	 * changeGrid method: Changes the value at a coordinate in the grid. Calls
	 * {@code affectNeighbours} to change all the surrounding squares in the
	 * grid with the same values. Increases {@code moves} by an appropriate
	 * value based on how the amount of change applied.
	 * 
	 * @param row
	 *            which to is the square to change?
	 * @param column
	 *            which column is the square to change?
	 * @param change
	 *            how much to change the square by? (positive adds, negative
	 *            subtracts)
	 */
	public void changeGrid(int row, int column, int change) {
		affectNeighbours(row, column, grid[row][column], grid[row][column] + change);
		moves += Math.abs(change);
	}// end changeGrid method

	/**
	 * affectNeighbours method: Recursively changes all squares adjacent to a
	 * square from one value to another. Recursively calls itself for the 4
	 * adjacent squares in the grid. Will only apply the change and recurse if
	 * the current square is within the boundary of the array and has a value
	 * equal to {@code from}.
	 * 
	 * @param row
	 *            the row in the grid for the square to change
	 * @param column
	 *            the column in the grid for the square to change
	 * @param from
	 *            what value should this square be for it to change?
	 * @param to
	 *            what value should this square change to?
	 */
	void affectNeighbours(int row, int column, int from, int to) {
		if (grid[row][column] == from) {
			grid[row][column] = to;
			if (row < size-1)
				affectNeighbours(row + 1, column, from, to);
			if (row > 0)
				affectNeighbours(row - 1, column, from, to);
			if (column < size-1)
				affectNeighbours(row, column + 1, from, to);
			if (column > 0)
				affectNeighbours(row, column - 1, from, to);
		}

	}// end affectNeighbours method

	/**
	 * applyDiffState method: calculates the difference between two subsequent
	 * BoardState instances, then applies that difference to this instance of
	 * Board. This calls {@code changeGrid} method to change the value of the
	 * board to match {@code nextState}.
	 * 
	 * @param currentState
	 *            an instance of BoardState that should match this Board
	 * @param nextState
	 *            an instance of BaordState that proceeds {@code nextState}
	 */
	void applyDiffState(BoardState currentState, BoardState nextState) {
		for (Piece curPiece : currentState.pieces) {
			for (Piece nextPiece : nextState.pieces) {
				if (curPiece.id == nextPiece.id && curPiece.value != nextPiece.value) {
					changeGrid(BoardState.idToCoord.get(curPiece.id), curPiece.value - nextPiece.value);
					return;
				}
			}
		}
	}// end applyDiffState method

	/**
	 * hasWon method: Checks if this instance represents a state of the board in
	 * which the game has been won. The game is won if all values in the board
	 * are equal.
	 * 
	 * @return true if all values in the board are equal (game has been won).
	 */
	public boolean hasWon() {
		int number = grid[0][0];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (grid[i][j] != number) {
					return false;
				}
			}
		}
		return true;
	}// end hasWon method

	/**
	 * getDeepClone method: Returns a deep clone of this instance. The deep
	 * clone will be a separate instance of the Board class with all variables
	 * of this instance copied over.
	 * <p>
	 * Local variables:
	 * <li>Board clone - a deep clone of this instance
	 * 
	 * @return a completely separate but equal clone of this instance
	 */
	public Board getDeepClone() {
		Board clone = new Board(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				clone.grid[i][j] = grid[i][j];
			}
		}
		clone.moves = moves;
		clone.previousState = previousState;
		return clone;
	}// end getDeepClone

	/**
	 * getNewBranch method: Returns a deep clone of this instance, except with
	 * the {@code previousState} of the returned instance set to this instance.
	 * <p>
	 * Local variables:
	 * <li>Board clone - a deep clone of this instance
	 * 
	 * @return a deep clone of this instance with {@code previousState}
	 *         referencing to this instance.
	 */
	public Board getNewBranch() {
		Board clone = getDeepClone();
		clone.previousState = this;
		return clone;
	}// end getNewBranch

}// end Board class
