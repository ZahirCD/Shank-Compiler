package ShankInterpreter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import ShankInterpreter.MathOpNode.possibleOperations;

public class Parser {
    private ArrayList<Token> tokenArray = new ArrayList<Token>();
    private LinkedHashMap<String, VariableNode> functionVariables = new LinkedHashMap<String, VariableNode>();
    
    private Token newToken = new Token();

    private ProgramNode programNode;

    private int indentationLevel = 0;
    
    public Parser(ArrayList<Token> inputTokenArray) {
        tokenArray = inputTokenArray;
    }

    public Node parse() throws SyntaxErrorException {
        Node newNode;
        programNode = new ProgramNode();
        Token endOfLineNode;
        do {
            newNode = expression();
            if (newNode != null) { //Then we have an expression
                if (newNode instanceof MathOpNode) { //Checks what kind of expression we have
                    MathOpNode expressionNode = (MathOpNode) newNode;
                    System.out.println(expressionNode.ToString());
                }
                else if (newNode instanceof IntegerNode) {
                    IntegerNode integerNode = (IntegerNode) newNode;
                    System.out.println(integerNode.toString());
                }
                else if (newNode instanceof RealNode) {
                    RealNode realNode = (RealNode) newNode;
                    System.out.println(realNode.toString());
                }
                else {
                    throw new SyntaxErrorException(tokenArray.get(0));
                }
            }
            else { //If line isn't an expression, check for a function
                newNode = function();
                if (newNode != null) {
                    FunctionNode functionNode = (FunctionNode) newNode;
                    programNode.addToFunctionMap(functionNode);
                    functionVariables.clear();
                }
            }
            endOfLineNode = expectEndsOfLine();
        } while (newNode != null && endOfLineNode != null); 
        return programNode;
    }

    private void addConstantToArray(ArrayList<VariableNode> inputVariableNodeArray) throws SyntaxErrorException {
        int negativeMultiplier;
        Token currentToken;
        do {
            currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
            negativeMultiplier = 1;
            if (currentToken != null) {
                String functionName = currentToken.getValue();
                if (matchAndRemove(Token.tokenType.COMPARISONEQUAL) != null) {
                    currentToken = matchAndRemove(Token.tokenType.MINUS);
                    //If a minus sign is detected in front of a number, will multiply detected number by -1
                    if (currentToken != null) {
                        negativeMultiplier = -1;
                    }
                    switch (peek(0).getToken()) { //Searches for the constant type and adds the constant to the variable node array
                        case REAL:
                            currentToken = matchAndRemove(Token.tokenType.REAL);
                            float realValue = Float.parseFloat(currentToken.getValue());
                            RealNode newRealNode = new RealNode(realValue * negativeMultiplier);
                            inputVariableNodeArray.add(new VariableNode(functionName, VariableNode.variableType.REAL, false, newRealNode));
                            break;
                        case INTEGER:
                            currentToken = matchAndRemove(Token.tokenType.INTEGER);
                            int intValue = Integer.parseInt(currentToken.getValue());
                            IntegerNode newIntegerNode = new IntegerNode((int) intValue * negativeMultiplier);
                            inputVariableNodeArray.add(new VariableNode(functionName, VariableNode.variableType.INTEGER, false, newIntegerNode));
                            break;
                        case CHARACTERLITERAL:
                            currentToken = matchAndRemove(Token.tokenType.CHARACTERLITERAL);
                            CharacterNode newCharacterNode = new CharacterNode(currentToken.getValue().charAt(0));
                            inputVariableNodeArray.add(new VariableNode(functionName, VariableNode.variableType.CHARACTER, false, newCharacterNode));
                            break;
                        case STRINGLITERAL:
                            currentToken = matchAndRemove(Token.tokenType.STRINGLITERAL);
                            StringNode newStringNode = new StringNode(currentToken.getValue());
                            inputVariableNodeArray.add(new VariableNode(functionName, VariableNode.variableType.STRING, false, newStringNode));
                            break;
                        case TRUE:
                            currentToken = matchAndRemove(Token.tokenType.TRUE);
                            BoolNode newBooleanTrueNode = new BoolNode(true);
                            inputVariableNodeArray.add(new VariableNode(functionName, VariableNode.variableType.BOOLEAN, false, newBooleanTrueNode));
                            break;
                        case FALSE:
                            currentToken = matchAndRemove(Token.tokenType.FALSE);
                            BoolNode newBooleanFalseNode = new BoolNode(false);
                            inputVariableNodeArray.add(new VariableNode(functionName, VariableNode.variableType.BOOLEAN, false, newBooleanFalseNode));
                            break;
                        default:
                            throw new SyntaxErrorException(tokenArray.get(0));
                    }
                } else throw new SyntaxErrorException(tokenArray.get(0));
            } else throw new SyntaxErrorException(tokenArray.get(0));
        currentToken = matchAndRemove(Token.tokenType.COMMA); //If a comma follows the data, another constant should exist
        } while (currentToken != null);
    }

