package com.example.retrospect.web.services.import_export;

import com.example.retrospect.web.services.import_export.v1_0.V1_0_RetrospectiveImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RetrospectiveImporterFactory {
    @Autowired
    public V1_0_RetrospectiveImporter v1_0_retrospectiveImporter;

    public RetrospectiveImporter getImporter(String version) {
        if (version.equals("1.0")) {
            return v1_0_retrospectiveImporter;
        }

        throw new RuntimeException("Unknown import version");
    }
}
