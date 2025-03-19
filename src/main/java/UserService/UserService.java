package UserService;



import Entity.User;

import java.util.Optional;

public interface UserService {

    User registerUser(User user); // Register a new user

    Optional<User> getUserByEmail(String email); // Fetch user by email
}
