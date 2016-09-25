import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

class EvalFunctions {
    static TreeNode evaluateBinaryFunction(TreeNode expressionNode, BiFunction<TreeNode, TreeNode, TreeNode> biFunction) {
        TreeNode operation = BuiltInOperations.car(expressionNode);
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        if (allArguments.length() != 2) {
            throw new EvaluationException(String.format("Incorrect number of arguments: %s. %s operation expects 2 arguments, but found %d arguments",
                    expressionNode.toListExpr(), operation.getCellToken().getTokenValue().toString(), allArguments.length()));
        }
        TreeNode firstArgument = BuiltInOperations.car(allArguments);
        TreeNode secondArgument = BuiltInOperations.car(BuiltInOperations.cdr(allArguments));
        return biFunction.apply(firstArgument.eval(), secondArgument.eval());
    }

    static TreeNode evaluateUnaryFunction(TreeNode expressionNode, Function<TreeNode, TreeNode> unaryOperation) {
        TreeNode operation = BuiltInOperations.car(expressionNode);
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        if (allArguments.length() != 1) {
            throw new EvaluationException(String.format("Incorrect number of arguments: %s. %s operation expects 1 argument, but found %d arguments",
                    expressionNode.toListExpr(), operation.getCellToken().getTokenValue().toString(), allArguments.length()));
        }
        TreeNode firstArgument = BuiltInOperations.car(allArguments);
        return unaryOperation.apply(firstArgument.eval());
    }

    static TreeNode evaluateCondOperation(TreeNode expressionNode) {
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        String expressionInListNotation = expressionNode.toListExpr();
        if (allArguments.length() == 0) {
            throw new EvaluationException(String.format("Incorrect number of arguments: %s. COND operation expects at least 1 argument, but found none", expressionInListNotation));
        }
        List<TreeNode> arguments = new ArrayList<>();
        TreeNode remainingArguments = allArguments;
        for (int i = 0; i < allArguments.length(); i++) {
            TreeNode arg = BuiltInOperations.car(remainingArguments);
            if (!arg.isList() || arg.length() != 2) {
                throw new EvaluationException(String.format("Invalid expression: %s. All arguments to the COND expression must be a list of length 2. Found invalid argument %s at position %d", expressionInListNotation, arg.toListExpr(), i + 1));
            }
            arguments.add(arg);
            remainingArguments = BuiltInOperations.cdr(remainingArguments);
        }
        for (TreeNode argument : arguments) {
            TreeNode booleanExprOutput = BuiltInOperations.car(argument).eval();
            if (!booleanExprOutput.getCellToken().isNil()) {
                return BuiltInOperations.car(BuiltInOperations.cdr(argument)).eval();
            }
        }
        throw new EvaluationException(String.format("Invalid expression: %s, All expressions in COND evaluated to NIL. At least one should evaluate to not NIL", expressionInListNotation));
    }

    static TreeNode evaluateQuoteOperation(TreeNode expressionNode) {
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        if (allArguments.length() != 1) {
            throw new EvaluationException(String.format("Incorrect number of arguments: %s. %s operation expects 1 argument, but found %d arguments",
                    expressionNode.toListExpr(), "QUOTE", allArguments.length()));
        }
        return BuiltInOperations.car(allArguments);
    }
}
