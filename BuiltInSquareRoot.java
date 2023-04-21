package ShankInterpreter;
import java.util.ArrayList;
import java.lang.Math;

public class BuiltInSquareRoot extends FunctionNode {

    public BuiltInSquareRoot() {
        super("SquareRoot");
    }
    
    /*
     * Checks the input if its square root 
     * @param inputData : the inputted data
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        if (isValidInput(inputData)) {
            RealDataType floatArg = (RealDataType) inputData.get(0);
            RealDataType resultArg = (RealDataType) inputData.get(1);
            float inputFloat = floatArg.getData();
            float result = (float) Math.sqrt(inputFloat);
            resultArg.setData(result);
            inputData.set(1, resultArg);
        } else {
            System.out.println("Error: Invalid arguments for SquareRoot function.");
            System.exit(1);
        }
    }
    
    /*
     * Checks if the input is valid 
     * @param inputData : the inputted data
     */
    private boolean isValidInput(ArrayList<InterpreterDataType> inputData) {
        if (inputData.size() != 2) {
            return false;
        }
        if (!(inputData.get(0) instanceof RealDataType && inputData.get(1) instanceof RealDataType)) {
            return false;
        }
        if (!inputData.get(1).modifyType()) {
            return false;
        }
        return true;
    }

}

