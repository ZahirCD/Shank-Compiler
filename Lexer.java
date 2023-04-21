package ShankInterpreter;
import java.util.HashMap;
import java.util.ArrayList;

public class Lexer {

    private int stringIndex;
    private String stringValue;
    private ArrayList<Token> tokenArray = new ArrayList<Token>();
    private int tokenArrayIndex = 0;
    private int inputStringLength;
    private int commentSwitch = 0;
    private int exceptionSwitch = 0;
    private int lineNumber = 1;
    private int spacesAtStartOfString;
    private double indentationLevel = 0;
    private double previousIndentationLevel = 0;

    private HashMap<String, Token.tokenType> knownWords = new HashMap<String, Token.tokenType>();

    public Lexer() {
        initializeKeys();
    }

    public void addLastLineDedents() {
        lineNumber++;
        for (int i = 0; i < previousIndentationLevel; i++) {
            tokenArray.add(new Token());
            tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.DEDENT);
            tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
            tokenArrayIndex++;
        }
    }

    public ArrayList<Token> getArray() {
        return tokenArray;
    }

    public void lex(String inputString) throws SyntaxErrorException {
        if (exceptionSwitch == 1) { //Terminates program if exception was thrown by previous execution of lex method
            System.exit(0);
        }
        stringIndex = 0;
        inputStringLength = inputString.length();
        if (commentSwitch != 1) {
            if (inputStringLength == 0) {
                checkIndentLevel(inputString);
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.ENDOFLINE);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                lineNumber++;
            }
            else {
                checkForComment(inputString);
                checkIndentLevel(inputString);
                startState(inputString);
                lineNumber++;
            }   
        }
        else {
            checkForComment(inputString);
            lineNumber++;
        }
    }

    public void printLexer() {
        for (int i = 0; i < tokenArrayIndex; i++) {
            if (tokenArray.get(i).getToken() != Token.tokenType.ENDOFLINE) {
                System.out.printf("%d: %s (%s)\n", tokenArray.get(i).getLineNumber(), tokenArray.get(i).getToken(), tokenArray.get(i).getValue());
            }
            else {
                System.out.printf("%d: %s\n", tokenArray.get(i).getLineNumber(), tokenArray.get(i).getToken());
            }
        }
    }

    private void checkForComment(String inputString) {
        if (commentSwitch == 1) { //Allows multiline commenting
            while (stringIndex < inputStringLength && inputString.charAt(stringIndex) != '}') {
                stringIndex++;
                if (stringIndex < inputStringLength && inputString.charAt(stringIndex) == '}') { 
                    commentSwitch = 0;
                }
            }
        }
    }

    private void checkIndentLevel(String inputString) throws SyntaxErrorException {
        spacesAtStartOfString = 0;
        while (stringIndex < inputStringLength && inputString.charAt(stringIndex) == ' ') {
            spacesAtStartOfString++;
            stringIndex++;
        }
        if (inputString.length() == 0) {
            indentationLevel = 0;
            if (indentationLevel < previousIndentationLevel) {
                for (int i = 0; i < (previousIndentationLevel - indentationLevel); i++) {
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.DEDENT);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                }
            }
            else if (indentationLevel > previousIndentationLevel) {
                for (int i = 0; i < (indentationLevel - previousIndentationLevel); i++) {
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.INDENT);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                }
            }
            previousIndentationLevel = indentationLevel;
        }
        else if (stringIndex < inputStringLength && inputString.charAt(stringIndex) != '{') { //If first char is open curly braces, line is a comment, and no check for indentation is necessary
            indentationLevel = Math.floor(spacesAtStartOfString/4);
            if (indentationLevel < previousIndentationLevel) {
                for (int i = 0; i < (previousIndentationLevel - indentationLevel); i++) {
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.DEDENT);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                }
            }
            else if (indentationLevel > previousIndentationLevel) {
                for (int i = 0; i < (indentationLevel - previousIndentationLevel); i++) {
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.INDENT);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                }
            }
            previousIndentationLevel = indentationLevel;
        }
    }

    private void createStringLiteral(String inputString) throws SyntaxErrorException {
        stringValue = "";
        while (stringIndex < inputStringLength && inputString.charAt(stringIndex) != '"') {
            stringValue += inputString.charAt(stringIndex);
            stringIndex++;
        }
        if (stringIndex >= inputStringLength) { 
                exceptionSwitch = 1;
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken((Token.tokenType.UNTERMINATEDSTRING));
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArray.get(tokenArrayIndex).setValue(stringValue);
                throw new SyntaxErrorException(tokenArray.get(tokenArrayIndex));
        }
        tokenArray.add(new Token());
        tokenArray.get(tokenArrayIndex).setValue(stringValue);
        if (stringValue.length() == 1) { 
            tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.CHARACTERLITERAL);
            tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
        }
        else {
            tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.STRINGLITERAL);
            tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
        }
        tokenArrayIndex++;
    }

    private void decimalState(String inputString) throws SyntaxErrorException {
        stringValue += inputString.charAt(stringIndex);
        stringIndex++;
        while (stringIndex < inputStringLength && Character.isDigit(inputString.charAt(stringIndex))) {
            stringValue += inputString.charAt(stringIndex);
            stringIndex++;
        }
        tokenArray.add(new Token());
        tokenArray.get(tokenArrayIndex).setValue(stringValue);
        tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.REAL);
        tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
        tokenArrayIndex++;
        startState(inputString);
    }

    private void identifierState(String inputString) throws SyntaxErrorException {
        while (stringIndex < inputStringLength && Character.isLetterOrDigit(inputString.charAt(stringIndex))) {
            stringValue += inputString.charAt(stringIndex);
            stringIndex++;
            if (knownWords.containsKey(stringValue)) {
                if (knownWords.get(stringValue) == Token.tokenType.MOD) {
                    break;
                }
            }
        }
        tokenArray.add(new Token());
        if (knownWords.containsKey(stringValue)) {
            tokenArray.get(tokenArrayIndex).setToken(knownWords.get(stringValue));
            tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
        }
        else {
            tokenArray.get(tokenArrayIndex).setValue(stringValue);
            tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.IDENTIFIER);
            tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
        }
        tokenArrayIndex++;
        startState(inputString);
    }

    private void initializeKeys() {
        knownWords.put("while", Token.tokenType.WHILE);
        knownWords.put("define", Token.tokenType.DEFINE);
        knownWords.put("array", Token.tokenType.ARRAY);
        knownWords.put("of", Token.tokenType.OF);
        knownWords.put("string", Token.tokenType.STRING);
        knownWords.put("constants", Token.tokenType.CONSTANTS);
        knownWords.put("variables", Token.tokenType.VARIABLES);
        knownWords.put("integer", Token.tokenType.INTEGER);
        knownWords.put("for", Token.tokenType.FOR);
        knownWords.put("from", Token.tokenType.FROM);
        knownWords.put("to", Token.tokenType.TO);
        knownWords.put("real", Token.tokenType.REAL);
        knownWords.put("boolean", Token.tokenType.BOOLEAN);
        knownWords.put("character", Token.tokenType.CHARACTER);
        knownWords.put("var", Token.tokenType.VAR);
        knownWords.put("if", Token.tokenType.IF);
        knownWords.put("then", Token.tokenType.THEN);
        knownWords.put("mod", Token.tokenType.MOD);
        knownWords.put("elsif", Token.tokenType.ELSIF);
        knownWords.put("else", Token.tokenType.ELSE);
        knownWords.put("repeat", Token.tokenType.REPEAT);
        knownWords.put("until", Token.tokenType.UNTIL);
        knownWords.put("not", Token.tokenType.NOT);
        knownWords.put("and", Token.tokenType.AND);
        knownWords.put("or", Token.tokenType.OR);
        knownWords.put("mod", Token.tokenType.MOD);
        knownWords.put("true", Token.tokenType.TRUE);
        knownWords.put("false", Token.tokenType.FALSE);
    }

    private void numberState(String inputString) throws SyntaxErrorException {
        while (stringIndex < inputStringLength && Character.isDigit(inputString.charAt(stringIndex))) {
            stringValue += inputString.charAt(stringIndex);
            stringIndex++;
        }
        if (stringIndex < inputStringLength && inputString.charAt(stringIndex) == '.') {
            decimalState(inputString);
        }
        else {
            tokenArray.add(new Token());
            tokenArray.get(tokenArrayIndex).setValue(stringValue);
            tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.INTEGER);
            tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
            tokenArrayIndex++;
            startState(inputString);
        }
    }

    private void searchPunctuation(String inputString) throws SyntaxErrorException {
        switch (inputString.charAt(stringIndex)) {
            case ' ':
                break;
            case ',':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.COMMA);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '=':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.COMPARISONEQUAL);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '+':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.PLUS);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '-':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.MINUS);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '/':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.DIVIDED);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case ')':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.RIGHTPARENTHESES);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '[':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.LEFTBRACKET);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case ']':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.RIGHTBRACKET);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case ';':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.SEMICOLON);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '(':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.LEFTPARENTHESES);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '*':
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.MULTIPLIED);
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArrayIndex++;
                break;
            case '"':
                stringIndex++;
                createStringLiteral(inputString);
                break;
            case '{': //Searches for closing curly brace in order to complete comment
                while (stringIndex < inputStringLength && inputString.charAt(stringIndex) != '}') {
                    stringIndex++;
                }
                if (stringIndex >= inputStringLength) {
                    commentSwitch = 1;
                }
                break;
            case '}':
                break;
            case ':':
                if (inputString.charAt(stringIndex+1) == '=') {
                    stringIndex++;
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.ASSIGNMENTEQUAL);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                    break;
                }
                else {
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.COLON);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                    break;
                }
            case '<':
                if (inputString.charAt(stringIndex+1) == '=') {
                    stringIndex++;
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.LESSTHANOREQUALTO);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                    break;
                }
                else if (inputString.charAt(stringIndex+1) == '>') {
                    stringIndex++;
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.NOTEQUAL);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                    break;
                }
                else {
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.LESSTHAN);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                    break;
                }
            case '>':
                if (inputString.charAt(stringIndex+1) == '=') {
                    stringIndex++;
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.GREATERTHANOREQUALTO);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                    break;
                }
                else {
                    tokenArray.add(new Token());
                    tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.GREATERTHAN);
                    tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                    tokenArrayIndex++;
                    break;
                }
            default: //If any character has gotten to this point, it is an invalid symbol
                stringValue += inputString.charAt(stringIndex);
                exceptionSwitch = 1;
                tokenArray.add(new Token());
                tokenArray.get(tokenArrayIndex).setToken((Token.tokenType.INVALIDSYMBOL));
                tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
                tokenArray.get(tokenArrayIndex).setValue(stringValue);
                throw new SyntaxErrorException(tokenArray.get(tokenArrayIndex));
        }
    }

    private void startState(String inputString) throws SyntaxErrorException {
        stringValue = "";
        if (stringIndex >= inputStringLength) {
            tokenArray.add(new Token());
            tokenArray.get(tokenArrayIndex).setToken(Token.tokenType.ENDOFLINE);
            tokenArray.get(tokenArrayIndex).setLineNumber(lineNumber);
            tokenArrayIndex++;
        }
        else if (Character.isLetter(inputString.charAt(stringIndex))) {
            identifierState(inputString);
        }
        else if (inputString.charAt(stringIndex) == '.') {
            decimalState(inputString);
        }
        else if (Character.isDigit(inputString.charAt(stringIndex))) {
            numberState(inputString);
        }
        else {
            searchPunctuation(inputString);
            stringIndex++;
            startState(inputString);
        }
    }
}