
public class EvaluationException extends RuntimeException {
    private String message;

    public EvaluationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
