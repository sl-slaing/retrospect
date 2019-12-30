package com.example.retrospect.web.services.import_export;

import com.example.retrospect.core.models.Retrospective;
import com.example.retrospect.web.models.import_export.ExportSettings;
import com.example.retrospect.web.services.import_export.v1_0.V1_0ImportExportAdapter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class ExportAdapter {
    private static final Map<String, BiFunction<Retrospective, ExportSettings, String>> versionMappings = getVersionMappings();

    private static Map<String, BiFunction<Retrospective, ExportSettings, String>> getVersionMappings() {
        var map = new HashMap<String, BiFunction<Retrospective, ExportSettings, String>>();
        map.put("1.0", V1_0ImportExportAdapter::exportRetrospective);

        return map;
    }

    public String adaptSingleRetrospective(Retrospective retrospective, String version, ExportSettings settings) {
        var versionMapping = versionMappings.getOrDefault(version, null);
        if (versionMapping == null) {
            throw new RuntimeException("Version mapping not found");
        }

        return versionMapping.apply(retrospective, settings);
    }
}
