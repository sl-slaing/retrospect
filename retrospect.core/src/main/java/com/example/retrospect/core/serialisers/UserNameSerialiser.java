package com.example.retrospect.core.serialisers;

import com.example.retrospect.core.models.LoggedInUser;
import com.example.retrospect.core.models.NotFoundUser;
import com.example.retrospect.core.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.retrospect.core.repositories.UserRepository;

@Service
public class UserNameSerialiser {
    private final UserRepository userRepository;

    @Autowired
    public UserNameSerialiser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String serialise(User user){
        if (user == null){
            return null;
        }

        return user.getUsername();
    }

    public User deserialise(LoggedInUser loggedInUser, String id){
        if (id == null){
            return null;
        }

        var user = userRepository.getUser(loggedInUser, id);

        if (user == null){
            return new NotFoundUser(id);
        }

        return user;
    }
}
