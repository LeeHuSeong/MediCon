package com.medicon.server.service;

import com.medicon.server.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean validateUser(String id, String pw) {
        try {
            return userRepository.existsByIdAndPassword(id, pw);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
