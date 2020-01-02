package com.example.retrospect.web.services.import_export;

import com.example.retrospect.web.models.import_export.ImportSettings;

public interface RetrospectiveImporter {
    void importRetrospective(ImportableDataItem importable, ImportResult result, ImportSettings settings);
    void importTenant(ImportableDataItem importableTenant, ImportResult result, ImportSettings settings);
}
