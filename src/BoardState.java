import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * An abstract representation of the state of a board. Each instance stores an
 * array of Pieces representing the pieces on the board, and a list of Pairs
 * which keeps track of which pieces are connected to which.
 * <p>
 * Identifiers:
 * <li>static HashMap idToCoord - a HashMap which maps the id of a piece to the
 * coordinates on a grid (used by Tracer class for converting the abstract
 * representation back into a concrete representation)
 * <li>short moves - the amount of moves it takes to get to this state
 * <li>Piece[] pieces - an array of instances of the Piece class representing
 * the pieces in this board
 * <li>List links - a list of instances of the Pair class used to store in pairs
 * the IDs of the pieces that are touching each other
 * <li>BoardState previousState - reference to the previous state that this
 * state originated from
 */
public class BoardState implements Comparable<BoardState> {
	static HashMap<Short, Pair> idToCoord;

	short moves;
	Piece[] pieces;
	List<Pair> links;

	BoardState previousState;

	/**
	 * constructor for BoardState class: Initializes {@code moves} as 0,
	 * {@code pieces} as null, {@code links} as a new ArrayList of type Pair.
	 */
	public BoardState() {
		moves = 0;
		pieces = null;
		links = new ArrayList<Pair>();
	}// end BoardState constructor

	/**
	 * constructor for BoardState class: Initializes {@code moves} as 0 and
	 * {@code links} as a new ArrayList of type Pair.
	 * 
	 * Also initializes the static {@code idToCoord} HashMap by, and fills the
	 * HashMap, the {@code pieces} array and the {@code links} list through
	 * calls of {@code makePieces} and {@code assignNeighbors}.
	 * 
	 * @param grid
	 */
	public BoardState(byte[][] grid) {
		this();
		idToCoord = new HashMap<Short, Pair>();
		pieces = makePieces(grid);
		assignNeighbors(grid);
	}// end BoardState constructor

	/**
	 * makePieces method: Instantiates {@code pieces} as a new ArrayList of
	 * Pieces. Iterates through the grid passed as parameter, for each tile that
	 * hasn't been already "counted", create a new Piece with the value of that
	 * tile and link the id of the piece with the coordinates of that tile by
	 * adding the combination to the static HashMap, then, call
	 * {@code countPiece} to count all the tiles that are touching that have the
	 * same value as the current tile. All the of the instances of Piece created
	 * are stored in a list and returned as a sorted array (ordered by piece
	 * value).
	 * <p>
	 * Local variables
	 * <li>List pieces - a temporary list used to hold all the Pieces
	 * <li>Piece[] finalPieces - the final array containing all the Pieces and
	 * to be returned
	 * <li>boolean[][] counted - a 2D array of boolean, each corresponding to a
	 * tile in the grid, representing whether that tile has been "counted"
	 * <li>short id - counter for id, starts as 0 and increments for each piece
	 * created
	 * 
	 * @param grid
	 *            the 2D array of integers to make pieces out of
	 * @return a sorted array of type Piece containing one Piece instance for
	 *         each actual "piece" in the grid
	 */
	private Piece[] makePieces(byte[][] grid) {
		List<Piece> pieces = new ArrayList<Piece>();
		Piece[] finalPieces;
		boolean[][] counted = new boolean[grid.length][grid.length];

		short id = 0;
		for (byte i = 0; i < grid.length; i++) {
			for (byte j = 0; j < grid.length; j++) {
				if (!counted[i][j]) {
					id++;
					pieces.add(new Piece(grid[i][j], id));
					idToCoord.put(id, new Pair(i, j));
					countPiece(i, j, grid[i][j], counted, grid);
				}
			}
		}
		finalPieces = new Piece[pieces.size()];
		pieces.toArray(finalPieces);
		Arrays.sort(finalPieces);
		return finalPieces;
	} // end makePieces method

