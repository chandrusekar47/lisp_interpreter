import java.util.List;

public class FunctionDefinition {
    private String functionName;
    private List<String> parameterNames;
    private TreeNode functionBody;

    public FunctionDefinition(String functionName, List<String> parameterNames, TreeNode functionBody) {
        this.functionName = functionName;
        this.parameterNames = parameterNames;
        this.functionBody = functionBody;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public TreeNode getFunctionBody() {
        return functionBody;
    }
}
