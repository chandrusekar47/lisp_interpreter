import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Arrays.asList;

public class TreeNode {
    //    public static final TreeNode UNDEFINED = new TreeNode(null, null, null);
    public static final TreeNode T = new TreeNode(null, null, Token.createTToken());
    public static final TreeNode NIL = new TreeNode(null, null, Token.createNilToken());
    public static final Map<String, BiFunction<TreeNode, TreeNode, TreeNode>> BINARY_ATOM_OPERATIONS = new HashMap<String, BiFunction<TreeNode, TreeNode, TreeNode>>() {{
        put("PLUS", BuiltInOperations::plus);
        put("MINUS", BuiltInOperations::minus);
        put("TIMES", BuiltInOperations::times);
        put("LESS", BuiltInOperations::less);
        put("GREATER", BuiltInOperations::greater);
        put("EQ", BuiltInOperations::eq);
        put("CONS", BuiltInOperations::cons);
    }};

    public static final List<String> RESERVED_LITERALS = asList("T", "NIL", "CAR", "CDR", "CONS", "ATOM", "EQ", "NULL", "INT", "PLUS", "MINUS", "TIMES", "LESS", "GREATER", "COND", "QUOTE", "DEFUN");

    public static final Map<String, Function<TreeNode, TreeNode>> UNARY_ATOM_OPERATIONS = new HashMap<String, Function<TreeNode, TreeNode>>() {{
        put("ATOM", BuiltInOperations::atom);
        put("INT", BuiltInOperations::int_);
        put("NULL", BuiltInOperations::null_);
    }};

    public static final Map<String, Function<TreeNode, TreeNode>> UNARY_TREE_OPERATIONS = new HashMap<String, Function<TreeNode, TreeNode>>() {{
        put("CAR", BuiltInOperations::car);
        put("CDR", BuiltInOperations::cdr);
    }};

    private TreeNode leftChild;
    private TreeNode rightChild;
    private Token cellToken;

    public TreeNode(TreeNode leftChild, TreeNode rightChild, Token cellToken) {
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.cellToken = cellToken;
    }

    public TreeNode() {
        this(null, null, null);
    }

    public TreeNode createEmptyRightChild() {
        rightChild = new TreeNode();
        return rightChild;
    }

    public TreeNode createEmptyLeftChild() {
        leftChild = new TreeNode();
        return leftChild;
    }

    public void setCellToken(Token cellToken) {
        this.cellToken = cellToken;
    }

    public TreeNode getLeftChild() {
        return leftChild;
    }

    public TreeNode getRightChild() {
        return rightChild;
    }

    public Token getCellToken() {
        return cellToken;
    }

    public boolean isLeaf() {
        return leftChild == null && rightChild == null && cellToken != null;
    }

    public TreeNode eval(Map<String, FunctionDefinition> functionDefinitions, List<Pair> associations) {
        if (isLeaf()) {
            if (this.cellToken.isTrue() || this.cellToken.isNil() || BuiltInOperations.int_(this) == T) {
                return this;
            }
            Optional<Pair> association = associations.stream().filter(pair -> pair.getName().equals(this.cellToken.getTokenValue().toString())).findFirst();
            if (association.isPresent())
                return association.get().getValue();
            throw new EvaluationException("Unexpected token " + this.cellToken.getTokenValue().toString());
        }
        TreeNode operation = BuiltInOperations.car(this);
        Optional<Token> cellToken = Optional.ofNullable(operation.getCellToken());
        String operationName = cellToken.map(token -> token.getTokenValue() != null ? token.getTokenValue().toString() : "").orElse(operation.toListExpr());
        if (BINARY_ATOM_OPERATIONS.containsKey(operationName)) {
            return EvalFunctions.evaluateBinaryFunction(this, BINARY_ATOM_OPERATIONS.get(operationName), functionDefinitions, associations);
        }
        if (UNARY_ATOM_OPERATIONS.containsKey(operationName)) {
            return EvalFunctions.evaluateUnaryFunction(this, UNARY_ATOM_OPERATIONS.get(operationName), functionDefinitions, associations);
        }
        if (UNARY_TREE_OPERATIONS.containsKey(operationName)) {
            return EvalFunctions.evaluateUnaryTreeFunction(this, UNARY_TREE_OPERATIONS.get(operationName), functionDefinitions, associations);
        }
        if ("QUOTE".equals(operationName)) {
            return EvalFunctions.evaluateQuoteOperation(this);
        }
        if ("COND".equals(operationName)) {
            return EvalFunctions.evaluateCondOperation(this, functionDefinitions, associations);
        }
        if ("DEFUN".equals(operationName)) {
            return EvalFunctions.evaluateFunctionDefinition(this, functionDefinitions);
        }
        if (functionDefinitions.containsKey(operationName)) {
            return EvalFunctions.evaluateFunctionInvocation(this, functionDefinitions, associations);
        }
        throw new EvaluationException(String.format("Unable to evaluate list: %s. Unknown operation: %s", this.toListExpr(), operationName));
    }

    public int length() {
        if (!isList())
            return -1;
        return computeLength();
    }

    private int computeLength() {
        if (isLeaf() && cellToken.isNil()) {
            return 0;
        }
        return rightChild.computeLength() + 1;
    }

    public boolean isList() {
        return (isLeaf() && cellToken.isNil()) || ((rightChild != null) && rightChild.isList());
    }

    public String toListExpr() {
        List<TreeNode> allRightNodes = new ArrayList<>();
        StringBuilder output = new StringBuilder();
        TreeNode rightNode = this;
        while (rightNode != null) {
            allRightNodes.add(rightNode);
            rightNode = rightNode.rightChild;
        }
        if (allRightNodes.size() == 1) {
            return allRightNodes.get(0).getCellToken().getTokenValue().toString();
        }
        output.append("(");
        List<String> leftChildValues = new ArrayList<>();
        for (TreeNode rootNode : allRightNodes) {
            TreeNode leftChild = rootNode.leftChild;
            if (leftChild == null)
                break;
            leftChildValues.add(leftChild.toListExpr());
        }
        output.append(String.join(" ", leftChildValues));
        TreeNode lastLevelRightNode = allRightNodes.get(allRightNodes.size() - 1);
        if (!lastLevelRightNode.getCellToken().isNil()) {
            output.append(" . ").append(lastLevelRightNode.getCellToken().getTokenValue().toString());
        }
        return output.append(")").toString();
    }

    public String toString() {
        if (this.cellToken != null && (this.cellToken.isAtom() || this.cellToken.isNil())) {
            return this.cellToken.getTokenValue().toString();
        }
        if (leftChild != null && rightChild == null) {
            return leftChild.toString();
        }
        if (leftChild != null) {
            return "(" + leftChild.toString() + " . " + rightChild.toString() + ")";
        }
        return "";
    }
}
