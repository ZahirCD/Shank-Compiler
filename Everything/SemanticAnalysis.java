package ShankInterpreter;

import java.util.ArrayList;

public class SemanticAnalysis {
	
	
	/*
	 * Checks the assignments in a given ProgramNode by iterating over the AssignmentNode objects in the ArrayList. It determines the variable type of the left-hand side and right-hand side of the assignment using the getLeftSideType() and 
	 * getRightSideType() helper functions. It then compares the types and checks if they match. If the left-hand side type is not recognizable, it will print an error message and exit with code 0. If the right-hand side type is not recognizable, 
	 * it will print an error message and exit with code 1. If the types do not match, it will print an error message and exit with code 2.
	 * @param inputProgramNode : ProgramNode
	 */
	public void CheckAssignments(ProgramNode inputProgramNode) {
        ArrayList<AssignmentNode> assignmentNodeArray = inputProgramNode.getAssignmentNodeArray();
        for (AssignmentNode assignmentNode : assignmentNodeArray) {
            VariableNode.variableType leftType;
            VariableNode.variableType rightType;
            String variableName = assignmentNode.getTarget().getName();
            leftType = getLeftSideType(assignmentNode);
            rightType = getRightSideType(assignmentNode);
            if (leftType == null) {
                System.out.println("Variable '" + variableName + "' has an unrecognizable data type.");
                System.exit(0);
            }
            if (rightType == null) {
                System.out.println("Data assigned to variable: '" + variableName +  "' has an unrecognizable data type");
                System.exit(1);
            }
            if (leftType != rightType) {
                System.out.println("Data assigned to variable: '" + variableName +  "' does not match variable's data type");
                System.exit(2);
            }
        }
    }
	
	/*
	 * Determines the data type of the left-hand side of the assignment statement.
	 * @param inputAssignmentNode : AssignmentNode 
	 */
    private VariableNode.variableType getLeftSideType(AssignmentNode inputAssignmentNode) {
        if (inputAssignmentNode.getTarget().getReferencedVariable().getmodifyType() == false) { //Checks for constants
            String variableName = inputAssignmentNode.getTarget().getName();
            System.out.println("Variable '" + variableName +"' is a constant and cannot be changed.");
            System.exit(3);
        }
        return inputAssignmentNode.getTarget().getType();
    }
    
    /*
     * Returns the data type of the right-hand side value in an assignment statement
     * @param inputAssignmentNode : AssignmentNode 
     */
    private VariableNode.variableType getRightSideType(AssignmentNode inputAssignmentNode) {
        Node value = inputAssignmentNode.getValue();
        if (value instanceof MathOpNode) {
            MathOpNode mathOpNodeValue = (MathOpNode) value;
            return mathOpNodeValue.getDataType();
        }
        else if (value instanceof IntegerNode) {
            return VariableNode.variableType.INTEGER;
        }
        else if (value instanceof RealNode) {
            return VariableNode.variableType.REAL;
        }
        else if (value instanceof BoolNode) {
            return VariableNode.variableType.BOOLEAN;
        }
        else if (value instanceof StringNode) {
            return VariableNode.variableType.STRING;
        }
        else if (value instanceof CharacterNode) {
            return VariableNode.variableType.CHARACTER;
        }
        return null;
    }
	
}
