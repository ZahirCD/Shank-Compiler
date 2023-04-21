package ShankInterpreter;

import java.util.ArrayList;
import java.util.Scanner;

public class BuiltInRead extends FunctionNode {

    public BuiltInRead() {
        super("Read");
    }
    
    /*
     * method to read the input that is detected 
     * @param inputData : data that is being inputted 
     */
    public void execute(ArrayList<InterpreterDataType> inputData) {
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < inputData.size(); i++) {
            InterpreterDataType arg = inputData.get(i);
            if (!arg.modifyType()) {
                System.out.println("Error: Argument " + (i + 1) + " is non-changeable.");
                System.exit(1);
            }
            System.out.print("Enter value for argument " + (i + 1) + ": ");
            if (arg instanceof IntegerDataType) {
                ((IntegerDataType) arg).setData(scanner.nextInt());
            } else if (arg instanceof RealDataType) {
                ((RealDataType) arg).setData(scanner.nextFloat());
            } else if (arg instanceof StringDataType) {
                ((StringDataType) arg).setData(scanner.nextLine());
            } else if (arg instanceof CharacterDataType) {
                ((CharacterDataType) arg).setData(scanner.nextLine().charAt(0));
            } else if (arg instanceof BooleanDataType) {
                ((BooleanDataType) arg).setData(scanner.nextBoolean());
            } else {
                System.out.println("Error: Argument " + (i + 1) + " has an invalid data type.");
                System.exit(1);
            }
        }
        scanner.close();
    }

}