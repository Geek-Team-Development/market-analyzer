package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.Role;
import ru.manalyzer.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {

    private final UserRepository userRepository;

    private final Mapper<ru.manalyzer.persist.User, UserDto> userMapper;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, Mapper<ru.manalyzer.persist.User, UserDto> userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(
                        user -> new User(
                                user.getEmail(),
                                user.getPassword(),
                                mapRolesToAuthorities(user.getRoles())
                        )
                )
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
