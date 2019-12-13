package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.User;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class UserRepository extends PersistenceRepository<User> {
    private final File file;

    public UserRepository() {
        var dataStoragePath = System.getProperty("DataStoragePath");
        file = Path.of(dataStoragePath).resolve("users.json").toFile();
    }

    @Override
    protected Map<String, User> loadData() {
        return loadDataFromFile(file);
    }

    @Override
    protected void saveData(Map<String, User> data) {
        saveDataToFile(file, data);
    }

    @Override
    protected User deserialiseValue(Map<String, Object> map) {
        return new User(
                (String)map.get("username"),
                (String)map.get("displayName"),
                (String)map.get("avatarUrl"),
                (String)map.get("provider")
        );
    }

    public User getUser(String username){
        return getData().getOrDefault(username, null);
    }

    public void addOrUpdateUserDetails(User user) {
        updateData(data -> data.put(user.getUsername(), user));
    }

    public Stream<User> getAllUsers(){
        return getData().values().stream();
    }
}
