public final class Pair {

	public final short i1;
	public final short i2;

	public Pair(short i1, short i2) {
		this.i1 = i1;
		this.i2 = i2;
	}

	public Pair clone() {
		return new Pair(i1, i2);
	}

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

	@Override
	public int hashCode() {
		if (i1 < i2) {
			return (i1 + "" + i2).hashCode();
		} else {
			return (i2 + "" + i1).hashCode();
		}
	}
}
