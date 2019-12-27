package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.User;
import com.example.retrospect.core.serialisable.SerialisableUser;
import org.springframework.stereotype.Service;

@Service
public class UserDataSerialiser {
    public SerialisableUser serialise(User user) {
        var serialisable = new SerialisableUser();
        serialisable.setAvatarUrl(user.getAvatarUrl());
        serialisable.setDisplayName(user.getDisplayName());
        serialisable.setProvider(user.getProvider());
        serialisable.setUsername(user.getUsername());

        return serialisable;
    }

    public User deserialise(SerialisableUser serialisable) {
        return new User(
                serialisable.getUsername(),
                serialisable.getDisplayName(),
                serialisable.getAvatarUrl(),
                serialisable.getProvider()
        );
    }
}
