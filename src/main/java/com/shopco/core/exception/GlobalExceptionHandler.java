package com.shopco.core.exception;


import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleValidationsException(MethodArgumentNotValidException ex){
        log.error("An Unexpected error occurred in Method Argument : {}", ex.getMessage());

        String fieldName = ex.getBindingResult().getFieldError() != null ?
                ex.getBindingResult().getFieldError().getField() : "";

        String errorMessage = ex.getBindingResult().getFieldError() != null ?
                ex.getBindingResult().getFieldError().getDefaultMessage() : "Validation error";

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.BAD_REQUEST.value(), "error with : " + fieldName, errorMessage, null);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleEmailConflict(EmailAlreadyExistsException ex) {
        ApiResponse errorResponse = ResponseUtil.error(HttpStatus.CONFLICT.value(),
                    ex.getMessage(), "Duplicate Email", null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUsernameConflict(UsernameAlreadyExistsException ex) {
        ApiResponse errorResponse = ResponseUtil.error(HttpStatus.CONFLICT.value(),
                    ex.getMessage(), "Duplicate Username", null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse> handleMissingPart(MissingServletRequestPartException ex) {
        log.error("An Unexpected error occurred: {}", ex.getMessage());

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "The required part '" + ex.getRequestPartName() + "' is not present", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }



    // Handle unsupported media type (e.g., wrong Content-Type)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<ApiResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {

        log.error("Unsupported Content-Type: {}", ex.getContentType());

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ex.getMessage(),
                "Unsupported Content-Type. Expected 'multipart/form-data', but received: " + ex.getContentType(),
                null
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    //Handle Custom exceptions (eg. UserAlreadyExistsException)
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyExistsException(UserAlreadyExistException e){

        log.error("An unexpected error occurred {}", e.getMessage(), e);

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.CONFLICT.value(), e.getMessage(), "user already exist", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ApiResponse> handleInvalidCredentialsException(InvalidCredentialException e) {
        log.error("An unexpected error occurred {}", e.getMessage());

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.UNAUTHORIZED.value(), e.getMessage(), "Invalid Credential", null);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception e){
        log.error("An Unexpected error {} " , e.getMessage(), e);

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), "Something went wrong ", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(NetworkConnectivityException.class)
    public ResponseEntity<ApiResponse> handleNetworkConnectivityException(NetworkConnectivityException e) {
        log.error("Network connectivity issue: {}", e.getMessage());

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.SERVICE_UNAVAILABLE.value(), e.getMessage(), "InternetConnection Error.", null);

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiResponse> handleInvalidFileException(InvalidFileException e) {
        log.error("Unsupported Media file {}", e.getMessage() );

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), e.getMessage(), "unsupported file format", null);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found {}", ex.getMessage());

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.NOT_FOUND.value(), ex.getMessage(), "resource not found", null);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException e){
        log.error("Bad request {} ", e.getMessage());
        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.BAD_REQUEST.value(), e.getMessage(), "Bad Request", null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(UsernameNotFoundException ex) {
        log.warn("Authentication failed: {}", ex.getMessage()); // concise log

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), "Authentication failed", null
        );

        return new  ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(UsernameNotFoundException ex) {
        log.warn("Bad Credentials: {}", ex.getMessage()); // concise log

        ApiResponse errorResponse = ResponseUtil.error(
                HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), "Authentication Failed", null
        );

        return new  ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);

    }








}
