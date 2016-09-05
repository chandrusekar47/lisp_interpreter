package cs.pl.lisp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ParserTest {

    @Test
    public void shouldParseExpressions1() throws Exception {
        String expression = "(1 2 ((4 5) (6 7)) () (()) ((5 () ())))";
        Scanner scanner = new Scanner(new ByteArrayInputStream(expression.getBytes()));
        Parser parser = new Parser(scanner);
        parser.start();
        TreeNode tree = parser.getTree();
        String outputExpr = tree.toString();
        assertThat(outputExpr, is("(1 . (2 . (((4 . (5 . NIL)) . ((6 . (7 . NIL )) . NIL)) . (NIL . ((NIL . NIL) . (((5 . (NIL . (NIL . NIL))) . NIL) . NIL))))))"));
    }
}