package ShankInterpreter;
import java.util.*;
public class IfNode extends StatementNode {
	
	private BoolCompareNode bool;
	private List<StatementNode> statements;
	private IfNode next;
	
	public IfNode(BoolCompareNode torf, List<StatementNode> commands, IfNode nextInLine){
		bool = torf;
		statements = commands;
		next = nextInLine;
	}
	
	 public IfNode(ArrayList<StatementNode> inputStatementArray) { // For else statements that have no conditions
	        statements = inputStatementArray;
	    }
	
	public BoolCompareNode getCondition() {
        if (bool == null)
            return null;
        return bool;
    }
	
	//getters for comparing nodes
	public BoolCompareNode getComparison(){
		return bool;
	}
	//getters for statements
	public List<StatementNode> getStatements(){
		return statements;
	}
	//getters for next Node
	public IfNode getNext(){
		return next;
	}
	//String literal
	public String toString(){
		String output;
		if(bool!=null){
			output = "IF: "+ bool.toString();
		}
		else {
			output = "ELSE \n";
		}
		
		if(statements.isEmpty()){
			output = output + "NO STATEMENTS  \n";
		}
		else{
			for(StatementNode n : statements){
				output = output + n.toString();
			}
		}
		
		if(next!=null){
			output = output + next.toString();
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


	
}