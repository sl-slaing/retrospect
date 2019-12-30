package com.example.retrospect.web.services.import_export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportResult {
    private Map<String, List<String>> messages = new HashMap<>();
    private List<String> generalMessages = new ArrayList<>();
    private boolean success = true;

    public void addMessage(String id, String message) {
        var listOfMessages = messages.getOrDefault(id, new ArrayList<>());
        listOfMessages.add(message);
        messages.put(id, listOfMessages);
    }

    public void addMessage(String message) {
        generalMessages.add(message);
    }

    public Map<String, List<String>> getMessages() {
        return messages;
    }

    public List<String> getGeneralMessages() {
        return generalMessages;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
