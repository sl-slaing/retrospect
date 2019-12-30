package com.example.retrospect.web.models.import_export;

import java.util.List;

public class ImportRequest {
    private List<String> retrospectives;
    private String version;
    private ImportSettings settings;

    public List<String> getRetrospectives() {
        return retrospectives;
    }

    public void setRetrospectives(List<String> retrospectives) {
        this.retrospectives = retrospectives;
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
}
