package ShankInterpreter;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class ProgramNode extends Node {
    private LinkedHashMap<String, FunctionNode> functionMap = new LinkedHashMap<String, FunctionNode>();
    private ArrayList<AssignmentNode> assignmentNodeArray = new ArrayList<AssignmentNode>();

    private BuiltInWrite builtInWrite = new BuiltInWrite();
    private BuiltInLeft builtInLeft = new BuiltInLeft();
    private BuiltInRight builtInRight = new BuiltInRight();
    private BuiltInSubstring builtInSubstring = new BuiltInSubstring();
    private BuiltInSquareRoot builtInSquareRoot = new BuiltInSquareRoot();
    private BuiltInGetRandom builtInGetRandom = new BuiltInGetRandom();
    private BuiltInIntegerToReal builtInIntegerToReal = new BuiltInIntegerToReal();
    private BuiltInRealToInteger builtInRealToInteger = new BuiltInRealToInteger();
    private BuiltInStart builtInStart = new BuiltInStart();
    private BuiltInEnd builtInEnd = new BuiltInEnd();
    private BuiltInRead builtInRead = new BuiltInRead();

    public ProgramNode() {
        addToFunctionMap(builtInWrite);
        addToFunctionMap(builtInLeft);
        addToFunctionMap(builtInRight);
        addToFunctionMap(builtInSubstring);
        addToFunctionMap(builtInSquareRoot);
        addToFunctionMap(builtInGetRandom);
        addToFunctionMap(builtInIntegerToReal);
        addToFunctionMap(builtInRealToInteger);
        addToFunctionMap(builtInStart);
        addToFunctionMap(builtInEnd);
        addToFunctionMap(builtInRead);
    }

    public void addToAssignmentNodeArray(AssignmentNode inputAssignmentNode) {
        assignmentNodeArray.add(inputAssignmentNode);
    }

    public void addToFunctionMap(FunctionNode inputFunctionNode) {
        functionMap.put(inputFunctionNode.getName(), inputFunctionNode);
    }

    public ArrayList<AssignmentNode> getAssignmentNodeArray() {
        return assignmentNodeArray;
    }

    public LinkedHashMap<String, FunctionNode> getFunctionMap() {
        return functionMap;
    }

    public boolean isAFunction(String inputString) {
        return functionMap.containsKey(inputString);
    }

    public String ToString() {
        String str = "";
        for (String key: functionMap.keySet()){
            str += functionMap.get(key).ToString() + "\n\n";
        }
        return str;
    }
}