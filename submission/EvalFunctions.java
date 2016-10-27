import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.format;

class EvalFunctions {
    static TreeNode evaluateBinaryFunction(TreeNode expressionNode, BiFunction<TreeNode, TreeNode, TreeNode> biFunction, Map<String, FunctionDefinition> functionDefinitions, List<Pair> associations) {
        TreeNode operation = BuiltInOperations.car(expressionNode);
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        if (allArguments.length() != 2) {
            throw new EvaluationException(format("Incorrect number of arguments: %s. %s operation expects 2 arguments, but found %d arguments",
                    expressionNode.toListExpr(), operation.getCellToken().getTokenValue().toString(), allArguments.length()));
        }
        TreeNode firstArgument = BuiltInOperations.car(allArguments);
        TreeNode secondArgument = BuiltInOperations.car(BuiltInOperations.cdr(allArguments));
        return biFunction.apply(firstArgument.eval(functionDefinitions, associations), secondArgument.eval(functionDefinitions, associations));
    }

    static TreeNode evaluateUnaryFunction(TreeNode expressionNode, Function<TreeNode, TreeNode> unaryOperation, Map<String, FunctionDefinition> functionDefinitions, List<Pair> associations) {
        TreeNode operation = BuiltInOperations.car(expressionNode);
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        if (allArguments.length() != 1) {
            throw new EvaluationException(format("Incorrect number of arguments: %s. %s operation expects 1 argument, but found %d arguments",
                    expressionNode.toListExpr(), operation.getCellToken().getTokenValue().toString(), allArguments.length()));
        }
        TreeNode firstArgument = BuiltInOperations.car(allArguments);
        return unaryOperation.apply(firstArgument.eval(functionDefinitions, associations));
    }

    static TreeNode evaluateUnaryTreeFunction(TreeNode expressionNode, Function<TreeNode, TreeNode> unaryOperation, Map<String, FunctionDefinition> functionDefinitions, List<Pair> associations) {
        TreeNode operation = BuiltInOperations.car(expressionNode);
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        String listExpr = expressionNode.toListExpr();
        String operationName = operation.getCellToken().getTokenValue().toString();
        if (allArguments.length() != 1) {
            throw new EvaluationException(format("Incorrect number of arguments: %s. %s operation expects 1 argument, but found %d arguments",
                    listExpr, operationName, allArguments.length()));
        }
        TreeNode firstArgument = BuiltInOperations.car(allArguments);
        TreeNode inputForUnaryTreeOperation = firstArgument.eval(functionDefinitions, associations);
        if (inputForUnaryTreeOperation.getRightChild() == null || inputForUnaryTreeOperation.getLeftChild() == null)
            throw new EvaluationException(format("Invalid expression: %s. %s expects the argument to be an S-expr and not an atom: %s", listExpr, operationName, inputForUnaryTreeOperation.toListExpr()));
        return unaryOperation.apply(inputForUnaryTreeOperation);
    }

    static TreeNode evaluateCondOperation(TreeNode expressionNode, Map<String, FunctionDefinition> functionDefinitions, List<Pair> associations) {
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        String expressionInListNotation = expressionNode.toListExpr();
        if (allArguments.length() == 0) {
            throw new EvaluationException(format("Incorrect number of arguments: %s. COND operation expects at least 1 argument, but found none", expressionInListNotation));
        }
        List<TreeNode> arguments = new ArrayList<>();
        TreeNode remainingArguments = allArguments;
        for (int i = 0; i < allArguments.length(); i++) {
            TreeNode arg = BuiltInOperations.car(remainingArguments);
            if (!arg.isList() || arg.length() != 2) {
                throw new EvaluationException(format("Invalid expression: %s. All arguments to the COND expression must be a list of length 2. Found invalid argument %s at position %d", expressionInListNotation, arg.toListExpr(), i + 1));
            }
            arguments.add(arg);
            remainingArguments = BuiltInOperations.cdr(remainingArguments);
        }
        for (TreeNode argument : arguments) {
            TreeNode booleanExprOutput = BuiltInOperations.car(argument).eval(functionDefinitions, associations);
            if (!booleanExprOutput.isLeaf() || !booleanExprOutput.getCellToken().isNil()) {
                return BuiltInOperations.car(BuiltInOperations.cdr(argument)).eval(functionDefinitions, associations);
            }
        }
        throw new EvaluationException(format("Invalid expression: %s, All expressions in COND evaluated to NIL. At least one should evaluate to not NIL", expressionInListNotation));
    }

