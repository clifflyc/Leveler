
/**
 * A class that stores two numbers of data type short. Objects of this class are
 * considered equal if they contain the same two numbers, in any order. For
 * example {1,2} is equal to {1,2} and is equal to {2,1}.
 * <p>
 * Identifiers:
 * <li>short i1 - the first number
 * <li>short i2 - the second number
 */
public final class Pair {

	public final short i1;
	public final short i2;

	/**
	 * Constructor method for the class Pair, this is the only place where set
	 * the two numbers can be set.
	 * 
	 * @param i1
	 *            the first number in the pair
	 * @param i2
	 *            the second number in the pair
	 */
	public Pair(short i1, short i2) {
		this.i1 = i1;
		this.i2 = i2;
	}

	/**
	 * clone method: Creates a clone of this instance by copying {@code i1} and
	 * {@code i2} into a new instance.
	 * 
	 * @return a new instance of the Pair class with the same {@code i1} and
	 *         {@code i2} values as this instance.
	 */
	public Pair clone() {
		return new Pair(i1, i2);
	}

	/**
	 * equals method: check if this instance is considered equal to another
	 * instance of this class.
	 * 
	 * @param other
	 *            the other object to compare this instance with
	 * @return {@code true} if this instance is considered equal with the other
	 *         instance passed as parameter
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof Pair) {
			Pair otherCoord = (Pair) other;
			if ((otherCoord.i1 == i1 && otherCoord.i2 == i2) || (otherCoord.i1 == i2 && otherCoord.i2 == i1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * hashCode method: returns a hash code that will be the same number for
	 * instances that are considered equal. This is done by combining the two
	 * numbers in the pair in order into a string and returning the hash code of
	 * the string.
	 * 
	 * @return the hash code for this instance
	 */
	@Override
	public int hashCode() {
		if (i1 < i2) {
			return (i1 + "" + i2).hashCode();
		} else {
			return (i2 + "" + i1).hashCode();
		}
	}
}
