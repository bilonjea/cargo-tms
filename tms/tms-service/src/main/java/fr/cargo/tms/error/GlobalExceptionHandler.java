package fr.cargo.tms.error;


import fr.cargo.tms.contracts.model.ProblemDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDto> handleNotFound(NotFoundException ex) {
        return buildProblem(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDto> handleIllegalArgument(IllegalArgumentException ex) {
        return buildProblem(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDto> handleValidation(MethodArgumentNotValidException ex) {
        // extrait le premier message de validation (tu peux les agréger si tu veux plus de détails)
        String detail = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Validation failed");
        return buildProblem(HttpStatus.BAD_REQUEST, "Validation Error", detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDto> handleGeneric(Exception ex) {
        // logguer ex en interne pour l’équipe
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error", "Unexpected error occurred");
    }

     @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ProblemDto> handleBusiness(BusinessRuleException ex) {
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, "Business Rule Violation", ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDto> handleValidation(ValidationException ex) {
        if (ex.getProblem() != null) {
            return ResponseEntity
                    .status(ex.getProblem().getStatus())
                    .body(ex.getProblem());
        }
        return buildProblem(HttpStatus.BAD_REQUEST, "Validation Error", ex.getMessage());
    }

    // ---- utilitaire commun ----
    private ResponseEntity<ProblemDto> buildProblem(HttpStatus status, String title, String detail) {
        ProblemDto problem = new ProblemDto()
                .status(status.value())
                .title(title)
                .detail(detail)
                .type(URI.create("about:blank"));

        return ResponseEntity.status(status).body(problem);
    }

   

}
