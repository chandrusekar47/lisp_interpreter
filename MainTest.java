import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MainTest {
    @Test
    public void shouldReturnTheExpectedSetOfLiterals() throws IOException {
        Main.inputStream = new ByteArrayInputStream("OHIOSU((223)444)     555       ) adfasdf".getBytes());
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.LITERAL_ATOM, "OHIOSU");
        assertThat(Main.getNextToken().getTokenType(), is(TokenType.OPEN_PARANTHESIS));
        assertThat(Main.getNextToken().getTokenType(), is(TokenType.OPEN_PARANTHESIS));
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.NUMERIC_ATOM, 223);
        assertThat(Main.getNextToken().getTokenType(), is(TokenType.CLOSE_PARANTHESIS));
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.NUMERIC_ATOM, 444);
        assertThat(Main.getNextToken().getTokenType(), is(TokenType.CLOSE_PARANTHESIS));
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.NUMERIC_ATOM, 555);
        assertThat(Main.getNextToken().getTokenType(), is(TokenType.CLOSE_PARANTHESIS));
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.LITERAL_ATOM, "adfasdf");
    }

    @Test
    public void shouldErrorOutIfAnyLiteralAtomStartsWithANumber() throws Exception {
        Main.inputStream = new ByteArrayInputStream("((1212(adfaf1121 23333 23411) 434343ab".getBytes());
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.OPEN_PARANTHESIS, '(');
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.OPEN_PARANTHESIS, '(');
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.NUMERIC_ATOM, 1212);
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.OPEN_PARANTHESIS, '(');
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.LITERAL_ATOM, "adfaf1121");
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.NUMERIC_ATOM, 23333);
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.NUMERIC_ATOM, 23411);
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.CLOSE_PARANTHESIS,')');
        assertTokenTypeAndValue(Main.getNextToken(), TokenType.ERROR, "434343ab");
    }

    private void assertTokenTypeAndValue(Token nextToken, TokenType tokenType, Object value) throws IOException {
        assertThat(nextToken.getTokenType(), is (tokenType));
        assertThat(nextToken.getTokenValue(), is (value));
    }
}