	/**
	 * countPiece method: If the tile in the {@code grid} at the specified
	 * coordinates matches the parameter {@code pieceValue}, mark down the tile
	 * in the {@code counted} boolean array and call itself on the 4 adjacent
	 * tiles. If the tile at the specified coordinates is out of bounds or does
	 * not match {@code pieceValue}, returns without recursing.
	 * 
	 * @param row
	 *            the row in the grid for the tile
	 * @param column
	 *            the column in the grid for the tile
	 * @param pieceValue
	 *            the value of the pieces that should be marked
	 * @param counted
	 *            a 2D boolean array where each value in the array represents
	 *            whether the corresponding tile in the {@code grid} array has
	 *            been "counted"
	 * @param grid
	 *            the 2D array of integers representing the grid
	 */
	private void countPiece(byte row, byte column, byte pieceValue, boolean[][] counted, byte[][] grid) {
		if (row >= 0 && column >= 0 && row < grid.length && column < grid.length) {
			if (!counted[row][column] && grid[row][column] == pieceValue) {
				counted[row][column] = true;
				countPiece((byte) (row + 1), column, pieceValue, counted, grid);
				countPiece((byte) (row - 1), column, pieceValue, counted, grid);
				countPiece(row, (byte) (column + 1), pieceValue, counted, grid);
				countPiece(row, (byte) (column - 1), pieceValue, counted, grid);
			}
		}
	}// end countPiece

	/**
	 * assignNeighbors method: Iterates through the pieces in the {@code pieces}
	 * array. For each piece, calls {@code traverseToNeighbors} and passes it a
	 * list of type short. When the call returns, the list will be filled.
	 * Iterate through the values in the list and instantiate a Pair for each
	 * value. The Pair contains the neighboring piece's id from the list and the
	 * current piece's id. Add the Pair to the {@code links} list if it doesn't
	 * already exist.
	 * <p>
	 * Local variables:
	 * <li>List neighborIDs - a list of type Short used to hold the id of
	 * neighboring pieces to the current piece
	 * <li>boolean[][] counted - a 2D array of boolean, each corresponding to a
	 * tile in the grid, representing whether that tile has been "counted"
	 * <li>Pair coord - the coordinates in the grid for the current piece
	 * 
	 * @param grid
	 *            the 2D array of integers representing the grid
	 */
	private void assignNeighbors(byte[][] grid) {
		for (Piece piece : pieces) {
			List<Short> neighborIDs = new ArrayList<Short>();
			boolean[][] counted = new boolean[grid.length][grid.length];
			Pair coord = idToCoord.get(piece.id);
			traverseToNeighbors((byte) coord.i1, (byte) coord.i2, grid[coord.i1][coord.i2], counted, grid, neighborIDs);

			for (short neighborId : neighborIDs) {
				Pair link = new Pair(piece.id, neighborId);
				if (!links.contains(link)) {
					links.add(link);
				}
			}
		}
	}// end assignNeighbors method

	/**
	 * traverseToNeighbors method: If within bounds of the grid and the tile
	 * specified by the parameters has value equal to the {@code pieceValue}
	 * parameter, calls itself on the 4 adjacent tiles and mark the tile as
	 * counted. This will traverse recursively through the tiles of the same
	 * piece.
	 * <p>
	 * If the tile specified does not have a value equal to {@code pieceValue},
	 * call {@code traverseToPiece} method on the tile. This will occur only
	 * when the a neighboring piece has been traversed to.
	 * 
	 * @param row
	 *            the row of the tile in the grid
	 * @param column
	 *            the column of the tile in the grid
	 * @param pieceValue
	 *            the value of the piece to stay within
	 * @param counted
	 *            a 2D array of boolean, each corresponding to a tile in the
	 *            grid, representing whether that tile has been "counted"
	 * @param grid
	 *            the 2D array of integers representing the grid
	 * @param neighborIDs
	 *            a list of type Short used to hold the id of neighboring pieces
	 *            to the current piece, this will be passed onto
	 *            {@code traverseToPiece}
	 */
	private void traverseToNeighbors(byte row, byte column, byte pieceValue, boolean[][] counted, byte[][] grid,
			List<Short> neighborIDs) {
		if (row >= 0 && column >= 0 && row < grid.length && column < grid.length) {
			if (!counted[row][column]) {
				if (grid[row][column] == pieceValue) {
					counted[row][column] = true;
					traverseToNeighbors((byte) (row + 1), column, pieceValue, counted, grid, neighborIDs);
					traverseToNeighbors((byte) (row - 1), column, pieceValue, counted, grid, neighborIDs);
					traverseToNeighbors(row, (byte) (column + 1), pieceValue, counted, grid, neighborIDs);
					traverseToNeighbors(row, (byte) (column - 1), pieceValue, counted, grid, neighborIDs);
				} else {
					traverseToPiece(row, column, grid[row][column], counted, grid, neighborIDs);
				}
			}
		}
	}// end traverseToNeighbors method

