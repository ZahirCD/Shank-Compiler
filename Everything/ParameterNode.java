package ShankInterpreter;

public class ParameterNode extends StatementNode {
    
    private VariableReferenceNode varIdentifier = null;
    private Node expression = null;
    private boolean isChangeable;

    public ParameterNode(VariableReferenceNode inputVarIdentifier, boolean inputIsChangeable) {
        varIdentifier = inputVarIdentifier;
        isChangeable = inputIsChangeable;
        expression = null;
    }

    public ParameterNode(Node inputExpression) {
        expression = inputExpression;
        varIdentifier = null;
        isChangeable = false;
    }

    public boolean getChangeable() {
        return isChangeable;
    }

    public Node getExpression() {
        return expression;
    }

    public String getName() {
        return varIdentifier.getName();
    }

    public VariableReferenceNode getVarIdentifier() {
        return varIdentifier;
    }

    public String ToString() {
        String str = "\nParameterNode(\n";
        if (varIdentifier != null)
            str += " Var Identifier: " +varIdentifier.toString();
        if (expression != null) {
            str += " Expression: " + expression.toString();
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
