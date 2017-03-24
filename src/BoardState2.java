import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BoardState2 implements Comparable<BoardState2> {
	static HashMap<Short, Pair> idToCoord;

	short moves;
	public List<Piece> pieces = new ArrayList<Piece>();
	public List<Pair> links;

	BoardState2 previousState;

	public BoardState2() {
		moves = 0;
		pieces = new ArrayList<Piece>();
		links = new ArrayList<Pair>();
	}

	// --

	public void setup(byte size, byte[][] grid) {
		makePieces(size, grid);
		assignNeighbours(size, grid);
		
	}

	private void makePieces(byte size, byte[][] grid) {
		idToCoord = new HashMap<Short, Pair>();
		List<Piece> pieces = new ArrayList<Piece>();
		boolean[][] counted = new boolean[size][size];
		short id = 0;
		for (byte i = 0; i < size; i++) {
			for (byte j = 0; j < size; j++) {
				if (!counted[i][j]) {
					id++;
					pieces.add(new Piece(grid[i][j], id));
					idToCoord.put(id, new Pair(i, j));
					countPiece(i, j, grid[i][j], counted, size, grid);
				}
			}
		}
		this.pieces = pieces;
	}

	private void countPiece(byte row, byte column, byte pieceValue, boolean[][] counted, byte size, byte [][] grid) {
		if (row >= 0 && column >= 0 && row < size && column < size) {
			if (!counted[row][column] && grid[row][column] == pieceValue) {
				counted[row][column] = true;
				countPiece((byte)(row + 1), column, pieceValue, counted, size, grid);
				countPiece((byte)(row - 1), column, pieceValue, counted, size, grid);
				countPiece(row, (byte)(column + 1), pieceValue, counted, size, grid);
				countPiece(row, (byte)(column - 1), pieceValue, counted, size, grid);
			}
		}
	}

	// --

	private void assignNeighbours(byte size, byte[][] grid) {
		for (Piece piece : pieces) {
			List<Short> neighbourIDs = new ArrayList<Short>();
			boolean[][] counted = new boolean[size][size];
			Pair coord = idToCoord.get(piece.id);
			byte row = (byte)coord.i1;
			byte column = (byte)coord.i2;
			traverseToNeighbours(row, column, grid[row][column], counted, size, grid, neighbourIDs);

			for (short neighbourId : neighbourIDs) {
				Pair link = new Pair(piece.id, neighbourId);
				if (!links.contains(link)) {
					links.add(link);
				}
			}
		}
	}

	private void traverseToNeighbours(byte row, byte column, byte pieceValue, boolean[][] counted, byte size, byte[][] grid,
			List<Short> neighbourIDs) {
		if (row >= 0 && column >= 0 && row < size && column < size) {
			if (!counted[row][column]) {
				if (grid[row][column] == pieceValue) {
					counted[row][column] = true;
					traverseToNeighbours((byte) (row + 1), column, pieceValue, counted, size, grid, neighbourIDs);
					traverseToNeighbours((byte) (row - 1), column, pieceValue, counted, size, grid, neighbourIDs);
					traverseToNeighbours(row, (byte) (column + 1), pieceValue, counted, size, grid, neighbourIDs);
					traverseToNeighbours(row, (byte) (column - 1), pieceValue, counted, size, grid, neighbourIDs);
				} else {
					traverseToPiece(row, column, grid[row][column], counted, size, grid, neighbourIDs);
				}
			}
		}
	}

	private void traverseToPiece(byte row, byte column, byte pieceValue, boolean[][] counted, byte size, byte[][] grid,
			List<Short> neighbourIDs) {
		if (row >= 0 && column >= 0 && row < size && column < size) {
			if (!counted[row][column]) {
				if (grid[row][column] == pieceValue) {
					counted[row][column] = true;
					for (Piece piece : pieces) {
						Pair coord = idToCoord.get(piece.id);
						if (coord.i1 == row && coord.i2 == column) {
							if (!neighbourIDs.contains(piece.id)) {
								neighbourIDs.add(piece.id);
							}
							return;
						}
					}

					traverseToPiece((byte) (row + 1), column, pieceValue, counted, size, grid, neighbourIDs);
					traverseToPiece((byte) (row - 1), column, pieceValue, counted, size, grid, neighbourIDs);
					traverseToPiece(row, (byte) (column + 1), pieceValue, counted, size, grid, neighbourIDs);
					traverseToPiece(row, (byte) (column - 1), pieceValue, counted, size, grid, neighbourIDs);
				}
			}
		}
	}

	public Pair getNextHighLow(Piece piece){

		byte nextHighest = Byte.MAX_VALUE;
		byte nextLowest = Byte.MIN_VALUE;
		for (Piece neighbour : getNeighbours(piece)) {
			byte neighbourValue = neighbour.value;
			if (neighbourValue > piece.value && neighbourValue < nextHighest) {
				nextHighest = neighbourValue;
			}
			if (neighbourValue < piece.value && neighbourValue > nextLowest) {
				nextLowest = neighbourValue;
			}
		}
		return new Pair(nextHighest,nextLowest);
	}
	
	private List<Piece> getNeighbours(Piece piece) {
		List<Piece> neighbours = new ArrayList<Piece>();

		for (Pair link : links) {
			if (link.i1 == piece.id) {
				neighbours.add(getPiece(link.i2));
				continue;
			}
			if (link.i2 == piece.id) {
				neighbours.add(getPiece(link.i1));
			}
		}

		return neighbours;
	}

	private Piece getPiece(int id) {
		for (Piece piece : pieces) {
			if (piece.id == id) {
				return piece;
			}
		}
		return null;
	}

	public void changeGrid(short pieceId, int change) {
		Piece piece=null;
		for (Piece p : pieces) {
			if (p.id == pieceId) {
				piece = p;
				break;
			}
		}
		if (piece == null) {
			System.out.println("error! piece not found in grid");
			return;
		}

		piece.value += change;
		moves += Math.abs(change);
		List<Piece> neighbours = getNeighbours(piece);
		Piece[] neighboursArray = new Piece[neighbours.size()];
		neighbours.toArray(neighboursArray);

		for (Piece neighbour : neighboursArray) {
			if (piece.value == neighbour.value) {
				mergePieces(piece, neighbour);
			}
		}
	}

	public void mergePieces(Piece p1, Piece p2) {
		Pair[] curLinks = new Pair[links.size()];
		links.toArray(curLinks);
		List<Piece> p1Neighbours = getNeighbours(p1);
		List<Piece> p2Neighbours = getNeighbours(p2);

		for (Pair curLink : curLinks) {
			if (curLink.i1 == p2.id || curLink.i2 == p2.id) {
				links.remove(curLink);
			}
		}

		a: for (Piece p2Neighbour : p2Neighbours) {
			for (Piece p1Neighbour : p1Neighbours) {
				if (p2Neighbour.id == p1Neighbour.id || p2Neighbour.id == p1.id) {
					continue a;
				}
			}
			links.add(new Pair(p1.id, p2Neighbour.id));
		}
		pieces.remove(p2);

	}

	public int pieceCount() {
		return pieces.size();
	}

	public BoardState2 getKindaDeepClone() {
		BoardState2 clone = new BoardState2();
		clone.moves = moves;
		clone.previousState = previousState;

		for (Piece piece : pieces) {
			clone.pieces.add(piece.clone());
		}
		for (Pair link : links) {
			clone.links.add(link.clone());
		}

		return clone;
	}

	public BoardState2 getNewBranch() {
		BoardState2 clone = getKindaDeepClone();
		clone.previousState = this;
		return clone;
	}

	public Piece[] getOrderedPieces(){
		Piece[] pieces = new Piece[this.pieces.size()];
		this.pieces.toArray(pieces);
		Arrays.sort(pieces);
		return pieces;
	}
	
	public int getLeastPossibleMovesLeft(){ 
		Piece[] pieces = getOrderedPieces();
		return(pieces[pieces.length-1].value-pieces[0].value);
	}
	
	@Override
	public int compareTo(BoardState2 other) {
		if (pieces.size() > other.pieces.size()) {
			return 1;
		}
		{
			return -1;
		}
	}

	/*
	 * else if (pieces.size() == other.pieces.size()) { return 0; } else
	 * 
	 * static BoardState2 generateRandomBoard(int size, int[][] grid) {
	 * BoardState2 newBoard = new BoardState2(size, grid); return newBoard; }
	 * 
	 */
}
