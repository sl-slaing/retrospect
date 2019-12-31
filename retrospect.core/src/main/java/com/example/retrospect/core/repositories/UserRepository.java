package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.User;
import com.example.retrospect.core.serialisable.SerialisableUser;
import com.example.retrospect.core.serialisers.UserDataSerialiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class UserRepository {
    private final DataStorage<SerialisableUser> storage;
    private final UserDataSerialiser serialiser;

    @Autowired
    public UserRepository(DataStorageFactory storageFactory, UserDataSerialiser serialiser) {
        storage = storageFactory.getStorage(SerialisableUser.class);
        this.serialiser = serialiser;
    }

    public User getUser(LoggedInUser loggedInUser, String username){
        var user = storage.getOne(loggedInUser.getTenantId(), username);
        if (user == null){
            return null;
        }

        return serialiser.deserialise(user);
    }

    public void addOrUpdateUserDetails(String tenantId, User user) {
        storage.addOrUpdate(tenantId, user.getUsername(), serialiser.serialise(user));
    }

    public Stream<User> getAllUsers(LoggedInUser loggedInUser){
        return storage.getAll(loggedInUser.getTenantId())
                .map(serialiser::deserialise);
    }

    public void removeUser(LoggedInUser loggedInUser, String username) {
        storage.remove(loggedInUser.getTenantId(), username);
    }
}
