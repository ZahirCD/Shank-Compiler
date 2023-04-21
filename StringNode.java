package ShankInterpreter;

public class StringNode extends StatementNode{
	String val;
	//constructor
	public StringNode(String s){
		val = s;
	}
	
	//getter
	public String getString(){
		return val;
	}
	
	//string representation
	public String toString(){
		return val;
	}

	@Override
	public Node getRight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getLeft() {
		// TODO Auto-generated method stub
		return null;
	}
}
