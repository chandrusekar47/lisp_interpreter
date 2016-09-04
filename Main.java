import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;
import static java.lang.String.format;

enum TokenType {
    LITERAL_ATOM,
    NUMERIC_ATOM,
    OPEN_PARANTHESIS,
    CLOSE_PARANTHESIS,
    EOF,
    ERROR
}

class Token {
    private Object tokenValue;
    private TokenType tokenType;

    Token(Object tokenValue, TokenType tokenType) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
    }

    Object getTokenValue() {
        return tokenValue;
    }

    TokenType getTokenType() {
        return tokenType;
    }
}


public class Main {

    private static Integer lookaheadCharacter = null;
    public static InputStream inputStream = System.in;

    public static void main(String[] args) throws IOException {
        List<String> literalTokens = new ArrayList<>();
        int sumOfNumericTokens = 0;
        int countOfNumericTokens = 0;
        int countOfClosingParenthesis = 0;
        int countOfOpeningParenthesis = 0;
        Token currentToken;
        while (true) {
            currentToken = getNextToken();
            if (currentToken.getTokenType() == TokenType.EOF)
                break;
            if (currentToken.getTokenType() == TokenType.ERROR) {
                System.out.println(format("ERROR: Invalid token %s", currentToken.getTokenValue()));
                System.exit(-1);
            }
            if (currentToken.getTokenType() == TokenType.CLOSE_PARANTHESIS) countOfClosingParenthesis++;
            if (currentToken.getTokenType() == TokenType.OPEN_PARANTHESIS) countOfOpeningParenthesis++;
            if (currentToken.getTokenType() == TokenType.LITERAL_ATOM)
                literalTokens.add(currentToken.getTokenValue().toString());
            if (currentToken.getTokenType() == TokenType.NUMERIC_ATOM) {
                sumOfNumericTokens += (Integer) currentToken.getTokenValue();
                countOfNumericTokens++;
            }
        }
        System.out.println(format("LITERAL ATOMS: %d, %s", literalTokens.size(), String.join(", ", literalTokens)));
        System.out.println(format("NUMERIC ATOMS: %d, %d", countOfNumericTokens, sumOfNumericTokens));
        System.out.println(format("OPEN PARENTHESES: %d", countOfOpeningParenthesis));
        System.out.println(format("CLOSING PARENTHESES: %d", countOfClosingParenthesis));
    }

    public static Token getNextToken() throws IOException {
        StringBuilder tokenBuffer = new StringBuilder();
        if (lookaheadCharacter != null) {
            if (lookaheadCharacter == '(') {
                lookaheadCharacter = null;
                return new Token('(', TokenType.OPEN_PARANTHESIS);
            } else if (lookaheadCharacter == ')') {
                lookaheadCharacter = null;
                return new Token(')', TokenType.CLOSE_PARANTHESIS);
            } else if (lookaheadCharacter == -1) {
                lookaheadCharacter = null;
                return new Token(null, TokenType.EOF);
            }
        }
        while (true) {
            int currentCharacter = inputStream.read();
            if (currentCharacter == -1) {
                if (tokenBuffer.length() == 0) {
                    return new Token(null, TokenType.EOF);
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
                    return new Token((char) currentCharacter, currentCharacter == '(' ? TokenType.OPEN_PARANTHESIS : TokenType.CLOSE_PARANTHESIS);
                }
                lookaheadCharacter = currentCharacter;
                break;
            } else {
                tokenBuffer.append((char) currentCharacter);
            }
        }
        if (isNumber(tokenBuffer)) {
            return new Token(Integer.parseInt(tokenBuffer.toString()), TokenType.NUMERIC_ATOM);
        }
        if (isDigit(tokenBuffer.charAt(0))) {
            return new Token(tokenBuffer.toString(), TokenType.ERROR);
        }
        return new Token(tokenBuffer.toString(), TokenType.LITERAL_ATOM);
    }

    private static boolean isNumber(StringBuilder tokenBuffer) {
        return tokenBuffer.toString().matches("^\\d+$");
    }
}
