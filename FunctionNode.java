package ShankInterpreter;

import java.util.ArrayList;

public class FunctionNode extends Node {
    private String name;
    private boolean isBuiltIn = false;
    private ArrayList<VariableNode> parameterArray = new ArrayList<VariableNode>();
    private ArrayList<VariableNode> variableArray = new ArrayList<VariableNode>();
    private ArrayList<StatementNode> statementArray = new ArrayList<StatementNode>();
    private ArrayList<InterpreterDataType> argumentArray = new ArrayList<InterpreterDataType>();

    public FunctionNode(String inputName, ArrayList<VariableNode> inputParameterArray,
    ArrayList<VariableNode> inputVariableArray, ArrayList<StatementNode> inputStatementArray, ArrayList<InterpreterDataType> inputArgumentArray, boolean inputIsBuiltIn) {
        name = inputName;
        parameterArray = inputParameterArray;
        variableArray = inputVariableArray;
        statementArray = inputStatementArray;
        argumentArray = inputArgumentArray;
        isBuiltIn = inputIsBuiltIn;
    }

    public String getName() {
        return name;
    }

    public ArrayList<InterpreterDataType> getArgumentArray() {
        return argumentArray;
    }

    public ArrayList<VariableNode> getParameterArray() {
        return parameterArray;
    }

    public ArrayList<VariableNode> getVariableArray() {
        return variableArray;
    }

    public ArrayList<StatementNode> getStatementArray() {
        return statementArray;
    }

    public boolean isBuiltIn() {
        return (isBuiltIn) ? true : false;
    }

    public boolean isVariadic() {
        boolean result = (name == "read" || name == "write") ? true : false;
        return result;
    }

    public void setArgumentArray(ArrayList<InterpreterDataType> inputArgumentArray) {
        argumentArray = inputArgumentArray;
        updateParameterVariables();
    }

    public String ToString() {
        String str = "Function Name: " + name + "\n\n";
        if (parameterArray != null)
            str += ToStringParameterArray();
        if (variableArray != null)
            str += ToStringVariableArray();
        if (statementArray != null)
            str += ToStringStatementArray();
        str += "\n";
        return str;
    }

    public void updateArgumentVariables(ArrayList<InterpreterDataType> inputNewArgumentArray) {
        // Copies changed parameter variables back to appropriate argument variables after function execution
        if (argumentArray != null) {
            for (int i = 0; i < argumentArray.size(); i++) {
                if (argumentArray.get(i) instanceof IntegerDataType) {
                    IntegerDataType integerArgument = (IntegerDataType) argumentArray.get(i);
                    IntegerDataType newIntegerArgument = (IntegerDataType) inputNewArgumentArray.get(i);
                    integerArgument.setData(newIntegerArgument.getData());
                    argumentArray.set(i, integerArgument);
                }
                else if (argumentArray.get(i) instanceof RealDataType) {
                    RealDataType realArgument = (RealDataType) argumentArray.get(i);
                    RealDataType newRealArgument = (RealDataType) inputNewArgumentArray.get(i);
                    realArgument.setData(newRealArgument.getData());
                    argumentArray.set(i, realArgument);
                }
                else if (argumentArray.get(i) instanceof BooleanDataType) {
                    BooleanDataType booleanArgument = (BooleanDataType) argumentArray.get(i);
                    BooleanDataType newBooleanArgument = (BooleanDataType) inputNewArgumentArray.get(i);
                    booleanArgument.setData(newBooleanArgument.getData());
                    argumentArray.set(i, booleanArgument);
                }
                else if (argumentArray.get(i) instanceof StringDataType) {
                    StringDataType stringArgument = (StringDataType) argumentArray.get(i);
                    StringDataType newStringArgument = (StringDataType) inputNewArgumentArray.get(i);
                    stringArgument.setData(newStringArgument.getData());
                    argumentArray.set(i, stringArgument);
                }
                else if (argumentArray.get(i) instanceof CharacterDataType) {
                    CharacterDataType characterArgument = (CharacterDataType) argumentArray.get(i);
                    CharacterDataType newCharacterArgument = (CharacterDataType) inputNewArgumentArray.get(i);
                    characterArgument.setData(newCharacterArgument.getData());
                    argumentArray.set(i, characterArgument);
                }
                else if (argumentArray.get(i) instanceof ArrayDataType) {
                    ArrayDataType arrayArgument = (ArrayDataType) argumentArray.get(i);
                    ArrayDataType newArrayArgument = (ArrayDataType) inputNewArgumentArray.get(i);
                    arrayArgument.setArray(newArrayArgument.getArray());
                    argumentArray.set(i, arrayArgument);
                }
                else {
                    System.out.println("ERROR: Unknown data type found in function.");
                    System.exit(7);
                }
            }
        }
    }

    private String ToStringParameterArray() {
        String str = "Parameters:\n";
        for (VariableNode node : parameterArray) {
            str += "    ";
            if (node.getmodifyType() == true)
                str += "var ";
            str += node.getName() + ":" + node.getType() + "\n";
        }
        str += "\n";
        return str;
    }

