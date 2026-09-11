package com.archive.archive.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(
            ResourceNotFoundException exception
    ){
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Ресурс не найден");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception
    ){
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Validation failed");
        problem.setDetail("В запросе есть invalid fields");

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        problem.setProperty("errors", errors);

        return problem;
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(
            AccessDeniedException exception
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problem.setTitle("Доступ запрещен");
        problem.setDetail(exception.getMessage());

        return problem;
    }

    @ExceptionHandler(InvalidRequestStateException.class)
    public ProblemDetail handleInvalidRequestState(
            InvalidRequestStateException exception){
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("Неверное состояние запроса документа");
        problem.setDetail(exception.getMessage());

        return problem;
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(
            ObjectOptimisticLockingFailureException exception
    ){
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("Concurrent modification");
        problem.setDetail("Этот ресурс был изменен другим пользователем. " +
                "Обновите данные и попробуйте снова.");

        return problem;
    }

    @ExceptionHandler(InvalidDocumentStateException.class)
    public ProblemDetail handleInvalidDocumentState(
            InvalidDocumentStateException exception
    ){

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("Invalid document state");
        problem.setDetail(exception.getMessage());

        return problem;
    }
}
