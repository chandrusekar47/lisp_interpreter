import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            PrintStream printStream = new PrintStream(System.out);
            new Parser(new Scanner(System.in), System.out, treeNode -> {
                try {
                    printStream.println(treeNode.eval(new HashMap<>(), new ArrayList<>()).toListExpr());
                } catch (EvaluationException ex) {
                    System.out.println("ERROR: " + ex.getMessage());
                    System.out.println("ERROR expression: " + treeNode.toListExpr());
                    System.exit(-1);
                }
            }).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
