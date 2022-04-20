package ru.manalyzer.service;

import ru.manalyzer.dto.UserDto;


public interface AuthenticationService {

    UserDto findUserByEmail(String email);

    UserDto saveNewUser(UserDto userDto);
}
