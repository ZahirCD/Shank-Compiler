package ShankInterpreter;
import java.util.ArrayList;

public class BuiltInEnd extends FunctionNode {

    public BuiltInEnd() {
        super("End");
    }

    public void execute(ArrayList<InterpreterDataType> inputData) {
        // Check that the input data contains exactly one argument
        if (inputData.size() != 1) {
            System.out.println("Error: End function takes exactly one argument.");
            System.exit(1);
        }

        // Check that the input argument is an array
        InterpreterDataType arg = inputData.get(0);
        if (!(arg instanceof ArrayDataType)) {
            System.out.println("Error: End function argument must be an array.");
            System.exit(1);
        }
        ArrayDataType inputArray = (ArrayDataType) arg;
        ArrayList<InterpreterDataType> arrayData = inputArray.getData();

        // Check that the array is not empty
        if (arrayData.isEmpty()) {
            System.out.println("Error: End function cannot be called on an empty array.");
            System.exit(1);
        }

        // Get the last element of the array
        InterpreterDataType lastIndex = arrayData.get(arrayData.size()-1);

        // Replace the input array argument with the last element of the array
        if (lastIndex instanceof IntegerDataType) {
            IntegerDataType intLastIndex = (IntegerDataType) lastIndex;
            inputData.set(0, intLastIndex);
        }
        else if (lastIndex instanceof RealDataType) {
            RealDataType realLastIndex = (RealDataType) lastIndex;
            inputData.set(0, realLastIndex);
        }
        else if (lastIndex instanceof StringDataType) {
            StringDataType stringLastIndex = (StringDataType) lastIndex;
            inputData.set(0, stringLastIndex);
        }
        else if (lastIndex instanceof CharacterDataType) {
            CharacterDataType charLastIndex = (CharacterDataType) lastIndex;
            inputData.set(0, charLastIndex);
        }
        else if (lastIndex instanceof BooleanDataType) {
            BooleanDataType boolLastIndex = (BooleanDataType) lastIndex;
            inputData.set(0, boolLastIndex);
        }
        else {
            System.out.println("Error: Incorrect data type given as argument in End function call.");
            System.exit(1); 
        }
    }
}

