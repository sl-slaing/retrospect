package com.example.retrospect.core.serialisable;

import java.util.Map;

public class SerialisableAudit {
    private String createdOn;
    private String lastUpdatedOn;
    private String lastUpdatedBy;
    private String createdBy;
    private String lastChange;

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastChange() {
        return lastChange;
    }

    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }

    public static SerialisableAudit deserialiseFromMap(Map<String, Object> auditData) {
        var audit = new SerialisableAudit();
        audit.setCreatedBy((String)auditData.get("createdBy"));
        audit.setLastUpdatedBy((String)auditData.get("lastUpdatedBy"));
        audit.setLastChange((String)auditData.get("lastChange"));
        audit.setCreatedOn((String)auditData.get("createdOn"));
        audit.setLastUpdatedOn((String)auditData.get("lastUpdatedOn"));

        return audit;
    }
}
