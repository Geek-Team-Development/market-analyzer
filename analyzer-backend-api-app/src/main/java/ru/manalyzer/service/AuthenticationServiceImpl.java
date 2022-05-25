package ru.manalyzer.service;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.Role;
import ru.manalyzer.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {

    private final UserRepository userRepository;

    private final Mapper<ru.manalyzer.persist.User, UserDto> userMapper;

    private final PasswordEncoder passwordEncoder;

    private final DirectExchange frontNotifyExchange;

    private final AmqpAdmin amqpAdmin;

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository,
                                     Mapper<ru.manalyzer.persist.User,
                                             UserDto> userMapper,
                                     PasswordEncoder passwordEncoder,
                                     DirectExchange frontNotifyExchange,
                                     AmqpAdmin amqpAdmin) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.frontNotifyExchange = frontNotifyExchange;
        this.amqpAdmin = amqpAdmin;
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

    @Override
    public UserDto saveNewUser(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setRoles(List.of(Role.USER));
        ru.manalyzer.persist.User savedUser = userRepository.save(userMapper.toEntity(userDto));
        createProductUpdateQueueForUser(savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    private void createProductUpdateQueueForUser(String userId) {
        Queue frontNotifyQueue = new Queue("front.notify.queue." + userId, false);
        Binding binding = BindingBuilder
                .bind(frontNotifyQueue)
                .to(frontNotifyExchange)
                .with(userId);
        amqpAdmin.declareQueue(frontNotifyQueue);
        amqpAdmin.declareBinding(binding);
    }
}
