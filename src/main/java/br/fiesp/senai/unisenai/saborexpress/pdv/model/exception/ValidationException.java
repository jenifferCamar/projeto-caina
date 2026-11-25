package br.fiesp.senai.unisenai.saborexpress.pdv.model.exception;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
