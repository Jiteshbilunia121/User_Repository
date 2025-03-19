package UserService;


import Entity.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }


    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
