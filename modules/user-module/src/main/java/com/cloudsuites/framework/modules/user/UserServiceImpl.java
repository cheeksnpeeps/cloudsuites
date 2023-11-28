package com.cloudsuites.framework.modules.user;

import com.cloudsuites.framework.services.common.entities.user.User;
import com.cloudsuites.framework.services.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (userRepository.existsById(userId)) {
            user.setId(userId);
            return userRepository.save(user);
        }
        return null; // User not found
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
