package com.example.retrospect.web.models.import_export;

import java.util.List;

public class ExportRequest {
    private List<String> ids;
    private String version;
    private ExportSettings settings;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ExportSettings getSettings() {
        return settings;
    }

    public void setSettings(ExportSettings settings) {
        this.settings = settings;
    }
}

