package ShankInterpreter;
import java.util.ArrayList;

public class BuiltInSubstring extends FunctionNode {

    public BuiltInSubstring() {
        super("Substring");
    }
    
    /*
     * Checks where the input is 
     * @param inputData : the inputted data
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        if (isValidInput(inputData)) {
            StringDataType stringArg = (StringDataType) inputData.get(0);
            IntegerDataType startIndexArg = (IntegerDataType) inputData.get(1);
            IntegerDataType lengthArg = (IntegerDataType) inputData.get(2);
            StringDataType resultArg = (StringDataType) inputData.get(3);
            String inputString = stringArg.getData();
            int startIndex = startIndexArg.getData();
            int length = lengthArg.getData();
            String resultString = inputString.substring(startIndex, startIndex + length);
            resultArg.setData(resultString);
            inputData.set(3, resultArg);
        } else {
            System.out.println("Error: Invalid arguments for Substring function.");
            System.exit(1);
        }
    }
    
    /*
     * Checks if the input is valid 
     * @param inputData : the inputted data
     */
    private boolean isValidInput(ArrayList<InterpreterDataType> inputData) {
        if (inputData.size() != 4) {
            return false;
        }
        if (!(inputData.get(0) instanceof StringDataType && inputData.get(1) instanceof IntegerDataType && inputData.get(2) instanceof IntegerDataType && inputData.get(3) instanceof StringDataType)) {
            return false;
        }
        if (!inputData.get(3).modifyType()) {
            return false;
        }
        return true;
    }

}

