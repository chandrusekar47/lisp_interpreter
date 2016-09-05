package cs.pl.lisp;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ParserTest {

    public static final String NEWLINE = System.getProperty("line.separator");

    @Test
    public void shouldParseEmptyList() throws Exception {
        assertThat(parseExpression("()"), is("NIL" + NEWLINE));
    }

    @Test
    public void shouldParseIndividualAtom() throws Exception {
        assertThat(parseExpression("123"), is("123" + NEWLINE));
        assertThat(parseExpression("XYZ"), is("XYZ" + NEWLINE));
    }

    @Test
    public void shouldParseExpressionListWithOneAtom() throws Exception {
        assertThat(parseExpression("(123)"), is("(123 . NIL)" + NEWLINE));
    }

    @Test
    public void shouldParseExpressionListWithTwoAtoms() throws Exception {
        assertThat(parseExpression("(123 234)"), is("(123 . (234 . NIL))" + NEWLINE));
    }

    @Test
    public void shouldParseExpressionListWithFourAtoms() throws Exception {
        assertThat(parseExpression("(123 234 456 789)"), is("(123 . (234 . (456 . (789 . NIL))))" + NEWLINE));
    }

    @Test
    public void shouldReplaceEmptyBracesWithinListWithNil() throws Exception {
        assertThat(parseExpression("(123 () 234)"), is("(123 . (NIL . (234 . NIL)))" + NEWLINE));
    }

    @Test
    public void shouldHandleNestedLists() throws Exception {
        assertThat(parseExpression("(123 (X Y ()))"), is("(123 . ((X . (Y . (NIL . NIL))) . NIL))" + NEWLINE));
    }

    @Test
    public void shouldHandleComplexExpressions() throws Exception {
        assertThat(parseExpression("(1 2 ((4 5) (6 7)) () (()) ((5 () ())))"), is("(1 . (2 . (((4 . (5 . NIL)) . ((6 . (7 . NIL)) . NIL)) . (NIL . ((NIL . NIL) . (((5 . (NIL . (NIL . NIL))) . NIL) . NIL))))))" + NEWLINE));
    }

    @Test
    public void shouldHandleMultiLineExpressions() throws Exception {
        assertThat(parseExpression("123 (3 5 (XYZ) 7) (NIL 5 ( ) (( )) 7 (( ) 9 ( )) )(DEFUN F23 (X) (PLUS X 12 55))"), is("123" + NEWLINE +
                "(3 . (5 . ((XYZ . NIL) . (7 . NIL))))" + NEWLINE +
                "(NIL . (5 . (NIL . ((NIL . NIL) . (7 . ((NIL . (9 . (NIL . NIL))) . NIL))))))" + NEWLINE +
                "(DEFUN . (F23 . ((X . NIL) . ((PLUS . (X . (12 . (55 . NIL)))) . NIL))))" + NEWLINE));
    }

    private Scanner setupScannerFor(String expression) {
        return new Scanner(new ByteArrayInputStream(expression.getBytes()));
    }

    private String parseExpression(String expression) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new Parser(setupScannerFor(expression), outputStream).start();
        outputStream.flush();
        return new String(outputStream.toByteArray());
    }


}