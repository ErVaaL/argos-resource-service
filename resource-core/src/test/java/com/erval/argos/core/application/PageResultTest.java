package com.erval.argos.core.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class PageResultTest {

    @Test
    void calculatesTotalPagesWithRemainder() {
        PageResult<String> result = new PageResult<>(List.of("a", "b"), 5, 0, 2);

        assertEquals(3, result.totalPages());
    }

    @Test
    void returnsZeroPagesWhenSizeIsZero() {
        PageResult<String> result = new PageResult<>(List.of(), 10, 0, 0);

        assertEquals(0, result.totalPages());
    }
}