	/**
	 * traverseToPiece method: If within bounds of the grid and the tile
	 * specified by the parameters has value equal to the {@code pieceValue}
	 * parameter, calls itself on the 4 adjacent tiles and mark the tile as
	 * counted. This will traverse recursively through the tiles of the same
	 * piece.
	 * <p>
	 * However, before calling itself on the 4 adjacent tiles, check if the
	 * current tile is a coordinate that corresponds to a piece, if so, add the
	 * piece's id to the {@code neighborIDs} list and return without recursing.
	 * This will ensure that the piece we are currently on will have its ID
	 * correctly added to the {@code neighborIDs} list.
	 * 
	 * @param row
	 *            the row of the tile in the grid
	 * @param column
	 *            the column of the tile in the grid
	 * @param pieceValue
	 *            the value of the piece to stay within
	 * @param counted
	 *            a 2D array of boolean, each corresponding to a tile in the
	 *            grid, representing whether that tile has been "counted"
	 * @param grid
	 *            the 2D array of integers representing the grid
	 * @param neighborIDs
	 *            a list of type Short used to hold the id of neighboring pieces
	 *            to the current piece
	 */
	private void traverseToPiece(byte row, byte column, byte pieceValue, boolean[][] counted, byte[][] grid,
			List<Short> neighborIDs) {
		if (row >= 0 && column >= 0 && row < grid.length && column < grid.length) {
			if (!counted[row][column] && grid[row][column] == pieceValue) {
				counted[row][column] = true;
				for (Piece piece : pieces) {
					Pair coord = idToCoord.get(piece.id);
					if (coord.i1 == row && coord.i2 == column) {
						if (!neighborIDs.contains(piece.id)) {
							neighborIDs.add(piece.id);
						}
						return;
					}
				}
				traverseToPiece((byte) (row + 1), column, pieceValue, counted, grid, neighborIDs);
				traverseToPiece((byte) (row - 1), column, pieceValue, counted, grid, neighborIDs);
				traverseToPiece(row, (byte) (column + 1), pieceValue, counted, grid, neighborIDs);
				traverseToPiece(row, (byte) (column - 1), pieceValue, counted, grid, neighborIDs);

			}
		}
	}// end traverseToPiece method

	/**
	 * getNextHighLow method: Iterates through the values the neighboring pieces
	 * to a piece, and returns the next highest and next lowest value in a Pair.
	 * 
	 * @param piece
	 *            the piece to look around
	 * @return a Pair where the first number is the next highest value and the
	 *         second number is the next lowest value
	 */
	public Pair getNextHighLow(Piece piece) {
		byte nextHighest = Byte.MAX_VALUE;
		byte nextLowest = Byte.MIN_VALUE;
		for (Piece neighbor : getNeighbors(piece)) {
			byte neighborValue = neighbor.value;
			if (neighborValue > piece.value && neighborValue < nextHighest) {
				nextHighest = neighborValue;
			}
			if (neighborValue < piece.value && neighborValue > nextLowest) {
				nextLowest = neighborValue;
			}
		}
		return new Pair(nextHighest, nextLowest);
	}// end getNextHighLow method

