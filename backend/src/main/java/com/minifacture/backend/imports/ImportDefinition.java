package com.minifacture.backend.imports;

import java.util.List;
import java.util.Set;

public interface ImportDefinition<T> {

    int columnCount();

    T fromRow(List<String> cells);

    default String uniqueKey(T item) {
        return null;
    }

    default Set<String> existingKeys() {
        return Set.of();
    }

    void saveAll(List<T> items);
}