package com.example.retrospect.core.models;

public interface Identifiable {
    String getId();
    String getType();

    default boolean equals(Identifiable other){
        if (other == null){
            return false;
        }

        return other.getId().equals(getId())
                && other.getType().equals(getType());
    }
}
