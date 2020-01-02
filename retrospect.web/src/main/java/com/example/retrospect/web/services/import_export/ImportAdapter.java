package com.example.retrospect.web.services.import_export;

import com.example.retrospect.web.services.import_export.v1_0.V1_0ImportExportAdapter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class ImportAdapter {
    private static final Map<String, Function<String, ImportableDataItem>> retrospectiveVersionMappings = getRetrospectiveVersionMappings();
    private static final Map<String, Function<String, ImportableDataItem>> tenantVersionMappings = getTenantVersionMappings();

    private static Map<String, Function<String, ImportableDataItem>> getRetrospectiveVersionMappings() {
        var map = new HashMap<String, Function<String, ImportableDataItem>>();
        map.put("1.0", V1_0ImportExportAdapter::adaptRetrospective);

        return map;
    }

    private static Map<String, Function<String, ImportableDataItem>> getTenantVersionMappings() {
        var map = new HashMap<String, Function<String, ImportableDataItem>>();
        map.put("1.0", V1_0ImportExportAdapter::adaptTenant);

        return map;
    }

    public ImportableDataItem adaptSingleRetrospective(String importableJson, String version) {
        var versionMapping = retrospectiveVersionMappings.getOrDefault(version, null);
        if (versionMapping == null) {
            throw new RuntimeException("Version mapping not found");
        }

        return versionMapping.apply(importableJson);
    }

    public ImportableDataItem adaptSingleTenant(String importableJson, String version) {
        var versionMapping = tenantVersionMappings.getOrDefault(version, null);
        if (versionMapping == null) {
            throw new RuntimeException("Version mapping not found");
        }

        return versionMapping.apply(importableJson);
    }
}
