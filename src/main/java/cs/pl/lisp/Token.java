package cs.pl.lisp;

public class Token {
    public static final Token NIL = new Token(null, TokenType.NIL);
    private Object tokenValue;
    private TokenType tokenType;

    Token(Object tokenValue, TokenType tokenType) {
        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
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
}
