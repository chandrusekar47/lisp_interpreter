import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            new Parser(new Scanner(System.in), System.out).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
