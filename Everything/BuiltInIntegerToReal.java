package ShankInterpreter;

import java.util.ArrayList;

public class BuiltInIntegerToReal extends FunctionNode {

    public BuiltInIntegerToReal() {
        super("IntegerToReal");
    }
    
    /*
     * checks input for integer to change to real 
     * @param inputData : the inputted data
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        if (isValidInput(inputData)) {
            IntegerDataType intArg = (IntegerDataType) inputData.get(0);
            int intValue = intArg.getData();
            float floatValue = intValue * (float) 1;
            RealDataType realArg = (RealDataType) inputData.get(1);
            realArg.setData(floatValue);
            inputData.set(1, realArg);
        } else {
            System.out.println("Error: Invalid arguments for IntegerToReal function.");
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
        if (!(inputData.get(0) instanceof IntegerDataType)) {
            return false;
        }
        if (!(inputData.get(1) instanceof RealDataType)) {
            return false;
        }
        if (!inputData.get(1).modifyType()) {
            return false;
        }
        return true;
    }

}