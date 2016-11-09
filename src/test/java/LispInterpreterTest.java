import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LispInterpreterTest {
    @Test
    public void shouldEvaluateGivenProgramAsExpected() throws Exception {
        evaluateProgram(asList(
                "(DEFUN ISNULL (X) (NULL X))",
                "(ISNULL 3)",
                "(DEFUN ADD (X Y) (PLUS X (RAND)))",
                "(DEFUN RAND () 4)",
                "(ADD 3 4)",
                "(ADD (RAND) 9)",
                "(DEFUN MEM (X LIST) (COND ( (NULL LIST) NIL ) ( T (COND ( (EQ X (CAR LIST)) T ) ( T (MEM X (CDR LIST)))))))",
                "(MEM 3 (QUOTE (2 3 4)))",
                "(MEM 9 (QUOTE (2 3 4)))",
                "(DEFUN UNI (S1 S2) (COND ( (NULL S1) S2) ( (NULL S2) S1) ( T (COND ( (MEM (CAR S1) S2) (UNI (CDR S1) S2) ) ( T (CONS (CAR S1) (UNI (CDR S1) S2) )))) ))",
                "(UNI (QUOTE (1 2)) (QUOTE (2 3)))",
                "(UNI (QUOTE (1 2)) (QUOTE (3 4)))"
        ), asList(
                "ISNULL",
                "NIL",
                "ADD",
                "RAND",
                "7",
                "8",
                "MEM",
                "T",
                "NIL",
                "UNI",
                "(1 2 3)",
                "(1 2 3 4)",
                ""
        ));
    }

    @Test
    public void name() throws Exception {
        evaluateProgram(asList(
                "(DEFUN GENNUMS (X) (COND ((EQ X 0) NIL) (T (CONS X (GENNUMS (MINUS X 1))))))",
                "(DEFUN MEM (X LIST) (COND ( (NULL LIST) NIL ) ( T (COND ( (EQ X (CAR LIST)) T ) ( T (MEM X (CDR LIST)))))))",
                "(MEM 12 (GENNUMS 20))"
        ), asList(
                "GENNUMS",
                "MEM",
                "T",
                ""
        ));
    }

    private void evaluateProgram(List<String> inputLines, List<String> expectedOutputLines) throws IOException {
        String expectedOutput = String.join(System.lineSeparator(), expectedOutputLines);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new LispInterpreter(new ByteArrayInputStream(String.join(System.lineSeparator(), inputLines).getBytes()), outputStream).start();
        outputStream.flush();
        outputStream.close();
        assertThat(new String(outputStream.toByteArray()), is(expectedOutput));
    }
}