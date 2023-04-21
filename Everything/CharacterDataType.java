package ShankInterpreter;

public class CharacterDataType extends InterpreterDataType{
	
	private char data;
    private boolean modifyType;

    public CharacterDataType(char inputData, boolean modifyArrayType) {
        data = inputData;
        modifyType = modifyArrayType;
    }

    public void FromString(String input) {
        
    }
    
    /*
     * getter for data
     */
    public char getData() {
        return data;
    }
    
    /*
     * getter for type
     */
    public boolean getmodifyType() {
        return modifyType;
    }
    
    /*
     * setter for data
     */
    public void setData(char inputData) {
        if (modifyType == true)
            data = inputData;
    }
    
    /*
     * String literal
     */
    public String ToString() {
        return "CharacterDataType(" + data + ")";
    }

	@Override
	protected boolean modifyType() {
		// TODO Auto-generated method stub
		return false;
	}
}
