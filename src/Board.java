public class Board {
	static int GENERATED_RANDOM_VALUE_MAX=5;
	
	int size;
	int[][] grid;

	int moves = 0;
	Board previousState;

	static Board generateRandomBoard(int size) {
		Board newBoard = new Board(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				newBoard.grid[i][j] = (int) (Math.random() * GENERATED_RANDOM_VALUE_MAX + 1);
			}
		}
		return newBoard;
	}

	public Board(int size) {
		this.size = size;
		grid = new int[size][size];
	}

	public void changeGrid(Pair coord, int change) {
		changeGrid(coord.i1, coord.i2, change);
	}

	public void changeGrid(int row, int column, int change) {
		affectNeighbours(row, column, grid[row][column], grid[row][column] + change);
		moves += Math.abs(change);
	}

	void affectNeighbours(int row, int column, int from, int to) {
		if (row >= 0 && column >= 0 && row < size && column < size) {
			if (grid[row][column] == from) {
				grid[row][column] = to;
				affectNeighbours(row + 1, column, from, to);
				affectNeighbours(row - 1, column, from, to);
				affectNeighbours(row, column + 1, from, to);
				affectNeighbours(row, column - 1, from, to);
			}
		}
	}

	void applyDiffState(BoardState currentState, BoardState nextState){
		for (Piece curPiece : currentState.pieces) {
			for (Piece nextPiece : nextState.pieces) {
				if(curPiece.id==nextPiece.id && curPiece.value!=nextPiece.value){
					changeGrid(BoardState.idToCoord.get(curPiece.id),curPiece.value-nextPiece.value);
					return;
				}
			}	
		}
	}
	
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
	}

	public Board getNewBranch() {
		Board clone = getDeepClone();
		clone.previousState = this;
		return clone;
	}

}
