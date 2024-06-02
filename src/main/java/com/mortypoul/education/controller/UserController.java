package com.mortypoul.education.controller;

import com.mortypoul.education.dto.UserDto;
import com.mortypoul.education.dto.UserDtoPersister;
import com.mortypoul.education.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping("/create")
    public UserDto create(@RequestBody @Valid UserDtoPersister input) {
        return userService.save(input);
    }

    @PostMapping("/changePassword")
    public void changePassword(Long userId, String password) {
        userService.changePassword(userId, password);
    }

    @PostMapping("/changeRole")
    public void changeRole(Long userId, String roles) {
        userService.changeRule(userId, roles);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

}