	/**
	 * getNeighbors method: Iterates through the {@code links} list, for each
	 * occurrence (in the Pairs being iterated through) of the ID of the piece
	 * received as parameter, add the piece of the other ID in the Pair to a
	 * list of neighbors, which will be returned after.
	 * <p>
	 * Local variables:
	 * <li>List neighbors - a list of type Piece to store the neighbors found
	 * and to be returned
	 * 
	 * @param piece
	 *            the piece to get a list of neighbors for.
	 * @return a list of type Piece containing references to the neighboring
	 *         pieces of the piece received as a parameter
	 */
	private List<Piece> getNeighbors(Piece piece) {
		List<Piece> neighbors = new ArrayList<Piece>();

		for (Pair link : links) {
			if (link.i1 == piece.id) {
				neighbors.add(getPiece(link.i2));
				continue;
			}
			if (link.i2 == piece.id) {
				neighbors.add(getPiece(link.i1));
			}
		}
		return neighbors;
	}// end getNeighbors method

	/**
	 * getPiece method: iterates through the {@code pieces} array, if a Piece
	 * with the id that matches the parameter is found, returns it.
	 * 
	 * @param id
	 *            the id of the piece to look for
	 * @return the Piece if found, null if not
	 */
	private Piece getPiece(int id) {
		for (Piece piece : pieces) {
			if (piece.id == id) {
				return piece;
			}
		}
		return null;
	}// end getPiece method

	/**
	 * Changes the value of a piece. Finds the piece that corresponds to the ID
	 * received from parameter, adds the {@code change} parameter to the value
	 * of that piece. Also increases move count by the amount changed.
	 * <p>
	 * After changing, iterate through piece's neighbors and calls
	 * {@code mergePieces} to merge the piece and a neighbor if the two's values
	 * are the same. Sorts the {@code pieces} array before returning.
	 * 
	 * @param pieceId
	 *            the id of the piece to change
	 * @param change
	 *            the amount to change the piece by
	 */
	public void changeGrid(short pieceId, int change) {
		Piece piece = null;
		for (Piece p : pieces) {
			if (p.id == pieceId) {
				piece = p;
				break;
			}
		}

		piece.value += change;
		moves += Math.abs(change);
		List<Piece> neighbors = getNeighbors(piece);
		Piece[] neighborsArray = new Piece[neighbors.size()];
		neighbors.toArray(neighborsArray);

		for (Piece neighbor : neighborsArray) {
			if (piece.value == neighbor.value) {
				mergePieces(piece, neighbor);
			}
		}
		Arrays.sort(pieces);
	}// end changeGrid method

	/**
	 * mergePieces method: Merges two pieces together. First, removes all the
	 * Pairs in {@code links} thats contain {@code p2}. Then, iterate through
	 * p2's and p1's neighbors. If p2 has a neighbor that isn't p1 or p1's
	 * neighbor, add a Pair between that neighbor of p2's and p1 to the
	 * {@code links} list. Finally, call {@coderemovePiece} to remove the Piece
	 * {@code p2}.
	 * <p>
	 * Local variables:
	 * <li>Pair[] curLinks - an array to temporarily store the contents of
	 * {@code links} while we are removing from it.
	 * <li>List p1Neighbours - a list of type Pieces containing all the
	 * neighbors of {@code p1}
	 * <li>List p2Neighbours - a list of type Pieces containing all the
	 * neighbors of {@code p2}
	 * 
	 * @param p1
	 *            one of the pieces to merge
	 * @param p2
	 *            the other piece to merge, this one will be removed and merged
	 *            into the {@code p1}
	 */
	private void mergePieces(Piece p1, Piece p2) {
		Pair[] curLinks = new Pair[links.size()];
		List<Piece> p1Neighbors = getNeighbors(p1);
		List<Piece> p2Neighbors = getNeighbors(p2);

		links.toArray(curLinks);
		for (Pair curLink : curLinks) {
			if (curLink.i1 == p2.id || curLink.i2 == p2.id) {
				links.remove(curLink);
			}
		}

		a: for (Piece p2Neighbor : p2Neighbors) {
			for (Piece p1Neighbor : p1Neighbors) {
				if (p2Neighbor.id == p1Neighbor.id || p2Neighbor.id == p1.id) {
					continue a;
				}
			}
			links.add(new Pair(p1.id, p2Neighbor.id));
		}

		removePiece(p2.id);
	}// end mergePiece

