package cs.pl.lisp;

import java.io.IOException;

public class Parser {
    private final Scanner scanner;
    private final TreeNode rootNode;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        rootNode = new TreeNode();
    }
// <start> := <Expr> <Start> | <Expr> eof
// <Expr> := atom | ( <List> )
// <List> := <Expr> <List> | <empty-string>

    public void start() throws IOException {
        TreeNode currentNode = this.rootNode;
        do {
            parseExpr(currentNode);
            currentNode = this.rootNode.createEmptyRightChild();
        } while (scanner.getCurrentToken().getTokenType() != TokenType.EOF);
    }

    private void parseExpr(TreeNode currentExprNode) throws IOException {
        if (scanner.getCurrentToken().isAtom()) {
            currentExprNode.updateLeftChild(scanner.getCurrentToken());
            scanner.moveToNextToken();
        } else if (scanner.getCurrentToken().getTokenType() == TokenType.OPEN_PARENTHESIS) {
            scanner.moveToNextToken();
            while (scanner.getCurrentToken().getTokenType() != TokenType.CLOSE_PARENTHESIS) {
                parseExpr(rootNode.createEmptyRightChild());
            }
            scanner.moveToNextToken();
        } else {
            System.out.printf("Error");
        }
    }

    public TreeNode getTree() {
        return rootNode;
    }

}
