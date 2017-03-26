
public class Board {
	int size;
	int[][] grid;

	int moves = 0;
	Board previousState;

	static Board generateRandomBoard(int size) {
		Board newBoard = new Board(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				newBoard.grid[i][j] = (int) (Math.random() * size + 1);
			}
		}
		return newBoard;
	}

	public Board(int size) {
		this.size = size;
		grid = new int[size][size];
	}

	public void changeGrid(int[] coord, int change) {
		changeGrid(coord[0], coord[1], change);
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
