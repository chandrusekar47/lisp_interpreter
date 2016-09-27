import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TreeNodeIntegrationTest {
    @Test
    public void shouldEvaluateBinaryFunction() throws Exception {
        parseExpression("(PLUS 1 2)", treeNode -> assertThat(treeNode.eval().getCellToken().getTokenValue(), is(3)));
    }

    @Test
    public void shouldEvaluateCarFunction() throws Exception {
        parseExpression("(CAR (CONS 1 2))", treeNode -> assertThat(treeNode.eval().getCellToken().getTokenValue(), is(1)));
    }

    @Test
    public void shouldEvaluateCdrFunction() throws Exception {
        parseExpression("(CDR (CONS 1 (QUOTE (2))) )", treeNode -> assertThat(treeNode.eval().toListExpr(), is("(2)")));
    }

    @Test
    public void shouldEvaluateAllValidExpressions() throws Exception {
        parseExpression("5", treeNode -> assertThat(treeNode.eval().toListExpr(), is("5")));
        parseExpression("T", treeNode -> assertThat(treeNode.eval().toListExpr(), is("T")));
        parseExpression("NIL", treeNode -> assertThat(treeNode.eval().toListExpr(), is("NIL")));
        parseExpression("(PLUS 1 2)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("3")));
        parseExpression("(PLUS (MINUS 4 1) (TIMES 2 2))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("7")));
        parseExpression("(EQ T T)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("T")));
        parseExpression("(EQ 1 1)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("T")));
        parseExpression("(EQ 1 2)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("NIL")));
        parseExpression("(EQ (PLUS 1 1) (TIMES 1 2))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("T")));
        parseExpression("(ATOM 1)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("T")));
        parseExpression("(ATOM NIL)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("T")));
        parseExpression("(ATOM (PLUS 2 3))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("T")));
        parseExpression("(CAR (CONS 1 2))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("1")));
        parseExpression("(CDR (CONS 1 2))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("2")));
        parseExpression("(CONS (PLUS 1 2) (LESS 4 1))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("(3)")));
        parseExpression("(CONS (LESS 4 1) (PLUS 1 2))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("(NIL . 3)")));
        parseExpression("(CONS 1 2)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("(1 . 2)")));
        parseExpression("(QUOTE 1)", treeNode -> assertThat(treeNode.eval().toListExpr(), is("1")));
        parseExpression("(QUOTE (PLUS 1 2))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("(PLUS 1 2)")));
        parseExpression("(COND ((PLUS 1 1) 99) (ASDF 82))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("99")));
        parseExpression("(COND ((PLUS 1 1) (MINUS 1 1)) (ASDF 82))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("0")));
        parseExpression("(COND ((EQ 1 2) 99) ((LESS 1 2) (QUOTE 82)))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("82")));
        parseExpression("(CONS 2 (CONS 3 (CONS 4 5)))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("(2 3 4 . 5)")));
        parseExpression("(COND ((CONS 1 1) (MINUS 1 1)) (ASDF 82))", treeNode -> assertThat(treeNode.eval().toListExpr(), is("0")));
    }

    @Test
    public void shouldHandleAllInvalidExpressions() throws Exception {
        assertIncorrectExpression("(5)", "Unable to evaluate list: (5). Unknown operation: 5");
        assertIncorrectExpression("(T)", "Unable to evaluate list: (T). Unknown operation: T");
        assertIncorrectExpression("(NIL)", "Unable to evaluate list: (NIL). Unknown operation: NIL");
        assertIncorrectExpression("(ASDF 1 2)", "Unable to evaluate list: (ASDF 1 2). Unknown operation: ASDF");
        assertIncorrectExpression("(PLUS 1 2 3)", "Incorrect number of arguments: (PLUS 1 2 3). PLUS operation expects 2 arguments, but found 3 arguments");
        assertIncorrectExpression("(TIMES 1)", "Incorrect number of arguments: (TIMES 1). TIMES operation expects 2 arguments, but found 1 arguments");
        assertIncorrectExpression("(MINUS (PLUS 1) 2)", "Incorrect number of arguments: (PLUS 1). PLUS operation expects 2 arguments, but found 1 arguments");
        assertIncorrectExpression("(LESS 2 (PLUS 1))", "Incorrect number of arguments: (PLUS 1). PLUS operation expects 2 arguments, but found 1 arguments");
        assertIncorrectExpression("(GREATER 2 (ADSD 1))", "Unable to evaluate list: (ADSD 1). Unknown operation: ADSD");
        assertIncorrectExpression("(PLUS (LESS 1 4) (TIMES 2 2))", "Unexpected arguments found. PLUS operation expects 2 numeric atoms as arguments, actual arguments were 1st arg: T, 2nd arg: 4");
        assertIncorrectExpression("(EQ 1)", "Incorrect number of arguments: (EQ 1). EQ operation expects 2 arguments, but found 1 arguments");
        assertIncorrectExpression("(EQ 1 2 3)", "Incorrect number of arguments: (EQ 1 2 3). EQ operation expects 2 arguments, but found 3 arguments");
        assertIncorrectExpression("(ATOM 1 2)", "Incorrect number of arguments: (ATOM 1 2). ATOM operation expects 1 argument, but found 2 arguments");
        assertIncorrectExpression("(ATOM)", "Incorrect number of arguments: (ATOM). ATOM operation expects 1 argument, but found 0 arguments");
        assertIncorrectExpression("(ATOM (PLUS 1))", "Incorrect number of arguments: (PLUS 1). PLUS operation expects 2 arguments, but found 1 arguments");
        assertIncorrectExpression("(CONS (PLUS 1))", "Incorrect number of arguments: (CONS (PLUS 1)). CONS operation expects 2 arguments, but found 1 arguments");
        assertIncorrectExpression("(QUOTE 1 2)", "Incorrect number of arguments: (QUOTE 1 2). QUOTE operation expects 1 argument, but found 2 arguments");
        assertIncorrectExpression("(COND (T T) (2) 1)", "Invalid expression: (COND (T T) (2) 1). All arguments to the COND expression must be a list of length 2. Found invalid argument (2) at position 2");
        assertIncorrectExpression("(COND (T T) (2 3) (1 2 3))", "Invalid expression: (COND (T T) (2 3) (1 2 3)). All arguments to the COND expression must be a list of length 2. Found invalid argument (1 2 3) at position 3");
        assertIncorrectExpression("(COND)", "Incorrect number of arguments: (COND). COND operation expects at least 1 argument, but found none");
        assertIncorrectExpression("(COND (NIL BLAH) (NIL BLEH))", "Invalid expression: (COND (NIL BLAH) (NIL BLEH)), All expressions in COND evaluated to NIL. At least one should evaluate to not NIL");
        assertIncorrectExpression("(COND ((PLUS) 2) (NIL BLEH))", "Incorrect number of arguments: (PLUS). PLUS operation expects 2 arguments, but found 0 arguments");
        assertIncorrectExpression("(COND (T (PLUS)) (NIL BLEH))", "Incorrect number of arguments: (PLUS). PLUS operation expects 2 arguments, but found 0 arguments");
        assertIncorrectExpression("(PLUS ((1)) 2)", "Unable to evaluate list: ((1)). Unknown operation: (1)");
        assertIncorrectExpression("(PLUS (()) 2)", "Unable to evaluate list: (NIL). Unknown operation: NIL");
        assertIncorrectExpression("((PLUS 1 2))", "Unable to evaluate list: ((PLUS 1 2)). Unknown operation: (PLUS 1 2)");
        assertIncorrectExpression("(CAR 3)", "Invalid expression: (CAR 3). CAR expects the argument to be an S-expr and not an atom: 3");
        assertIncorrectExpression("(CDR 3)", "Invalid expression: (CDR 3). CDR expects the argument to be an S-expr and not an atom: 3");
    }

    private void assertIncorrectExpression(String expression, String errorMessage) throws IOException {
        try {
            parseExpression(expression, TreeNode::eval);
            fail();
        } catch (EvaluationException ex) {
            assertThat(ex.getMessage(), is(errorMessage));
        }
    }

    private void parseExpression(String expression, Consumer<TreeNode> treeNodeConsumer) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new Parser(new Scanner(new ByteArrayInputStream(expression.getBytes())), outputStream, treeNodeConsumer).start();
    }
}