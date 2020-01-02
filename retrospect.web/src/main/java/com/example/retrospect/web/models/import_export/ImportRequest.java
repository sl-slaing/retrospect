package com.example.retrospect.web.models.import_export;

import com.example.retrospect.web.services.import_export.ImportableData;

import java.util.List;

public class ImportRequest implements ImportableData {
    private List<String> dataItems;
    private String version;
    private ImportSettings settings;
    private ImportExportDataType type;

    public List<String> getDataItems() {
        return dataItems;
    }

    public void setDataItems(List<String> dataItems) {
        this.dataItems = dataItems;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public ImportSettings getSettings() {
        return settings;
    }

    public void setSettings(ImportSettings settings) {
        this.settings = settings;
    }

    public ImportExportDataType getType() {
        return type;
    }

    public void setType(ImportExportDataType type) {
        this.type = type;
    }
}
