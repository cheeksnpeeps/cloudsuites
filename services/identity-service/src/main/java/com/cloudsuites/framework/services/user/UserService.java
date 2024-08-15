package com.cloudsuites.framework.services.user;

import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.user.entities.Identity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    Identity findByPhoneNumber(String phoneNumber);

    Identity findByEmail(String email);

    Identity getUserById(String userId);

    List<Identity> getAllUsers();

    Identity createUser(Identity identity) throws UserAlreadyExistsException;

    Identity updateUser(String userId, Identity identity);

    void deleteUser(String userId);

    boolean existsByEmail(String username);
}
