package ShankInterpreter;

public class VariableNode extends Node {
	 private String name;
	 
	    public enum variableType { 
	    	REAL, INTEGER, CHARACTER, STRING, BOOLEAN, ARRAY 
	    
	    }
	    private variableType type;
	    private variableType arrayType;
	    private Node value = null;
	    private boolean modifyType;
	    private boolean hasTypeLimit;
	    private int initialSetSwitch = 0;
	    private int intFrom;
	    private int intTo;
	    private float realFrom;
	    private float realTo;
	    private float realArray[];
	    private int intArray[];
	    private boolean boolArray[];
	    private String stringArray[];
	    private char charArray[];

	    public VariableNode(String inputName, variableType inputType, boolean inputChangeable) { //For variables
	        name = inputName;
	        type = inputType;
	        modifyType = inputChangeable;
	    }

	    public VariableNode(String inputName, variableType inputType, boolean inputChangeable, Node inputValue) { //For constants
	        name = inputName;
	        type = inputType;
	        modifyType = inputChangeable;
	        value = inputValue;
	    }

	    public VariableNode(String inputName, int inputFrom, int inputTo, variableType inputArrayType) { //For arrays
	        name = inputName;
	        type = VariableNode.variableType.ARRAY;
	        arrayType = inputArrayType;
	        intFrom = inputFrom;
	        intTo = inputTo;
	        modifyType = true;
	        hasTypeLimit = true;
	        realArray = new float[inputTo - inputFrom];
	        intArray = new int[inputTo - inputFrom];
	        boolArray = new boolean[inputTo - inputFrom];
	        stringArray = new String[inputTo - inputFrom];
	        charArray = new char[inputTo - inputFrom];
	    }

	    public VariableNode(String inputName, variableType inputType, int inputFrom, int inputTo) { //For int and string ranges
	        name = inputName;
	        type = inputType;
	        intFrom = inputFrom;
	        intTo = inputTo;
	        modifyType = true;
	        hasTypeLimit = true;
	    }

	    public VariableNode(String inputName, variableType inputType, float inputRealFrom, float inputRealTo) { //For real ranges
	        name = inputName;
	        type = inputType;
	        realFrom = inputRealFrom;
	        realTo = inputRealTo;
	        modifyType = true;
	        hasTypeLimit = true;
	    }

	    public Node getArrayValueAtIndex(int inputIndex, variableType inputType) { //For adding parameters to the variable map
	        switch (inputType) {
	            case REAL :
	                if (realArray.length <= inputIndex)
	                    return null;
	                float realValue = realArray[inputIndex];
	                RealNode arrayRealNode = new RealNode(realValue);
	                return arrayRealNode;
	            case INTEGER :
	                if (intArray.length <= inputIndex)
	                    return null;
	                int intValue = intArray[inputIndex];
	                IntegerNode arrayIntNode = new IntegerNode(intValue);
	                return arrayIntNode;
	            case STRING :
	                if (stringArray.length <= inputIndex)
	                    return null;
	                String stringValue = stringArray[inputIndex];
	                StringNode arrayStringNode = new StringNode(stringValue);
	                return arrayStringNode;
	            case CHARACTER :
	                if (charArray.length <= inputIndex)
	                    return null;
	                Character characterValue = charArray[inputIndex];
	                CharacterNode arrayCharacterNode = new CharacterNode(characterValue);
	                return arrayCharacterNode;
	            case BOOLEAN :
	                if (boolArray.length <= inputIndex)
	                    return null;
	                boolean booleanValue = boolArray[inputIndex];
	                BoolNode arrayBooleanNode = new BoolNode(booleanValue);
	                return arrayBooleanNode;
	            default :
	                return null;
	        }
	    }

	    public variableType getArrayType() {
	        return arrayType;
	    }

	    public boolean getmodifyType() {
	        return modifyType;
	    }

	    public boolean getTypeLimit() {
	        return hasTypeLimit;
	    }

	    public int getIntFrom() {
	        return intFrom;
	    }

	    public int getIntTo() {
	        return intTo;
	    }

	    public String getName() {
	        return name;
	    }

	    public float getRealFrom() {
	        return realFrom;
	    }

	    public float getRealTo() {
	        return realTo;
	    }

	    public variableType getType() {
	        return type;
	    }

	    public Node getValue() {
	        return value;
	    }

	    public void setArraySize(int inputStart, int inputEnd) {
	        intFrom = inputStart;
	        intTo = inputEnd;
	        realArray = new float[intTo - intFrom];
	        intArray = new int[intTo - intFrom];
	        boolArray = new boolean[intTo - intFrom];
	        stringArray = new String[intTo - intFrom];
	        charArray = new char[intTo - intFrom];
	    }

	    public void setArrayValueAtIndex(int inputIndex, Node inputNode, variableType inputVariableType) { // For updating parameters
	        switch (inputVariableType) {
	            case INTEGER:
	                IntegerNode newIntegerNode = (IntegerNode) inputNode;
	                int newInteger = newIntegerNode.getValue();
	                intArray[inputIndex] = newInteger;
	                break;
	            case REAL:
	                RealNode newRealNode = (RealNode) inputNode;
	                float newReal = newRealNode.getVal();
	                realArray[inputIndex] = newReal;
	                break;
	            case STRING:
	                StringNode newStringNode = (StringNode) inputNode;
	                String newString = newStringNode.getString();
	                stringArray[inputIndex] = newString;
	                break;
	            case CHARACTER:
	                CharacterNode newCharacterNode = (CharacterNode) inputNode;
	                char newCharacter = newCharacterNode.getValue();
	                charArray[inputIndex] = newCharacter;
	                break;
	            case BOOLEAN:
	                BoolNode newBooleanNode = (BoolNode) inputNode;
	                boolean newBoolean = newBooleanNode.getBool();
	                boolArray[inputIndex] = newBoolean;
	                break;
	            default :
	                System.out.println("ERROR: Incorrect data type given in argument array.");
	                System.exit(0);
	        }
	    }

	    public void setValue(Node inputValue) { 
	        if (modifyType == true)
	            value = inputValue;
	        if (modifyType == false && initialSetSwitch == 0) { //Only works once for constants
	            value = inputValue;
	            initialSetSwitch = 1;
	        }
	    }
	    
	    public String ToString() {
	        return "VariableNode(" + name + "," + type + ")";
	    }
}