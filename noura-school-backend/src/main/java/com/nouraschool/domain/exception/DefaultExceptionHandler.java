package com.nouraschool.domain.exception;

import com.nouraschool.domain.exception.codes.ErrorDto;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.ServiceException;
import com.nouraschool.runtime.correlation.CorrelationContext;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Provider
public class DefaultExceptionHandler {

    private static final Logger LOG = Logger.getLogger(DefaultExceptionHandler.class);

    @Inject
    BackendErrorResolver backendErrorResolver;

    @Inject
    CorrelationContext correlationContext;

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleInvalidRequestException(InvalidRequestException ex) {
        var backendError = backendErrorResolver.resolveByCodeName(ex.getMessage());
        var errorDto = buildErrorDto(backendError.getInternalNameCode(), backendError.getInternalMessage(),
                backendError.getHttpCode(), List.of(backendError.getInternalMessage()));
        return RestResponse.status(Response.Status.fromStatusCode(backendError.getHttpCode()), errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleNotFoundException(com.nouraschool.domain.exception.errors.NotFoundException ex) {
        String code = ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : "RESSOURCE_INTROUVABLE";
        var backendError = backendErrorResolver.resolveByCodeName(code);
        var errorDto = buildErrorDto(backendError.getInternalNameCode(), backendError.getInternalMessage(),
                backendError.getHttpCode(), null);
        return RestResponse.status(Response.Status.fromStatusCode(backendError.getHttpCode()), errorDto);
    }

    /** Gère la 404 JAX-RS (route inexistante ou non autorisée). */
    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleJaxRsNotFoundException(NotFoundException ex) {
        var errorDto = buildErrorDto("RESSOURCE_INTROUVABLE",
                ex.getMessage() != null ? ex.getMessage() : "Ressource non trouvée", 404, null);
        return RestResponse.status(Response.Status.NOT_FOUND, errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleServiceException(ServiceException ex) {
        var backendError = backendErrorResolver.resolveByCodeName(ex.getMessage());
        var errorDto = buildErrorDto(backendError.getInternalNameCode(), backendError.getInternalMessage(),
                backendError.getHttpCode(), null);
        return RestResponse.status(Response.Status.fromStatusCode(backendError.getHttpCode()), errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleConstraintViolationException(ConstraintViolationException ex) {
        var details = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());
        var errorDto = buildErrorDto("VALIDATION_ECHOUEE", "Contraintes de validation non respectées", 400, details);
        return RestResponse.status(Response.Status.BAD_REQUEST, errorDto);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> handleGlobalException(Exception ex) {
        LOG.error("[DefaultExceptionHandler][handleGlobalException] Exception non gérée", ex);
        var errorDto = buildErrorDto("ERREUR_INTERNE", "Une erreur inattendue s'est produite", 500, null);
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, errorDto);
    }

    /** Format standard task.md section 3.6 : code, message, details, correlationId, timestamp (ISO 8601). */
    private ErrorDto buildErrorDto(String code, String message, int httpStatus, List<String> details) {
        String correlationId = correlationContext != null && correlationContext.getCorrelationId() != null
                ? correlationContext.getCorrelationId()
                : UUID.randomUUID().toString();
        return ErrorDto.builder()
                .code(code)
                .message(message)
                .details(details != null ? details : List.of())
                .correlationId(correlationId)
                .timestamp(Instant.now().atOffset(ZoneOffset.UTC).toString())
                .build();
    }
}