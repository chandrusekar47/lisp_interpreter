import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Parser {
    private final Scanner scanner;
    private PrintStream outputStream;
    private final Consumer<TreeNode> parseTreeConsumer;
    private List<String> tokensProcessed;

    public Parser(Scanner scanner, OutputStream outputStream, Consumer<TreeNode> parseTreeConsumer) {
        this.scanner = scanner;
        this.outputStream = new PrintStream(outputStream);
        this.parseTreeConsumer = parseTreeConsumer;
        this.tokensProcessed = new ArrayList<>();
    }
// <start> := <Expr> <Start> | <Expr> eof
// <Expr> := atom | ( <List> )
// <List> := <Expr> <List> | <empty-string>

    public void start() throws IOException {
        moveToNextToken();
        do {
            TreeNode currentNode = new TreeNode();
            parseExpr(currentNode);
            parseTreeConsumer.accept(currentNode);
        } while (scanner.getCurrentToken().getTokenType() != TokenType.EOF);
    }

    private void moveToNextToken() throws IOException {
        if (scanner.getCurrentToken() != null) {
            tokensProcessed.add(scanner.getCurrentToken().getTokenValue().toString());
        }
        scanner.moveToNextToken();
    }

    private void parseExpr(TreeNode rootNode) throws IOException {
        if (scanner.getCurrentToken().isAtom()) {
            rootNode.setCellToken(scanner.getCurrentToken());
            moveToNextToken();
        } else if (scanner.getCurrentToken().getTokenType() == TokenType.OPEN_PARENTHESIS) {
            moveToNextToken();
            TreeNode currentNode = rootNode;
            while (scanner.getCurrentToken().getTokenType() != TokenType.CLOSE_PARENTHESIS) {
                parseExpr(currentNode.createEmptyLeftChild());
                currentNode = currentNode.createEmptyRightChild();
            }
            currentNode.setCellToken(Token.createNilToken());
            moveToNextToken();
        } else {
            String errorMessage;
            String exprBeforeErrorToken = String.join(" ", tokensProcessed);
            Token errorToken = scanner.getCurrentToken();
            if (errorToken.isEOF()) {
                errorMessage = tokensProcessed.isEmpty() ? "ERROR: No input given." : "ERROR: Reached end of input while parsing, expecting ) but could not find any.";
            } else {
                String errorTokenValue = errorToken.getTokenValue().toString();
                List<String> nextThreeTokens = grabNextThreeTokens();
                String exprAfterErrorToken = String.join(" ", nextThreeTokens);
                errorMessage = String.format("ERROR: Unexpected token %s at the following location: %n", errorTokenValue) +
                        String.format("%s ----> %s <---- %s%n", exprBeforeErrorToken, errorTokenValue, exprAfterErrorToken) +
                        "Expecting ( or an identifier or a number";
            }
            throw new RuntimeException(errorMessage);
        }
    }

    private List<String> grabNextThreeTokens() throws IOException {
        try {
            List<String> nextThreeTokens = new ArrayList<>();
            for (int i = 0; i < 3 && scanner.getCurrentToken().getTokenType() != TokenType.EOF; i++) {
                scanner.moveToNextToken();
                nextThreeTokens.add(scanner.getCurrentToken().getTokenValue().toString());
            }
            return nextThreeTokens;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
