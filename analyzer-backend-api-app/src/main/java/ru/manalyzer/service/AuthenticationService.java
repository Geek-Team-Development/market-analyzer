package ru.manalyzer.service;

import ru.manalyzer.dto.UserDto;
import ru.manalyzer.persist.User;

import java.util.Optional;

public interface AuthenticationService {

    UserDto findUserByEmail(String email);
}
