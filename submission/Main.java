import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            new LispInterpreter(System.in, System.out).start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
