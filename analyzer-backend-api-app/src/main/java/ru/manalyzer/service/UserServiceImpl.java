package ru.manalyzer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper<User, UserDto> userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           Mapper<User, UserDto> userMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream().map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDto> getUserById(String id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        userDto.setPassword(user.getPassword());
        user = userRepository.save(userMapper.toEntity(userDto));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updatePassword(String id, String password) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(password));
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}
