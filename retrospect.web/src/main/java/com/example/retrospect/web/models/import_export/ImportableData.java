package com.example.retrospect.web.models.import_export;

import java.util.List;

public class ImportableData {
    private List<String> retrospectives;
    private String version;

    public ImportableData(List<String> retrospectives, String version) {
        this.retrospectives = retrospectives;
        this.version = version;
    }

    public List<String> getRetrospectives() {
        return retrospectives;
    }

    public String getVersion() {
        return version;
    }
}
