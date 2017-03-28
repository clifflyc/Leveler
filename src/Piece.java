/**
 * A class that represents a single piece in the puzzle. Instances of this class
 * can have an id (assigned externally) and a value. Instances can be compared
 * to other instances based on their value.
 * <p>
 * Variables:
 * <li>byte value - the value of this piece in the game
 * <li>short id - the id assigned to this piece, id should be unique for every
 * piece on a single board
 */
public class Piece implements Comparable<Piece> {
	public byte value;
	public short id;

	/**
	 * Constructor for the Piece class: Assigns a {@code value} and {@code id}
	 * for this instance.
	 * 
	 * @param value
	 *            - the value of the piece
	 * @param id
	 *            - an id to assign the piece
	 */
	public Piece(byte value, short id) {
		this.value = value;
		this.id = id;
	}// end piece constructor

	/**
	 * clone method: Creates a clone of this instance by copying {@code value}
	 * and {@code id} into a new instance.
	 * 
	 * @return a new instance of the Piece class with the same {@code value} and
	 *         {@code id} values as this instance.
	 */
	public Piece clone() {
		return new Piece(value, id);
	}// end clone method

	/**
	 * compareTo method: Compares this Piece to another Piece by comparing the
	 * two pieces' {@code value}, and returns an integer based on the result of
	 * this comparison.
	 * 
	 * @param other
	 *            the other Piece to compare this piece to.
	 * @return positive integer if this instance is greater, negative integer if
	 *         this instance is lesser, or 0 if both are equal.
	 */
	@Override
	public int compareTo(Piece other) {
		return value - other.value;
	}// end compareTo method
}// end Piece class
