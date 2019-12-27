package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

@Service
public class UserRepository {
    private final DataStorage<User> storage;

    public UserRepository() {
        storage = new FileStorage<>("users", UserRepository::deserialiseValue);
    }

    private static User deserialiseValue(Map<String, Object> map) {
        return new User(
                (String)map.get("username"),
                (String)map.get("displayName"),
                (String)map.get("avatarUrl"),
                (String)map.get("provider")
        );
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
