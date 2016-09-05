package cs.pl.lisp;

public class Token {
    private Object tokenValue;
    private TokenType tokenType;

    Token(Object tokenValue, TokenType tokenType) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
    }

    static Token createEOFToken() {
        return new Token("", TokenType.EOF);
    }

    public boolean isAtom() {
        return this.tokenType == TokenType.LITERAL_ATOM || this.tokenType == TokenType.NUMERIC_ATOM;
    }

    public boolean isNil() {
        return this.tokenType == TokenType.NIL;
    }

    public Object getTokenValue() {
        return this.tokenType == TokenType.NIL ? "NIL" : tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public static Token createNilToken() {
        return new Token(null, TokenType.NIL);
    }

    public boolean isEOF() {
        return this.tokenType == TokenType.EOF;
    }
}
