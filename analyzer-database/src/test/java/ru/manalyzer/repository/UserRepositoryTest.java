package ru.manalyzer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import ru.manalyzer.persist.Role;
import ru.manalyzer.persist.User;

@DataMongoTest
@OverrideAutoConfiguration(enabled = true)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static User expectedUser;

    @BeforeAll
    public static void init() {
        expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        expectedUser.setFirstName("Test");
        expectedUser.setLastName("");
        expectedUser.setPassword("$2a$12$eulRnwp94lJ1pCIOe3nIS.mVtg4d9tdX03snWvn2GdgidIh/bYjCO");
        expectedUser.setCity("");
        expectedUser.getRoles().add(Role.ADMIN);
    }

    @Test
    public void findUserByEmailTest() {
        userRepository.save(expectedUser);

        User user = userRepository.findByEmail(expectedUser.getEmail()).orElseThrow(IllegalArgumentException::new);

        Assertions.assertEquals(expectedUser.getEmail(), user.getEmail());
        Assertions.assertEquals(expectedUser.getFirstName(), user.getFirstName());
        Assertions.assertEquals(expectedUser.getLastName(), user.getLastName());
        Assertions.assertEquals(expectedUser.getPassword(), user.getPassword());
        Assertions.assertEquals(expectedUser.getCity(), user.getCity());
        Assertions.assertArrayEquals(expectedUser.getRoles().toArray(), user.getRoles().toArray());

        userRepository.delete(expectedUser);
    }

    @Test
    public void userNotFoundTest() {
        Assertions.assertFalse(userRepository.findByEmail("unknown").isPresent());
    }

    @Test
    public void failDuplicateEmailTest() {
        userRepository.save(expectedUser);

        User duplicateUser = new User();
        duplicateUser.setEmail("test@mail.ru");
        duplicateUser.setFirstName("Duplicate");
        duplicateUser.setLastName("");
        duplicateUser.setPassword("$2a$12$eulRnwp94lJ1pCIOe3nIS.mVtg4d9tdX03snWvn2GdgidIh/bYjCO");
        duplicateUser.setCity("");
        duplicateUser.getRoles().add(Role.USER);

        Assertions.assertThrows(DuplicateKeyException.class, () -> userRepository.save(duplicateUser));

        userRepository.delete(expectedUser);
    }

}
