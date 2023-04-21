package ShankInterpreter;

public class RealDataType extends InterpreterDataType {
    
    private float data;
    private boolean modifyType;
    private boolean hasTypeLimit;
    private float typeLimitFrom;
    private float typeLimitTo;

    public RealDataType(float inputData, boolean inputIsModifiable) {
        data = inputData;
        modifyType = inputIsModifiable;
    }

    public RealDataType(float inputData, boolean inputIsModifiable, float inputTypeLimitFrom, float inputTypeLimitTo) {
        data = inputData;
        modifyType = inputIsModifiable;
        typeLimitFrom = inputTypeLimitFrom;
        typeLimitTo = inputTypeLimitTo;
        hasTypeLimit = true;
    }

    public void FromString(String input) {
        
    }

    public float getData() {
        return data;
    }

    public float getTypeLimitFrom() {
        return typeLimitFrom;
    }

    public float getTypeLimitTo() {
        return typeLimitTo;
    }

    public boolean hasTypeLimit() {
        return hasTypeLimit;
    }

    public boolean modifyType() {
        return modifyType;
    }

    public void setData(float inputData) {
        if (modifyType == true) {
            if (hasTypeLimit == true) {
                if (inputData >= typeLimitFrom && inputData <= typeLimitTo)
                    data = inputData;
                else {
                    System.out.println("ERROR: " + inputData + " is outside of range for real variable with type limit.");
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
        return "RealDataType(" + data + ")";
    }
}
