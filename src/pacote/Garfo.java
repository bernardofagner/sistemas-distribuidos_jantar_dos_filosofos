package pacote;

public class Garfo {

	private final int number; //informa o numero do garfo
	private boolean used; // flag que informa se o garfo esta sendo usado ou nao: true significa que esta sendo usado
	
	public Garfo(int number, boolean used) {
		this.number = number;
		this.used = used;
	}

	public boolean getIsUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public int getNumber() {
		return number;
	}	
		
}
