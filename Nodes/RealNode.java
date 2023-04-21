package ShankInterpreter;

public class RealNode extends Node {
	private float num;
	/*
	 * Constructor
	 */
	public RealNode() {
		num = -1;
	}
	
	/*
	 * Parameterized Constructor
	 * val : int
	 */
	public RealNode(int val){
		num = val;
	}
	
	public RealNode(float data) {
		num = data;
	}

	/*
	 * Return number as float
	 */
	public float getVal(){
		return (float)num;
	}
	
	/*
	 * Return number as string literal
	 */
	public String toString(){
		return String.valueOf(num);
	}
}
