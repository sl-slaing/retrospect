package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.NotFoundUser;
import com.example.retrospect.core.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.repositories.UserRepository;

@Service
public class UserSerialiser {
    private final UserRepository userRepository;

    @Autowired
    public UserSerialiser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String serialise(User user){
        return user.getUsername();
    }

    public User deserialise(String id){
        var user = userRepository.getUser(id);

        if (user == null){
            return new NotFoundUser(id);
        }

        return user;
    }
}