    private void addVariableNodesToArray(boolean inputChangeable, ArrayList<VariableNode> inputVariableNodeArray) throws SyntaxErrorException {
        ArrayList<String> variableNameArray = new ArrayList<String>(); //Used to store multiple variable declarations that are on the same line
        Token currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
        if (currentToken != null) {
            variableNameArray.add(currentToken.getValue());
            while (matchAndRemove(Token.tokenType.COMMA) != null) { //If more than one variable is being declared, continue adding to variableNameArray
                currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
                if (currentToken == null)
                    throw new SyntaxErrorException(tokenArray.get(0));
                variableNameArray.add(currentToken.getValue());
            }
            if (matchAndRemove(Token.tokenType.COLON) != null) {
                VariableNode.variableType variableType = searchForType();
                if (variableType == null)
                    throw new SyntaxErrorException(tokenArray.get(0));
                if (variableType == VariableNode.variableType.ARRAY) {
                    createArray(variableNameArray, inputVariableNodeArray);
                }
                else if (variableType == VariableNode.variableType.INTEGER || variableType == VariableNode.variableType.STRING || variableType == VariableNode.variableType.REAL) {
                    //If variableType is Integer, String, or Real, then need to check for type limit
                    if (matchAndRemove(Token.tokenType.INTEGER) != null) {
                        if (matchAndRemove(Token.tokenType.FROM) != null) { //Check for type limit
                            Node fromNode = expression();
                            if (fromNode instanceof IntegerNode) {
                                IntegerNode fromIntegerNode = (IntegerNode) fromNode;
                                int from = fromIntegerNode.getValue();
                                if (matchAndRemove(Token.tokenType.TO) != null) {
                                    Node toNode = expression();
                                    if (toNode instanceof IntegerNode) {
                                        IntegerNode toIntegerNode = (IntegerNode) toNode;
                                        int to = toIntegerNode.getValue();
                                        for (int i = 0; i < variableNameArray.size(); i++) {
                                            inputVariableNodeArray.add(new VariableNode(variableNameArray.get(i), variableType, from, to));
                                        }
                                    } else throw new SyntaxErrorException(tokenArray.get(0));
                                } else throw new SyntaxErrorException(tokenArray.get(0));
                            } else throw new SyntaxErrorException(tokenArray.get(0));
                        }
                        else {
                            for (int i = 0; i < variableNameArray.size(); i++) {
                                inputVariableNodeArray.add(new VariableNode(variableNameArray.get(i), variableType, inputChangeable));
                            }
                        }
                    }
                    else if (matchAndRemove(Token.tokenType.STRING) != null) {
                        if (matchAndRemove(Token.tokenType.FROM) != null) {
                            Node fromNode = expression();
                            if (fromNode instanceof IntegerNode) {
                                IntegerNode fromIntegerNode = (IntegerNode) fromNode;
                                int from = fromIntegerNode.getValue();
                                if (matchAndRemove(Token.tokenType.TO) != null) {
                                    Node toNode = expression();
                                    if (toNode instanceof IntegerNode) {
                                        IntegerNode toIntegerNode = (IntegerNode) toNode;
                                        int to = toIntegerNode.getValue();
                                        for (int i = 0; i < variableNameArray.size(); i++) {
                                            inputVariableNodeArray.add(new VariableNode(variableNameArray.get(i), variableType, from, to));
                                        }
                                    } else throw new SyntaxErrorException(tokenArray.get(0));
                                } else throw new SyntaxErrorException(tokenArray.get(0));
                            } else throw new SyntaxErrorException(tokenArray.get(0));
                        }
                        else {
                            for (int i = 0; i < variableNameArray.size(); i++) {
                                inputVariableNodeArray.add(new VariableNode(variableNameArray.get(i), variableType, inputChangeable));
                            }
                        }
                    }
                    else if (matchAndRemove(Token.tokenType.REAL) != null) {
                        if (matchAndRemove(Token.tokenType.FROM) != null) {
                            Node fromNode = expression();
                            if (fromNode instanceof RealNode) {
                                RealNode fromRealNode = (RealNode) fromNode;
                                float from = fromRealNode.getVal();
                                if (matchAndRemove(Token.tokenType.TO) != null) {
                                    Node toNode = expression();
                                    if (toNode instanceof RealNode) {
                                        RealNode toIntegerNode = (RealNode) toNode;
                                        float to = toIntegerNode.getVal();
                                        for (int i = 0; i < variableNameArray.size(); i++) {
                                            inputVariableNodeArray.add(new VariableNode(variableNameArray.get(i), variableType, from, to));
                                        }
                                    } else throw new SyntaxErrorException(tokenArray.get(0));
                                } else throw new SyntaxErrorException(tokenArray.get(0));
                            } else throw new SyntaxErrorException(tokenArray.get(0));
                        }
                        else {
                            for (int i = 0; i < variableNameArray.size(); i++) {
                                inputVariableNodeArray.add(new VariableNode(variableNameArray.get(i), variableType, inputChangeable));
                            }
                        }
                    } else throw new SyntaxErrorException(tokenArray.get(0));
                }
                else {
                    for (int i = 0; i < variableNameArray.size(); i++) {
                        inputVariableNodeArray.add(new VariableNode(variableNameArray.get(i), variableType, inputChangeable));
                    }
                    matchAndRemove(Token.tokenType.CHARACTER);
                    matchAndRemove(Token.tokenType.BOOLEAN);
                }
                matchAndRemove(Token.tokenType.SEMICOLON);
            } else throw new SyntaxErrorException(tokenArray.get(0));
        } else throw new SyntaxErrorException(tokenArray.get(0));
    }
    
