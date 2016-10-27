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

    static Token createTToken() {
        return new Token("T", TokenType.LITERAL_ATOM);
    }

    public boolean isAtom() {
        return this.tokenType == TokenType.LITERAL_ATOM || this.tokenType == TokenType.NUMERIC_ATOM;
    }

    public boolean isNumeric() {
        return this.tokenType == TokenType.NUMERIC_ATOM;
    }

    public boolean isValidIdentifier() {
        return this.tokenValue != null && this.tokenType == TokenType.LITERAL_ATOM && !TreeNode.RESERVED_LITERALS.contains(tokenValue.toString());
    }

    public boolean isNil() {
        return this.tokenType == TokenType.NIL || "NIL".equals(this.tokenValue);
    }

    public Object getTokenValue() {
        return tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public static Token createNilToken() {
        return new Token("NIL", TokenType.NIL);
    }

    public boolean isEOF() {
        return this.tokenType == TokenType.EOF;
    }

    public boolean isTrue() {
        return tokenType == TokenType.LITERAL_ATOM && tokenValue.equals("T");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (tokenValue != null ? !tokenValue.equals(token.tokenValue) : token.tokenValue != null) return false;
        return tokenType == token.tokenType;

    }

    @Override
    public int hashCode() {
        int result = tokenValue != null ? tokenValue.hashCode() : 0;
        result = 31 * result + (tokenType != null ? tokenType.hashCode() : 0);
        return result;
    }
}
