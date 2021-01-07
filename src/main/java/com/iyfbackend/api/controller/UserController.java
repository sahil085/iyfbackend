package com.iyfbackend.api.controller;

import com.iyfbackend.api.dto.UserDTO;
import com.iyfbackend.api.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    @GetMapping("/volunteers")
    public List<String> fetchAllVolunteers() {
        return userService.fetchAllVolunteers();
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody UserDTO userDTO) {
        return userService.registerUser(userDTO);
    }

    @PostMapping("/update")
    public String update(@Valid @RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @GetMapping("/fetchAll")
    public List<UserDTO> fetchAllUsers() {
        return userService.fetchAllUsers();
    }

}
