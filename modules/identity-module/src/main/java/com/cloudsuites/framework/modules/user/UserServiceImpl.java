package com.cloudsuites.framework.modules.user;


import com.cloudsuites.framework.modules.user.repository.UserRepository;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Identity findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }

    @Override
    public Identity findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Identity getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<Identity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Identity createUser(Identity identity) throws UserAlreadyExistsException {
        Optional<Identity> existingIdentity = userRepository.findByEmail(identity.getEmail());
        if (existingIdentity.isEmpty()) {
            return userRepository.save(identity);
        }
        throw new UserAlreadyExistsException("Username already exists: " + identity.getEmail());
    }

    @Override
    public Identity updateUser(String userId, Identity identity) {
        if (userRepository.existsById(userId)) {
            identity.setUserId(userId);
            return userRepository.save(identity);
        }
        return null; // Identity not found
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsByEmail(String username) {
        return userRepository.existsByEmail(username);
    }

}
