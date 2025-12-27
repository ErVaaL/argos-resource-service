package com.erval.argos.resource.dto;

import com.erval.argos.core.application.SortDirection;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * GraphQL input mirroring {@link com.erval.argos.core.application.PageRequest}.
 */
public record PageRequestInput(
        @Min(0) Integer page,
        @Min(1) @Max(500) Integer size,
        String sortBy,
        SortDirection sortDirection) {
}
