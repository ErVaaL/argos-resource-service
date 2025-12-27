package com.erval.argos.core.application;

/**
 * Sorting direction used across paginated queries.
 * <p>
 * Conventions:
 * <ul>
 * <li>{@link #ASC} means smallest-to-largest (e.g., A-Z, oldest-first)</li>
 * <li>{@link #DESC} means largest-to-smallest (e.g., Z-A, newest-first)</li>
 * </ul>
 */
public enum SortDirection {
    ASC,
    DESC
}
