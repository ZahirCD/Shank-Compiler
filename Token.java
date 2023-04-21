package ShankInterpreter;

public class Token {
    public enum tokenType {
    	IDENTIFIER, ENDOFLINE, WHILE, DEFINE, ARRAY, OF, STRING, CONSTANTS, VARIABLES, INTEGER, FOR, FROM, TO, MULTIPLIED,
        REAL, BOOLEAN, CHARACTER, VAR, IF, THEN, MOD, ELSIF, ELSE, REPEAT, UNTIL, NOT, AND, OR, COMMA, STRINGLITERAL,  
        COMPARISONEQUAL, PLUS, CHARACTERLITERAL, INVALIDSYMBOL, UNTERMINATEDSTRING, SPACE, DEDENT, INDENT, TRUE, FALSE,
        MINUS, DIVIDED, RIGHTPARENTHESES, LEFTBRACKET, RIGHTBRACKET, LEFTCURLYBRACE, RIGHTCURLYBRACE, QUOTATION, SEMICOLON,
        COLON, ASSIGNMENTEQUAL, LESSTHANOREQUALTO, NOTEQUAL, LESSTHAN, GREATERTHANOREQUALTO, GREATERTHAN, LEFTPARENTHESES, 
    }

    private tokenType type;
    private String value;
    private int lineNumber;

    public Token() {
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public tokenType getToken() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setLineNumber(int inputLineNumber) {
        lineNumber = inputLineNumber;
    }

    public void setToken(tokenType inputToken) {
        type = inputToken;
    }

    public void setValue(String inputValue) {
        value = inputValue;
    }
}
