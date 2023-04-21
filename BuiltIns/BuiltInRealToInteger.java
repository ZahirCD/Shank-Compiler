package ShankInterpreter;
import java.util.ArrayList;

public class BuiltInRealToInteger extends FunctionNode {

    public BuiltInRealToInteger() {
        super("RealToInteger");
    }
    
    /*
     * checks input for real to change to integer 
     * @param inputData : the inputted data
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        if (isValidInput(inputData)) {
            RealDataType realArg = (RealDataType) inputData.get(0);
            float realValue = realArg.getData();
            int intValue = (int) realValue;
            IntegerDataType intArg = (IntegerDataType) inputData.get(1);
            intArg.setData(intValue);
            inputData.set(1, intArg);
        } else {
            System.out.println("Error: Invalid arguments for RealToInteger function.");
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
        if (!(inputData.get(0) instanceof RealDataType)) {
            return false;
        }
        if (!(inputData.get(1) instanceof IntegerDataType)) {
            return false;
        }
        if (!inputData.get(1).modifyType()) {
            return false;
        }
        return true;
    }

}
