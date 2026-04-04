package com.nouraschool.domain.exception;

import com.nouraschool.domain.exception.codes.BackendError;

public interface BackendErrorResolver {
    BackendError resolveByCodeName(String internalCodeName);
}
