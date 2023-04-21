package ShankInterpreter;
import java.util.ArrayList;

public class BuiltInStart extends FunctionNode {
    
    public BuiltInStart() {
        super("Start");
    }

    public void execute(ArrayList<InterpreterDataType> inputData) {
        // Check if the input arguments are valid
        if (inputData.size() != 1 || !(inputData.get(0) instanceof ArrayDataType) || !inputData.get(0).modifyType()) {
            System.out.println("Error: Incorrect arguments for Start function.");
            System.exit(0);
        }
        
        // Get the first element of the input array
        InterpreterDataType firstElement = inputData.get(0);
        if (firstElement instanceof ArrayDataType) {
            System.out.println("Error: Cannot get the start index of a nested array.");
            System.exit(1);
        }
        
        // Get the data type of the first element
        String dataType = firstElement.getClass().getSimpleName();
        switch (dataType) {
            case "IntegerDataType":
                IntegerDataType intStartIndex = new IntegerDataType();
                intStartIndex.setData(((IntegerDataType) firstElement).getData());
                inputData.set(0, intStartIndex);
                break;
            case "RealDataType":
                RealDataType realStartIndex = new RealDataType(0, false);
                realStartIndex.setData(((RealDataType) firstElement).getData());
                inputData.set(0, realStartIndex);
                break;
            case "StringDataType":
                StringDataType stringStartIndex = new StringDataType(dataType, false);
                stringStartIndex.setData(((StringDataType) firstElement).getData());
                inputData.set(0, stringStartIndex);
                break;
            case "CharacterDataType":
                CharacterDataType charStartIndex = new CharacterDataType((char) 0, false);
                charStartIndex.setData(((CharacterDataType) firstElement).getData());
                inputData.set(0, charStartIndex);
                break;
            case "BooleanDataType":
                BooleanDataType boolStartIndex = new BooleanDataType(false, false);
                boolStartIndex.setData(((BooleanDataType) firstElement).getData());
                inputData.set(0, boolStartIndex);
                break;
            default:
                System.out.println("Error: Incorrect data type given as argument in Start function call.");
                System.exit(1);
        }
    }
}

