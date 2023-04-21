package ShankInterpreter;

import java.util.ArrayList;

public class RepeatNode extends StatementNode {

    private BoolCompareNode condition;
    private ArrayList<StatementNode> statements = new ArrayList<StatementNode>();

    public RepeatNode(BoolCompareNode inputCondition, ArrayList<StatementNode> inputStatementArray) {
        condition = inputCondition;
        statements = inputStatementArray;
    }

    public BoolCompareNode getCondition() {
        return condition;
    }

    public ArrayList<StatementNode> getStatements() {
        return statements;
    }

    public String toString() {
        String str = "\nRepeatNode(\n Condition: ";
        str += condition.toString() + "\n Statements:\n";
        
        for (int i = 0; i < statements.size() ; i++) {
            str += statements.get(i).toString();
        }
        str += "\n)";
        return str;
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