    static TreeNode evaluateQuoteOperation(TreeNode expressionNode) {
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        if (allArguments.length() != 1) {
            throw new EvaluationException(format("Incorrect number of arguments: %s. %s operation expects 1 argument, but found %d arguments",
                    expressionNode.toListExpr(), "QUOTE", allArguments.length()));
        }
        return BuiltInOperations.car(allArguments);
    }

    public static TreeNode evaluateFunctionDefinition(TreeNode expressionNode, Map<String, FunctionDefinition> functionDefinitions) {
        TreeNode allArguments = BuiltInOperations.cdr(expressionNode);
        if (allArguments.length() != 3) {
            throw new EvaluationException(format("Incorrect number of arguments: %s. %s operation expects 3 arguments, but found %d arguments",
                    expressionNode.toListExpr(), "DEFUN", allArguments.length()));
        }
        TreeNode functionNameNode = BuiltInOperations.car(allArguments);
        TreeNode functionBody = BuiltInOperations.car(BuiltInOperations.cdr(BuiltInOperations.cdr(allArguments)));
        if (!functionNameNode.isLeaf()) {
            throw new EvaluationException(format("Invalid value for function name: %s. Expecting a literal atom but found: %s",
                    expressionNode.toListExpr(), functionNameNode.toListExpr()));
        }
        String functionName = functionNameNode.getCellToken().getTokenValue().toString();
        if (functionDefinitions.containsKey(functionName)) {
            throw new EvaluationException("Function with same name is already ");
        }
        if (!functionNameNode.getCellToken().isValidIdentifier()) {
            throw new EvaluationException(format("Invalid value for function name: %s. %s is a reserved keyword",
                    expressionNode.toListExpr(), functionName));
        }
        TreeNode parameterList = BuiltInOperations.car(BuiltInOperations.cdr(allArguments));
        if (!parameterList.isList()) {
            throw new EvaluationException(format("Invalid value for parameter list: %s. Expecting a list but got %s",
                    expressionNode.toListExpr(), parameterList.toListExpr()));
        }
        List<String> parameterNames = new ArrayList<>();
        Integer numberOfParameters = parameterList.length();
        for (int i = 0; i < numberOfParameters; i++) {
            TreeNode parameter = BuiltInOperations.car(parameterList);
            String parameterName = parameter.getCellToken().getTokenValue().toString();
            if (!parameter.isLeaf()) {
                throw new EvaluationException(format("Invalid value for parameter name at position %d: %s. Expecting literal atom but got %s", i + 1, expressionNode.toListExpr(), parameter.toListExpr()));
            }
            if (!parameter.getCellToken().isValidIdentifier()) {
                throw new EvaluationException(format("Invalid value for parameter name at position %d: %s. Parameter name cannot be a reserved keyword %s", i + 1, expressionNode.toListExpr(), parameter.getCellToken().getTokenValue()));
            }
            if (parameterNames.contains(parameterName)) {
                throw new EvaluationException(format("Parameter names must be unique: %s. %s appears more than once", expressionNode.toListExpr(), parameterName));
            }
            parameterNames.add(parameterName);
            parameterList = BuiltInOperations.cdr(parameterList);
        }
        FunctionDefinition functionDefinition = new FunctionDefinition(functionName, parameterNames, functionBody);
        functionDefinitions.put(functionName, functionDefinition);
        return functionNameNode;
    }

    public static TreeNode evaluateFunctionInvocation(TreeNode expressionNode, Map<String, FunctionDefinition> functionDefinitions, List<Pair> associations) {
        TreeNode functionNameNode = BuiltInOperations.car(expressionNode);
        String functionName = functionNameNode.getCellToken().getTokenValue().toString();
        FunctionDefinition functionDefinition = functionDefinitions.get(functionName);
        List<String> parameterNames = functionDefinition.getParameterNames();
        TreeNode actualArguments = BuiltInOperations.cdr(expressionNode);
        if (actualArguments.length() != parameterNames.size()) {
            throw new EvaluationException(format("Arguments mismatch for %s. %s function expects %d arguments but found %d arguments", expressionNode.toListExpr(), functionName, parameterNames.size(), actualArguments.length()));
        }
        List<Pair> functionArguments = new ArrayList<>();
        int numberOfArguments = actualArguments.length();
        for (int i = 0; i < numberOfArguments; i++) {
            TreeNode actualArgument = BuiltInOperations.car(actualArguments);
            TreeNode argumentValue = actualArgument.eval(functionDefinitions, associations);
            functionArguments.add(0, new Pair(parameterNames.get(i), argumentValue));
            actualArguments = BuiltInOperations.cdr(actualArguments);
        }
        functionArguments.addAll(associations);
        return functionDefinition.getFunctionBody().eval(functionDefinitions, functionArguments);
    }
}
