package fr.cargo.tms.error;

import fr.cargo.tms.contracts.model.ProblemDto;

public class ValidationException extends RuntimeException {
    private final ProblemDto problem;

    public ValidationException(String message) {
        super(message);
        this.problem = null;
    }

    public ValidationException(ProblemDto problem) {
        super(problem != null ? problem.getDetail() : null);
        this.problem = problem;
    }

    public ProblemDto getProblem() {
        return problem;
    }
}

