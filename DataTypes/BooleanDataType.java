package ShankInterpreter;

public class BooleanDataType extends InterpreterDataType {
    
    private boolean data;
    private boolean modifyType;

    public BooleanDataType(boolean inputData, boolean modifyDataType) {
        data = inputData;
        modifyType = modifyDataType;
    }

    public void FromString(String input) {
        
    }
    
    /*
     * getter for data
     */
    public boolean getData() {
        return data;
    }
    
    /*
     * getter for modifiable type 
     */
    public boolean getDataType() {
        return modifyType;
    }
    
    /*
     * Setter for data
     */
    public void setData(Boolean inputData) {
        if (modifyType == true)
            data = inputData;
    }
    
    /*
     * String literal
     */
    public String ToString() {
        return "BooleanDataType(" + data + ")";
    }

	@Override
	protected boolean modifyType() {
		// TODO Auto-generated method stub
		return false;
	}

}
