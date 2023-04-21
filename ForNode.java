package ShankInterpreter;
import java.util.*;
public class ForNode extends StatementNode{
	
	Node start, end;
	VariableReferenceNode variable;
	List<StatementNode> statements = new ArrayList<StatementNode>();
	
	public ForNode(VariableReferenceNode local, Node begin, Node finish, List<StatementNode> commands){
		variable = local;
		start = begin;
		end = finish;
		statements = commands;
	}
	
	public Node getStartValue(){
		return start;
	}
	
	public Node getEndValue(){
		return end;
	}
	
	//getters for variables
	public VariableReferenceNode getVariable(){
		return variable;
	}
	
	//getters for statements
	public List<StatementNode> getStatements(){
		return statements;
	}
	
	//String literal
	public String toString(){
		String output = variable.toString() + " starts at " + start.toString() + " and ends at " + end.toString() + "\n";
		for(StatementNode n: statements)
		{
			output = output + n.toString() + "\n";
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
