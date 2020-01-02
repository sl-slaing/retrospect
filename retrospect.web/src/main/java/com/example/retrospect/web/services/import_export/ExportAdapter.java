package com.example.retrospect.web.services.import_export;

import com.example.retrospect.core.models.Retrospective;
import com.example.retrospect.core.models.Tenant;
import com.example.retrospect.web.models.import_export.ExportSettings;
import com.example.retrospect.web.services.import_export.v1_0.V1_0ImportExportAdapter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Service
public class ExportAdapter {
    private static final Map<String, BiFunction<Retrospective, ExportSettings, String>> retrospectiveVersionMappings = getRetrospectiveVersionMappings();
    private static final Map<String, BiFunction<Tenant, ExportSettings, String>> tenantVersionMappings = getTenantVersionMappings();

    private static Map<String, BiFunction<Retrospective, ExportSettings, String>> getRetrospectiveVersionMappings() {
        var map = new HashMap<String, BiFunction<Retrospective, ExportSettings, String>>();
        map.put("1.0", V1_0ImportExportAdapter::exportRetrospective);

        return map;
    }

    private static Map<String, BiFunction<Tenant, ExportSettings, String>> getTenantVersionMappings() {
        var map = new HashMap<String, BiFunction<Tenant, ExportSettings, String>>();
        map.put("1.0", V1_0ImportExportAdapter::exportTenant);

        return map;
    }

    public String adaptSingleRetrospective(Retrospective retrospective, String version, ExportSettings settings) {
        var versionMapping = retrospectiveVersionMappings.getOrDefault(version, null);
        if (versionMapping == null) {
            throw new RuntimeException("Version mapping not found");
        }

        return versionMapping.apply(retrospective, settings);
    }

    public String adaptSingleTenant(Tenant tenant, String version, ExportSettings settings) {
        var versionMapping = tenantVersionMappings.getOrDefault(version, null);
        if (versionMapping == null) {
            throw new RuntimeException("Version mapping not found");
        }

        return versionMapping.apply(tenant, settings);
    }
}
