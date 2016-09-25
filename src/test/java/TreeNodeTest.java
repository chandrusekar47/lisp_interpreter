import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TreeNodeTest extends BaseTest {
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

    @Test
    public void shouldPrintTheBinaryTreeInListNotation() throws Exception {
        // NIL
        TreeNode nilTreeNode = createNilNode();
        // 5
        TreeNode atomTreeNode = createAtom(5);
        // (5 . NIL) -> (5)
        TreeNode oneItemList = new TreeNode(createAtom(5), createNilNode(), null);
        // (5 . (4 . NIL)) -> (5 4)
        TreeNode twoItemList = new TreeNode(createAtom(5), new TreeNode(createAtom(4), createNilNode(), null), null);
        // (4 . 5)
        TreeNode twoItemNonList = new TreeNode(createAtom(4), createAtom(5), null);
        // (2 4 . 5)
        TreeNode threeItemNonList = new TreeNode(createAtom(2), new TreeNode(createAtom(4), createAtom(5), null), null);
        // ((4 . 5) 2)
        TreeNode listWithLeftNonList = new TreeNode(new TreeNode(createAtom(4), createAtom(5), null), new TreeNode(createAtom(2), createNilNode(), null), null);
        // (2 (4 . 5))
        TreeNode listWithRightNonList = new TreeNode(createAtom(2), new TreeNode(new TreeNode(createAtom(4), createAtom(5), null), createNilNode(), null), null);

        TreeNode listWithNulls = new TreeNode(null, null, null);

        assertThat(nilTreeNode.toListExpr(), is("NIL"));
        assertThat(atomTreeNode.toListExpr(), is("5"));
        assertThat(oneItemList.toListExpr(), is("(5)"));
        assertThat(twoItemList.toListExpr(), is("(5 4)"));
        assertThat(twoItemNonList.toListExpr(), is("(4 . 5)"));
        assertThat(threeItemNonList.toListExpr(), is("(2 4 . 5)"));
        assertThat(listWithLeftNonList.toListExpr(), is("((4 . 5) 2)"));
        assertThat(listWithRightNonList.toListExpr(), is("(2 (4 . 5))"));
        assertThat(listWithNulls.toListExpr(), is(""));
    }

}