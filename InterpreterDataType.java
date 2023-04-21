package ShankInterpreter;

public abstract class InterpreterDataType {
	
	public abstract String ToString();
	public abstract void FromString(String input);
	protected abstract boolean modifyType();
	
}
