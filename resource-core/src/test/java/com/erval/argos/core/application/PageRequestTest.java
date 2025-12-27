package com.erval.argos.core.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PageRequestTest {

    @Test
    void storesPagingAndSortFields() {
        PageRequest request = new PageRequest(2, 50, "name", SortDirection.DESC);

        assertEquals(2, request.page());
        assertEquals(50, request.size());
        assertEquals("name", request.sortBy());
        assertEquals(SortDirection.DESC, request.direction());
    }
}
