package com.inspirage.ilct.exceptions;
import com.inspirage.ilct.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.MalformedURLException;
import java.sql.SQLTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public Map<String, String> handleMethodArgsNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> resp = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            resp.put(fieldName, message);
        });
        return resp;
    }

    //To handle validations
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errorResponse = handleMethodArgsNotValidException(ex);
        ApiResponse response = composeAppResponseDTO(HttpStatus.BAD_REQUEST.value(), errorResponse);
        return handleExceptionInternal(ex, response, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleExceptions(Exception exception, WebRequest request) {

        if (exception instanceof RuntimeException) {
            return composeRunTimeException(exception, request);
        } else if (exception instanceof SQLTimeoutException) {
            return composeTimeOutException(exception, request);
        }  else if (exception instanceof MalformedURLException) {
            return composeMalformedURLException(exception, request);
        } else {
            return composeGenericException(exception, request);
        }
    }

    private ResponseEntity<ApiResponse> composeRunTimeException(Exception exception, WebRequest request) {
        ResponseEntity<ApiResponse> response = null;
        if (exception instanceof NullPointerException || exception instanceof ClassCastException || exception instanceof IllegalArgumentException || exception instanceof NoSuchElementException
                || exception instanceof IndexOutOfBoundsException || exception instanceof NotBlankException || exception instanceof InvalidLinkException) {
            response = new ResponseEntity<ApiResponse>(composeAppResponseDTO(400, exception.getMessage(), request.getDescription(true)), HttpStatus.BAD_REQUEST);
        } else if (exception instanceof NotFoundException) {
            response = new ResponseEntity<ApiResponse>(composeAppResponseDTO(404, exception.getMessage(), request.getDescription(true)), HttpStatus.NOT_FOUND);
        } else if (exception instanceof ValueAlreadyExistsException) {
            response = new ResponseEntity<ApiResponse>(composeAppResponseDTO(409, exception.getMessage(), request.getDescription(true)), HttpStatus.CONFLICT);
        } else if (exception instanceof AccessDeniedException) {
            response = new ResponseEntity<ApiResponse>(composeAppResponseDTO(403, exception.getMessage(), request.getDescription(true)), HttpStatus.FORBIDDEN);
        }else if (exception instanceof TokenInvalidException) {
            response = new ResponseEntity<ApiResponse>(composeAppResponseDTO(401, exception.getMessage(), request.getDescription(true)), HttpStatus.UNAUTHORIZED);
        } else {
            response = new ResponseEntity<ApiResponse>(composeAppResponseDTO(400, exception.getMessage(), request.getDescription(true)), HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    private ResponseEntity<ApiResponse> composeTimeOutException(Exception exception, WebRequest request) {
        return new ResponseEntity<ApiResponse>(composeAppResponseDTO(504, exception.getMessage(), request.getDescription(true)), HttpStatus.GATEWAY_TIMEOUT);
    }
    private ResponseEntity<ApiResponse> composeMalformedURLException(Exception exception, WebRequest request) {
        return new ResponseEntity<ApiResponse>(composeAppResponseDTO(400, exception.getMessage(), request.getDescription(true)), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiResponse> composeGenericException(Exception exception, WebRequest request) {
        return new ResponseEntity<>(composeAppResponseDTO(401, exception.getMessage(), request.getDescription(true)), HttpStatus.UNAUTHORIZED);
    }

    //Exception Response Methods
    private ApiResponse composeAppResponseDTO(int errorCode, String message, Object payload) {
        return new ApiResponse(HttpStatus.valueOf(errorCode), message, payload);
    }

    private ApiResponse composeAppResponseDTO(int errorCode, Object errors) {
        return new ApiResponse(errorCode, String.valueOf(errors));
    }

}
