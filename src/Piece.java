import java.util.ArrayList;
import java.util.List;

public class Piece implements Comparable<Piece>{
	public int value, id;

	public Piece(int value,int id) {
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
