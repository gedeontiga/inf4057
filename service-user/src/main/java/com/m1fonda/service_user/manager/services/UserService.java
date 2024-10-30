package com.m1fonda.service_user.manager.services;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.manager.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final RabbitTemplate rabbitTemplate;
    private UserRepository userRepository;

    public void saveUser(Users user) {
        userRepository.save(user);
    }

    public List<Users> deleteUser(Long userId) {
        userRepository.deleteById(userId);
        return userRepository.findAll();
    }

    public List<Users> getUsers() {
        return userRepository.findAll();
    }

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException());
    }

    public void sendUserData(String message) {
        rabbitTemplate.convertAndSend("spring-boot-exchange", "user", message);
    }
}
