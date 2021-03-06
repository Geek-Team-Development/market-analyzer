package ru.manalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable String id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @PutMapping(value = "/telegram", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void addTelegramId(@RequestBody String chatId, Authentication authentication) {
        userService.addTelegramId(((User) authentication.getPrincipal()).getUsername(), chatId);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto updateUser(@RequestBody UserDto userDto) {
        return userService.updateUser(userDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto updatePassword(@PathVariable String id, @RequestBody String password) {
        return userService.updatePassword(id, password);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteById(id);
    }
}