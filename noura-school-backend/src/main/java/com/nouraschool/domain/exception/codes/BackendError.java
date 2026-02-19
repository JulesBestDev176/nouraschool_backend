package com.nouraschool.domain.exception.codes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BackendError implements Serializable {
    private String internalNameCode;
    private int internalCode;
    private int httpCode;
    private String internalMessage;
}
