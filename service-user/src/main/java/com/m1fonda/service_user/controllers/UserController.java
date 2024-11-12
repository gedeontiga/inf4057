package com.m1fonda.service_user.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.services.UserService;

import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@AllArgsConstructor
@RequestMapping("/user-api")
public class UserController {

    private UserService userService;

    @GetMapping("/")
    public List<Users> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/add")
    public List<Users> postMethodName(@RequestBody Users user) {
        userService.saveUser(user); // save the user to the database

        return userService.getUsers();
    }

    @GetMapping("/delete/{id}")
    public List<Users> getMethodName(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String message) {
        userService.sendUserData(message);
        return "Message sent to RabbitMQ!";
    }

}
