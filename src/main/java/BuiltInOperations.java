import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;

class BuiltInOperations {
    static TreeNode car(TreeNode node) {
        return node.getLeftChild();
    }

    static TreeNode cdr(TreeNode node) {
        return node.getRightChild();
    }

    static TreeNode cons(TreeNode leftTree, TreeNode rightTree) {
        return new TreeNode(leftTree, rightTree, null);
    }

    static TreeNode atom(TreeNode node) {
        return node.isLeaf() ? TreeNode.T : TreeNode.NIL;
    }

    static TreeNode int_(TreeNode node) {
        if (node.isLeaf() && node.getCellToken().isNumeric()) {
            return TreeNode.T;
        }
        return TreeNode.NIL;
    }

    static TreeNode null_(TreeNode node) {
        if (node.isLeaf() && node.getCellToken().isNil()) {
            return TreeNode.T;
        }
        return TreeNode.NIL;
    }

    static TreeNode eq(TreeNode node1, TreeNode node2) {
        if (!node1.isLeaf() || !node2.isLeaf()) {
            throw new EvaluationException(String.format("Unexpected arguments found. " +
                    "%s operation expects 2 atoms as arguments, actual arguments were 1st arg: %s, 2nd arg: %s", "EQ", node1.toListExpr(), node2.toListExpr()));
        }
        if (node1.getCellToken().getTokenValue().equals(node2.getCellToken().getTokenValue())) {
            return TreeNode.T;
        }
        return TreeNode.NIL;
    }

    private static TreeNode binaryNumericOperation(TreeNode node1, TreeNode node2, IntBinaryOperator binaryOperator, String operationName) {
        if (!node1.isLeaf() || !node2.isLeaf())
            throw new EvaluationException(String.format("Unexpected arguments found. " +
                    "%s operation expects 2 atoms as arguments, actual arguments were 1st arg: %s, 2nd arg: %s", operationName, node1.toListExpr(), node2.toListExpr()));
        if (!node1.getCellToken().isNumeric() || !node2.getCellToken().isNumeric()) {
            throw new EvaluationException(String.format("Unexpected arguments found. " +
                    "%s operation expects 2 numeric atoms as arguments, actual arguments were 1st arg: %s, 2nd arg: %s", operationName, node1.toListExpr(), node2.toListExpr()));
        }
        int resultValue = binaryOperator.applyAsInt((int) node1.getCellToken().getTokenValue(), (int) node2.getCellToken().getTokenValue());
        return new TreeNode(null, null, new Token(resultValue, TokenType.NUMERIC_ATOM));
    }

    private static TreeNode binaryComparisonOperation(TreeNode node1, TreeNode node2, BiFunction<Integer, Integer, Boolean> comparisonFunction, String operationName) {
        if (!node1.isLeaf() || !node2.isLeaf())
            throw new EvaluationException(String.format("Unexpected arguments found. " +
                    "%s operation expects 2 atoms as arguments, actual arguments were 1st arg: %s, 2nd arg: %s", operationName, node1.toListExpr(), node2.toListExpr()));
        if (!node1.getCellToken().isNumeric() || !node2.getCellToken().isNumeric()) {
            throw new EvaluationException(String.format("Unexpected arguments found. " +
                    "%s operation expects 2 numeric atoms as arguments, actual arguments were 1st arg: %s, 2nd arg: %s", operationName, node1.toListExpr(), node2.toListExpr()));
        }
        Boolean resultValue = comparisonFunction.apply((int) node1.getCellToken().getTokenValue(), (int) node2.getCellToken().getTokenValue());
        return resultValue ? TreeNode.T : new TreeNode(null, null, Token.createNilToken());
    }

    static TreeNode plus(TreeNode node1, TreeNode node2) {
        return binaryNumericOperation(node1, node2, (left, right) -> left + right, "PLUS");
    }

    static TreeNode minus(TreeNode node1, TreeNode node2) {
        return binaryNumericOperation(node1, node2, (left, right) -> left - right, "MINUS");
    }

    static TreeNode times(TreeNode node1, TreeNode node2) {
        return binaryNumericOperation(node1, node2, (left, right) -> left * right, "TIMES");
    }

    static TreeNode less(TreeNode node1, TreeNode node2) {
        return binaryComparisonOperation(node1, node2, (integer, integer2) -> integer < integer2, "LESS");
    }

    static TreeNode greater(TreeNode node1, TreeNode node2) {
        return binaryComparisonOperation(node1, node2, (integer, integer2) -> integer > integer2, "GREATER");
    }

}
