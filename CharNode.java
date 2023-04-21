package ShankInterpreter;

public class CharNode {
	char val ;
	//constructor
	public CharNode(char c){
		val = c;
	}
	
	//getter
	public char getChar(){
		return val;
	}
	
	//string representation
	public String toString(){
		return Character.toString(val);
	}
}
