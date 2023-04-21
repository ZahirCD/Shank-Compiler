package ShankInterpreter;

public class StringDataType extends InterpreterDataType {
    
    private String data;
    private boolean modifyType;
    private boolean hasTypeLimit;
    private int typeLimitFrom;
    private int typeLimitTo;

    public StringDataType(String inputData, boolean inputIsModifiable) {
        data = inputData;
        modifyType = inputIsModifiable;
    }

    public StringDataType(String inputData, boolean inputIsModifiable, int inputTypeLimitFrom, int inputTypeLimitTo) {
        data = inputData;
        modifyType = inputIsModifiable;
        typeLimitFrom = inputTypeLimitFrom;
        typeLimitTo = inputTypeLimitTo;
        hasTypeLimit = true;
    }

    public void FromString(String input) {
        
    }

    public String getData() {
        return data;
    }

    public int getTypeLimitFrom() {
        return typeLimitFrom;
    }

    public int getTypeLimitTo() {
        return typeLimitTo;
    }

    public boolean hasTypeLimit() {
        return hasTypeLimit;
    }

    public boolean modifyType() {
        return modifyType;
    }

    public void setData(String inputData) {
        if (modifyType == true) {
            if (hasTypeLimit == true) {
                if (inputData.length() >= typeLimitFrom && inputData.length() <= typeLimitTo)
                    data = inputData;
                else {
                    System.out.println("ERROR: Length of string " + inputData + " is outside of range for string variable with type limit.");
                    System.exit(0);
                }
            }
            else
                data = inputData;
        }
    }

    public void setTypeLimit(boolean inputHasTypeLimit) {
        hasTypeLimit = inputHasTypeLimit;
    }

    public String ToString() {
        return "StringDataType(" + data + ")";
    }

	
}