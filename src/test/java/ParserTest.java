import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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

    @Test
    public void shouldHandleInvalidIdentifierNames() throws Exception {
        try {
            parseExpression("(1 2 ((4 5) (6 7)) () (()) ((12abcxyz () ())))");
        } catch (Exception ex) {
            assertThat(ex.getMessage(), is("ERROR: Invalid token 12abcxyz"));
            return;
        }
        fail();
    }

    @Test
    public void shouldParseAllLinesBeforeTheLineThatContainsTheInvalidIdentifier() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            parseExpression("(123 234 456 789)" + NEWLINE +
                    "(123 (X Y ()))" + NEWLINE +
                    "(1 2 ((4 5) (6 7)) () (()) ((12abcxyz () ())))", outputStream);
        } catch (Exception ex) {
            outputStream.flush();
            String output = new String(outputStream.toByteArray());
            assertThat(output, is("(123 . (234 . (456 . (789 . NIL))))" + NEWLINE
                    + "(123 . ((X . (Y . (NIL . NIL))) . NIL))" + NEWLINE));
            assertThat(ex.getMessage(), is("ERROR: Invalid token 12abcxyz"));
            return;
        }
        fail();
    }

    @Test
    public void shouldDisplayErrorMessageIfTheInputAtTheBeginningOfTheStringCannotBeParsedProperly() throws Exception {
        try {
            parseExpression(")12 ab ())");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("ERROR: Unexpected token ) at the following location: " + NEWLINE +
                    " ----> ) <---- 12 ab (" + NEWLINE +
                    "Expecting ( or an identifier or a number"));
            return;
        }
        fail();
    }

    @Test
    public void shouldDisplayErrorMessageIfTheInputInTheMiddleOfTheStringCannotBeParsedProperly() throws Exception {
        try {
            parseExpression("12 ab bc cd ()) 234");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("ERROR: Unexpected token ) at the following location: " + NEWLINE +
                    "12 ab bc cd ( ) ----> ) <---- 234 " + NEWLINE +
                    "Expecting ( or an identifier or a number"));
            return;
        }
        fail();
    }

    @Test
    public void shouldDisplayErrorMessageIfTheInputInTheEndOfTheStringCannotBeParsedProperly() throws Exception {
        try {
            parseExpression("12 ab bc cd () 234)");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("ERROR: Unexpected token ) at the following location: " + NEWLINE +
                    "12 ab bc cd ( ) 234 ----> ) <---- " + NEWLINE +
                    "Expecting ( or an identifier or a number"));
            return;
        }
        fail();
    }

    @Test
    public void shouldDisplayAnErrorMessageIfTheInputStringDoesNotTerminateProperly() throws Exception {
        try {
            parseExpression("12 ab bc cd ( 234 ");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("ERROR: Reached end of input while parsing, expecting ) but could not find any."));
            return;
        }
        fail();
    }

    @Test
    public void shouldFailWithACustomMessageIfThereIsNoInputGiven() throws Exception {
        try {
            parseExpression("");
        } catch (Exception e) {
            assertThat(e.getMessage(), is("ERROR: No input given."));
            return;
        }
        fail();
    }

    private Scanner setupScannerFor(String expression) {
        return new Scanner(new ByteArrayInputStream(expression.getBytes()));
    }

    private String parseExpression(String expression) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        return parseExpression(expression, outputStream);
    }

    private String parseExpression(String expression, ByteArrayOutputStream outputStream) throws IOException {
        new Parser(setupScannerFor(expression), outputStream).start();
        outputStream.flush();
        return new String(outputStream.toByteArray());
    }


}