    private AssignmentNode assignment() throws SyntaxErrorException {
        Node targetNode = getTargetNode();
        if (targetNode == null)
            return null;
        VariableReferenceNode targetReferenceNode;
        if (targetNode instanceof VariableReferenceNode)
            targetReferenceNode = (VariableReferenceNode) targetNode;
        else 
            throw new SyntaxErrorException(tokenArray.get(0));
        if (matchAndRemove(Token.tokenType.ASSIGNMENTEQUAL) == null)
            return null;
        Node assignmentValue = boolCompare();
        if (assignmentValue == null)
            throw new SyntaxErrorException(tokenArray.get(0));
        AssignmentNode newAssignmentNode = new AssignmentNode(targetReferenceNode, assignmentValue);
        return newAssignmentNode;
    }

    private Node boolCompare() throws SyntaxErrorException {
        Node left = expression();
        BoolCompareNode.comparisonType comparisonType;
        if (matchAndRemove(Token.tokenType.LESSTHAN) != null)
            comparisonType = BoolCompareNode.comparisonType.LESSTHAN;
        else if (matchAndRemove(Token.tokenType.GREATERTHAN) != null)
            comparisonType = BoolCompareNode.comparisonType.GREATERTHAN;
        else if (matchAndRemove(Token.tokenType.GREATERTHANOREQUALTO) != null)
            comparisonType = BoolCompareNode.comparisonType.GREATERTHANOREQUALTO;
        else if (matchAndRemove(Token.tokenType.LESSTHANOREQUALTO) != null)
            comparisonType = BoolCompareNode.comparisonType.LESSTHANOREQUALTO;
        else if (matchAndRemove(Token.tokenType.COMPARISONEQUAL) != null)
            comparisonType = BoolCompareNode.comparisonType.EQUAL;
        else if (matchAndRemove(Token.tokenType.NOTEQUAL) != null)
            comparisonType = BoolCompareNode.comparisonType.NOTEQUAL;
        else
            return left;
        Node right = expression();
        BoolCompareNode newBooleanCompareNode = new BoolCompareNode(comparisonType, left, right);
        return newBooleanCompareNode;
    }

    private void createArray(ArrayList<String> inputNameArray, ArrayList<VariableNode> inputVariableNodeArray) throws SyntaxErrorException {
                if (matchAndRemove(Token.tokenType.ARRAY) != null) {
                    if (matchAndRemove(Token.tokenType.FROM) != null) {
                        Node fromNode = expression();
                        if (fromNode instanceof IntegerNode) {
                            IntegerNode fromIntegerNode = (IntegerNode) fromNode;
                            int from = fromIntegerNode.getValue();
                            if (matchAndRemove(Token.tokenType.TO) != null) {
                                Node toNode = expression();
                                if (toNode instanceof IntegerNode) {
                                    IntegerNode toIntegerNode = (IntegerNode) toNode;
                                    int to = toIntegerNode.getValue();
                                    if (matchAndRemove(Token.tokenType.OF) != null) {
                                        VariableNode.variableType arrayType = searchForType();
                                        if (arrayType == VariableNode.variableType.ARRAY || arrayType == null)
                                            throw new SyntaxErrorException(tokenArray.get(0));
                                        matchAndRemove(Token.tokenType.INTEGER);
                                        matchAndRemove(Token.tokenType.REAL);
                                        matchAndRemove(Token.tokenType.CHARACTER);
                                        matchAndRemove(Token.tokenType.ARRAY);
                                        matchAndRemove(Token.tokenType.BOOLEAN);
                                        matchAndRemove(Token.tokenType.STRING);
                                        for (int i = 0; i < inputNameArray.size(); i++) {
                                            inputVariableNodeArray.add(new VariableNode(inputNameArray.get(i), from, to, arrayType));
                                        }
                                    } else throw new SyntaxErrorException(tokenArray.get(0));
                                } else throw new SyntaxErrorException(tokenArray.get(0));
                            } else throw new SyntaxErrorException(tokenArray.get(0));
                        } else throw new SyntaxErrorException(tokenArray.get(0));
                    } else throw new SyntaxErrorException(tokenArray.get(0));
                } else throw new SyntaxErrorException(tokenArray.get(0));
    }

    private MathOpNode createMathOpNode(MathOpNode.possibleOperations inputOperationType, Node inputLeftChild, Node inputRightChild) throws SyntaxErrorException {
        if (inputLeftChild == null || inputRightChild == null)
            throw new SyntaxErrorException(tokenArray.get(0));
        MathOpNode newMathOpNode = new MathOpNode(inputOperationType, inputLeftChild, inputRightChild);
        return newMathOpNode;
    }

    private Token expectEndsOfLine() throws SyntaxErrorException {
        Token currentToken = matchAndRemove(Token.tokenType.ENDOFLINE);
        if (currentToken != null) {
            while (tokenArray.size() > 0 && matchAndRemove(Token.tokenType.ENDOFLINE) != null) { //Eats up any additional ENDOFLINE tokens
            }
            return currentToken;
        }
        else {
            return null;
        }
    }

