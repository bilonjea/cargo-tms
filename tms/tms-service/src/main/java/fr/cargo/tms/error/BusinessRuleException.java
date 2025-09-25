package fr.cargo.tms.error;


import org.springframework.http.HttpStatus;

/** Pour exprimer une violation métier → HTTP 422 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

