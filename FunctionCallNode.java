package ShankInterpreter;

import java.util.ArrayList;

public class FunctionCallNode extends StatementNode {
    
    private String functionName;
    private ArrayList<ParameterNode> parameterArray = new ArrayList<ParameterNode>();

    public FunctionCallNode(String inputName, ArrayList<ParameterNode> inputParameterArray) {
        functionName = inputName;
        parameterArray = inputParameterArray;
    }

    public String getName() {
        return functionName;
    }

    public ArrayList<ParameterNode> getParameterArray() {
        return parameterArray;
    }

    public String ToString() {
        String str = "\nFunctionCallNode(\n Name: " + functionName + "\n Parameters:\n";
        for (int i = 0; i < parameterArray.size() ; i++) {
            str += parameterArray.get(i).ToString() + "\n";
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