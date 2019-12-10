package serialisers;

import models.Identifiable;
import models.NotFoundUser;
import models.User;
import repositories.UserRepository;

public class UserSerialiser {
    private final UserRepository userRepository;

    public UserSerialiser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String serialise(Identifiable user){
        return user.getId();
    }

    public User deserialise(String id){
        var user = userRepository.getUser(id);

        if (user == null){
            return new NotFoundUser(id);
        }

        return user;
    }
}
