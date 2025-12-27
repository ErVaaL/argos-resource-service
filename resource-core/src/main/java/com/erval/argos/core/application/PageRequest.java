package com.erval.argos.core.application;

/**
 * Represents a request for a page of results from a query.
 * <p>
 * Usage tips:
 * <ul>
 * <li>{@code page} is zero-based</li>
 * <li>{@code sortBy} may be {@code null} to use adapter defaults</li>
 * <li>Always pair with {@link PageResult} to communicate totals</li>
 * </ul>
 *
 * @param page      zero-based page index
 * @param size      number of items per page
 * @param sortBy    field name to sort by (e.g. "timestamp", "name")
 * @param direction sort direction (ASC or DESC)
 */
public record PageRequest(int page, int size, String sortBy, SortDirection direction) {
}
