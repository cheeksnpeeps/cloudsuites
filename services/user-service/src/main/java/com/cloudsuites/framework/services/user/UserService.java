package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.common.entities.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public User getUserById(Long userId);
    public List<User> getAllUsers();
    public User createUser(User user);
    public User updateUser(Long userId, User user);
    public void deleteUser(Long userId);


}
