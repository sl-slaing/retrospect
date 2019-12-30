package com.example.retrospect.web.models.import_export;

public class ImportSettings {
    private boolean permitMerge;
    private boolean restoreData;
    private boolean restoreDeleted;
    private boolean dryRun;

    public boolean isPermitMerge() {
        return permitMerge;
    }

    public void setPermitMerge(boolean permitMerge) {
        this.permitMerge = permitMerge;
    }

    public boolean isRestoreData() {
        return restoreData;
    }

    public void setRestoreData(boolean restoreData) {
        this.restoreData = restoreData;
    }

    public boolean isRestoreDeleted() {
        return restoreDeleted;
    }

    public void setRestoreDeleted(boolean restoreDeleted) {
        this.restoreDeleted = restoreDeleted;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public boolean applyChanges() {
        return !isDryRun();
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }
}
