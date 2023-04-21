package ShankInterpreter;

public class BoolCompareNode extends StatementNode {
    public enum comparisonType { 
    	LESSTHAN, GREATERTHAN, LESSTHANOREQUALTO, GREATERTHANOREQUALTO, EQUAL, NOTEQUAL 
    }
    private comparisonType type;
    private Node left;
    private Node right;

    public BoolCompareNode(comparisonType inputType, Node inputLeft, Node inputRight) {
    type = inputType;
    left = inputLeft;
    right = inputRight;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public comparisonType getType() {
        return type;
    }

    public String ToString() {
        return "BooleanCompareNode(\n  Type: " + type + "\n  Left: " + left.toString() + "\n  Right: " + right.toString() + "\n)";
    }

	
}
