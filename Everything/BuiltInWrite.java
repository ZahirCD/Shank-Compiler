package ShankInterpreter;
import java.util.ArrayList;

public class BuiltInWrite extends FunctionNode {

    public BuiltInWrite() {
        super("Write");
    }
    
    /*
     * method to write the input that is detected 
     * @param inputData : data that is being inputted 
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        System.out.print("Output: ");
        for (int i = 0; i < inputData.size(); i++) {
            InterpreterDataType arg = inputData.get(i);
            if (arg instanceof ArrayDataType) {
                ArrayDataType arrayArg = (ArrayDataType) arg;
                System.out.print("Array[");
                ArrayList<InterpreterDataType> arrayData = arrayArg.getData();
                for (int j = 0; j < arrayData.size(); j++) {
                    System.out.print(arrayData.get(j).toString());
                    if (j < arrayData.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print("] ");
            } else if (arg instanceof IntegerDataType) {
                IntegerDataType intArg = (IntegerDataType) arg;
                System.out.print(intArg.getData() + " ");
            } else if (arg instanceof RealDataType) {
                RealDataType realArg = (RealDataType) arg;
                System.out.print(realArg.getData() + " ");
            } else if (arg instanceof StringDataType) {
                StringDataType stringArg = (StringDataType) arg;
                System.out.print(stringArg.getData() + " ");
            } else if (arg instanceof CharacterDataType) {
                CharacterDataType charArg = (CharacterDataType) arg;
                System.out.print(charArg.getData() + " ");
            } else if (arg instanceof BooleanDataType) {
                BooleanDataType boolArg = (BooleanDataType) arg;
                System.out.print(boolArg.getData() + " ");
            } else {
                System.out.println("Error: Invalid argument data type in Write function call.");
                System.exit(1);
            }
        }
        System.out.println();
    }

}

