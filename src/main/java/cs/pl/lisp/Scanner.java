package cs.pl.lisp;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

public class Scanner {
    private Integer lookaheadCharacter = null;
    private InputStream inputStream = System.in;
    private Token currentToken;

    public Scanner(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Scanner moveToNextToken() throws IOException {
        StringBuilder tokenBuffer = new StringBuilder();
        if (lookaheadCharacter != null) {
            if (lookaheadCharacter == '(') {
                lookaheadCharacter = null;
                this.currentToken = new Token('(', TokenType.OPEN_PARENTHESIS);
                return this;
            } else if (lookaheadCharacter == ')') {
                lookaheadCharacter = null;
                this.currentToken = new Token(')', TokenType.CLOSE_PARENTHESIS);
                return this;
            } else if (lookaheadCharacter == -1) {
                lookaheadCharacter = null;
                this.currentToken = Token.createEOFToken();
                return this;
            }
        }
        while (true) {
            int currentCharacter = inputStream.read();
            if (currentCharacter == -1) {
                if (tokenBuffer.length() == 0) {
                    this.currentToken = Token.createEOFToken();
                    return this;
                }
                break;
            }
            if (isWhitespace(currentCharacter)) {
                if (tokenBuffer.length() != 0) {
                    break;
                }
            } else if (currentCharacter == '(' || currentCharacter == ')') {
                if (tokenBuffer.length() == 0) {
                    lookaheadCharacter = null;
                    this.currentToken = new Token((char) currentCharacter, currentCharacter == '(' ? TokenType.OPEN_PARENTHESIS : TokenType.CLOSE_PARENTHESIS);
                    return this;
                }
                lookaheadCharacter = currentCharacter;
                break;
            } else {
                tokenBuffer.append((char) currentCharacter);
            }
        }
        if (isNumber(tokenBuffer)) {
            this.currentToken = new Token(Integer.parseInt(tokenBuffer.toString()), TokenType.NUMERIC_ATOM);
            return this;
        }
        if (isDigit(tokenBuffer.charAt(0))) {
            throw new IllegalArgumentException(String.format("ERROR: Invalid token %s", tokenBuffer.toString()));
        }
        this.currentToken = new Token(tokenBuffer.toString(), TokenType.LITERAL_ATOM);
        return this;
    }

    public Token getCurrentToken() {
        return this.currentToken;
    }

    private boolean isNumber(StringBuilder tokenBuffer) {
        return tokenBuffer.toString().matches("^\\d+$");
    }
}
