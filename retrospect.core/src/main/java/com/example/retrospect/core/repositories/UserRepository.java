package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.User;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class UserRepository {
    private final DataStorage<User> storage;

    public UserRepository(DataStorageFactory storageFactory) {
        storage = storageFactory.getStorage(User.class);
    }

    public User getUser(String username){
        return storage.getOne(username);
    }

    public void addOrUpdateUserDetails(User user) {
        storage.addOrUpdate(user.getUsername(), user);
    }

    public Stream<User> getAllUsers(){
        return storage.getAll();
    }
}
