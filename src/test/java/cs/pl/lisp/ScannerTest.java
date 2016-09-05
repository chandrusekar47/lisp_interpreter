package cs.pl.lisp;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ScannerTest {
    @Test
    public void shouldReturnTheExpectedSetOfLiterals() throws IOException {
        Scanner scanner = new Scanner(new ByteArrayInputStream("OHIOSU((223)444)     555       ) adfasdf".getBytes()));
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.LITERAL_ATOM, "OHIOSU");
        assertThat(scanner.moveToNextToken().getCurrentToken().getTokenType(), is(TokenType.OPEN_PARENTHESIS));
        assertThat(scanner.moveToNextToken().getCurrentToken().getTokenType(), is(TokenType.OPEN_PARENTHESIS));
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.NUMERIC_ATOM, 223);
        assertThat(scanner.moveToNextToken().getCurrentToken().getTokenType(), is(TokenType.CLOSE_PARENTHESIS));
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.NUMERIC_ATOM, 444);
        assertThat(scanner.moveToNextToken().getCurrentToken().getTokenType(), is(TokenType.CLOSE_PARENTHESIS));
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.NUMERIC_ATOM, 555);
        assertThat(scanner.moveToNextToken().getCurrentToken().getTokenType(), is(TokenType.CLOSE_PARENTHESIS));
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.LITERAL_ATOM, "adfasdf");
    }

    @Test
    public void shouldErrorOutIfAnyLiteralAtomStartsWithANumber() throws Exception {
        Scanner scanner = new Scanner(new ByteArrayInputStream("((1212(adfaf1121 23333 23411) 434343ab".getBytes()));
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.OPEN_PARENTHESIS, '(');
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.OPEN_PARENTHESIS, '(');
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.NUMERIC_ATOM, 1212);
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.OPEN_PARENTHESIS, '(');
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.LITERAL_ATOM, "adfaf1121");
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.NUMERIC_ATOM, 23333);
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.NUMERIC_ATOM, 23411);
        assertTokenTypeAndValue(scanner.moveToNextToken().getCurrentToken(), TokenType.CLOSE_PARENTHESIS, ')');
        try {
            scanner.moveToNextToken().getCurrentToken();
        } catch (Exception e) {
            assertThat(e.getMessage(), is("ERROR: Invalid token 434343ab"));
            return;
        }
        fail();
    }

    private void assertTokenTypeAndValue(Token nextToken, TokenType tokenType, Object value) throws IOException {
        assertThat(nextToken.getTokenType(), is(tokenType));
        assertThat(nextToken.getTokenValue(), is(value));
    }
}