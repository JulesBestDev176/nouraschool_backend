package com.nouraschool.domain.exception.impl;

import com.nouraschool.domain.exception.BackendErrorResolver;
import com.nouraschool.domain.exception.codes.BackendError;
import com.nouraschool.domain.utils.CsvDataLoaderUtils;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class CsvBackendErrorResolver implements BackendErrorResolver {

    private static final String CSV_FILE = "csv/status_code.csv";
    private final List<BackendError> errorList;

    public CsvBackendErrorResolver() {
        this.errorList = CsvDataLoaderUtils.loadObjectList(BackendError.class, CSV_FILE);
    }

    @Override
    public BackendError resolveByCodeName(String internalCodeName) {
        return errorList.stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getInternalNameCode().equalsIgnoreCase(internalCodeName))
                .findFirst()
                .orElseGet(() -> {
                    var error = new BackendError();
                    error.setInternalNameCode(internalCodeName);
                    error.setInternalMessage("Error: " + internalCodeName);
                    error.setInternalCode(500);
                    error.setHttpCode(500);
                    return error;
                });
    }
}
