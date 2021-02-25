package ir.bontech.financial.exception;

public class TransferMoneyException extends RuntimeException {
    public TransferMoneyException() {
    }

    public TransferMoneyException(String message) {
        super(message);
    }

    public TransferMoneyException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferMoneyException(Throwable cause) {
        super(cause);
    }
}
