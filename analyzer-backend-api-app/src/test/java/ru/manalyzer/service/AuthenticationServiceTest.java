package ru.manalyzer.service;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.mapper.Mapper;
import ru.manalyzer.mapper.UserMapper;
import ru.manalyzer.persist.Role;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.UserRepository;

import java.util.Optional;

public class AuthenticationServiceTest {

    private static AuthenticationService authenticationService;

    private static UserRepository userRepository;

    @BeforeAll
    public static void init() {
        userRepository = Mockito.mock(UserRepository.class);
        Mapper<User, UserDto> userMapper = new UserMapper<>(new ModelMapper(), User.class, UserDto.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authenticationService = new AuthenticationServiceImpl(userRepository, userMapper, passwordEncoder);
    }

    @Test
    public void findUserByEmailTest() {
        User user = new User();
        user.setId("1");
        user.setEmail("test@mail.ru");
        user.setFirstName("first");
        user.setLastName("last");
        user.setPassword("pass");
        user.setCity("city");
        user.getRoles().add(Role.USER);

        Mockito.when(userRepository.findByEmail("test@mail.ru"))
                .thenReturn(Optional.of(user));

        UserDto savedUserDto = authenticationService.findUserByEmail("test@mail.ru");

        Assertions.assertEquals("1", savedUserDto.getId());
        Assertions.assertEquals("test@mail.ru", savedUserDto.getEmail());
        Assertions.assertEquals("first", savedUserDto.getFirstName());
        Assertions.assertEquals("last", savedUserDto.getLastName());
        Assertions.assertEquals("pass", savedUserDto.getPassword());
        Assertions.assertEquals("city", savedUserDto.getCity());
        MatcherAssert.assertThat(savedUserDto.getRoles(), Matchers.hasItem(Role.USER));

    }
}
