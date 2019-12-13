package com.example.retrospect.core.models;

import java.util.List;

public interface RetrospectiveAdministrationDetails {
    String getId();

    String getReadableId();

    String getPreviousRetrospectiveId();

    List<String> getMembers();

    List<String> getAdministrators();
}