    private String ToStringVariableArray() {
        String str = "Variables:\n";
        for (VariableNode node : variableArray) {
            str += "    " + node.getName() + ":" + node.getType();
            if (node.getValue() != null) {
                str += " = " + node.getValue().toString();
            }
            if (node.getTypeLimit() == true) {
                if (node.getType() == VariableNode.variableType.REAL) {
                    str += " From " + node.getRealFrom() + " To " + node.getRealTo();
                }
                else {
                    str += " From " + node.getIntFrom() + " To " + node.getIntTo();
                }
            }
            str += "\n";
        }
        str += "\n";
        return str;
    }

    private String ToStringStatementArray() {
        String str = "Function Statements:\n\n";
        for (StatementNode node : statementArray) {
            str += node.toString() + "\n";
        }
        return str;
    }

    private void updateParameterVariables() {
        // Copies argument variables given in function call to matching parameter variables
        if (argumentArray != null & parameterArray != null) {
            for (int i = 0; i < argumentArray.size(); i++) {
                if (argumentArray.get(i) instanceof IntegerDataType) {
                    IntegerDataType integerArgument = (IntegerDataType) argumentArray.get(i);
                    IntegerNode integerArgumentToNode = new IntegerNode(integerArgument.getData());
                    parameterArray.get(i).setValue(integerArgumentToNode);
                }
                else if (argumentArray.get(i) instanceof RealDataType) {
                    RealDataType realArgument = (RealDataType) argumentArray.get(i);
                    RealNode realArgumentToNode = new RealNode(realArgument.getData());
                    parameterArray.get(i).setValue(realArgumentToNode);
                }
                else if (argumentArray.get(i) instanceof BooleanDataType) {
                    BooleanDataType booleanArgument = (BooleanDataType) argumentArray.get(i);
                    BoolNode booleanArgumentToNode = new BoolNode(booleanArgument.getData());
                    parameterArray.get(i).setValue(booleanArgumentToNode);
                }
                else if (argumentArray.get(i) instanceof StringDataType) {
                    StringDataType stringArgument = (StringDataType) argumentArray.get(i);
                    StringNode stringArgumentToNode = new StringNode(stringArgument.getData());
                    parameterArray.get(i).setValue(stringArgumentToNode);
                }
                else if (argumentArray.get(i) instanceof CharacterDataType) {
                    CharacterDataType characterArgument = (CharacterDataType) argumentArray.get(i);
                    CharacterNode characterArgumentToNode = new CharacterNode(characterArgument.getData());
                    parameterArray.get(i).setValue(characterArgumentToNode);
                }
                else if (argumentArray.get(i) instanceof ArrayDataType) {
                    ArrayDataType arrayArgument = (ArrayDataType) argumentArray.get(i);
                    int start = arrayArgument.getStartIndex();
                    int end = arrayArgument.getEndIndex();
                    ArrayList<InterpreterDataType> arrayArgumentArray = arrayArgument.getArray();
                    VariableNode parameter = parameterArray.get(i);
                    parameter.setArraySize(start, end);
                    for (int j = 0; j < arrayArgumentArray.size(); j++) {
                        if (arrayArgumentArray.get(j) instanceof IntegerDataType) {
                            IntegerDataType integerArgument = (IntegerDataType) arrayArgumentArray.get(i);
                            IntegerNode integerArgumentToNode = new IntegerNode(integerArgument.getData());
                            parameter.setArrayValueAtIndex(j, integerArgumentToNode, VariableNode.variableType.INTEGER);
                        }
                        else if (arrayArgumentArray.get(j) instanceof RealDataType) {
                            RealDataType realArgument = (RealDataType) arrayArgumentArray.get(i);
                            RealNode realArgumentToNode = new RealNode(realArgument.getData());
                            parameter.setArrayValueAtIndex(j, realArgumentToNode, VariableNode.variableType.REAL);
                        }
                        else if (arrayArgumentArray.get(j) instanceof BooleanDataType) {
                            BooleanDataType booleanArgument = (BooleanDataType) arrayArgumentArray.get(i);
                            BoolNode booleanArgumentToNode = new BoolNode(booleanArgument.getData());
                            parameter.setArrayValueAtIndex(j, booleanArgumentToNode, VariableNode.variableType.BOOLEAN);
                        }
                        else if (arrayArgumentArray.get(j) instanceof StringDataType) {
                            StringDataType stringArgument = (StringDataType) arrayArgumentArray.get(i);
                            StringNode stringArgumentToNode = new StringNode(stringArgument.getData());
                            parameter.setArrayValueAtIndex(j, stringArgumentToNode, VariableNode.variableType.STRING);
                        }
                        else if (arrayArgumentArray.get(j) instanceof CharacterDataType) {
                            CharacterDataType characterArgument = (CharacterDataType) arrayArgumentArray.get(i);
                            CharacterNode characterArgumentToNode = new CharacterNode(characterArgument.getData());
                            parameter.setArrayValueAtIndex(j, characterArgumentToNode, VariableNode.variableType.CHARACTER);
                        }
                        else {
                            System.out.println("ERROR: Unknown data type found in argument array.");
                            System.exit(6);
                        }
                    }
                }
                else {
                    System.out.println("ERROR: Unknown data type found in function.");
                    System.exit(7);
                }
            }
        }
    }   
}