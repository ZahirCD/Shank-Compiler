package ShankInterpreter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import ShankInterpreter.ArrayDataType.arrayDataType;

public class Interpreter {

	private LinkedHashMap<String, FunctionNode> functionMap;
	
	/*
	 * Constructor of the Interpreter class, which takes a LinkedHashMap of inputFunctionMap as a parameter and throws a SyntaxErrorException if an error occurs during the execution.
	 * @param inputFunctionMap : LinkedHashMap<String, FunctionNode>
	 */
    public Interpreter(LinkedHashMap<String, FunctionNode> inputFunctionMap) throws SyntaxErrorException {
        functionMap = inputFunctionMap;
        Collection<FunctionNode> functionArray = functionMap.values(); // Converts hash map into list in order to iterate through functions
        for (FunctionNode function : functionArray) {
            LinkedHashMap<String, InterpreterDataType> localVariableMap = new LinkedHashMap<String, InterpreterDataType>();
            interpretFunction(localVariableMap, function);
        }
    }
    
    /*
     *  Extracts the name of the variable to be assigned and the value to be assigned from the AssignmentNode object. It then retrieves the current value of the variable from the LinkedHashMap based on its name. 
     *  The function checks whether the variable is a constant and exits the program with an error message if the user attempts to modify it.
     *  @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     *  @param inputAssignmentNode : AssignmentNode
     */
    private void AssignmentNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, AssignmentNode inputAssignmentNode) throws SyntaxErrorException {
        String assignmentTarget = inputAssignmentNode.getTarget().getName();
        Node assignmentValue = inputAssignmentNode.getValue();
        InterpreterDataType assignmentVariable = inputLocalVariableMap.get(assignmentTarget);
        if (!(assignmentVariable.modifyType())) { //System exits and gives error message if user attempts to alter a constant
            System.out.println("ERROR: Cannot change the value of a constant.");
            System.exit(7);
        }
        if (assignmentValue instanceof VariableReferenceNode) { //Replaces a variable reference with the actual variable value
            VariableReferenceNode variableReferenceNode = (VariableReferenceNode) assignmentValue;
            InterpreterDataType variableReferenceData = VariableReferenceNodeFunction(inputLocalVariableMap, variableReferenceNode);
            if (variableReferenceData instanceof IntegerDataType) {
                IntegerDataType integerData = (IntegerDataType) variableReferenceData;
                IntegerNode integerNode = new IntegerNode(integerData.getData());
                assignmentValue = integerNode;
            }
            else if (variableReferenceData instanceof StringDataType) {
                StringDataType stringData = (StringDataType) variableReferenceData;
                StringNode stringNode = new StringNode(stringData.getData());
                assignmentValue = stringNode;
            }
            else if (variableReferenceData instanceof RealDataType) {
                RealDataType realData = (RealDataType) variableReferenceData;
                RealNode realNode = new RealNode(realData.getData());
                assignmentValue = realNode;
            }
            else if (variableReferenceData instanceof BooleanDataType) {
                BooleanDataType booleanData = (BooleanDataType) variableReferenceData;
                BoolNode booleanNode = new BoolNode(booleanData.getData());
                assignmentValue = booleanNode;
            }
            else if (variableReferenceData instanceof CharacterDataType) {
                CharacterDataType characterData = (CharacterDataType) variableReferenceData;
                CharacterNode characterNode = new CharacterNode(characterData.getData());
                assignmentValue = characterNode;
            }
        }
        if (inputAssignmentNode.getTarget().getIndex() != null) { //Array with index value
            if (assignmentVariable instanceof ArrayDataType) {
                ArrayDataType array = (ArrayDataType) assignmentVariable;
                int arrayIndex = 0;
                //Converts any data type found in index to integer
                if (inputAssignmentNode.getTarget().getIndex() instanceof IntegerNode) {
                    IntegerNode arrayIndexNode = (IntegerNode) inputAssignmentNode.getTarget().getIndex();
                    arrayIndex = arrayIndexNode.getValue();
                }
                else if (inputAssignmentNode.getTarget().getIndex() instanceof MathOpNode) {
                    MathOpNode mathOpNodeIndex = (MathOpNode) inputAssignmentNode.getTarget().getIndex();
                    InterpreterDataType indexData = MathOpNodeFunction(inputLocalVariableMap, mathOpNodeIndex);
                    if (indexData instanceof IntegerDataType) {
                        IntegerDataType integerIndexData = (IntegerDataType) indexData;
                        arrayIndex = integerIndexData.getData();
                        IntegerNode arrayIndexNode = new IntegerNode(arrayIndex);
                        inputAssignmentNode.getTarget().setIndex(arrayIndexNode); //Replaces previous index with index converted to integer
                    }
                    else {
                        System.out.println("ERROR: Index provided for array is of an incorrect data type.");
                        System.exit(8);  
                    }
                }
                else if (inputAssignmentNode.getTarget().getIndex() instanceof VariableReferenceNode) {
                    VariableReferenceNode variableReferenceIndex = (VariableReferenceNode) inputAssignmentNode.getTarget().getIndex();
                    if (inputLocalVariableMap.containsKey(variableReferenceIndex.getName())) {
                        InterpreterDataType referencedData = inputLocalVariableMap.get(variableReferenceIndex.getName());
                        if (referencedData instanceof IntegerDataType) {
                            IntegerDataType indexData = (IntegerDataType) referencedData;
                            arrayIndex = indexData.getData();
                        }
                        else {
                            System.out.println("ERROR: Index provided for array is of an incorrect data type.");
                            System.exit(9);
                        }
                    }
                    else {
                        System.out.println("ERROR: Variable referenced in array index not found.");
                        System.exit(10);
                    }
                }
                // Must check that assignment value matches array type of assignment target  
                if (assignmentValue instanceof MathOpNode) {
                    MathOpNode mathOpNode = (MathOpNode) assignmentValue;
                    InterpreterDataType mathOpExpressionResultData = MathOpNodeFunction(inputLocalVariableMap, mathOpNode);
                    if (mathOpExpressionResultData instanceof IntegerDataType && array.getArrayType() == ArrayDataType.arrayDataType.INTEGER) {
                        IntegerDataType integerData = (IntegerDataType) mathOpExpressionResultData;
                        array.setIndex(arrayIndex, integerData); // Overwrite index value in array 
                        inputLocalVariableMap.put(assignmentTarget, array); // Update variables data in variable map
                    }
                    else if (mathOpExpressionResultData instanceof RealDataType && array.getArrayType() == ArrayDataType.arrayDataType.REAL) {
                        RealDataType realData = (RealDataType) mathOpExpressionResultData;
                        array.setIndex(arrayIndex, realData);
                        inputLocalVariableMap.put(assignmentTarget, array);
                    }
                    else if (mathOpExpressionResultData instanceof StringDataType && array.getArrayType() == ArrayDataType.arrayDataType.STRING) {
                        StringDataType stringData = (StringDataType) mathOpExpressionResultData;
                        array.setIndex(arrayIndex, stringData);
                        inputLocalVariableMap.put(assignmentTarget, array);
                    }
                    else {
                        System.out.println("ERROR: Expression assigned to index is of an incorrect data type.");
                        System.exit(11);
                    }
                }
                else if (assignmentValue instanceof IntegerNode && array.getArrayType() == ArrayDataType.arrayDataType.INTEGER) {
                    IntegerNode intAssignmentValue = (IntegerNode) assignmentValue;
                    int intValue = intAssignmentValue.getValue();
                    IntegerDataType newIntegerData = new IntegerDataType(intValue, false);
                    array.setIndex(arrayIndex, newIntegerData);
                    inputLocalVariableMap.put(assignmentTarget, array);             
                }
                else if (assignmentValue instanceof RealNode && array.getArrayType() == ArrayDataType.arrayDataType.REAL) {
                    RealNode realAssignmentValue = (RealNode) assignmentValue;
                    float realValue = realAssignmentValue.getVal();
                    RealDataType newRealData = new RealDataType(realValue, false);
                    array.setIndex(arrayIndex, newRealData);
                    inputLocalVariableMap.put(assignmentTarget, array);
                }
                else if (assignmentValue instanceof CharacterNode && array.getArrayType() == ArrayDataType.arrayDataType.CHARACTER) {
                    CharacterNode characterAssignmentValue = (CharacterNode) assignmentValue;
                    char characterValue = characterAssignmentValue.getValue();
                    CharacterDataType newCharacterData = new CharacterDataType(characterValue, false);
                    array.setIndex(arrayIndex, newCharacterData);
                    inputLocalVariableMap.put(assignmentTarget, array);
                }
                else if (assignmentValue instanceof BoolNode && array.getArrayType() == ArrayDataType.arrayDataType.BOOLEAN) {
                    BoolNode booleanAssignmentValue = (BoolNode) assignmentValue;
                    boolean booleanValue = booleanAssignmentValue.getBool();
                    BooleanDataType newBooleanData = new BooleanDataType(booleanValue, false);
                    array.setIndex(arrayIndex, newBooleanData);
                    inputLocalVariableMap.put(assignmentTarget, array);
                }
                else if (assignmentValue instanceof StringNode && array.getArrayType() == ArrayDataType.arrayDataType.STRING) {
                    StringNode stringAssignmentValue = (StringNode) assignmentValue;
                    String stringValue = stringAssignmentValue.getString();
                    StringDataType newStringData = new StringDataType(stringValue, false);
                    array.setIndex(arrayIndex, newStringData);
                    inputLocalVariableMap.put(assignmentTarget, array);
                }
                else {
                    System.out.println("ERROR: Assignment data type does not match array data type.");
                    System.exit(12);
                }
            }
            else {
                System.out.println("ERROR: Index given for non-array data type.");
                System.exit(13);
            }
        }
        else if (assignmentValue instanceof IntegerNode) {
            IntegerNode currentIntegerNode = (IntegerNode) assignmentValue;
            IntegerDataType referencedIntegerData = (IntegerDataType) assignmentVariable;
            if (referencedIntegerData.hasTypeLimit()) {
                if (currentIntegerNode.getValue() >= referencedIntegerData.getTypeLimitFrom() && currentIntegerNode.getValue() <= referencedIntegerData.getTypeLimitTo()) {
                    IntegerDataType newIntegerData = new IntegerDataType(currentIntegerNode.getValue(), assignmentVariable.modifyType(), referencedIntegerData.getTypeLimitFrom(), referencedIntegerData.getTypeLimitTo());
                    inputLocalVariableMap.put(assignmentTarget, newIntegerData); //Add new Integer data to the variable map, with the assignment target as its variable name
                }
                else {
                    System.out.println("ERROR: Assignment value outside of type limit range.");
                    System.exit(13);
                }
            }
            else {
                IntegerDataType newIntegerData = new IntegerDataType(currentIntegerNode.getValue(), assignmentVariable.modifyType());
                inputLocalVariableMap.put(assignmentTarget, newIntegerData); //Add new Integer data to the variable map, with the assignment target as its variable name
            }
            
        }
        else if (assignmentValue instanceof MathOpNode) {
            Node newNode = expression(inputLocalVariableMap, assignmentValue); //Run through expression method in order to simplify math op expression and receive a basic data type
            if (newNode instanceof IntegerNode) {
                IntegerNode currentIntegerNode = (IntegerNode) newNode;
                IntegerDataType referencedIntegerData = (IntegerDataType) assignmentVariable;
                if (referencedIntegerData.hasTypeLimit()) {
                    if (currentIntegerNode.getValue() >= referencedIntegerData.getTypeLimitFrom() && currentIntegerNode.getValue() <= referencedIntegerData.getTypeLimitTo()) {
                        IntegerDataType newIntegerData = new IntegerDataType(currentIntegerNode.getValue(), assignmentVariable.modifyType(), referencedIntegerData.getTypeLimitFrom(), referencedIntegerData.getTypeLimitTo());
                        inputLocalVariableMap.put(assignmentTarget, newIntegerData); //Add new Integer data to the variable map, with the assignment target as its variable name
                    }
                    else {
                        System.out.println("ERROR: Assignment value outside of type limit range.");
                        System.exit(13);
                    }
                }
                else {
                    IntegerDataType newIntegerData = new IntegerDataType(currentIntegerNode.getValue(), assignmentVariable.modifyType());
                    inputLocalVariableMap.put(assignmentTarget, newIntegerData); //Add new Integer data to the variable map, with the assignment target as its variable name
                }
            }
            else if (newNode instanceof RealNode) {
                RealNode currentRealNode = (RealNode) newNode;
                RealDataType referencedRealData = (RealDataType) assignmentVariable;
                if (referencedRealData.hasTypeLimit()) {
                    if (currentRealNode.getVal() >= referencedRealData.getTypeLimitFrom() && currentRealNode.getVal() <= referencedRealData.getTypeLimitTo()) {
                        RealDataType newRealData = new RealDataType(currentRealNode.getVal(), assignmentVariable.modifyType(), referencedRealData.getTypeLimitFrom(), referencedRealData.getTypeLimitTo());
                        inputLocalVariableMap.put(assignmentTarget, newRealData); //Add new Real data to the variable map, with the assignment target as its variable name
                    }
                    else {
                        System.out.println("ERROR: Assignment value outside of type limit range.");
                        System.exit(13);
                    }
                }
                else {
                    RealDataType newRealData = new RealDataType(currentRealNode.getVal(), assignmentVariable.modifyType());
                    inputLocalVariableMap.put(assignmentTarget, newRealData); //Add new Real data to the variable map, with the assignment target as its variable name
                }
            }
            else if (newNode instanceof StringNode) {
                StringNode currentStringNode = (StringNode) newNode;
                StringDataType referencedStringData = (StringDataType) assignmentVariable;
                if (referencedStringData.hasTypeLimit()) {
                    if (currentStringNode.getString().length() >= referencedStringData.getTypeLimitFrom() && currentStringNode.getString().length() <= referencedStringData.getTypeLimitTo()) {
                        StringDataType newStringData = new StringDataType(currentStringNode.getString(), assignmentVariable.modifyType(), referencedStringData.getTypeLimitFrom(), referencedStringData.getTypeLimitTo());
                        inputLocalVariableMap.put(assignmentTarget, newStringData); //Add new String data to the variable map, with the assignment target as its variable name
                    }
                    else {
                        System.out.println("ERROR: Assignment value outside of type limit range.");
                        System.exit(13);
                    }
                }
                else {
                    StringDataType newStringData = new StringDataType(currentStringNode.getString(), assignmentVariable.modifyType());
                    inputLocalVariableMap.put(assignmentTarget, newStringData); //Add new String data to the variable map, with the assignment target as its variable name
                }
            }
            else {
                System.out.println("ERROR: Incorrect data type returned from math op calculation.");
                System.exit(14);
            }
        }
        else if (assignmentValue instanceof RealNode) {
            RealNode currentRealNode = (RealNode) assignmentValue;
                RealDataType referencedRealData = (RealDataType) assignmentVariable;
                if (referencedRealData.hasTypeLimit()) {
                    if (currentRealNode.getVal() >= referencedRealData.getTypeLimitFrom() && currentRealNode.getVal() <= referencedRealData.getTypeLimitTo()) {
                        RealDataType newRealData = new RealDataType(currentRealNode.getVal(), assignmentVariable.modifyType(), referencedRealData.getTypeLimitFrom(), referencedRealData.getTypeLimitTo());
                        inputLocalVariableMap.put(assignmentTarget, newRealData); //Add new Real data to the variable map, with the assignment target as its variable name
                    }
                    else {
                        System.out.println("ERROR: Assignment value outside of type limit range.");
                        System.exit(13);
                    }
                }
                else {
                    RealDataType newRealData = new RealDataType(currentRealNode.getVal(), assignmentVariable.modifyType());
                    inputLocalVariableMap.put(assignmentTarget, newRealData); //Add new Real data to the variable map, with the assignment target as its variable name
                }
        }
        else if (assignmentValue instanceof CharacterNode) {
            CharacterNode currentCharacterNode = (CharacterNode) assignmentValue;
            CharacterDataType newCharacterData = new CharacterDataType(currentCharacterNode.getValue(), assignmentVariable.modifyType());
            inputLocalVariableMap.put(assignmentTarget, newCharacterData);
        }
        else if (assignmentValue instanceof StringNode) {
            StringNode currentStringNode = (StringNode) assignmentValue;
                StringDataType referencedStringData = (StringDataType) assignmentVariable;
                if (referencedStringData.hasTypeLimit()) {
                    if (currentStringNode.getString().length() >= referencedStringData.getTypeLimitFrom() && currentStringNode.getString().length() <= referencedStringData.getTypeLimitTo()) {
                        StringDataType newStringData = new StringDataType(currentStringNode.getString(), assignmentVariable.modifyType(), referencedStringData.getTypeLimitFrom(), referencedStringData.getTypeLimitTo());
                        inputLocalVariableMap.put(assignmentTarget, newStringData); //Add new String data to the variable map, with the assignment target as its variable name
                    }
                    else {
                        System.out.println("ERROR: Assignment value outside of type limit range.");
                        System.exit(13);
                    }
                }
                else {
                    StringDataType newStringData = new StringDataType(currentStringNode.getString(), assignmentVariable.modifyType());
                    inputLocalVariableMap.put(assignmentTarget, newStringData); //Add new String data to the variable map, with the assignment target as its variable name
                }
        }
        else if (assignmentValue instanceof BoolNode) {
            BoolNode currentBooleanNode = (BoolNode) assignmentValue;
            BooleanDataType newBooleanData = new BooleanDataType(currentBooleanNode.getBool(), assignmentVariable.modifyType());
            inputLocalVariableMap.put(assignmentTarget, newBooleanData);
        }
        else if (assignmentValue instanceof BoolCompareNode) {
            BoolCompareNode currentBooleanCompareNode = (BoolCompareNode) assignmentValue;
            BooleanDataType newBooleanCompareData = booleanCompareNodeFunction(inputLocalVariableMap, currentBooleanCompareNode);
            inputLocalVariableMap.put(assignmentTarget, newBooleanCompareData);
        }
    }
    
    /*
     * Evaluate boolean comparison operations in an interpreter program. It takes as input a LinkedHashMap object containing the current values of local variables and a BoolCompareNode object that 
     * represents the boolean comparison operation to be evaluated.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputBoolCompareNode : BoolCompareNode
     * 
     */
    private BooleanDataType booleanCompareNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, BoolCompareNode inputBoolCompareNode) throws SyntaxErrorException {
        Node leftChild = expression(inputLocalVariableMap, inputBoolCompareNode.getLeft()); //Run both children through expression to simplify any nodes down to a basic data type
        Node rightChild = expression(inputLocalVariableMap, inputBoolCompareNode.getRight());
        boolean value = false;
        if (leftChild instanceof IntegerNode) {
            IntegerNode leftIntChild = (IntegerNode) leftChild; //Children of booleanCompareNode must simplify to either an integer or a real node
            if (rightChild instanceof IntegerNode) {
                IntegerNode rightIntChild = (IntegerNode) rightChild; //Can only compare integers to integers, and reals to reals
                if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.EQUAL) {
                    value = (leftIntChild.getValue() == rightIntChild.getValue()) ? true : false; //Compare both sides of booleanCompareNode using the given comparison operator
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.GREATERTHAN) {
                    value = (leftIntChild.getValue() > rightIntChild.getValue()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.GREATERTHANOREQUALTO) {
                    value = (leftIntChild.getValue() >= rightIntChild.getValue()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.LESSTHAN) {
                    value = (leftIntChild.getValue() < rightIntChild.getValue()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.LESSTHANOREQUALTO) {
                    value = (leftIntChild.getValue() <= rightIntChild.getValue()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.NOTEQUAL) {
                    value = (leftIntChild.getValue() != rightIntChild.getValue()) ? true : false;
                }
                else {
                    System.out.println("ERROR: Incorrect comparison operator given for boolean compare.");
                    System.exit(15);
                }
            }
            else {
                System.out.println("ERROR: Incorrect data type on right side of boolean compare."); 
                System.exit(16);
            }
        }
        else if (leftChild instanceof RealNode) {
            RealNode leftRealChild = (RealNode) leftChild;
            if (rightChild instanceof RealNode) {
                RealNode rightRealChild = (RealNode) rightChild;
                if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.EQUAL) {
                    value = (leftRealChild.getVal() == rightRealChild.getVal()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.GREATERTHAN) {
                    value = (leftRealChild.getVal() > rightRealChild.getVal()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.GREATERTHANOREQUALTO) {
                    value = (leftRealChild.getVal() >= rightRealChild.getVal()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.LESSTHAN) {
                    value = (leftRealChild.getVal() < rightRealChild.getVal()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.LESSTHANOREQUALTO) {
                    value = (leftRealChild.getVal() <= rightRealChild.getVal()) ? true : false;
                }
                else if (inputBoolCompareNode.getType() == BoolCompareNode.comparisonType.NOTEQUAL) {
                    value = (leftRealChild.getVal() != rightRealChild.getVal()) ? true : false;
                }
                else {
                    System.out.println("ERROR: Incorrect comparison operator given for boolean compare.");
                    System.exit(17);
                }
            }
            else {
                System.out.println("ERROR: Incorrect data type on right side of boolean compare.");
                System.exit(18);
            }
        }
        else {
            System.out.println("ERROR: Incorrect data type on left side of boolean compare.");
            System.exit(19);
        }
        BooleanDataType newBooleanData = new BooleanDataType(value, false);
        return newBooleanData;
    }
    
    /*
     * Returns a Node that represents the evaluated expression
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputNode : Node
     */
    private Node expression(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, Node inputNode) throws SyntaxErrorException {
        if (inputNode instanceof MathOpNode) {
            MathOpNode currentMathOpNode = (MathOpNode) inputNode;
            Node leftChild = expression(inputLocalVariableMap, currentMathOpNode.getLeft()); //Recursively calls expression in order to simplify nested MathOpNodes
            Node rightChild = expression(inputLocalVariableMap, currentMathOpNode.getRight());
            if (leftChild instanceof IntegerNode && rightChild instanceof IntegerNode) { //Both sides of MathOpNode must be of same data type
                IntegerNode leftIntChild = (IntegerNode) leftChild;
                IntegerNode rightIntChild = (IntegerNode) rightChild;
                int value = 0;
                if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.PLUS)
                    value = leftIntChild.getValue() + rightIntChild.getValue();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.DIVIDED)
                    value = leftIntChild.getValue() / rightIntChild.getValue();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.MOD)
                    value = leftIntChild.getValue() % rightIntChild.getValue();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.MULTIPLIED)
                    value = leftIntChild.getValue() * rightIntChild.getValue();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.MINUS)
                    value = leftIntChild.getValue() - rightIntChild.getValue();
                else {
                    System.out.println("ERROR: Incorrect operator given for math operation.");
                    System.exit(32);
                }
                IntegerNode newIntNode = new IntegerNode(value);
                return newIntNode;
            }
            else if (leftChild instanceof RealNode && rightChild instanceof RealNode) {
                RealNode leftRealChild = (RealNode) leftChild;
                RealNode rightRealChild = (RealNode) rightChild;
                float value = 0;
                if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.PLUS)
                    value = leftRealChild.getVal() + rightRealChild.getVal();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.DIVIDED)
                    value = leftRealChild.getVal() / rightRealChild.getVal();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.MOD)
                    value = leftRealChild.getVal() % rightRealChild.getVal();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.MULTIPLIED)
                    value = leftRealChild.getVal() * rightRealChild.getVal();
                else if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.MINUS)
                    value = leftRealChild.getVal() - rightRealChild.getVal();
                else {
                    System.out.println("ERROR: Incorrect operator given for math operation.");
                    System.exit(33);
                }
                RealNode newRealNode = new RealNode(value);
                return newRealNode;
            }
            else if (leftChild instanceof StringNode && rightChild instanceof StringNode) {
                StringNode leftStringChild = (StringNode) leftChild;
                StringNode rightStringChild = (StringNode) rightChild;
                String value = "";
                if (currentMathOpNode.getOp() == MathOpNode.possibleOperations.PLUS)
                    value = leftStringChild.getString() + rightStringChild.getString(); //Can only concatenate strings
                else {
                    System.out.println("ERROR: Incorrect operator given for math operation.");
                    System.exit(34);
                }
                StringNode newStringNode = new StringNode(value);
                return newStringNode;
            }
            else {
                System.out.println("ERROR: Can only perform operations on similar data types.");
                System.exit(35);
            }
        }
        else if (inputNode instanceof IntegerNode || inputNode instanceof RealNode || inputNode instanceof StringNode) {
            return inputNode;
        }
        else if (inputNode instanceof VariableReferenceNode) {
            VariableReferenceNode currentVariableReferenceNode = (VariableReferenceNode) inputNode;
            InterpreterDataType currentData = inputLocalVariableMap.get(currentVariableReferenceNode.getName());
            if (currentData == null) {
                System.out.println("ERROR: Referenced variable not declared.");
                System.exit(36);
            }
            else if (currentData instanceof IntegerDataType) { //Only Integer, Real, and String data types can be found in expressions
                IntegerDataType currentIntData = (IntegerDataType) currentData; //Extracts data from referenced variable...
                int intData = currentIntData.getData();
                IntegerNode newIntegerNode = new IntegerNode(intData);
                return newIntegerNode; //...and returns a new node containing extracted data
            }
            else if (currentData instanceof RealDataType) {
                RealDataType currentRealData = (RealDataType) currentData;
                float realData = currentRealData.getData();
                RealNode newRealNode = new RealNode(realData);
                return newRealNode;
            }
            else if (currentData instanceof StringDataType) {
                StringDataType currentStringData = (StringDataType) currentData;
                String stringData = currentStringData.getData();
                StringNode newStringNode = new StringNode(stringData);
                return newStringNode;
            }
            else if (currentData instanceof ArrayDataType) { //For array index references
                ArrayDataType referencedArray = (ArrayDataType) currentData;
                Node index = currentVariableReferenceNode.getIndex();
                int arrayIndex = 0;
                if (index == null) {
                    System.out.println("ERROR: Cannot use array in expression.");
                    System.exit(37);
                }
                else {
                    if (index instanceof IntegerNode) { //First convert the index into an integer
                        IntegerNode intIndex = (IntegerNode) index;
                        arrayIndex = intIndex.getValue();
                    }
                    else if (index instanceof MathOpNode) {
                        MathOpNode mathOpIndex = (MathOpNode) index;
                        InterpreterDataType mathOpIndexData = MathOpNodeFunction(inputLocalVariableMap, mathOpIndex);
                        if (mathOpIndexData instanceof IntegerDataType) {
                            IntegerDataType intIndex = (IntegerDataType) mathOpIndexData;
                            arrayIndex = intIndex.getData();
                        }
                        else {
                            System.out.println("ERROR: Incorrect data type given for array index.");
                            System.exit(37);
                        }
                    }
                    else if (index instanceof VariableReferenceNode) {
                        VariableReferenceNode variableIndex = (VariableReferenceNode) index;
                        InterpreterDataType variableIndexData = VariableReferenceNodeFunction(inputLocalVariableMap, variableIndex);
                        if (variableIndexData instanceof IntegerDataType) {
                            IntegerDataType intIndex = (IntegerDataType) variableIndexData;
                            arrayIndex = intIndex.getData();
                        }
                        else {
                            System.out.println("ERROR: Incorrect data type given for array index.");
                            System.exit(37);
                        }
                    }
                    else {
                        System.out.println("ERROR: Incorrect data type given for array index.");
                        System.exit(37);
                    }
                }
                InterpreterDataType dataAtIndex = referencedArray.getDataAtIndex(arrayIndex);
                if (dataAtIndex == null) {
                    System.out.println("ERROR: Referenced variable not declared.");
                    System.exit(36);
                }
                else if (dataAtIndex instanceof IntegerDataType) { //Only Integer, Real, and String data types can be found in expressions
                    IntegerDataType currentIntData = (IntegerDataType) dataAtIndex; //Extracts data from referenced variable...
                    int intData = currentIntData.getData();
                    IntegerNode newIntegerNode = new IntegerNode(intData);
                    return newIntegerNode; //...and returns a new node containing extracted data
                }
                else if (dataAtIndex instanceof RealDataType) {
                    RealDataType currentRealData = (RealDataType) dataAtIndex;
                    float realData = currentRealData.getData();
                    RealNode newRealNode = new RealNode(realData);
                    return newRealNode;
                }
                else if (dataAtIndex instanceof StringDataType) {
                    StringDataType currentStringData = (StringDataType) dataAtIndex;
                    String stringData = currentStringData.getData();
                    StringNode newStringNode = new StringNode(stringData);
                    return newStringNode;
                }
            }
            else {
                System.out.println("ERROR: Variable referenced within expression has incorrect data type.");
                System.exit(37);
            }
        }
        return null;
    }
    
    /*
     * Executes a For loop in an interpreted programming language. It takes as input a LinkedHashMap of local variables and a ForNode object representing the For loop statement.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputForNode : ForNode
     */
    private void forNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, ForNode inputForNode) throws SyntaxErrorException {
        Node fromNode = expression(inputLocalVariableMap, inputForNode.getStartValue()); //Simplifies possibly complex From node expression
        Node toNode = expression(inputLocalVariableMap, inputForNode.getEndValue()); //Does the same for To node
        if (!(fromNode instanceof IntegerNode && toNode instanceof IntegerNode)) { //For node From and To values must be expressed as integers
            System.out.println("ERROR: Incorrect values given from FOR loop.");
            System.exit(38);
        }
        if (!(inputLocalVariableMap.containsKey(inputForNode.getVariable().getName()))) { //Integer variable used for For loop must be previously declared
            System.out.println("ERROR: Integer variable for FOR loop not declared.");
            System.exit(39);
        }
        IntegerNode fromIntNode = (IntegerNode) fromNode;
        IntegerNode toIntNode = (IntegerNode) toNode;
        int fromValue = fromIntNode.getValue();
        int toValue = toIntNode.getValue();
        if (fromValue > toValue) {
            System.out.println("ERROR: Starting index given for FOR loop is greater than ending index.");
            System.exit(40);
        }
        IntegerDataType fromData = new IntegerDataType(fromValue, true);
        String integerVariableName = inputForNode.getVariable().getName();
        inputLocalVariableMap.put(integerVariableName, fromData); //Stores integer variable back into variable map initialized with From value
        InterpreterDataType integerVariableData = inputLocalVariableMap.get(integerVariableName);
        IntegerDataType integerVariableIntegerData = (IntegerDataType) integerVariableData;
        while (integerVariableIntegerData.getData() <= toValue) { //For loop continues, until integer value is equal to To value
            interpretBlock(inputLocalVariableMap, inputForNode.getStatements());
            integerVariableIntegerData.setData(integerVariableIntegerData.getData()+1); //Integer variables increments by one on each iteration of loop
        }
    }
    
    /*
     * Collect the arguments of the function call using the collectFunctionCallArguments() function and storing them in an ArrayList. 
     * Then it sets the argument array for the function being called using the setArgumentArray() method.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputFunctionCallNode : FunctionCallNode
     */
    private void functionCallNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, FunctionCallNode inputFunctionCallNode) throws SyntaxErrorException {
        ArrayList<InterpreterDataType> functionCallArgumentArray = new ArrayList<InterpreterDataType>();
        functionCallArgumentArray = collectFunctionCallArguments(inputLocalVariableMap, inputFunctionCallNode);
        functionMap.get(inputFunctionCallNode.getName()).setArgumentArray(functionCallArgumentArray);
        if (functionMap.get(inputFunctionCallNode.getName()).isBuiltIn()) {
            executeBuiltInFunction(inputFunctionCallNode, functionCallArgumentArray);
        }
        else {
            interpretFunction(inputLocalVariableMap, functionMap.get(inputFunctionCallNode.getName()));
        }
    }
    
    /*
     * Represents the implementation of an if statement in the interpreter program. It takes in a LinkedHashMap of local variables and an IfNode representing the if statement, and returns void. 
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputIfNode : IfNode
     */
    private void ifNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, IfNode inputIfNode) throws SyntaxErrorException {
        BoolCompareNode ifNodeConditionNode = inputIfNode.getCondition();
        BooleanDataType ifNodeCondition = booleanCompareNodeFunction(inputLocalVariableMap, ifNodeConditionNode);
        while (ifNodeCondition.getData() == false) { //Continues through linked list of if, elsif nodes until a condition is true
            inputIfNode = inputIfNode.getNext();
            if (inputIfNode == null || inputIfNode.getCondition() == null) //If inputIfNode == null, then no conditions were met. If condition == null, then we are at Else node
                break;
            ifNodeConditionNode = inputIfNode.getCondition();
            ifNodeCondition = booleanCompareNodeFunction(inputLocalVariableMap, ifNodeConditionNode);
        }
        if (inputIfNode != null) //Dont interpret any statements if no If conditions were met
            interpretBlock(inputLocalVariableMap, inputIfNode.getStatements());
    }
    
    /*
     * Takes a LinkedHashMap of local variables and a List of StatementNodes as input. It iterates through the list and interprets each statement by calling the corresponding functions. 
     * If the statement is an AssignmentNode, it calls the AssignmentNodeFunction, if it is a ForNode, it calls the ForNodeFunction, and so on. 
     * The function ensures that each statement is properly interpreted by checking the type of the StatementNode and calling the corresponding function.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param list : List,StatementNode>
     */
    private void interpretBlock(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, List<StatementNode> list) throws SyntaxErrorException {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof AssignmentNode) {
                    AssignmentNode newAssignmentNode = (AssignmentNode) list.get(i);
                    AssignmentNodeFunction(inputLocalVariableMap, newAssignmentNode);
                }
                else if (list.get(i) instanceof ForNode) {
                    ForNode newForNode = (ForNode) list.get(i);
                    forNodeFunction(inputLocalVariableMap, newForNode);
                }
                else if (list.get(i) instanceof FunctionCallNode) {
                    FunctionCallNode newFunctionCallNode = (FunctionCallNode) list.get(i);
                    functionCallNodeFunction(inputLocalVariableMap, newFunctionCallNode);
                }
                else if (list.get(i) instanceof IfNode) {
                    IfNode newIfNode = (IfNode) list.get(i);
                    ifNodeFunction(inputLocalVariableMap, newIfNode);
                }
                else if (list.get(i) instanceof RepeatNode) {
                    RepeatNode newRepeatNode = (RepeatNode) list.get(i);
                    RepeatNodeFunction(inputLocalVariableMap, newRepeatNode);
                }
                else if (list.get(i) instanceof WhileNode) {
                    WhileNode newWhileNode = (WhileNode) list.get(i);
                    WhileNodeFunction(inputLocalVariableMap, newWhileNode);
                }
            }
        }
    }
    
    /*
     * takes in a LinkedHashMap<String, InterpreterDataType> and a FunctionNode object and throws a SyntaxErrorException. 
     * The purpose of this function is to interpret the statements within a function, which includes adding the function's parameters and variables/constants to the local variable map, interpreting the block of statements within the function, 
     * and updating the function's argument variables with their corresponding values.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputFunctionNode : FunctionNode
     * 
     */
    private void interpretFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, FunctionNode inputFunctionNode) throws SyntaxErrorException {
        ArrayList<VariableNode> parameterArray = inputFunctionNode.getParameterArray();
        ArrayList<VariableNode> variableArray = inputFunctionNode.getVariableArray();
        ArrayList<StatementNode> statementArray = inputFunctionNode.getStatementArray();
        addParametersToVariableMap(inputLocalVariableMap, parameterArray);
        addVariablesAndConstantsToVariableMap(inputLocalVariableMap, variableArray);
        interpretBlock(inputLocalVariableMap, statementArray);
        if (parameterArray != null) {
            ArrayList<InterpreterDataType> newArgumentArray = new ArrayList<InterpreterDataType>();
            for (int i = 0; i < parameterArray.size(); i++) {
                InterpreterDataType currentVariableData = inputLocalVariableMap.get(parameterArray.get(i).getName());
                newArgumentArray.add(currentVariableData);
            }
            inputFunctionNode.updateArgumentVariables(newArgumentArray);
        }
    }
    
    /*
     * The function first retrieves the left and right child nodes of the operation node using the expression function. It then checks the types of the two child nodes and performs the appropriate mathematical operation based on the type of the data. 
     * If the child nodes are both strings, the function concatenates them with a + operator. If the child nodes are both reals or both integers, the function performs the appropriate mathematical operation (+, -, *, /, %).
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputMathOpNode : MathOpNode
     */
    private InterpreterDataType MathOpNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, MathOpNode inputMathOpNode) throws SyntaxErrorException {
        Node leftChildNode = expression(inputLocalVariableMap, inputMathOpNode.getLeft());
        Node rightChildNode = expression(inputLocalVariableMap, inputMathOpNode.getRight());
        if (leftChildNode instanceof StringNode && rightChildNode instanceof StringNode) { //Can only perform operations on data of the same type
            if (inputMathOpNode.getOp() != MathOpNode.possibleOperations.PLUS) //Can only concatenates strings
                throw new SyntaxErrorException(null);
            StringNode leftStringNode = (StringNode) leftChildNode;
            StringNode rightStringNode = (StringNode) rightChildNode;
            String resultString = leftStringNode.getString() + rightStringNode.getString();
            StringDataType newStringData = new StringDataType(resultString, false);
            return newStringData;
        }
        else if (leftChildNode instanceof RealNode && rightChildNode instanceof RealNode) {
            RealNode leftRealNode = (RealNode) leftChildNode;
            float leftValue = leftRealNode.getVal();
            RealNode rightRealNode = (RealNode) rightChildNode;
            float rightValue = rightRealNode.getVal();
            float resultValue = 0;
            switch (inputMathOpNode.getOp()) {
                case PLUS :
                    resultValue = leftValue + rightValue;
                    break;
                case MINUS :
                    resultValue = leftValue - rightValue;
                    break;
                case MULTIPLIED :
                    resultValue = leftValue * rightValue;
                    break;
                case DIVIDED :
                    resultValue = leftValue / rightValue;
                    break;
                case MOD :
                    resultValue = leftValue % rightValue;
                    break;
                default : 
                    System.out.println("ERROR: Unrecognized operator given for expression.");
                    System.exit(41);
            }
            RealDataType newRealData = new RealDataType(resultValue, false);
            return newRealData;
        }
        else if (leftChildNode instanceof IntegerNode && rightChildNode instanceof IntegerNode) {
            IntegerNode leftIntegerNode = (IntegerNode) leftChildNode;
            int leftValue = leftIntegerNode.getValue();
            IntegerNode rightIntegerNode = (IntegerNode) rightChildNode;
            int rightValue = rightIntegerNode.getValue();
            int resultValue = 0;
            switch (inputMathOpNode.getOp()) {
                case PLUS :
                    resultValue = leftValue + rightValue;
                    break;
                case MINUS :
                    resultValue = leftValue - rightValue;
                    break;
                case MULTIPLIED :
                    resultValue = leftValue * rightValue;
                    break;
                case DIVIDED :
                    resultValue = leftValue / rightValue;
                    break;
                case MOD :
                    resultValue = leftValue % rightValue;
                    break;
                default : 
                    System.out.println("ERROR: Unrecognized operator given for expression.");
                    System.exit(42);
            }
            IntegerDataType newIntegerData = new IntegerDataType(resultValue, false);
            return newIntegerData;
        }
        else
            return null;
    }
    
    /*
     * Implements a repeat-until loop structure in the interpreter. 
     * The function takes in a LinkedHashMap of InterpreterDataType objects that represent the current state of the program's local variables and a RepeatNode object that contains the condition to check and the statements to execute in the loop.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputRepeatNode : RepeatNode
     */
    private void RepeatNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, RepeatNode inputRepeatNode) throws SyntaxErrorException {
        BooleanDataType booleanCompare = booleanCompareNodeFunction(inputLocalVariableMap, inputRepeatNode.getCondition());
        do { //Repeat Until loop runs once before first boolean check
            interpretBlock(inputLocalVariableMap, inputRepeatNode.getStatements());
            booleanCompare = booleanCompareNodeFunction(inputLocalVariableMap, inputRepeatNode.getCondition());
        } while (booleanCompare.getData() == false);
    }
    
    /*
     * Implements the functionality of a variable reference node in an interpreter. The function takes in a reference to a map of local variables and a reference to a VariableReferenceNode object. 
     * The VariableReferenceNode object contains the name of the variable being referenced and, if applicable, an index value to reference a specific element in an array.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputVariableReferenceNode : VariableReferenceNode
     */
    private InterpreterDataType VariableReferenceNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, VariableReferenceNode inputVariableReferenceNode) throws SyntaxErrorException {
        if (inputVariableReferenceNode.getIndex() != null) {
            Node arrayIndex = inputVariableReferenceNode.getIndex();
            String variableName = inputVariableReferenceNode.getName();
            InterpreterDataType arrayData = inputLocalVariableMap.get(variableName);
            if (arrayData instanceof ArrayDataType) {
                ArrayDataType array = (ArrayDataType) arrayData;
                if (arrayIndex instanceof IntegerNode) {
                    IntegerNode integerNode = (IntegerNode) arrayIndex;
                    InterpreterDataType valueAtIndex = array.getDataAtIndex(integerNode.getValue());
                    return valueAtIndex;
                }
                else if (arrayIndex instanceof MathOpNode) {
                    MathOpNode mathOpNode = (MathOpNode) arrayIndex;
                    InterpreterDataType newData = MathOpNodeFunction(inputLocalVariableMap, mathOpNode);
                    if (newData instanceof IntegerDataType) {
                        IntegerDataType newIntData = (IntegerDataType) newData;
                        InterpreterDataType valueAtIndex = array.getDataAtIndex(newIntData.getData());
                        return valueAtIndex;
                    }
                    else {
                        System.out.println("ERROR: Reference node index data type unrecognized.");
                        System.exit(43);
                    }
                }
                else if (arrayIndex instanceof VariableReferenceNode) {
                    VariableReferenceNode variableReferenceNode = (VariableReferenceNode) arrayIndex;
                    InterpreterDataType newData = VariableReferenceNodeFunction(inputLocalVariableMap, variableReferenceNode);
                    if (newData instanceof IntegerDataType) {
                        IntegerDataType newIntData = (IntegerDataType) newData;
                        InterpreterDataType valueAtIndex = array.getDataAtIndex(newIntData.getData());
                        return valueAtIndex;
                    }
                    else {
                        System.out.println("ERROR: Reference node index data type unrecognized.");
                        System.exit(44);
                    }
                }
                else {
                    System.out.println("ERROR: Reference node index data type unrecognized.");
                    System.exit(45);
                }
            }
            else {
                System.out.println("ERROR: Index assigned to non-array data type.");
                System.exit(46);
            }
            
            return null;
        }
        else {
            InterpreterDataType variableMapKeyValue = inputLocalVariableMap.get(inputVariableReferenceNode.getName());
            if (variableMapKeyValue == null) {
                System.out.println("ERROR: Referenced variable not declared.");
                System.exit(47);
            }
            return variableMapKeyValue;
        }
    }
    
    /*
     * Implements the execution of a while loop in an interpreter. The function takes in two arguments: inputLocalVariableMap, which is a LinkedHashMap holding the local variables defined within the current scope, 
     * and inputWhileNode, which is a WhileNode object that holds the condition and statements to be executed within the loop.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputWhileNode : WhileNode
     */
    private void WhileNodeFunction(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, WhileNode inputWhileNode) throws SyntaxErrorException {
        BooleanDataType booleanCompare = booleanCompareNodeFunction(inputLocalVariableMap, inputWhileNode.getCondition());
        while (booleanCompare.getData() == true) {
            interpretBlock(inputLocalVariableMap, inputWhileNode.getStatements());
            booleanCompare = booleanCompareNodeFunction(inputLocalVariableMap, inputWhileNode.getCondition());
        }
    }
    
    /*
     * 
     * 
     * Helper methods below 
     * 
     * 
     */
    
    /*
     * Adds a constant value to a local variable map. The function takes in a LinkedHashMap representing the current local variable map and a VariableNode representing the variable being added. 
     * The function first extracts the name of the variable from the VariableNode. It then uses a switch statement to determine the type of the variable and creates a new InterpreterDataType object of the corresponding type with 
     * the value of the constant from the VariableNode. This new InterpreterDataType object is then added to the local variable map with the extracted variable name as the key. If the VariableNode represents an array, 
     * the function also creates a new ArrayDataType object and adds it to the local variable map. The ArrayDataType object is created with the type, start index, end index, and modify type specified in the VariableNode.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputVariableNode : VariableNode
     */
    private void addConstantToVariableMap(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, VariableNode inputVariableNode) {
        String name = inputVariableNode.getName();
        switch (inputVariableNode.getType()) {
            case INTEGER:
                IntegerNode newIntNode = (IntegerNode) inputVariableNode.getValue();
                IntegerDataType newIntegerData = new IntegerDataType(newIntNode.getValue(), false); // Constants are automatically not changeable
                inputLocalVariableMap.put(name, newIntegerData);
                break;
            case REAL:
                RealNode newRealNode = (RealNode) inputVariableNode.getValue();
                RealDataType newRealData = new RealDataType(newRealNode.getVal(), false);
                inputLocalVariableMap.put(name, newRealData);
                break;
            case CHARACTER:
                CharacterNode newCharNode = (CharacterNode) inputVariableNode.getValue();
                CharacterDataType newCharData = new CharacterDataType(newCharNode.getValue(), false);
                inputLocalVariableMap.put(name, newCharData);
                break;
            case STRING:
                StringNode newStringNode = (StringNode) inputVariableNode.getValue();
                StringDataType newStringData = new StringDataType(newStringNode.getString(), false);
                inputLocalVariableMap.put(name, newStringData);
                break;
            case BOOLEAN:
                BoolNode newBooleanNode = (BoolNode) inputVariableNode.getValue();
                BooleanDataType newBooleanData = new BooleanDataType(newBooleanNode.getBool(), false);
                inputLocalVariableMap.put(name, newBooleanData);
                break;
            case ARRAY:
                int startIndex = inputVariableNode.getIntFrom();
                int endIndex = inputVariableNode.getIntTo();
                ArrayDataType newArrayData = null;
                switch (inputVariableNode.getArrayType()) {
                    case INTEGER:
                        newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.INTEGER, startIndex, endIndex, inputVariableNode.getmodifyType());
                        break;
                    case REAL:
                        newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.REAL, startIndex, endIndex, inputVariableNode.getmodifyType());
                        break;
                    case CHARACTER:
                        newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.CHARACTER, startIndex, endIndex, inputVariableNode.getmodifyType());
                        break;
                    case BOOLEAN:
                        newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.BOOLEAN, startIndex, endIndex, inputVariableNode.getmodifyType());
                        break;
                    case STRING:
                        newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.STRING, startIndex, endIndex, inputVariableNode.getmodifyType());
                        break;
                    default:
                        System.out.println("Error: Array type not detected.");
                        System.exit(0);
                }
                inputLocalVariableMap.put(name, newArrayData);
                break;
            default:
                System.out.println("ERROR: Variable type not detected.");
                System.exit(1);
                break;
        }
    }
    
    /*
     * Takes in a variable node and a local variable map and adds a constant variable to the map. It extracts the name of the variable from the input node and uses a switch statement to determine the type of constant. 
     * For each type, it extracts the value of the constant from the node and creates a corresponding data type. Then it puts this data type into the local variable map under the name of the variable. If the type is an array, 
     * it determines the start and end indices and creates a new ArrayDataType object accordingly. If the type is not recognized, 
     * it prints an error message and exits the program. The modifyType parameter in ArrayDataType specifies whether the array elements are modifiable or not.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputArray : ArrayList<VariableNode>
     */
    private void addParametersToVariableMap(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, ArrayList<VariableNode> inputArray) {
        if (inputArray != null) {
            for (int i = 0; i < inputArray.size(); i++) {
                VariableNode currentVariableNode = inputArray.get(i);
                String name = currentVariableNode.getName();
                switch (currentVariableNode.getType()) {
                    case INTEGER:
                        if (currentVariableNode.getValue() == null) { // If parameter does not already contain a value, a default value will be assigned
                            IntegerDataType newIntegerData = new IntegerDataType(0, currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newIntegerData);
                            break;
                        }
                        else {
                            IntegerNode newIntegerNode = (IntegerNode) currentVariableNode.getValue();
                            IntegerDataType newIntegerData = new IntegerDataType(newIntegerNode.getValue(), currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newIntegerData);
                            break;
                        }
                    case REAL:
                        if (currentVariableNode.getValue() == null) {
                            RealDataType newRealData = new RealDataType(0, currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newRealData);
                            break;
                        }
                        else {
                            RealNode newRealNode = (RealNode) currentVariableNode.getValue();
                            RealDataType newRealData = new RealDataType(newRealNode.getVal(), currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newRealData);
                            break;
                        }
                    case CHARACTER:
                        if (currentVariableNode.getValue() == null) {
                            CharacterDataType newCharacterData = new CharacterDataType(' ', currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newCharacterData);
                            break;
                        }
                        else {
                            CharacterNode newCharacterNode = (CharacterNode) currentVariableNode.getValue();
                            CharacterDataType newCharacterData = new CharacterDataType(newCharacterNode.getValue(), currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newCharacterData);
                            break;
                        }
                    case STRING:
                        if (currentVariableNode.getValue() == null) {
                            StringDataType newStringData = new StringDataType("", currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newStringData);
                            break;
                        }
                        else {
                            StringNode newStringNode = (StringNode) currentVariableNode.getValue();
                            StringDataType newStringData = new StringDataType(newStringNode.getString(), currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newStringData);
                            break;
                        }
                    case BOOLEAN:
                        if (currentVariableNode.getValue() == null) {
                            BooleanDataType newBooleanData = new BooleanDataType(false, currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newBooleanData);
                            break;
                        }
                        else {
                            BoolNode newBooleanNode = (BoolNode) currentVariableNode.getValue();
                            BooleanDataType newBooleanData = new BooleanDataType(newBooleanNode.getBool(), currentVariableNode.getmodifyType());
                            inputLocalVariableMap.put(name, newBooleanData);
                            break;
                        }
                    case ARRAY:
                        int startIndex = currentVariableNode.getIntFrom();
                        int endIndex = currentVariableNode.getIntTo();
                        ArrayDataType newArrayData = null;
                        if (currentVariableNode.getArrayType() != null) { // If parameter array is not empty, will copy contents over to variable map
                            switch (currentVariableNode.getArrayType()) {
                                case INTEGER:
                                    newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.INTEGER, startIndex, endIndex, currentVariableNode.getmodifyType());
                                    if (currentVariableNode.getArrayValueAtIndex(startIndex, VariableNode.variableType.INTEGER) != null) {
                                        ArrayList<InterpreterDataType> arrayDataArrayList = newArrayData.getArray();
                                        for (int j = 0; j < (endIndex - startIndex); j++) {
                                            IntegerNode nodeAtIndex = (IntegerNode) currentVariableNode.getArrayValueAtIndex(j, VariableNode.variableType.INTEGER);
                                            IntegerDataType integerDataAtIndex = new IntegerDataType(nodeAtIndex.getValue(), currentVariableNode.getmodifyType());
                                            arrayDataArrayList.set(j, integerDataAtIndex);
                                        }
                                        newArrayData.setArray(arrayDataArrayList);
                                    }
                                    break;
                                case REAL:
                                    newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.REAL, startIndex, endIndex, currentVariableNode.getmodifyType());
                                    if (currentVariableNode.getArrayValueAtIndex(startIndex, VariableNode.variableType.REAL) != null) {
                                        ArrayList<InterpreterDataType> arrayDataArrayList = newArrayData.getArray();
                                        for (int j = 0; j < (endIndex - startIndex); j++) {
                                            RealNode nodeAtIndex = (RealNode) currentVariableNode.getArrayValueAtIndex(j, VariableNode.variableType.REAL);
                                            RealDataType realDataAtIndex = new RealDataType(nodeAtIndex.getVal(), currentVariableNode.getmodifyType());
                                            arrayDataArrayList.set(j, realDataAtIndex);
                                        }
                                        newArrayData.setArray(arrayDataArrayList);
                                    }
                                    break;
                                case CHARACTER:
                                    newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.CHARACTER, startIndex, endIndex, currentVariableNode.getmodifyType());
                                    if (currentVariableNode.getArrayValueAtIndex(startIndex, VariableNode.variableType.CHARACTER) != null) {
                                        ArrayList<InterpreterDataType> arrayDataArrayList = newArrayData.getArray();
                                        for (int j = 0; j < (endIndex - startIndex); j++) {
                                            CharacterNode nodeAtIndex = (CharacterNode) currentVariableNode.getArrayValueAtIndex(j, VariableNode.variableType.CHARACTER);
                                            CharacterDataType characterDataAtIndex = new CharacterDataType(nodeAtIndex.getValue(), currentVariableNode.getmodifyType());
                                            arrayDataArrayList.set(j, characterDataAtIndex);
                                        }
                                        newArrayData.setArray(arrayDataArrayList);
                                    }
                                    break;
                                case BOOLEAN:
                                    newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.BOOLEAN, startIndex, endIndex, currentVariableNode.getmodifyType());
                                    if (currentVariableNode.getArrayValueAtIndex(startIndex, VariableNode.variableType.BOOLEAN) != null) {
                                        ArrayList<InterpreterDataType> arrayDataArrayList = newArrayData.getArray();
                                        for (int j = 0; j < (endIndex - startIndex); j++) {
                                            BoolNode nodeAtIndex = (BoolNode) currentVariableNode.getArrayValueAtIndex(j, VariableNode.variableType.BOOLEAN);
                                            BooleanDataType booleanDataAtIndex = new BooleanDataType(nodeAtIndex.getBool(), currentVariableNode.getmodifyType());
                                            arrayDataArrayList.set(j, booleanDataAtIndex);
                                        }
                                        newArrayData.setArray(arrayDataArrayList);
                                    }
                                    break;
                                case STRING:
                                    newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.STRING, startIndex, endIndex, currentVariableNode.getmodifyType());
                                    if (currentVariableNode.getArrayValueAtIndex(startIndex, VariableNode.variableType.STRING) != null) {
                                        ArrayList<InterpreterDataType> arrayDataArrayList = newArrayData.getArray();
                                        for (int j = 0; j < (endIndex - startIndex); j++) {
                                            StringNode nodeAtIndex = (StringNode) currentVariableNode.getArrayValueAtIndex(j, VariableNode.variableType.STRING);
                                            StringDataType stringDataAtIndex = new StringDataType(nodeAtIndex.getString(), currentVariableNode.getmodifyType());
                                            arrayDataArrayList.set(j, stringDataAtIndex);
                                        }
                                        newArrayData.setArray(arrayDataArrayList);
                                    }
                                    break;
                                default:
                                    System.out.println("Error: Array type not detected.");
                                    System.exit(3);
                            }
                        }
                        inputLocalVariableMap.put(name, newArrayData);
                        break;
                    default:
                        System.out.println("ERROR: Variable type not detected.");
                        System.exit(4);
                        break;
                }
            }
        }
    }
    
    /*
     * Takes an ArrayList called inputArray as input. If the inputArray is not null, the function loops through each VariableNode in the ArrayList. 
     * If the modifyType attribute of the VariableNode is true, the function calls addVariableToVariableMap() to add the variable to inputLocalVariableMap. 
     * If the modifyType attribute is false, the function calls addConstantToVariableMap() to add the constant to inputLocalVariableMap.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputArray :  ArrayList<VariableNode>
     */
    private void addVariablesAndConstantsToVariableMap(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, ArrayList<VariableNode> inputArray) {
        if (inputArray != null) {
            for (int i = 0; i < inputArray.size(); i++) {
                VariableNode currentVariableNode = inputArray.get(i);
                if (currentVariableNode.getmodifyType() == true) { //Variable
                    addVariableToVariableMap(inputLocalVariableMap, currentVariableNode);
                }
                else { //Constant
                    addConstantToVariableMap(inputLocalVariableMap, currentVariableNode);
                }
            }
        }
    }
    
    /*
     * Adds a variable to a local variable map, which is used to store and access variables in the program's memory during interpretation. 
     * The function takes two parameters: the inputLocalVariableMap, which is the local variable map to add the variable to, and the inputVariableNode, which is the variable node containing the variable's name, type, and other properties.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputVariableNode : VariableNode
     */
    private void addVariableToVariableMap(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, VariableNode inputVariableNode) {
        String name = inputVariableNode.getName();
        if (inputVariableNode.getTypeLimit()) {
            switch (inputVariableNode.getType()) {
                case INTEGER:
                    IntegerDataType newIntegerData = new IntegerDataType(0, true, inputVariableNode.getIntFrom(), inputVariableNode.getIntTo()); // Variables declared before statements are automatically changeable
                    inputLocalVariableMap.put(name, newIntegerData);
                    break;
                case REAL:
                    RealDataType newRealData = new RealDataType(0, true, inputVariableNode.getRealFrom(), inputVariableNode.getRealTo());
                    inputLocalVariableMap.put(name, newRealData);
                    break;
                case STRING:
                    StringDataType newStringData = new StringDataType("", true, inputVariableNode.getIntFrom(), inputVariableNode.getIntTo());
                    inputLocalVariableMap.put(name, newStringData);
                    break;
                default:
                    System.out.println("ERROR: Variable type not allowed for type limit variable.");
                    System.exit(6);
                    break;
            }
        }
        else {
            switch (inputVariableNode.getType()) {
                case INTEGER:
                    IntegerDataType newIntegerData = new IntegerDataType(0, true); // Variables declared before statements are automatically changeable
                    inputLocalVariableMap.put(name, newIntegerData);
                    break;
                case REAL:
                    RealDataType newRealData = new RealDataType(0, true);
                    inputLocalVariableMap.put(name, newRealData);
                    break;
                case CHARACTER:
                    CharacterDataType newCharData = new CharacterDataType(' ', true);
                    inputLocalVariableMap.put(name, newCharData);
                    break;
                case STRING:
                    StringDataType newStringData = new StringDataType("", true);
                    inputLocalVariableMap.put(name, newStringData);
                    break;
                case BOOLEAN:
                    BooleanDataType newBooleanData = new BooleanDataType(false, true);
                    inputLocalVariableMap.put(name, newBooleanData);
                    break;
                case ARRAY:
                    int startIndex = inputVariableNode.getIntFrom();
                    int endIndex = inputVariableNode.getIntTo();
                    ArrayDataType newArrayData = null;
                    switch (inputVariableNode.getArrayType()) {
                        case INTEGER:
                            newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.INTEGER, startIndex, endIndex, inputVariableNode.getmodifyType());
                            break;
                        case REAL:
                            newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.REAL, startIndex, endIndex, inputVariableNode.getmodifyType());
                            break;
                        case CHARACTER:
                            newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.CHARACTER, startIndex, endIndex, inputVariableNode.getmodifyType());
                            break;
                        case BOOLEAN:
                            newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.BOOLEAN, startIndex, endIndex, inputVariableNode.getmodifyType());
                            break;
                        case STRING:
                            newArrayData = new ArrayDataType(ArrayDataType.arrayDataType.STRING, startIndex, endIndex, inputVariableNode.getmodifyType());
                            break;
                        default:
                            System.out.println("Error: Array type not detected.");
                            System.exit(5);
                    }
                    inputLocalVariableMap.put(name, newArrayData);
                    break;
                default:
                    System.out.println("ERROR: Variable type not detected.");
                    System.exit(6);
                    break;
            }
        }
    }
    
    /*
     * The function checks if the referenced function in the FunctionCallNode exists in the functionMap. If it doesn't exist, the function prints an error message and exits the program. If the referenced function is variadic, 
     * the function adds all arguments in the FunctionCallNode to the ArrayList without checking their data type or changeability.If the referenced function is not variadic, the function compares the number of arguments in the FunctionCallNode 
     * to the number of parameters in the referenced function. If they are not equal, the function prints an error message and exits the program.The function then checks each argument in the FunctionCallNode against the corresponding parameter in 
     * the referenced function to ensure that the argument matches the data type and changeability of the parameter. If the argument passes the test, it is added to the ArrayList of InterpreterDataType.
     * If the argument is a variable reference, the function looks up the variable in the LinkedHashMap of local variables, and adds the value of the variable to the ArrayList of InterpreterDataType. If the argument is an expression, 
     * the function evaluates the expression and adds the resulting value to the ArrayList.The function then returns the ArrayList of InterpreterDataType.
     * @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     * @param inputFunctionCallNode : FunctionCallNode
     */
    
    private ArrayList<InterpreterDataType> collectFunctionCallArguments(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, FunctionCallNode inputFunctionCallNode) throws SyntaxErrorException {
        ArrayList<InterpreterDataType> functionCallArgumentArray = new ArrayList<InterpreterDataType>();
        String functionName = inputFunctionCallNode.getName().toLowerCase();
        if (!functionMap.containsKey(functionName)) {
            System.out.println("ERROR: Referenced function does not exist.");
            System.exit(26);
        }
        FunctionNode referencedFunction = functionMap.get(functionName.toLowerCase());
        if (!referencedFunction.isVariadic()) {
            ArrayList<ParameterNode> functionCallParameterArray = inputFunctionCallNode.getParameterArray();
            ArrayList<VariableNode> referencedFunctionParameterArray = referencedFunction.getParameterArray();
            if (functionCallParameterArray.size() != referencedFunctionParameterArray.size()) {
                System.out.println("ERROR: Incorrect number of arguments for function call.");
                System.exit(27);
            }
            /* Following method checks each function call argument and makes sure it matches the variable data type and changeability of corresponding function parameter
               If arguments pass test, they are added to the function call argument array */ 
            checkArgumentsAndAddToArray(inputLocalVariableMap, functionCallArgumentArray, functionCallParameterArray, referencedFunctionParameterArray);
        }
        else { //Variadric functions do not require checking of argument changeability and data type.
            ArrayList<ParameterNode> functionCallParameterArray = inputFunctionCallNode.getParameterArray();
            for (int i = 0; i < functionCallParameterArray.size(); i++) { 
                ParameterNode currentFunctionCallParameterNode = functionCallParameterArray.get(i);
                if (currentFunctionCallParameterNode.getChangeable() == false && currentFunctionCallParameterNode.getExpression() != null) {
                    Node functionCallArgumentExpression = currentFunctionCallParameterNode.getExpression();
                    if (functionCallArgumentExpression instanceof BoolCompareNode) {
                        BoolCompareNode booleanCompareNodeArgument = (BoolCompareNode) functionCallArgumentExpression;
                        BooleanDataType newBooleanArgument = booleanCompareNodeFunction(inputLocalVariableMap, booleanCompareNodeArgument);
                        functionCallArgumentArray.add(newBooleanArgument);
                    }
                    else if (functionCallArgumentExpression instanceof IntegerNode) {
                        IntegerNode integerNodeArgument = (IntegerNode) functionCallArgumentExpression;
                        IntegerDataType newIntegerArgument = new IntegerDataType(integerNodeArgument.getValue(), false);
                        functionCallArgumentArray.add(newIntegerArgument);
                    }
                    else if (functionCallArgumentExpression instanceof RealNode) {
                        RealNode realNodeArgument = (RealNode) functionCallArgumentExpression;
                        RealDataType newRealArgument = new RealDataType(realNodeArgument.getVal(), false);
                        functionCallArgumentArray.add(newRealArgument);
                    }
                    else if (functionCallArgumentExpression instanceof BoolNode) {
                        BoolNode booleanNodeArgument = (BoolNode) functionCallArgumentExpression;
                        BooleanDataType newBooleanArgument = new BooleanDataType(booleanNodeArgument.getBool(), false);
                        functionCallArgumentArray.add(newBooleanArgument);
                    }
                    else if (functionCallArgumentExpression instanceof CharacterNode) {
                        CharacterNode characterNodeArgument = (CharacterNode) functionCallArgumentExpression;
                        CharacterDataType newCharacterArgument = new CharacterDataType(characterNodeArgument.getValue(), false);
                        functionCallArgumentArray.add(newCharacterArgument);
                    }
                    else if (functionCallArgumentExpression instanceof StringNode) {
                        StringNode stringNodeArgument = (StringNode) functionCallArgumentExpression;
                        StringDataType newStringArgument = new StringDataType(stringNodeArgument.getString(), false);
                        functionCallArgumentArray.add(newStringArgument);
                    }
                    else if (functionCallArgumentExpression instanceof VariableReferenceNode) {
                        VariableReferenceNode newVariableReferenceNode = (VariableReferenceNode) functionCallArgumentExpression;
                        InterpreterDataType referencedVariable = VariableReferenceNodeFunction(inputLocalVariableMap, newVariableReferenceNode);
                        if (referencedVariable instanceof IntegerDataType) {
                            IntegerDataType newIntArgument = (IntegerDataType) referencedVariable;
                            functionCallArgumentArray.add(newIntArgument);
                        }
                        else if (referencedVariable instanceof RealDataType) {
                            RealDataType newRealArgument = (RealDataType) referencedVariable;
                            functionCallArgumentArray.add(newRealArgument);
                        }
                        else if (referencedVariable instanceof BooleanDataType) {
                            BooleanDataType newBoolArgument = (BooleanDataType) referencedVariable;
                            functionCallArgumentArray.add(newBoolArgument);
                        }
                        else if (referencedVariable instanceof CharacterDataType) {
                            CharacterDataType newCharArgument = (CharacterDataType) referencedVariable;
                            functionCallArgumentArray.add(newCharArgument);
                        }
                        else if (referencedVariable instanceof StringDataType) {
                            StringDataType newStringArgument = (StringDataType) referencedVariable;
                            functionCallArgumentArray.add(newStringArgument);
                        }
                        else if (referencedVariable instanceof ArrayDataType) {
                            ArrayDataType newArrayArgument = (ArrayDataType) referencedVariable;
                            functionCallArgumentArray.add(newArrayArgument);
                        }
                    }
                    else if (functionCallArgumentExpression instanceof MathOpNode) {
                        MathOpNode mathOpNodeArgument = (MathOpNode) functionCallArgumentExpression;
                        InterpreterDataType mathOpNodeData = MathOpNodeFunction(inputLocalVariableMap, mathOpNodeArgument);
                        if (mathOpNodeData instanceof IntegerDataType) {
                            IntegerDataType newIntegerArgument = (IntegerDataType) mathOpNodeData;
                            functionCallArgumentArray.add(newIntegerArgument);
                        }
                        else if (mathOpNodeData instanceof RealDataType) {
                            RealDataType newRealArgument = (RealDataType) mathOpNodeData;
                            functionCallArgumentArray.add(newRealArgument);
                        }
                        else if (mathOpNodeData instanceof StringDataType) {
                            StringDataType newStringArgument = (StringDataType) mathOpNodeData;
                            functionCallArgumentArray.add(newStringArgument);
                        }
                        else {
                            System.out.println("ERROR: Unrecognized data type retrieved from expression within function call argument");
                            System.exit(28);
                        }
                    }
                }
                else {
                    String functionCallVariableName = currentFunctionCallParameterNode.getName();
                    if (!inputLocalVariableMap.containsKey(functionCallVariableName)) {
                        System.out.println("ERROR: Variable referenced in function call not found.");
                        System.exit(29);
                    }
                    InterpreterDataType variableReferencedByFunctionCall = inputLocalVariableMap.get(functionCallVariableName);
                    if (variableReferencedByFunctionCall instanceof IntegerDataType) {
                        IntegerDataType newIntArgument = (IntegerDataType) variableReferencedByFunctionCall;
                        functionCallArgumentArray.add(newIntArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof RealDataType) {
                        RealDataType newRealArgument = (RealDataType) variableReferencedByFunctionCall;
                        functionCallArgumentArray.add(newRealArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof BooleanDataType) {
                        BooleanDataType newBoolArgument = (BooleanDataType) variableReferencedByFunctionCall;
                        functionCallArgumentArray.add(newBoolArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof CharacterDataType) {
                        CharacterDataType newCharArgument = (CharacterDataType) variableReferencedByFunctionCall;
                        functionCallArgumentArray.add(newCharArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof StringDataType) {
                        StringDataType newStringArgument = (StringDataType) variableReferencedByFunctionCall;
                        functionCallArgumentArray.add(newStringArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof ArrayDataType) {
                        ArrayDataType newArrayArgument = (ArrayDataType) variableReferencedByFunctionCall;
                        functionCallArgumentArray.add(newArrayArgument);
                    }
                    else {
                        System.out.println("ERROR: Data type of variable referenced in function call not found.");
                        System.exit(30);
                    }
                }
            }
        }
        return functionCallArgumentArray;
    }
    
    /*
     * Takes a FunctionCallNode object and an ArrayList of InterpreterDataType objects as input. It checks which built-in function is being called by retrieving the corresponding FunctionNode object from a map of built-in functions. 
     * It then executes the appropriate built-in function using the input ArrayList as arguments and updates the values of the arguments if they were modified during execution. 
     * If the built-in function is not found, the function prints an error message and terminates the program.
     * @param inputFunctionCallNode : FunctionCallNode 
     * @param inputFunctionCallArgumentArray :  ArrayList<InterpreterDataType>
     */
    private void executeBuiltInFunction(FunctionCallNode inputFunctionCallNode, ArrayList<InterpreterDataType> inputFunctionCallArgumentArray) {
        FunctionNode builtInFunction = functionMap.get(inputFunctionCallNode.getName());
        if (builtInFunction instanceof BuiltInEnd) {
            BuiltInEnd builtInEndFunction = (BuiltInEnd) builtInFunction;
            builtInEndFunction.execute(inputFunctionCallArgumentArray);
            builtInEndFunction.updateArgumentVariables(inputFunctionCallArgumentArray); // Copies changed parameters back to argument variables
        }
        else if (builtInFunction instanceof BuiltInGetRandom) {
            BuiltInGetRandom builtInGetRandomFunction = (BuiltInGetRandom) builtInFunction;
            builtInGetRandomFunction.execute(inputFunctionCallArgumentArray);
            builtInGetRandomFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInIntegerToReal) {
            BuiltInIntegerToReal builtInIntegerToRealFunction = (BuiltInIntegerToReal) builtInFunction;
            builtInIntegerToRealFunction.execute(inputFunctionCallArgumentArray);
            builtInIntegerToRealFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInLeft) {
            BuiltInLeft builtInLeftFunction = (BuiltInLeft) builtInFunction;
            builtInLeftFunction.execute(inputFunctionCallArgumentArray);
            builtInLeftFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInRead) {
            BuiltInRead builtInReadFunction = (BuiltInRead) builtInFunction;
            builtInReadFunction.execute(inputFunctionCallArgumentArray);
            builtInReadFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInRealToInteger) {
            BuiltInRealToInteger builtInRealToIntegerFunction = (BuiltInRealToInteger) builtInFunction;
            builtInRealToIntegerFunction.execute(inputFunctionCallArgumentArray);
            builtInRealToIntegerFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInRight) {
            BuiltInRight builtInRightFunction = (BuiltInRight) builtInFunction;
            builtInRightFunction.execute(inputFunctionCallArgumentArray);
            builtInRightFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInSquareRoot) {
            BuiltInSquareRoot builtInSquareRootFunction = (BuiltInSquareRoot) builtInFunction;
            builtInSquareRootFunction.execute(inputFunctionCallArgumentArray);
            builtInSquareRootFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInStart) {
            BuiltInStart builtInStartFunction = (BuiltInStart) builtInFunction;
            builtInStartFunction.execute(inputFunctionCallArgumentArray);
            builtInStartFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInSubstring) {
            BuiltInSubstring builtInSubstringFunction = (BuiltInSubstring) builtInFunction;
            builtInSubstringFunction.execute(inputFunctionCallArgumentArray);
            builtInSubstringFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else if (builtInFunction instanceof BuiltInWrite) {
            BuiltInWrite builtInWriteFunction = (BuiltInWrite) builtInFunction;
            builtInWriteFunction.execute(inputFunctionCallArgumentArray);
            builtInWriteFunction.updateArgumentVariables(inputFunctionCallArgumentArray);
        }
        else {
            System.out.println("ERROR: Built-In function not found.");
            System.exit(31);
        }
    }
    
    /*
     *  Takes in several parameters: a LinkedHashMap of local variables, an ArrayList of InterpreterDataType objects for the function call arguments, an ArrayList of ParameterNode objects for the function call parameters, 
     *  and an ArrayList of VariableNode objects for the referenced function parameters. It loops through the inputFunctionCallParameterArray, which contains the parameter nodes for the function being called, 
     *  and compares them to the inputReferencedFunctionParameterArray, which contains the referenced function's parameter nodes.
     *  @param inputLocalVariableMap : LinkedHashMap<String, InterpreterDataType>
     *  @param inputFunctionCallArgumentArray : ArrayList<InterpreterDataType>
     *  @param inputFunctionCallParameterArray : ArrayList<ParameterNode>
     *  @param inputReferencedFunctionParameterArray : ArrayList<VariableNode>
     */
    
    private void checkArgumentsAndAddToArray(LinkedHashMap<String, InterpreterDataType> inputLocalVariableMap, ArrayList<InterpreterDataType> inputFunctionCallArgumentArray, ArrayList<ParameterNode> inputFunctionCallParameterArray, ArrayList<VariableNode> inputReferencedFunctionParameterArray) throws SyntaxErrorException {
        for (int i = 0; i < inputFunctionCallParameterArray.size(); i++) { 
            ParameterNode currentFunctionCallParameterNode = inputFunctionCallParameterArray.get(i);
            VariableNode currentReferencedFunctionParameterNode = inputReferencedFunctionParameterArray.get(i);
            VariableNode.variableType variableReferencedByFunctionCallType = null;
            if ((currentReferencedFunctionParameterNode.getmodifyType() == currentFunctionCallParameterNode.getChangeable()) || currentReferencedFunctionParameterNode.getType() == VariableNode.variableType.ARRAY) {
                if (currentFunctionCallParameterNode.getChangeable() == false && currentFunctionCallParameterNode.getExpression() != null) {
                    Node functionCallArgumentExpression = currentFunctionCallParameterNode.getExpression();
                    if (functionCallArgumentExpression instanceof BoolCompareNode) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.BOOLEAN;
                        BoolCompareNode booleanCompareNodeArgument = (BoolCompareNode) functionCallArgumentExpression;
                        BooleanDataType newBooleanArgument = booleanCompareNodeFunction(inputLocalVariableMap, booleanCompareNodeArgument);
                        inputFunctionCallArgumentArray.add(newBooleanArgument);
                    }
                    else if (functionCallArgumentExpression instanceof IntegerNode) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.INTEGER;
                        IntegerNode integerNodeArgument = (IntegerNode) functionCallArgumentExpression;
                        IntegerDataType newIntegerArgument = new IntegerDataType(integerNodeArgument.getValue(), false);
                        inputFunctionCallArgumentArray.add(newIntegerArgument);
                    }
                    else if (functionCallArgumentExpression instanceof RealNode) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.REAL;
                        RealNode realNodeArgument = (RealNode) functionCallArgumentExpression;
                        RealDataType newRealArgument = new RealDataType(realNodeArgument.getVal(), false);
                        inputFunctionCallArgumentArray.add(newRealArgument);
                    }
                    else if (functionCallArgumentExpression instanceof BoolNode) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.BOOLEAN;
                        BoolNode booleanNodeArgument = (BoolNode) functionCallArgumentExpression;
                        BooleanDataType newBooleanArgument = new BooleanDataType(booleanNodeArgument.getBool(), false);
                        inputFunctionCallArgumentArray.add(newBooleanArgument);
                    }
                    else if (functionCallArgumentExpression instanceof CharacterNode) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.CHARACTER;
                        CharacterNode characterNodeArgument = (CharacterNode) functionCallArgumentExpression;
                        CharacterDataType newCharacterArgument = new CharacterDataType(characterNodeArgument.getValue(), false);
                        inputFunctionCallArgumentArray.add(newCharacterArgument);
                    }
                    else if (functionCallArgumentExpression instanceof StringNode) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.STRING;
                        StringNode stringNodeArgument = (StringNode) functionCallArgumentExpression;
                        StringDataType newStringArgument = new StringDataType(stringNodeArgument.getString(), false);
                        inputFunctionCallArgumentArray.add(newStringArgument);
                    }
                    else if (functionCallArgumentExpression instanceof VariableReferenceNode) {
                        VariableReferenceNode newVariableReferenceNode = (VariableReferenceNode) functionCallArgumentExpression;
                        InterpreterDataType referencedVariable = VariableReferenceNodeFunction(inputLocalVariableMap, newVariableReferenceNode);
                        if (referencedVariable instanceof IntegerDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.INTEGER;
                            IntegerDataType newIntArgument = (IntegerDataType) referencedVariable;
                            inputFunctionCallArgumentArray.add(newIntArgument);
                        }
                        else if (referencedVariable instanceof RealDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.REAL;
                            RealDataType newRealArgument = (RealDataType) referencedVariable;
                            inputFunctionCallArgumentArray.add(newRealArgument);
                        }
                        else if (referencedVariable instanceof BooleanDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.BOOLEAN;
                            BooleanDataType newBoolArgument = (BooleanDataType) referencedVariable;
                            inputFunctionCallArgumentArray.add(newBoolArgument);
                        }
                        else if (referencedVariable instanceof CharacterDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.CHARACTER;
                            CharacterDataType newCharArgument = (CharacterDataType) referencedVariable;
                            inputFunctionCallArgumentArray.add(newCharArgument);
                        }
                        else if (referencedVariable instanceof StringDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.STRING;
                            StringDataType newStringArgument = (StringDataType) referencedVariable;
                            inputFunctionCallArgumentArray.add(newStringArgument);
                        }
                        else if (referencedVariable instanceof ArrayDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.ARRAY;
                            ArrayDataType newArrayArgument = (ArrayDataType) referencedVariable;
                            inputFunctionCallArgumentArray.add(newArrayArgument);
                        }

                    }
                    else if (functionCallArgumentExpression instanceof MathOpNode) {
                        MathOpNode mathOpNodeArgument = (MathOpNode) functionCallArgumentExpression;
                        InterpreterDataType mathOpNodeData = MathOpNodeFunction(inputLocalVariableMap, mathOpNodeArgument);
                        if (mathOpNodeData instanceof IntegerDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.INTEGER;
                            IntegerDataType newIntegerArgument = (IntegerDataType) mathOpNodeData;
                            inputFunctionCallArgumentArray.add(newIntegerArgument);
                        }
                        else if (mathOpNodeData instanceof RealDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.REAL;
                            RealDataType newRealArgument = (RealDataType) mathOpNodeData;
                            inputFunctionCallArgumentArray.add(newRealArgument);
                        }
                        else if (mathOpNodeData instanceof StringDataType) {
                            variableReferencedByFunctionCallType = VariableNode.variableType.STRING;
                            StringDataType newStringArgument = (StringDataType) mathOpNodeData;
                            inputFunctionCallArgumentArray.add(newStringArgument);
                        }
                        else {
                            System.out.println("ERROR: Unrecognized data type retrieved from expression within function call argument");
                            System.exit(20);
                        }
                    }
                    if (currentReferencedFunctionParameterNode.getType() != variableReferencedByFunctionCallType) {
                        System.out.println("ERROR: Data type of variable referenced in function call does not match data type of function parameter.");
                        System.exit(21);
                    }
                }
                else {
                    String functionCallVariableName = currentFunctionCallParameterNode.getName();
                    if (!inputLocalVariableMap.containsKey(functionCallVariableName)) {
                        System.out.println("ERROR: Variable referenced in function call not found.");
                        System.exit(22);
                    }
                    InterpreterDataType variableReferencedByFunctionCall = inputLocalVariableMap.get(functionCallVariableName);
                    if (variableReferencedByFunctionCall instanceof IntegerDataType) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.INTEGER;
                        IntegerDataType newIntArgument = (IntegerDataType) variableReferencedByFunctionCall;
                        inputFunctionCallArgumentArray.add(newIntArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof RealDataType) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.REAL;
                        RealDataType newRealArgument = (RealDataType) variableReferencedByFunctionCall;
                        inputFunctionCallArgumentArray.add(newRealArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof BooleanDataType) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.BOOLEAN;
                        BooleanDataType newBoolArgument = (BooleanDataType) variableReferencedByFunctionCall;
                        inputFunctionCallArgumentArray.add(newBoolArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof CharacterDataType) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.CHARACTER;
                        CharacterDataType newCharArgument = (CharacterDataType) variableReferencedByFunctionCall;
                        inputFunctionCallArgumentArray.add(newCharArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof StringDataType) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.STRING;
                        StringDataType newStringArgument = (StringDataType) variableReferencedByFunctionCall;
                        inputFunctionCallArgumentArray.add(newStringArgument);
                    }
                    else if (variableReferencedByFunctionCall instanceof ArrayDataType) {
                        variableReferencedByFunctionCallType = VariableNode.variableType.ARRAY;
                        ArrayDataType newArrayArgument = (ArrayDataType) variableReferencedByFunctionCall;
                        inputFunctionCallArgumentArray.add(newArrayArgument);
                    }
                    else {
                        System.out.println("ERROR: Data type of variable referenced in function call not found.");
                        System.exit(23);
                    }
                    if (currentReferencedFunctionParameterNode.getType() != variableReferencedByFunctionCallType) {
                        System.out.println("ERROR: Data type of variable referenced in function call does not match data type of function parameter.");
                        System.exit(24);
                    }
                }
            }
            else {
                System.out.println("ERROR: Arguments do not match parameter type.");
                System.exit(25);
            }
        }
    }

}