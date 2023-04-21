package ShankInterpreter;
import java.util.*;
public class WhileNode extends StatementNode {
	
	BoolCompareNode compare;
	Node bool;
	List<StatementNode> statements = new ArrayList<StatementNode>();
	
	public WhileNode(Node tof, List<StatementNode> commands){
		bool = tof;
		statements = commands;
	}
	//getters for comparing nodes
	public Node getComparison(){
		return bool;
	}
	//getters for statements
	public List<StatementNode> getStatements(){
		return statements;
	}
	//String literal
	public String toString(){
		String output = "WHILE: " + bool.toString();
		if(!statements.isEmpty()){
			for(StatementNode n : statements){
				output = output + n.toString();
			}
		}
		else{
			output = output + "NO STATEMENTS IN WHILE BODY \n";
		}
		return output;
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
	public BoolCompareNode getCondition() {
		return compare;
	}
}
