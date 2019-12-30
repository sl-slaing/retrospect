package com.example.retrospect.core.repositories;

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

    public User getUser(String username){
        var user = storage.getOne(username);
        if (user == null){
            return null;
        }

        return serialiser.deserialise(user);
    }

    public void addOrUpdateUserDetails(User user) {
        storage.addOrUpdate(user.getUsername(), serialiser.serialise(user));
    }

    public Stream<User> getAllUsers(){
        return storage.getAll()
                .map(serialiser::deserialise);
    }

    public void removeUser(String username) {
        storage.remove(username);
    }
}
