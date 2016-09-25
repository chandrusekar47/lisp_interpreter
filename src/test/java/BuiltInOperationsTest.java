import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BuiltInOperationsTest {

    private TreeNode numericAtom;
    private TreeNode literalAtom;
    private TreeNode tree;

    @Before
    public void setUp() throws Exception {
        numericAtom = new TreeNode(null, null, new Token(23, TokenType.NUMERIC_ATOM));
        literalAtom = new TreeNode(null, null, new Token("Tadfafd", TokenType.LITERAL_ATOM));
        tree = new TreeNode(numericAtom, literalAtom, null);
    }

    @Test
    public void shouldReturnTheLeftSubtreeOfGivenBinaryTreeAndHandleErrorConditionsAsWell() throws Exception {
        TreeNode leftChild = new TreeNode(null, null, null);
        TreeNode treeNode = new TreeNode(leftChild, new TreeNode(null, null, null), null);
        assertThat(BuiltInOperations.car(treeNode), CoreMatchers.sameInstance(leftChild));
//        assertThat(BuiltInOperations.car(leftChild), is(TreeNode.UNDEFINED));
//        assertThat(BuiltInOperations.car(new TreeNode(leftChild, null, null)), is(TreeNode.UNDEFINED));
    }

    @Test
    public void shouldReturnTheRightSubtreeOfGivenBinaryTreeAndHandleErrorConditionsAsWell() throws Exception {
        TreeNode rightChild = new TreeNode(null, null, null);
        TreeNode treeNode = new TreeNode(new TreeNode(null, null, null), rightChild, null);
        assertThat(BuiltInOperations.cdr(treeNode), CoreMatchers.sameInstance(rightChild));
//        assertThat(BuiltInOperations.cdr(rightChild), is(TreeNode.UNDEFINED));
//        assertThat(BuiltInOperations.cdr(new TreeNode(rightChild, null, null)), is(TreeNode.UNDEFINED));
    }

    @Test
    public void shouldConstructANewTreeWithGivenLeftAndRightSubtrees() throws Exception {
        TreeNode leftTree = new TreeNode(null, null, new Token(23, TokenType.NUMERIC_ATOM));
        TreeNode rightTree = new TreeNode(null, null, new Token(33, TokenType.NUMERIC_ATOM));
        assertThat(BuiltInOperations.cons(leftTree, rightTree).getLeftChild(), CoreMatchers.sameInstance(leftTree));
        assertThat(BuiltInOperations.cons(leftTree, rightTree).getRightChild(), CoreMatchers.sameInstance(rightTree));
    }

    @Test
    public void shouldReturnTrueLiteralIfTheTreeIsAnAtom() throws Exception {
        TreeNode numericAtom = new TreeNode(null, null, new Token(23, TokenType.NUMERIC_ATOM));
        TreeNode literalAtom = new TreeNode(null, null, new Token("Tadfafd", TokenType.LITERAL_ATOM));
        TreeNode tree = new TreeNode(numericAtom, literalAtom, null);
        assertThat(BuiltInOperations.atom(numericAtom).getCellToken(), is(Token.createTToken()));
        assertTrue(BuiltInOperations.atom(numericAtom).isLeaf());
        assertThat(BuiltInOperations.atom(literalAtom).getCellToken(), is(Token.createTToken()));
        assertTrue(BuiltInOperations.atom(literalAtom).isLeaf());
        assertThat(BuiltInOperations.atom(tree).getCellToken(), is(Token.createNilToken()));
        assertTrue(BuiltInOperations.atom(tree).isLeaf());
    }

    @Test
    public void shouldReturnTrueLiteralIfTheTreeIsANumericAtom() throws Exception {
        TreeNode numericAtom = new TreeNode(null, null, new Token(23, TokenType.NUMERIC_ATOM));
        TreeNode literalAtom = new TreeNode(null, null, new Token("Tadfafd", TokenType.LITERAL_ATOM));
        TreeNode tree = new TreeNode(numericAtom, literalAtom, null);
        assertThat(BuiltInOperations.int_(numericAtom).getCellToken(), is(Token.createTToken()));
        assertTrue(BuiltInOperations.int_(numericAtom).isLeaf());
        assertThat(BuiltInOperations.int_(literalAtom).getCellToken(), is(Token.createNilToken()));
        assertTrue(BuiltInOperations.int_(literalAtom).isLeaf());
        assertThat(BuiltInOperations.int_(tree).getCellToken(), is(Token.createNilToken()));
        assertTrue(BuiltInOperations.int_(tree).isLeaf());
    }

    @Test
    public void shouldAddTwoNumericAtoms() throws Exception {
        assertThat(BuiltInOperations.plus(numericAtom, numericAtom).getCellToken().getTokenValue(), is(46));
//        assertThat(BuiltInOperations.plus(numericAtom, literalAtom), is(TreeNode.UNDEFINED));
//        assertThat(BuiltInOperations.plus(literalAtom, literalAtom), is(TreeNode.UNDEFINED));
//        assertThat(BuiltInOperations.plus(tree, numericAtom), is(TreeNode.UNDEFINED));
//        assertThat(BuiltInOperations.plus(tree, tree), is(TreeNode.UNDEFINED));
    }

    @Test
    public void shouldCompareTwoNumericAtoms() throws Exception {
        assertThat(BuiltInOperations.eq(numericAtom, numericAtom).getCellToken(), is(Token.createTToken()));
        assertThat(BuiltInOperations.less(numericAtom, numericAtom).getCellToken(), is(Token.createNilToken()));
        assertThat(BuiltInOperations.greater(numericAtom, numericAtom).getCellToken(), is(Token.createNilToken()));
//        assertThat(BuiltInOperations.eq(tree, numericAtom), is(TreeNode.UNDEFINED));
//        assertThat(BuiltInOperations.less(tree, tree), is(TreeNode.UNDEFINED));
    }


}