package ShankInterpreter;

public class CharacterNode extends StatementNode {
    private char character;

    public CharacterNode(char inputChar) {
        character = inputChar;
    }

    public char getValue() {
        return character;
    }

    public String ToString() {
        return "CharacterNode(" + character + ")";
    }

	@Override
	public Node getRight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getLeft() {
		// TODO Auto-generated method stub
		return null;
	}
}
