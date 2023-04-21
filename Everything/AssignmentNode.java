package ShankInterpreter;
public class AssignmentNode extends StatementNode {

    private VariableReferenceNode targetOfAssignment;
    private Node assignmentValue;
    
    public AssignmentNode(VariableReferenceNode inputTargetOfAssignment, Node inputAssignmentValue) {
        targetOfAssignment = inputTargetOfAssignment;
        assignmentValue = inputAssignmentValue;
    }

    public VariableReferenceNode getTarget() {
        return targetOfAssignment;
    }

    public Node getValue() {
        return assignmentValue;
    }

    public String toString() {
        String str = "\nAssignmentNode(\n  Target: " + targetOfAssignment.toString();
        if (assignmentValue instanceof MathOpNode) {
            MathOpNode expressionValue = (MathOpNode) assignmentValue;
            str += "\n  Value: " + expressionValue.toString();
        }
        else if (assignmentValue instanceof IntegerNode) {
            IntegerNode expressionValue = (IntegerNode) assignmentValue;
            str += "\n  Value: " + expressionValue.toString();
        }
        else if (assignmentValue instanceof RealNode) {
            RealNode expressionValue = (RealNode) assignmentValue;
            str += "\n  Value:" + expressionValue.toString();
        }
        else if (assignmentValue instanceof BoolCompareNode) {
            BoolCompareNode expressionValue = (BoolCompareNode) assignmentValue;
            str += "\n  Value:" + expressionValue.toString();
        }
        else if (assignmentValue instanceof VariableReferenceNode) {
            VariableReferenceNode expressionValue = (VariableReferenceNode) assignmentValue;
            str += "\n  Value:" + expressionValue.toString();
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