	/**
	 * removePiece method: Creates a new array of type Piece that is one element
	 * shorter than the current {@code pieces} array. Copy over all of the
	 * pieces in the current {@code pieces} array except for the one specified
	 * by parameter {@code id}. After, replaces the current {@code pieces} with
	 * the new array created.
	 * <p>
	 * Local variables:
	 * <li>Piece[] newPieces - a new array that will contain all of the current
	 * pieces except for the one to be removed
	 * <li>int pos - the current index in {@code newPiece}, increments when
	 * objects are stored into {@code newPiece}
	 * 
	 * @param id
	 *            the id of the piece to remove
	 */
	private void removePiece(short id) {
		Piece[] newPieces = new Piece[pieces.length - 1];
		int pos = 0;

		for (int i = 0; i < pieces.length; i++) {
			if (pieces[i].id != id) {
				newPieces[pos] = pieces[i];
				pos++;
			}
		}
		pieces = newPieces;
	}// end removePiece method

	/**
	 * getDeepClone method: Returns a deep clone of this instance. The deep
	 * clone will be a separate instance of the BoardState class with all
	 * variables of this instance copied over. Each of the instances of Piece
	 * and instances of Pair will have their {@code clone} method called to get
	 * a clone of those instances too.
	 * <p>
	 * Local variables:
	 * <li>BoardState clone - a deep clone of this instance
	 * 
	 * @return a completely separate clone of this instance
	 */
	public BoardState getDeepClone() {
		BoardState clone = new BoardState();
		clone.moves = moves;
		clone.previousState = previousState;

		clone.pieces = new Piece[pieces.length];
		for (int i = 0; i < pieces.length; i++) {
			clone.pieces[i] = pieces[i].clone();
		}
		for (Pair link : links) {
			clone.links.add(link.clone());
		}
		return clone;
	}// end getDeepClone method

	/**
	 * getNewBranch method: Returns a deep clone of this instance, but with the
	 * clone's {@code previousState} set to this instance.
	 * <p>
	 * Local variables:
	 * <li>BoardState clone - a deep clone of this instance
	 * 
	 * @return a completely separate clone of this instance with its
	 *         {@code previousState} referencing this instance
	 */
	public BoardState getNewBranch() {
		BoardState clone = getDeepClone();
		clone.previousState = this;
		return clone;
	}// end getNewBranch method

	/**
	 * getLeastPossibleMovesLeft method: Subtracts the maximum value and the
	 * minimum value on the board.
	 * 
	 * @return the hard limit of the least possible amount of moves it would
	 *         take solve the board
	 */
	public int getLeastPossibleMovesLeft() {
		return (pieces[pieces.length - 1].value - pieces[0].value);
	}// end getLeastPossibleMovesLeft method

	/**
	 * compareTo method: Returns an integer based on whether this instance is
	 * considered greater than another instance.
	 * 
	 * @param other
	 *            the other instance to compare this one to
	 * @return a positive number if this is greater than the other, 0 is they
	 *         are equal, and a negative number if this instance is lesser
	 */
	@Override
	public int compareTo(BoardState other) {
		return pieces.length - other.pieces.length;
	}//end compareTo method
}//end BoardState class
