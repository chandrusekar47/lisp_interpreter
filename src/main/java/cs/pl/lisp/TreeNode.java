package cs.pl.lisp;

public class TreeNode {
    private TreeNode leftChild;

    private TreeNode rightChild;

    private Token cellToken = new Token(null, TokenType.NIL);

    public TreeNode(TreeNode leftChild, TreeNode rightChild, Token cellToken) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.cellToken = cellToken;
    }

    public TreeNode() {
    }

    public Token getCellToken() {
        return cellToken;
    }

    public TreeNode getLeftChild() {
        return leftChild;
    }

    public TreeNode getRightChild() {
        return rightChild;
    }

    public void updateLeftChild(Token currentToken) {
        this.leftChild = new TreeNode();
        this.leftChild.cellToken = currentToken;
    }

    public TreeNode createEmptyRightChild() {
        rightChild = new TreeNode();
        return rightChild;
    }

    public String toString() {
        if (this.cellToken != null && (this.cellToken.isAtom() || this.cellToken.isNil())) {
            return this.cellToken.getTokenValue().toString();
        }
        return "(" + leftChild.toString() + " . " + rightChild.toString() + ")";
    }

}
