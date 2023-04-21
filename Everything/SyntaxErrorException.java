package ShankInterpreter;

public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(Token inputToken) {
        Token token = new Token();
        token = inputToken;
        System.out.printf("Line Number: %d / Token type: %s / Token value: %s, \n", token.getLineNumber(), token.getToken().toString(), token.getValue());
    }
}
