package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.user.entities.Identity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    Identity findByPhoneNumber(String phoneNumber);

    Identity findByEmail(String email);

    Identity getUserById(Long userId);

    List<Identity> getAllUsers();

    Identity createUser(Identity identity);

    Identity updateUser(Long userId, Identity identity);
    void deleteUser(Long userId);
}
