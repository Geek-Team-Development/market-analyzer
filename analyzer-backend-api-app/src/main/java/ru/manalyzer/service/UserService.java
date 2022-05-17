package ru.manalyzer.service;

import ru.manalyzer.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getUsers();

    Optional<UserDto> getUserById(String id);

    UserDto updateUser(UserDto user);

    UserDto updatePassword(String id, String password);

    void deleteById(String id);
}