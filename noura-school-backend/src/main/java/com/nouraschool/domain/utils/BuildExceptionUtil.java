package com.nouraschool.domain.utils;

import com.nouraschool.domain.exception.codes.BackendError;
import com.nouraschool.domain.exception.codes.ErrorDto;
import com.nouraschool.domain.exception.errors.InvalidRequestException;
import com.nouraschool.domain.exception.errors.ServiceException;

import java.util.List;
import java.util.Objects;

@Deprecated
public final class BuildExceptionUtil {

    private static final List<BackendError> ERROR_LIST = CsvDataLoaderUtils.loadObjectList(BackendError.class, "csv/status_code.csv");

    private BuildExceptionUtil() {
    }

    public static BackendError getBackendErrorByInternalCodeName(String internalCodeName) {
        return ERROR_LIST.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getInternalNameCode().equalsIgnoreCase(internalCodeName))
                .findFirst()
                .orElseGet(() -> {
                    var error = new BackendError();
                    error.setInternalNameCode(internalCodeName);
                    error.setInternalMessage("Error: " + internalCodeName);
                    error.setHttpCode(500);
                    return error;
                });
    }
}