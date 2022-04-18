package ru.manalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.service.AuthenticationService;

@RestController
@RequestMapping("/")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/signin")
    public UserDto signIn(Authentication authentication) {
        return authenticationService.findUserByEmail(((User) authentication.getPrincipal()).getUsername());
    }
}
