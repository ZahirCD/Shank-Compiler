package ShankInterpreter;
import java.util.ArrayList;

public class BuiltInLeft extends FunctionNode {

    public BuiltInLeft() {
        super("Left");
    }
    
    /*
     * Checks where the input is 
     * @param inputData : the inputted data
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        if (isValidInput(inputData)) {
            StringDataType stringArg = (StringDataType) inputData.get(0);
            IntegerDataType lengthArg = (IntegerDataType) inputData.get(1);
            StringDataType resultArg = (StringDataType) inputData.get(2);
            String inputString = stringArg.getData();
            int length = lengthArg.getData();
            String resultString = inputString.substring(0, length);
            resultArg.setData(resultString);
            inputData.set(2, resultArg);
        } else {
            System.out.println("Error: Invalid arguments for Left function.");
            System.exit(1);
        }
    }
    
    /*
     * Checks if the input is valid 
     * @param inputData : the inputted data
     */
    private boolean isValidInput(ArrayList<InterpreterDataType> inputData) {
        if (inputData.size() != 3) {
            return false;
        }
        if (!(inputData.get(0) instanceof StringDataType && inputData.get(1) instanceof IntegerDataType && inputData.get(2) instanceof StringDataType)) {
            return false;
        }
        if (!inputData.get(2).modifyType()) {
            return false;
        }
        return true;
    }

}
