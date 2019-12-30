package com.example.retrospect.web.services.import_export;

import com.example.retrospect.web.services.import_export.v1_0.V1_0ImportExportAdapter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class ImportAdapter {
    private static final Map<String, Function<String, ImportableRetrospective>> versionMappings = getVersionMappings();

    private static Map<String, Function<String, ImportableRetrospective>> getVersionMappings() {
        var map = new HashMap<String, Function<String, ImportableRetrospective>>();
        map.put("1.0", V1_0ImportExportAdapter::adaptRetrospective);

        return map;
    }

    public ImportableRetrospective adaptSingleRetrospective(String importableJson, String version) {
        var versionMapping = versionMappings.getOrDefault(version, null);
        if (versionMapping == null) {
            throw new RuntimeException("Version mapping not found");
        }

        return versionMapping.apply(importableJson);
    }
}
