package ShankInterpreter;

public class BoolNode extends Node {
	boolean val;
	//constructor
	public BoolNode(boolean b){
		val = b;
	}
	//getter
	public boolean getBool(){
		return val;
	}
	//string representation
	public String toString(){
		return Boolean.toString(val);
	}
}
