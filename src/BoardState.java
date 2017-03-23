import java.util.ArrayList;
import java.util.List;

public class BoardState implements Comparable<BoardState> {
	int size;
	int[][] grid;

	int moves = 0;
	int pieces;
	BoardState previousState;

	static BoardState generateRandomBoard(int size) {
		BoardState newBoard = new BoardState(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				newBoard.grid[i][j] = (int) (Math.random() * size + 1);
			}
		}
		newBoard.pieces = newBoard.getPieces().size();
		return newBoard;
	}

	public BoardState(int size) {
		this.size = size;
		grid = new int[size][size];
	}
	
	public void changeGrid(int[] coord, int change) {
		changeGrid(coord[0], coord[1], change);
	}

	public void changeGrid(int row, int column, int change) {
		affectNeighbours(row, column, grid[row][column], grid[row][column] + change);
		moves += Math.abs(change);
		pieces = getPieces().size();
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

	// vector/point coord class? is there one already made?
	public ArrayList<int[]> getPieces() {
		ArrayList<int[]> coordinates = new ArrayList<int[]>();
		boolean[][] counted = new boolean[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (!counted[i][j]) {
					coordinates.add(new int[] { i, j });
					countPiece(i, j, grid[i][j], counted);
				}
			}
		}
		return coordinates;
	}

	void countPiece(int row, int column, int pieceValue, boolean[][] counted) {
		if (row >= 0 && column >= 0 && row < size && column < size) {
			if (!counted[row][column] && grid[row][column] == pieceValue) {
				counted[row][column] = true;
				countPiece(row + 1, column, pieceValue, counted);
				countPiece(row - 1, column, pieceValue, counted);
				countPiece(row, column + 1, pieceValue, counted);
				countPiece(row, column - 1, pieceValue, counted);
			}
		}
	}
	public int getNextHighestNeighbour(int[] coord) {
		return getNextHighestNeighbour(coord[0],coord[1]);
	}
	public int getNextHighestNeighbour(int row, int column) {
		int current = grid[row][column];
		int nextMore = Integer.MAX_VALUE;
		List<int[]> neighbours = getNeighbours(row, column);
		for (int[] coord : neighbours) {
			int t = grid[coord[0]][coord[1]];
			if (t > current && t < nextMore) {
				nextMore = t;
			}
		}
		return nextMore;
	}

	public int getNextLowestNeighbour(int[] coord){
		return getNextLowestNeighbour(coord[0],coord[1]);
	}
	public int getNextLowestNeighbour(int row, int column) {
		int current = grid[row][column];
		int nextLess = Integer.MIN_VALUE;
		List<int[]> neighbours = getNeighbours(row, column);
		for (int[] coord : neighbours) {
			int t = grid[coord[0]][coord[1]];
			if (t < current && t > nextLess) {
				nextLess = t;
			}
		}
		return nextLess;
	}


	public List<int[]> getNeighbours(int row, int column) {
		List<int[]> neighbours = new ArrayList<int[]>();
		List<int[]> pieces = getPieces();
		boolean[][] counted = new boolean[size][size];
		traverseToNeighbours(row, column, grid[row][column], neighbours, pieces, counted);
		return neighbours;
	}

	private void traverseToNeighbours(int row, int column, int pieceValue, List<int[]> neighbours, List<int[]> pieces,
			boolean[][] counted) {
		if (row >= 0 && column >= 0 && row < size && column < size) {
			if (!counted[row][column]) {
				if (grid[row][column] == pieceValue) {
					counted[row][column] = true;
					traverseToNeighbours(row + 1, column, pieceValue, neighbours, pieces, counted);
					traverseToNeighbours(row - 1, column, pieceValue, neighbours, pieces, counted);
					traverseToNeighbours(row, column + 1, pieceValue, neighbours, pieces, counted);
					traverseToNeighbours(row, column - 1, pieceValue, neighbours, pieces, counted);
				} else {
					traverseToPiece(row, column, grid[row][column], neighbours, pieces, counted);
				}
			}
		}
	}

	private void traverseToPiece(int row, int column, int pieceValue, List<int[]> neighbours, List<int[]> pieces,
			boolean[][] counted) {
		if (row >= 0 && column >= 0 && row < size && column < size) {
			if (!counted[row][column]) {
				if (grid[row][column] == pieceValue) {
					counted[row][column] = true;
					for (int[] coord : pieces) {
						if (coord[0] == row && coord[1] == column) {
							if (!neighbours.contains(coord)) {
								neighbours.add(coord);
							}
							return;
						}
					}

					traverseToPiece(row + 1, column, pieceValue, neighbours, pieces, counted);
					traverseToPiece(row - 1, column, pieceValue, neighbours, pieces, counted);
					traverseToPiece(row, column + 1, pieceValue, neighbours, pieces, counted);
					traverseToPiece(row, column - 1, pieceValue, neighbours, pieces, counted);
				}
			}
		}

	}

	public int[][] getBoardRef() {
		return grid;
	}

	public int getMoves() {
		return moves;
	}

	public int getAt(int[] coord) {
		return grid[coord[0]][coord[1]];
	}

	public BoardState getDeepClone() {
		BoardState clone = new BoardState(size);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				clone.grid[i][j] = grid[i][j];
			}
		}
		clone.moves = moves;
		clone.pieces = pieces;
		clone.previousState = previousState;
		return clone;
	}

	public BoardState getNewBranch() {
		BoardState clone = getDeepClone();
		clone.previousState = this;
		return clone;
	}

	@Override
	public int compareTo(BoardState other) {
		if (pieces > other.pieces) {
			return 1;
		} else {
			return -1;
		}
	}

}
