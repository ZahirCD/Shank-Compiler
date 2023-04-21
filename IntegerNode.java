package ShankInterpreter;

public class IntegerNode extends Node{
	private int num;
	
	/*
	 * Constructor
	 */
	public IntegerNode() {
		num = -1;
	}
	
	/*
	 * IntegerNode : parameterized constructor 
	 * val : int
	 */
	public IntegerNode(int val){
		num = val;
	}
	
	/*
	 * Get Float value 
	 */
	public float getVal(){
		return (float)num;
	}
	
	public int getIntValue() {
        return num;
    }
	
	/*
	 * Return number of string literal
	 */
	public String toString(){
		return String.valueOf(num);
	}

	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
