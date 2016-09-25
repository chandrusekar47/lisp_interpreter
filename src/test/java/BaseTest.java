public class BaseTest {
    protected TreeNode createAtom(int tokenValue) {
        return new TreeNode(null, null, new Token(tokenValue, TokenType.NUMERIC_ATOM));
    }

    protected TreeNode createNilNode() {
        return new TreeNode(null, null, Token.createNilToken());
    }
}
