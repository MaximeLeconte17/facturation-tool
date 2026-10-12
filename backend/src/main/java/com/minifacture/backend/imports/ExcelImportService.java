package com.minifacture.backend.imports;

import jakarta.validation.Validator;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ExcelImportService {

    private static final int MAX_ROWS = 5000;

    private final Validator validator;

    public ExcelImportService(Validator validator) {
        this.validator = validator;
    }

    @Transactional
    public <T> ImportResult importFile(InputStream file, ImportDefinition<T> definition) throws IOException {
        List<T> toCreate = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> knownKeys = new HashSet<>(definition.existingKeys());
        int ignored = 0;
        DataFormatter formatter = new DataFormatter();

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet.getLastRowNum() > MAX_ROWS) {
                throw new IllegalArgumentException("Maximum " + MAX_ROWS + " rows per import");
            }

            // Row 0 is the header, data starts at row 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                List<String> cells = new ArrayList<>();
                for (int c = 0; c < definition.columnCount(); c++) {
                    cells.add(formatter.formatCellValue(row.getCell(c)).trim());
                }
                if (cells.stream().allMatch(String::isEmpty))
                    continue;

                T item = definition.fromRow(cells);

                if (!validator.validate(item).isEmpty()) {
                    errors.add("Ligne " + (i + 1) + " : données invalides");
                    continue;
                }

                String key = definition.uniqueKey(item);
                if (key != null && !knownKeys.add(key)) {
                    ignored++;
                    errors.add("Ligne " + (i + 1) + " : doublon (" + key + ")");
                    continue;
                }

                toCreate.add(item);
            }
        }

        definition.saveAll(toCreate);
        return new ImportResult(toCreate.size(), ignored, errors);
    }
}