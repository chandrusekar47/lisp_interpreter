package cs.pl.lisp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class Parser {
    private final Scanner scanner;
    private PrintStream outputStream;

    public Parser(Scanner scanner, OutputStream outputStream) {
        this.scanner = scanner;
        this.outputStream = new PrintStream(outputStream);
    }
// <start> := <Expr> <Start> | <Expr> eof
// <Expr> := atom | ( <List> )
// <List> := <Expr> <List> | <empty-string>

    public void start() throws IOException {
        scanner.moveToNextToken();
        do {
            TreeNode currentNode = new TreeNode();
            parseExpr(currentNode);
            outputStream.println(currentNode.toString());
        } while (scanner.getCurrentToken().getTokenType() != TokenType.EOF);
    }

    private void parseExpr(TreeNode rootNode) throws IOException {
        if (scanner.getCurrentToken().isAtom()) {
            rootNode.setCellToken(scanner.getCurrentToken());
            scanner.moveToNextToken();
        } else if (scanner.getCurrentToken().getTokenType() == TokenType.OPEN_PARENTHESIS) {
            scanner.moveToNextToken();
            TreeNode currentNode = rootNode;
            while (scanner.getCurrentToken().getTokenType() != TokenType.CLOSE_PARENTHESIS) {
                parseExpr(currentNode.createEmptyLeftChild());
                currentNode = currentNode.createEmptyRightChild();
            }
            currentNode.setCellToken(Token.createNilToken());
            scanner.moveToNextToken();
        } else {
            System.out.printf("Error");
        }
    }

}
