import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TreeNodeIntegrationTest {
    @Test
    public void shouldEvaluateBinaryFunction() throws Exception {
        parseExpression("(PLUS 1 2)", treeNode -> assertThat(treeNode.eval(new HashMap<>(), emptyList()).getCellToken().getTokenValue(), is(3)));
    }

    @Test
    public void shouldEvaluateCarFunction() throws Exception {
        parseExpression("(CAR (CONS 1 2))", treeNode -> assertThat(treeNode.eval(new HashMap<>(), emptyList()).getCellToken().getTokenValue(), is(1)));
    }

    @Test
    public void shouldEvaluateCdrFunction() throws Exception {
        assertListExpression("(CDR (CONS 1 (QUOTE (2))) )", "(2)");
    }

    @Test
    public void shouldEvaluateAllValidExpressions() throws Exception {
        assertListExpression("5", "5");
        assertListExpression("T", "T");
        assertListExpression("NIL", "NIL");
        assertListExpression("(PLUS 1 2)", "3");
        assertListExpression("(PLUS (MINUS 4 1) (TIMES 2 2))", "7");
        assertListExpression("(EQ T T)", "T");
        assertListExpression("(EQ 1 1)", "T");
        assertListExpression("(EQ 1 2)", "NIL");
        assertListExpression("(EQ (PLUS 1 1) (TIMES 1 2))", "T");
        assertListExpression("(ATOM 1)", "T");
        assertListExpression("(ATOM NIL)", "T");
        assertListExpression("(ATOM (PLUS 2 3))", "T");
        assertListExpression("(CAR (CONS 1 2))", "1");
        assertListExpression("(CDR (CONS 1 2))", "2");
        assertListExpression("(CONS (PLUS 1 2) (LESS 4 1))", "(3)");
        assertListExpression("(CONS (LESS 4 1) (PLUS 1 2))", "(NIL . 3)");
        assertListExpression("(CONS 1 2)", "(1 . 2)");
        assertListExpression("(QUOTE 1)", "1");
        assertListExpression("(QUOTE (PLUS 1 2))", "(PLUS 1 2)");
        assertListExpression("(COND ((PLUS 1 1) 99) (ASDF 82))", "99");
        assertListExpression("(COND ((PLUS 1 1) (MINUS 1 1)) (ASDF 82))", "0");
        assertListExpression("(COND ((EQ 1 2) 99) ((LESS 1 2) (QUOTE 82)))", "82");
        assertListExpression("(CONS 2 (CONS 3 (CONS 4 5)))", "(2 3 4 . 5)");
        assertListExpression("(COND ((CONS 1 1) (MINUS 1 1)) (ASDF 82))", "0");
        assertListExpression("(CAR (QUOTE (7 10)))", "7");
        assertListExpression("(CDR (QUOTE (7 10)))", "(10)");
        assertListExpression("(CONS 7 10)", "(7 . 10)");
        assertListExpression("(CONS 7 (QUOTE (10)))", "(7 10)");
        assertListExpression("(CONS (CAR (QUOTE (7 10))) (CDR (QUOTE(7 10))))", "(7 10)");
    }

    @Test
    public void shouldHandleValidExpression() throws Exception {
        assertListExpression("123", "123");
        assertListExpression("T", "T");
        assertListExpression("NIL", "NIL");
        assertListExpression("(CAR (CONS 3 NIL))", "3");
        assertListExpression("(CDR (CONS 3 NIL))", "NIL");
        assertListExpression("(QUOTE (7))", "(7)"); // changed
        assertListExpression("(CAR (QUOTE(3)))", "3");
        assertListExpression("(CDR (QUOTE(3)))", "NIL");
        assertListExpression("(CONS (CAR (QUOTE (7 10))) (CDR (QUOTE(7 10)))) ", "(7 10)"); //changed
        assertListExpression("(CONS (PLUS 2 3) (CONS 8 (NULL 5))) ", "(5 8)"); //changed
        assertListExpression("(PLUS (PLUS 3 5) (CAR (QUOTE (7)))) ", "15");
        assertListExpression("(CONS 2 (CONS 3 (CONS 4 5)))", "(2 3 4 . 5)");
        assertListExpression("(CONS 4 5)", "(4 . 5)");
        assertListExpression("(QUOTE 3)", "3");
        assertListExpression("(QUOTE (3 4))", "(3 4)"); //changed
        assertListExpression("(CONS 4 (QUOTE (5 6)))", "(4 5 6)"); //changed
        assertListExpression("(CONS 4 (QUOTE (5 6 7 8)))", "(4 5 6 7 8)"); //changed
        assertListExpression("(QUOTE (3 4 5))", "(3 4 5)"); //changed
        assertListExpression("(CAR (CONS 40 50))", "40");
        assertListExpression("(CAR (CONS 7 (CONS 8 9)))", "7");
        assertListExpression("(CDR (CONS 40 50))", "50");
        assertListExpression("(CAR (QUOTE (3 4)))", "3");
        assertListExpression("(CDR (QUOTE (3 4)))", "(4)"); //changed
        assertListExpression("(CONS 30 (CONS 40 50))", "(30 40 . 50)");
        assertListExpression("(CAR (CONS 30 (CONS 40 50)))", "30");
        assertListExpression("(CDR (CONS 30 (CONS 40 50)))", "(40 . 50)");
        assertListExpression("(CAR (CONS (QUOTE (5 7)) 1))", "(5 7)"); //changed
        assertListExpression("(CONS (QUOTE (5 7)) 1)", "((5 7) . 1)"); //changed
        assertListExpression("(CAR (CONS 1 (QUOTE (5 7))))", "1");
        assertListExpression("(CDR (CONS 1 (QUOTE (5 7))))", "(5 7)"); //changed
        assertListExpression("(CONS 2 (CONS 3 (CONS 4 5)))", "(2 3 4 . 5)");
        assertListExpression("(CONS 4 5)", "(4 . 5)");
        assertListExpression("(CONS 4 (QUOTE (5 6)))", "(4 5 6)"); //changed
        assertListExpression("(ATOM 3)", "T");
        assertListExpression("(ATOM T)", "T");
        assertListExpression("(INT 3)", "T");
        assertListExpression("(INT T)", "NIL");
        assertListExpression("(NULL 3)", "NIL");
        assertListExpression("(NULL NIL)", "T");
        assertListExpression("(PLUS 2 3)", "5");
        assertListExpression("(MINUS 2 3)", "-1");
        assertListExpression("(TIMES 2 3)", "6");
        assertListExpression("(EQ 2 3)", "NIL");
        assertListExpression("(EQ 3 2)", "NIL");
        assertListExpression("(EQ 3 3)", "T");
        assertListExpression("(LESS 2 3)", "T");
        assertListExpression("(LESS 3 2)", "NIL");
        assertListExpression("(LESS 3 3)", "NIL");
        assertListExpression("(GREATER 2 3)", "NIL");
        assertListExpression("(GREATER 3 2)", "T");
        assertListExpression("(GREATER 3 3)", "NIL");
        assertListExpression("(PLUS (PLUS 5 6) (MINUS 5 20))", "-4");
        assertListExpression("(PLUS (PLUS 3 5) (CAR (CONS 7 8))) ", "15");
        assertListExpression("(QUOTE (7))", "(7)");
        assertListExpression("(CAR (QUOTE (7)))", "7");
        assertListExpression("(PLUS (PLUS 3 5) (CAR (QUOTE (7)))) ", "15");
        assertListExpression("(COND ((NULL NIL) 3) )", "3");
        assertListExpression("(COND ((NULL 2) 3) ((INT 2) 5) )", "5");
        assertListExpression("(COND ((NULL 2) 3) ((INT T) 5) ((INT 3) (CAR (QUOTE (7) )) )  )", "7");
        assertListExpression("(COND ((NULL 2) 3) ((INT T) 5) ((INT 3) (CDR (QUOTE (7) )) )  )", "NIL");
        assertListExpression("(COND ((NULL 2) 3) ((INT T) 5) ((INT NIL) (CAR (QUOTE (7) )) ) (T T) )", "T");
    }

    @Test
    public void name() throws Exception {
        assertListExpression("(CAR  (CONS (QUOTE (1 2 3)) (QUOTE (1 2 3))) )", "bladsfdsaf");

    }

    @Test
    public void shouldHandleValidFunctionDefinitions() throws Exception {
        assertListExpression("(DEFUN BLAH (X Y) 5)", "BLAH");
        assertListExpression("(DEFUN BLAH (X Y Z) (PLUS (PLUS X Y) Z))", "BLAH");
        assertListExpression("(DEFUN BLAH () (PLUS (PLUS X Y) Z))", "BLAH");
    }

    @Test
    public void shouldHandleInvalidFunctionDefinitions() throws Exception {
        assertIncorrectExpression("(DEFUN )", "Incorrect number of arguments: (DEFUN). DEFUN operation expects 3 arguments, but found 0 arguments");
        assertIncorrectExpression("(DEFUN XYZ )", "Incorrect number of arguments: (DEFUN XYZ). DEFUN operation expects 3 arguments, but found 1 arguments");
        assertIncorrectExpression("(DEFUN XYZ (X Z))", "Incorrect number of arguments: (DEFUN XYZ (X Z)). DEFUN operation expects 3 arguments, but found 2 arguments");
        assertIncorrectExpression("(DEFUN XYZ X ())", "Invalid value for parameter list: (DEFUN XYZ X NIL). Expecting a list but got X");
        assertIncorrectExpression("(DEFUN INT (X) ())", "Invalid value for function name: (DEFUN INT (X) NIL). INT is a reserved keyword");
        assertIncorrectExpression("(DEFUN XYZ (INT) ())", "Invalid value for parameter name at position 1: (DEFUN XYZ (INT) NIL). Parameter name cannot be a reserved keyword INT");
        assertIncorrectExpression("(DEFUN XYZ (X INT) ())", "Invalid value for parameter name at position 2: (DEFUN XYZ (X INT) NIL). Parameter name cannot be a reserved keyword INT");
        assertIncorrectExpression("(DEFUN XYZ (X X) ())", "Parameter names must be unique: (DEFUN XYZ (X X) NIL). X appears more than once");
    }

    @Test
    public void shouldCreateFunctionDefinitionsAsExpected() throws Exception {
        HashMap<String, FunctionDefinition> functionDefinitions = new HashMap<>();
        parseExpression("(DEFUN XYZ (X Y) (PLUS 4 5))", treeNode -> {
            TreeNode node = treeNode.eval(functionDefinitions, emptyList());
            assertThat(node.toListExpr(), is("XYZ"));
            assertThat(functionDefinitions.size(), is(1));
            assertThat(functionDefinitions.get("XYZ").getFunctionName(), is("XYZ"));
            assertThat(functionDefinitions.get("XYZ").getParameterNames(), is(asList("X", "Y")));
            assertThat(functionDefinitions.get("XYZ").getFunctionBody().eval(new HashMap<>(), emptyList()).toListExpr(), is("9"));
        });
    }

    @Test
    public void shouldHandleValidFunctionInvocations() throws Exception {
        HashMap<String, FunctionDefinition> functionDefinitions = new HashMap<>();
        List<Pair> associations = new ArrayList<>();
        parseExpression("(DEFUN XYZ (X Y) (PLUS X Y))", treeNode -> {
            treeNode.eval(functionDefinitions, associations);
            parseExpression("(XYZ 4 5)", treeNode1 -> {
                TreeNode output = treeNode1.eval(functionDefinitions, associations);
                assertThat(output.toListExpr(), is("9"));
            });
        });
    }

    @Test
    public void shouldHandleRecursiveFunctionInvocations() throws Exception {
        HashMap<String, FunctionDefinition> functionDefinitions = new HashMap<>();
        List<Pair> associations = new ArrayList<>();
        parseExpression("(DEFUN MEM (X LIST) (COND ( (NULL LIST) NIL ) ( T (COND ( (EQ X (CAR LIST)) T ) ( T (MEM X (CDR LIST)))))))", treeNode -> {
            treeNode.eval(functionDefinitions, associations);
            parseExpression("(MEM 3 (QUOTE (2 3 4)))", treeNode1 -> {
                TreeNode output = treeNode1.eval(functionDefinitions, associations);
                assertThat(output.toListExpr(), is("T"));
            });
        });
    }

    private void assertListExpression(String expression, String expectedListExpression) throws IOException {
        parseExpression(expression, treeNode -> assertThat(treeNode.eval(new HashMap<>(), emptyList()).toListExpr(), is(expectedListExpression)));
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
        assertIncorrectExpression("(COND ((INT T) 6) )", "Invalid expression: (COND ((INT T) 6)), All expressions in COND evaluated to NIL. At least one should evaluate to not NIL");
    }

    private void assertIncorrectExpression(String expression, String errorMessage) {
        try {
            parseExpression(expression, (treeNode) -> treeNode.eval(new HashMap<>(), emptyList()));
            fail();
        } catch (EvaluationException ex) {
            assertThat(ex.getMessage(), is(errorMessage));
        }
    }

    private void parseExpression(String expression, Consumer<TreeNode> treeNodeConsumer) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            new Parser(new Scanner(new ByteArrayInputStream(expression.getBytes())), outputStream, treeNodeConsumer).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}