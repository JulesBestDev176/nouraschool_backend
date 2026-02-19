package com.nouraschool.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
    private String sortBy;
    @Builder.Default
    private boolean ascending = true;

    public int getOffset() {
        return page * size;
    }
}
