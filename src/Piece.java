public class Piece implements Comparable<Piece>{
	public byte value;
	public short id;
	
	
	public Piece(byte value,short id) {
		this.value = value;
		this.id = id;
	}
	
	
	public Piece clone() {
		return new Piece(value,id);
	}


	@Override
	public int compareTo(Piece other) {
		return value-other.value;
	}

}
