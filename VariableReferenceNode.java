package ShankInterpreter;

public class VariableReferenceNode extends Node {
	String variableName;
    private Node arrayIndexExpression = null;
    private VariableNode referencedVariableNode = null;
    private VariableNode.variableType type = null;
	
	
	public VariableReferenceNode(String name){
		variableName = name;
	}
	
	//String literal
	public String toString(){
		return variableName;
	}
	
	public VariableReferenceNode(String inputName, VariableNode inputReferencedNode) {
        variableName = inputName;
        referencedVariableNode = inputReferencedNode;
        type = inputReferencedNode.getType();
    }

    public VariableReferenceNode(String inputName, VariableNode inputReferencedNode, Node inputArrayIndexExpression) { //For values at array index
        variableName = inputName;
        arrayIndexExpression = inputArrayIndexExpression;
        referencedVariableNode = inputReferencedNode;
        type = inputReferencedNode.getArrayType();
    }
    
    public VariableNode getReferencedVariable() {
        return referencedVariableNode;
    }
	
	 public Node getIndex() {
	        if (arrayIndexExpression instanceof IntegerNode) {
	            IntegerNode intNodeIndex = (IntegerNode) arrayIndexExpression;
	            return intNodeIndex;
	        }
	        else if (arrayIndexExpression instanceof MathOpNode) {
	            MathOpNode mathOpNodeIndex = (MathOpNode) arrayIndexExpression;
	            return mathOpNodeIndex;
	        }
	        else if (arrayIndexExpression instanceof VariableReferenceNode) {
	            VariableReferenceNode newVariableReferenceNode = (VariableReferenceNode) arrayIndexExpression;
	            return newVariableReferenceNode;
	        }
	        return null;
	    }
	 
	 public void setIndex(IntegerNode inputIntegerNode) {
	        arrayIndexExpression = inputIntegerNode;
	    }
	 
	 public VariableNode.variableType getType() {
	        return type;
	    }
}
