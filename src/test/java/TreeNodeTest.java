import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TreeNodeTest {
    @Test
    public void shouldPrintTheBinaryTreeInDepthFirstFashion() throws Exception {
//        (1) -> (1 . NIL)
        TreeNode oneLevelTree = new TreeNode(createAtom(1), createNilNode(), null);
//        (((1))) -> (((1 . NIL) . NIL) . NIL)
        TreeNode leftSkewedTree =
                new TreeNode(
                        new TreeNode(
                                new TreeNode(
                                        createAtom(1),
                                        createNilNode(),
                                        null),
                                createNilNode(),
                                null),
                        createNilNode(),
                        null);
//        (1 2 3) -> (1. (2 . (3 . NIL)))
        TreeNode rightSkewedTree =
                new TreeNode(
                        createAtom(1),
                        new TreeNode(
                                createAtom(2),
                                new TreeNode(
                                        createAtom(3),
                                        createNilNode()
                                        , null)
                                , null)
                        , null);
        assertThat(oneLevelTree.toString(), is("(1 . NIL)"));
        assertThat(leftSkewedTree.toString(), is("(((1 . NIL) . NIL) . NIL)"));
        assertThat(rightSkewedTree.toString(), is("(1 . (2 . (3 . NIL)))"));
    }

    private TreeNode createAtom(int tokenValue) {
        return new TreeNode(null, null, new Token(tokenValue, TokenType.NUMERIC_ATOM));
    }

    private TreeNode createNilNode() {
        return new TreeNode(null, null, new Token(null, TokenType.NIL));
    }
}