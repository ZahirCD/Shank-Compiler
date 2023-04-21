package ShankInterpreter;
import java.util.ArrayList;
import java.util.Random;

public class BuiltInGetRandom extends FunctionNode {

    public BuiltInGetRandom() {
        super("GetRandom");
    }
    
    /*
     * Checks the input if its get random 
     * @param inputData : the inputted data
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        if (isValidInput(inputData)) {
            Random random = new Random();
            IntegerDataType resultArg = (IntegerDataType) inputData.get(0);
            int result = random.nextInt(100); // Returns random number between 0 and 99
            resultArg.setData(result);
            inputData.set(0, resultArg);
        } else {
            System.out.println("Error: Invalid arguments for GetRandom function.");
            System.exit(1);
        }
    }
    
    /*
     * Checks if the input is valid 
     * @param inputData : the inputted data
     */
    private boolean isValidInput(ArrayList<InterpreterDataType> inputData) {
        if (inputData.size() != 1) {
            return false;
        }
        if (!(inputData.get(0) instanceof IntegerDataType)) {
            return false;
        }
        if (!inputData.get(0).modifyType()) {
            return false;
        }
        return true;
    }

}

