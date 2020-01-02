package com.example.retrospect.web.services.import_export;

import java.util.List;

public interface ImportableData {
    String getVersion();
    List<String> getDataItems();
}
