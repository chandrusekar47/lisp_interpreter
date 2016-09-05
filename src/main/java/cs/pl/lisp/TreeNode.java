package cs.pl.lisp;

public class TreeNode {
    private TreeNode leftChild;

    private TreeNode rightChild;

    private Token cellToken;

    public TreeNode(TreeNode leftChild, TreeNode rightChild, Token cellToken) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.cellToken = cellToken;
    }

    public TreeNode() {
        this(null, null, null);
    }

    public TreeNode createEmptyRightChild() {
        rightChild = new TreeNode();
        return rightChild;
    }

    public TreeNode createEmptyLeftChild() {
        leftChild = new TreeNode();
        return leftChild;
    }

    public String toString() {
        if (this.cellToken != null && (this.cellToken.isAtom() || this.cellToken.isNil())) {
            return this.cellToken.getTokenValue().toString();
        }
        if (leftChild != null && rightChild == null) {
            return leftChild.toString();
        }
        if (leftChild != null) {
            return "(" + leftChild.toString() + " . " + rightChild.toString() + ")";
        }
        return "";
    }

    public void setCellToken(Token cellToken) {
        this.cellToken = cellToken;
    }
}
