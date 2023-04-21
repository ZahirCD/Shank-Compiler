package ShankInterpreter;

import java.util.ArrayList;

public class ArrayDataType extends InterpreterDataType {
    
    public enum arrayDataType { REAL, INTEGER, CHARACTER, STRING, BOOLEAN;

	void set(int j, IntegerDataType integerDataAtIndex) {
		// TODO Auto-generated method stub
		
	} }
    private arrayDataType type;
    private ArrayList<InterpreterDataType> array = new ArrayList<InterpreterDataType>();
    private boolean modifyType;
    private int start = 0;
    private int end = 0;
    
    public ArrayDataType(arrayDataType inputArrayDataType, int inputStartIndex, int inputEndIndex, boolean inputIsChangeable) {
        type = inputArrayDataType;
        modifyType = inputIsChangeable;
        start = inputStartIndex;
        end = inputEndIndex;
        switch (type) { //Initializes default values for the specified array data type
            case REAL: 
                for (int i = 0; i < end - start; i++) {
                    RealDataType realData = new RealDataType(0, true, 0, 0);
                    array.add(realData);
                }
                break;
            case INTEGER: 
                for (int i = 0; i < end - start; i++) {
                    IntegerDataType integerData = new IntegerDataType(0, true);
                    array.add(integerData);
                }
                break;
            case BOOLEAN: 
                for (int i = 0; i < end - start; i++) {
                    BooleanDataType booleanData = new BooleanDataType(false, true);
                    array.add(booleanData);
                }
                break;
            case STRING: 
                for (int i = 0; i < end - start; i++) {
                    StringDataType stringData = new StringDataType("", true, 0, 0);
                    array.add(stringData);
                }
                break;
            case CHARACTER: 
                for (int i = 0; i < end - start; i++) {
                    CharacterDataType characterData = new CharacterDataType(' ', true);
                    array.add(characterData);
                }
                break;
            default :
                System.out.println("ERROR: Data type not provided for array.");
                System.exit(0);
        }
    }

    public ArrayDataType(ArrayList<InterpreterDataType> inputData, boolean inputIsChangeable) {
        array = inputData;
        modifyType = inputIsChangeable;
    }

    public void FromString(String input) {
        
    }

    public arrayDataType getArrayType() {
        return type;
    }

    public ArrayList<InterpreterDataType> getArray() {
        return array;
    }

    public InterpreterDataType getDataAtIndex(int inputIndex) {
        if (array.get(inputIndex) != null)
            return array.get(inputIndex);
        System.out.println("ERROR: Value at array index is null.");
        System.exit(0);
        return null;
    }

    public int getStartIndex() {
        return start;
    }

    public int getEndIndex() {
        return end;
    }

    public boolean isChangeable() {
        return modifyType;
    }

    public void setArray(arrayDataType arrayDataArrayList) {
        array = arrayDataArrayList;
    }

    public void setIndex(int inputIndex, InterpreterDataType inputData) { //Sets value at given index
        array.set(inputIndex, inputData);
    }

    public void setRange(int inputStartIndex, int inputEndIndex) {
        start = inputStartIndex;
        end = inputEndIndex;
    }

    public String ToString() {
        String str = "ArrayDataType(";
        for (int i = 0; i < array.size(); i++) {
            str += array.get(i) + ", ";
        }
        str += ")";
        return str;
    }

	@Override
	protected boolean modifyType() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setArray(ArrayList<InterpreterDataType> arrayDataArrayList) {
		// TODO Auto-generated method stub
		
	}
}