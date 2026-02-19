package com.nouraschool.domain.exception;

import com.nouraschool.domain.exception.codes.ErrorDto;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.ServiceException;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class DefaultExceptionHandler {

    private static final Logger LOG = Logger.getLogger(DefaultExceptionHandler.class);

    @Inject
    BackendErrorResolver backendErrorResolver;

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleInvalidRequestException(InvalidRequestException ex) {
        var backendError = backendErrorResolver.resolveByCodeName(ex.getMessage());

        var errorDto = ErrorDto.builder()
                .code(backendError.getInternalCode())
                .message(backendError.getInternalMessage())
                .title(getReasonPhrase(backendError.getHttpCode()))
                .status(backendError.getHttpCode())
                .timestamp(LocalDateTime.now())
                .details(List.of(backendError.getInternalMessage()))
                .build();

        return RestResponse.status(Response.Status.fromStatusCode(backendError.getHttpCode()), errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleNotFoundException(com.nouraschool.domain.exception.errors.NotFoundException ex) {
        String code = ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : "NOT_FOUND";
        var backendError = backendErrorResolver.resolveByCodeName(code);
        var errorDto = ErrorDto.builder()
                .code(backendError.getInternalCode())
                .message(backendError.getInternalMessage())
                .title(getReasonPhrase(backendError.getHttpCode()))
                .status(backendError.getHttpCode())
                .timestamp(LocalDateTime.now())
                .build();
        return RestResponse.status(Response.Status.fromStatusCode(backendError.getHttpCode()), errorDto);
    }

    /** Gère la 404 JAX-RS (route inexistante ou non autorisée). Évite de la traiter comme erreur 500. */
    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleJaxRsNotFoundException(NotFoundException ex) {
        var errorDto = ErrorDto.builder()
                .code(404)
                .message(ex.getMessage() != null ? ex.getMessage() : "Resource not found")
                .title("Not Found")
                .status(404)
                .timestamp(LocalDateTime.now())
                .build();
        return RestResponse.status(Response.Status.NOT_FOUND, errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleServiceException(ServiceException ex) {
        var backendError = backendErrorResolver.resolveByCodeName(ex.getMessage());

        var errorDto = ErrorDto.builder()
                .code(backendError.getInternalCode())
                .message(backendError.getInternalMessage())
                .title(getReasonPhrase(backendError.getHttpCode()))
                .status(backendError.getHttpCode())
                .timestamp(LocalDateTime.now())
                .build();

        return RestResponse.status(Response.Status.fromStatusCode(backendError.getHttpCode()), errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        var details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        var errorDto = ErrorDto.builder()
                .code(400)
                .message("Validation failed")
                .title("Bad Request")
                .status(400)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();

        return RestResponse.status(Response.Status.BAD_REQUEST, errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleGlobalException(Exception ex) {
        LOG.error("Unhandled exception caught: ", ex);

        var errorDto = ErrorDto.builder()
                .code(500)
                .message("An unexpected error occurred: " + ex.getMessage())
                .title("Internal Server Error")
                .status(500)
                .timestamp(LocalDateTime.now())
                .build();

        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, errorDto);
    }

    private String getReasonPhrase(int statusCode) {
        Response.Status status = Response.Status.fromStatusCode(statusCode);
        return status != null ? status.getReasonPhrase() : "Unknown Status";
    }
}