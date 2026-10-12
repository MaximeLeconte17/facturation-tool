package com.minifacture.backend.imports;

import java.util.List;

public record ImportResult(int imported, int ignored, List<String> errors) {
}