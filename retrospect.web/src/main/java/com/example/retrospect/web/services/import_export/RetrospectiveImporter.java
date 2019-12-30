package com.example.retrospect.web.services.import_export;

import com.example.retrospect.web.models.import_export.ImportSettings;

public interface RetrospectiveImporter {
    void importRetrospective(ImportableRetrospective importable, ImportResult result, ImportSettings settings);
}
