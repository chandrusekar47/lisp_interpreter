import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LispInterpreter {
    final Map<String, FunctionDefinition> functionDefinitions;
    final List<Pair> associations;
    private final InputStream in;
    private final PrintStream out;
    private final Parser parser;

    public LispInterpreter(InputStream in, OutputStream out) {
        this.in = in;
        this.out = new PrintStream(out);
        functionDefinitions = new HashMap<>();
        associations = new ArrayList<>();
        parser = new Parser(new Scanner(in), out, this::onExpressionParsed);
    }

    public void start() throws IOException {
        parser.start();
    }

    private void onExpressionParsed(TreeNode expression) {
        try {
            out.println(expression.eval(functionDefinitions, associations).toListExpr());
        } catch (EvaluationException ex) {
            out.println("ERROR: " + ex.getMessage());
            out.println("ERROR expression: " + expression.toListExpr());
            System.exit(-1);
        }
    }

}
