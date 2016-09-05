package cs.pl.lisp;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser(scanner);
        parser.start();
    }

}
