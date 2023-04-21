package ShankInterpreter;
import java.util.*;

public abstract class StatementNode extends Node {
	public String toString() {
		return null;
	}
	
	//getter 
	public Token getToken() {
		return null;
	}

	@Override
	public abstract Node getRight();

	@Override
	public abstract Node getLeft();

	
}
