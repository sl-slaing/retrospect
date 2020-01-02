package com.example.retrospect.web.models.import_export;

import com.example.retrospect.web.services.import_export.ImportableData;

import java.util.List;

public class DataExport implements ImportableData {
    private List<String> dataItems;
    private String version;

    public DataExport(List<String> dataItems, String version) {
        this.dataItems = dataItems;
        this.version = version;
    }

    public List<String> getDataItems() {
        return dataItems;
    }

    public String getVersion() {
        return version;
    }
}