    private Node expression() throws SyntaxErrorException {
        MathOpNode newMathOpNode;
        newMathOpNode = new MathOpNode();
        Node leftNode = term();
        if (leftNode == null) {
            return null;
        }
        if (matchAndRemove(Token.tokenType.PLUS) != null) {
            Node rightNode = term();
            if (leftNode instanceof StringNode ^ rightNode instanceof StringNode) {
                if (leftNode instanceof StringNode && !(rightNode instanceof VariableReferenceNode))
                    throw new SyntaxErrorException(tokenArray.get(0));
                else if (rightNode instanceof StringNode && !(leftNode instanceof VariableReferenceNode))
                    throw new SyntaxErrorException(tokenArray.get(0));
            }
            newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.PLUS, leftNode, rightNode);
        }
        else if (matchAndRemove(Token.tokenType.MINUS) != null) {
            if (leftNode instanceof StringNode) //String nodes can only be added, not subtracted
                throw new SyntaxErrorException(tokenArray.get(0));
            Node rightNode = term();
            if (rightNode instanceof StringNode)
                throw new SyntaxErrorException(tokenArray.get(0));
            newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.MINUS, leftNode, rightNode);
        }
        else {
            return leftNode; //If a factor is not succeeded by an operator, return it
        }
        while (tokenArray.size() > 0 && (peek(0).getToken() == Token.tokenType.PLUS 
        || peek(0).getToken() == Token.tokenType.MINUS)) { //While loop checks if current token without removing it from array, allowing it to be mathchAndRemove'd later
            if (matchAndRemove(Token.tokenType.PLUS) != null) {
                Node rightNode = term();
                if (leftNode instanceof StringNode ^ rightNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.PLUS, newMathOpNode, rightNode);
            }
            else if (matchAndRemove(Token.tokenType.MINUS) != null) {
                if (leftNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                Node rightNode = term();
                if (rightNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.MINUS, newMathOpNode, rightNode);
            }
        }
        return newMathOpNode;
    }

    private Node factor() throws SyntaxErrorException {
        int negativeMultiplier = 1;
        if (matchAndRemove(Token.tokenType.MINUS) != null) {
            negativeMultiplier = -1;
        }
        if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.IDENTIFIER) {
            if (functionVariables.containsKey(peek(0).getValue())) {
                Token currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
                VariableNode variable = functionVariables.get(currentToken.getValue());
                if (matchAndRemove(Token.tokenType.LEFTBRACKET) != null) {
                    Node index = expression();
                    if (index != null) {
                        if (matchAndRemove(Token.tokenType.RIGHTBRACKET) == null) {
                            throw new SyntaxErrorException(tokenArray.get(0));
                        }
                        VariableReferenceNode variableReferenceNode = new VariableReferenceNode(currentToken.getValue(), variable, index);
                        return variableReferenceNode;
                    }
                    else {
                        throw new SyntaxErrorException(tokenArray.get(0));
                    }
                }
                else {
                    VariableReferenceNode variableReferenceNode = new VariableReferenceNode(currentToken.getValue(), variable);
                    return variableReferenceNode;
                }
            }
            else {
                System.out.println("Unitialized variable '" + peek(0).getValue() +"' referenced.");
                System.exit(0);
            }
        }
        else if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.INTEGER) {
            newToken = matchAndRemove(Token.tokenType.INTEGER);
            int numberValue = Integer.parseInt(newToken.getValue());
            IntegerNode newIntegerNode = new IntegerNode(numberValue * negativeMultiplier);
            return newIntegerNode;
        }
        else if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.REAL) {
            newToken = matchAndRemove(Token.tokenType.REAL);
            float numberValue = Float.parseFloat(newToken.getValue());
            RealNode newRealNode = new RealNode(numberValue * negativeMultiplier);
            return newRealNode;
        }
        else if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.STRINGLITERAL) {
            newToken = matchAndRemove(Token.tokenType.STRINGLITERAL);
            StringNode newStringNode = new StringNode(newToken.getValue());
            return newStringNode;
        }
        else if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.CHARACTERLITERAL) {
            newToken = matchAndRemove(Token.tokenType.CHARACTERLITERAL);
            CharacterNode newCharacterNode = new CharacterNode(newToken.getValue().charAt(0));
            return newCharacterNode;
        }
        else if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.TRUE) {
            newToken = matchAndRemove(Token.tokenType.TRUE);
            BoolNode newBooleanNode = new BoolNode(true);
            return newBooleanNode;
        }
        else if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.FALSE) {
            newToken = matchAndRemove(Token.tokenType.FALSE);
            BoolNode newBooleanNode = new BoolNode(false);
            return newBooleanNode;
        }
        else if (matchAndRemove(Token.tokenType.LEFTPARENTHESES) != null) {
            Node currentNode = boolCompare(); // If a left parentheses is detected, will check for a boolean statement
            if (currentNode instanceof IntegerNode) {
                currentNode = (IntegerNode) currentNode;
                if (matchAndRemove(Token.tokenType.RIGHTPARENTHESES) != null)
                    return currentNode;
                else
                    throw new SyntaxErrorException(tokenArray.get(0));
            }
            else if (currentNode instanceof RealNode) {
                currentNode = (RealNode) currentNode;
                if (matchAndRemove(Token.tokenType.RIGHTPARENTHESES) != null)
                    return currentNode;
                else
                    throw new SyntaxErrorException(tokenArray.get(0));
            }
            else if (currentNode instanceof MathOpNode) {
                currentNode = (MathOpNode) currentNode;
                if (matchAndRemove(Token.tokenType.RIGHTPARENTHESES) != null)
                    return currentNode;
                else
                    throw new SyntaxErrorException(tokenArray.get(0));
            }
            else if (currentNode instanceof VariableReferenceNode) {
                currentNode = (VariableReferenceNode) currentNode;
                if (matchAndRemove(Token.tokenType.RIGHTPARENTHESES) != null)
                    return currentNode;
                else
                    throw new SyntaxErrorException(tokenArray.get(0));
            }
            else if (currentNode instanceof BoolCompareNode) {
                currentNode = (BoolCompareNode) currentNode;
                if (matchAndRemove(Token.tokenType.RIGHTPARENTHESES) != null)
                    return currentNode;
                else
                    throw new SyntaxErrorException(tokenArray.get(0));
            }
            return null;
        }
        return null;
    }

    private FunctionNode function() throws SyntaxErrorException {
        if (matchAndRemove(Token.tokenType.DEFINE) != null) {
            Token currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
            if (currentToken != null) {
                String functionName = currentToken.getValue();
                currentToken = matchAndRemove(Token.tokenType.LEFTPARENTHESES);
                if (currentToken != null) {
                    ArrayList<VariableNode> parameterArray = parameterDeclarations(); //Records parameters
                    ArrayList<VariableNode> variableArray = new ArrayList<VariableNode>();
                    variableArray = variableDeclarations(variableArray); //Records variables 
                    for (VariableNode variable : parameterArray) {
                        functionVariables.put(variable.getName(), variable);
                    }
                    for (VariableNode variable : variableArray) {
                        functionVariables.put(variable.getName(), variable);
                    }
                    currentToken = expectEndsOfLine();
                    if (currentToken == null)
                        throw new SyntaxErrorException(tokenArray.get(0));
                    ArrayList<StatementNode> statementNodeArray = statements(); //Records statements
                    FunctionNode functionNode = new FunctionNode(functionName, parameterArray, variableArray, statementNodeArray, null, false);
                    return functionNode;
                }
                throw new SyntaxErrorException(tokenArray.get(0));
            }
            throw new SyntaxErrorException(tokenArray.get(0));
        }
        return null;
    }

    private Node getTargetNode() throws SyntaxErrorException {
        String targetName;
        if (peek(0).getValue() == null)
            return null;
        if (tokenArray.size() > 0 && (programNode.isAFunction(peek(0).getValue()) || programNode.isAFunction(peek(0).getValue().toLowerCase())))
            return null;
        Token currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
        targetName = (currentToken != null) ? currentToken.getValue() : null;
        if (matchAndRemove(Token.tokenType.LEFTBRACKET) != null) {
            Node arrayIndex = getTargetNode(); //Recursively calls itself in order to obtain any VariableReferenceNodes nested in the array index expression
            if (arrayIndex == null) {
                throw new SyntaxErrorException(tokenArray.get(0));
            }
            matchAndRemove(Token.tokenType.RIGHTBRACKET);
            VariableNode referencedVariable = functionVariables.get(targetName);
            VariableReferenceNode newVariableReferenceNode = new VariableReferenceNode(targetName, referencedVariable, arrayIndex);
            return newVariableReferenceNode;
        }
        Node expressionNode = expression();
        if (expressionNode != null) {
            return expressionNode;
        }
        else if (expressionNode == null && currentToken != null) {
            VariableNode referencedVariable = functionVariables.get(targetName);
            VariableReferenceNode newVariableReferenceNode = new VariableReferenceNode(targetName, referencedVariable);
            return newVariableReferenceNode;
        }
        else {
            return null;
        }
    }

    private Token matchAndRemove(Token.tokenType inputTokenType) {
        if (tokenArray.size() > 0) {
            Token currentToken = tokenArray.get(0);
            if (inputTokenType == currentToken.getToken()) {
                tokenArray.remove(0);
                return currentToken;
            }
        }
        return null;
    }

    private ArrayList<VariableNode> parameterDeclarations() throws SyntaxErrorException {
        ArrayList<VariableNode> variableNodeArray = new ArrayList<VariableNode>();
        while (peek(0).getToken() != Token.tokenType.RIGHTPARENTHESES) {
            if (matchAndRemove(Token.tokenType.VAR) != null) {
                addVariableNodesToArray(true, variableNodeArray); //Variable is changeable
            }
            else {
                addVariableNodesToArray(false, variableNodeArray); //Variable can not be changed
            }
        }
        if (matchAndRemove(Token.tokenType.RIGHTPARENTHESES) != null) {
            if (variableNodeArray.size() == 0) {
                return null;
            }
            return variableNodeArray;
        }
        throw new SyntaxErrorException(tokenArray.get(0));
    }

    private ForNode parseFor() throws SyntaxErrorException {
        if (matchAndRemove(Token.tokenType.FOR) != null) {
            String targetName;
            Token currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
            if (currentToken != null) {
                targetName = currentToken.getValue();
                VariableReferenceNode integerVariableNode;
                if (matchAndRemove(Token.tokenType.LEFTBRACKET) != null) {
                    Node arrayIndex = getTargetNode(); // Recursively calls itself in order to obtain any VariableReferenceNodes nested in the array index expression
                    if (arrayIndex == null)
                        throw new SyntaxErrorException(tokenArray.get(0));
                    if (matchAndRemove(Token.tokenType.RIGHTBRACKET) == null)
                        throw new SyntaxErrorException(tokenArray.get(0));
                    VariableNode referencedVariable = functionVariables.get(targetName);
                    integerVariableNode = new VariableReferenceNode(targetName, referencedVariable, arrayIndex);
                }
                else {
                    VariableNode referencedVariable = functionVariables.get(targetName);
                    integerVariableNode = new VariableReferenceNode(targetName, referencedVariable);
                }
                if (matchAndRemove(Token.tokenType.FROM) != null) {
                    Node fromExpression = expression(); // From/To can be any expression
                    if (fromExpression == null)
                        throw new SyntaxErrorException(tokenArray.get(0));
                    if (matchAndRemove(Token.tokenType.TO) != null) {
                        Node toExpression = expression();
                        if (toExpression == null)
                            throw new SyntaxErrorException(tokenArray.get(0));
                        if (expectEndsOfLine() != null) {
                            ArrayList<StatementNode> statements = statements();
                            if (statements == null)
                                throw new SyntaxErrorException(tokenArray.get(0));
                            ForNode newForNode = new ForNode(integerVariableNode, fromExpression, toExpression, statements);
                            return newForNode;
                        } throw new SyntaxErrorException(tokenArray.get(0));
                    } throw new SyntaxErrorException(tokenArray.get(0));
                } throw new SyntaxErrorException(tokenArray.get(0));
            } throw new SyntaxErrorException(tokenArray.get(0));
        } return null;
    }

    private FunctionCallNode parseFunctionCalls() throws SyntaxErrorException {
        if (peek(0).getValue() == null)
            return null;
        if (tokenArray.size() > 0 && (programNode.isAFunction(peek(0).getValue()) || programNode.isAFunction(peek(0).getValue().toLowerCase()))) { //Checks if current token's value is the name of a declared function
            Token currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
            if (currentToken != null) {
                String functionName = currentToken.getValue();
                ArrayList<ParameterNode> parameterArray = new ArrayList<ParameterNode>();
                do {
                    if (matchAndRemove(Token.tokenType.VAR) != null) {
                        if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.IDENTIFIER) {
                            Node newVariableNode = getTargetNode(); //Adds complex variable references to parameter list
                            if (newVariableNode instanceof VariableReferenceNode) {
                                VariableReferenceNode newVariableReferenceNode = (VariableReferenceNode) newVariableNode;
                                ParameterNode newParameterNode = new ParameterNode(newVariableReferenceNode, true);
                                parameterArray.add(newParameterNode);
                            } 
                            else
                                throw new SyntaxErrorException(tokenArray.get(0));
                        }
                    }
                    else { //Adds bool and non-bool expressions to parameter list
                        Node newExpression = boolCompare();
                        if (newExpression != null) {
                            ParameterNode newParameterNode = new ParameterNode(newExpression);
                            parameterArray.add(newParameterNode);
                        }
                        else {
                            if (peek(0).getToken() == Token.tokenType.IDENTIFIER) {
                                currentToken = matchAndRemove(Token.tokenType.IDENTIFIER);
                                VariableNode referencedVariable = functionVariables.get(currentToken.getValue());
                                VariableReferenceNode newVariableReferenceNode = new VariableReferenceNode(currentToken.getValue(), referencedVariable);
                                ParameterNode newParameterNode = new ParameterNode(newVariableReferenceNode, false);
                                parameterArray.add(newParameterNode);
                            }
                        }
                    }
                    currentToken = matchAndRemove(Token.tokenType.COMMA);
                } while (currentToken != null);
                FunctionCallNode newFunctionCallNode = new FunctionCallNode(functionName, parameterArray);
                if (expectEndsOfLine() == null)
                    throw new SyntaxErrorException(tokenArray.get(0));
                return newFunctionCallNode;
            } throw new SyntaxErrorException(tokenArray.get(0));
        }   return null;
    }

    private IfNode parseIf(boolean isElsifOrElse) throws SyntaxErrorException {
        if (isElsifOrElse == true) { //Used to detect Else tokens without preceeding If tokens
            if (matchAndRemove(Token.tokenType.ELSIF) != null) {
                Node conditionNode = boolCompare();
                if (!(conditionNode instanceof BoolCompareNode)) {
                    throw new SyntaxErrorException(tokenArray.get(0));
                }
                BoolCompareNode condition = (BoolCompareNode) conditionNode;
                if (matchAndRemove(Token.tokenType.THEN) != null) {
                    if (expectEndsOfLine() != null) {
                        ArrayList<StatementNode> statements = statements();
                        if (peek(0).getToken() == Token.tokenType.ELSIF) {
                            IfNode nextIfNode = parseIf(true); //Uses recursive call to create linked list of if, elsif, and else
                            IfNode newIfNode = new IfNode(condition, statements, nextIfNode);
                            return newIfNode;
                        }
                        else if (peek(0).getToken() == Token.tokenType.ELSE) {
                            IfNode newElseNode = parseIf(true);
                            IfNode newIfNode = new IfNode(condition, statements, newElseNode);
                            return newIfNode;
                        }
                        else {
                            IfNode newIfNode = new IfNode(condition, statements, null);
                            return newIfNode;
                        }
                    }
                    throw new SyntaxErrorException(tokenArray.get(0));
                }
                throw new SyntaxErrorException(tokenArray.get(0));
            }
            else if (matchAndRemove(Token.tokenType.ELSE) != null) {
                if (expectEndsOfLine() != null) {
                    ArrayList<StatementNode> elseStatements = statements();
                    IfNode newElseNode = new IfNode(elseStatements);
                    return newElseNode;
                }
                throw new SyntaxErrorException(tokenArray.get(0));
            }
            return null;
        }
        else {
            if (matchAndRemove(Token.tokenType.IF) != null) {
                Node conditionNode = boolCompare();
                if (!(conditionNode instanceof BoolCompareNode)) {
                    throw new SyntaxErrorException(tokenArray.get(0));
                }
                BoolCompareNode condition = (BoolCompareNode) conditionNode;
                if (matchAndRemove(Token.tokenType.THEN) != null) {
                    if (expectEndsOfLine() != null) {
                        ArrayList<StatementNode> statements = statements();
                        if (peek(0).getToken() == Token.tokenType.ELSIF) {
                            IfNode nextIfNode = parseIf(true);
                            IfNode newIfNode = new IfNode(condition, statements, nextIfNode);
                            return newIfNode;
                        }
                        else if (peek(0).getToken() == Token.tokenType.ELSE) {
                            IfNode newElseNode = parseIf(true);
                            IfNode newIfNode = new IfNode(condition, statements, newElseNode);
                            return newIfNode;
                        }
                        else {
                            IfNode newIfNode = new IfNode(condition, statements, null);
                            return newIfNode;
                        }
                    }
                    throw new SyntaxErrorException(tokenArray.get(0));
                }
                throw new SyntaxErrorException(tokenArray.get(0));
            }
            if (tokenArray.size() > 0 && peek(0).getToken() == Token.tokenType.ELSE) { //Throws error for ELSE tokens that dont succeed an IF token
                throw new SyntaxErrorException(tokenArray.get(0));
            }
            return null;
        }
    }

    private WhileNode parseWhile() throws SyntaxErrorException {
        if (matchAndRemove(Token.tokenType.WHILE) != null) {
            Node condition = boolCompare();
            if (condition == null || !(condition instanceof BoolCompareNode)) //Do not want a numerical expression
                throw new SyntaxErrorException(tokenArray.get(0));
            BoolCompareNode booleanCondition = (BoolCompareNode) condition;
            if (expectEndsOfLine() != null) {
                ArrayList<StatementNode> statements = statements();
                if (statements == null)
                    throw new SyntaxErrorException(tokenArray.get(0));
                WhileNode newWhileNode = new WhileNode(booleanCondition, statements);
                return newWhileNode;
            }
            throw new SyntaxErrorException(tokenArray.get(0));
        }
        return null;
    }

    private RepeatNode parseRepeat() throws SyntaxErrorException {
        if (matchAndRemove(Token.tokenType.REPEAT) != null) {
            if (matchAndRemove(Token.tokenType.UNTIL) != null) {
                Node condition = boolCompare();
                if (condition == null || !(condition instanceof BoolCompareNode))
                    throw new SyntaxErrorException(tokenArray.get(0));
                BoolCompareNode booleanCondition = (BoolCompareNode) condition;
                if (expectEndsOfLine() != null) {
                    ArrayList<StatementNode> statements = statements();
                    if (statements == null)
                        throw new SyntaxErrorException(tokenArray.get(0));
                    RepeatNode newRepeatNode = new RepeatNode(booleanCondition, statements);
                    return newRepeatNode;
                }
                throw new SyntaxErrorException(tokenArray.get(0));
            }
            throw new SyntaxErrorException(tokenArray.get(0));
        }
        return null;
    }

    private Token peek(int inputInteger) {
        Token token = tokenArray.get(inputInteger) != null ? tokenArray.get(inputInteger) : null;
        return token;
    }

    private VariableNode.variableType searchForType() {
        for (int i = 0; i < 1; i++) { //Searches up to 10 tokens away for the variable type of the current variable
            switch (peek(i).getToken()) {
                case INTEGER:
                    return VariableNode.variableType.INTEGER;
                case REAL:
                    return VariableNode.variableType.REAL;
                case CHARACTER:
                    return VariableNode.variableType.CHARACTER;
                case STRING:
                    return VariableNode.variableType.STRING;
                case ARRAY:
                    return VariableNode.variableType.ARRAY;
                case BOOLEAN:
                    return VariableNode.variableType.BOOLEAN;
                default:
                    break;
            }
        }
        return null;
    }

    private StatementNode statement() throws SyntaxErrorException {
        AssignmentNode assignmentStatement = assignment();
        if (assignmentStatement != null) {
            programNode.addToAssignmentNodeArray(assignmentStatement); //Collects assignment statements to be used for semantic analysis
            return assignmentStatement;
        }
        FunctionCallNode functionCallStatement = parseFunctionCalls();
        if (functionCallStatement != null) {
            return functionCallStatement;
        }
        IfNode ifStatement = parseIf(false); //False indicates that this is looking for an IF statement and not an ELSIF statement
        if (ifStatement != null) {
            return ifStatement;
        }
        WhileNode whileStatement = parseWhile();
        if (whileStatement != null) {
            return whileStatement;
        }
        RepeatNode repeatStatement = parseRepeat();
        if (repeatStatement != null) {
            return repeatStatement;
        }
        ForNode forStatement = parseFor();
        if (forStatement != null) {
            return forStatement;
        }
        return null;
    }

    private ArrayList<StatementNode> statements() throws SyntaxErrorException {
        ArrayList<StatementNode> statementNodeArray = new ArrayList<StatementNode>();
        int startingIndentationLevel = indentationLevel;
        if (matchAndRemove(Token.tokenType.INDENT) != null) {
            indentationLevel++;
            StatementNode currentStatement;
            do {
                currentStatement = statement();
                if (currentStatement != null)
                    statementNodeArray.add(currentStatement);
                expectEndsOfLine();
                if (matchAndRemove(Token.tokenType.DEDENT) != null)
                    indentationLevel--;
                if (indentationLevel == startingIndentationLevel) { // Indentation tracking prevents statements outside of block from being included within block
                    break;
                }
            } while (currentStatement != null);
            return statementNodeArray;
        } return null;
    }

    private Node term() throws SyntaxErrorException {
        MathOpNode newMathOpNode;
        newMathOpNode = new MathOpNode();
        Node leftNode = factor();
        if (leftNode == null) {
            return null;
        }
            if (leftNode instanceof StringNode)
                throw new SyntaxErrorException(tokenArray.get(0));
            Node rightNode = factor();
            if (rightNode instanceof StringNode)
                throw new SyntaxErrorException(tokenArray.get(0));
            newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.MULTIPLIED, leftNode, rightNode);
        }
        else if (matchAndRemove(Token.tokenType.DIVIDED) != null) {
            if (leftNode instanceof StringNode)
                throw new SyntaxErrorException(tokenArray.get(0));
            Node rightNode = factor();
            if (rightNode instanceof StringNode)
                throw new SyntaxErrorException(tokenArray.get(0));
            newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.DIVIDED, leftNode, rightNode);
        }
        else if (matchAndRemove(Token.tokenType.MOD) != null) {
            if (leftNode instanceof StringNode)
                throw new SyntaxErrorException(tokenArray.get(0));
            Node rightNode = factor();
            if (rightNode instanceof StringNode)
                throw new SyntaxErrorException(tokenArray.get(0));
            newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.MOD, leftNode, rightNode);
        }
        else {
            return leftNode;
        }
        while (tokenArray.size() > 0 && (peek(0).getToken() == Token.tokenType.MULTIPLIED
        || peek(0).getToken() == Token.tokenType.DIVIDED || peek(0).getToken() == Token.tokenType.MOD)) {
            if (matchAndRemove(Token.tokenType.MULTIPLIED) != null) {
                if (leftNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                Node rightNode = factor();
                if (rightNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.MULTIPLIED, newMathOpNode, rightNode);
            }
            else if (matchAndRemove(Token.tokenType.DIVIDED) != null) {
                if (leftNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                Node rightNode = factor();
                if (rightNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.DIVIDED, newMathOpNode, rightNode);
            }
            else if (matchAndRemove(Token.tokenType.MOD) != null) {
                if (leftNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                Node rightNode = factor();
                if (rightNode instanceof StringNode)
                    throw new SyntaxErrorException(tokenArray.get(0));
                newMathOpNode = createMathOpNode(MathOpNode.possibleOperations.MOD, newMathOpNode, rightNode);
            }
        }
        return newMathOpNode;
    }
    
    

	private ArrayList<VariableNode> variableDeclarations(ArrayList<VariableNode> inputVariableArray) throws SyntaxErrorException {
        while (tokenArray.size() > 1 && (peek(1).getToken() == Token.tokenType.VARIABLES || peek(1).getToken() == Token.tokenType.CONSTANTS)) {
            Token currentToken = expectEndsOfLine();
            if (currentToken == null)
                throw new SyntaxErrorException(tokenArray.get(0));
            if (peek(0).getToken() == Token.tokenType.VARIABLES) {
                matchAndRemove(Token.tokenType.VARIABLES);
                addVariableNodesToArray(true, inputVariableArray);
            }
            else if (peek(0).getToken() == Token.tokenType.CONSTANTS) {
                matchAndRemove(Token.tokenType.CONSTANTS);
                addConstantToArray(inputVariableArray);
            }
            else {
                throw new SyntaxErrorException(tokenArray.get(0));
            }
        }
        return inputVariableArray;
    }
}