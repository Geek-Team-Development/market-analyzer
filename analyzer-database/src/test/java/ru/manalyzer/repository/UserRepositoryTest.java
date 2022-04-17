package ru.manalyzer.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.manalyzer.persist.Role;
import ru.manalyzer.persist.User;
import org.springframework.dao.DuplicateKeyException;


@DataMongoTest(excludeAutoConfiguration= {EmbeddedMongoAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static User expectedUser;

    @BeforeAll
    public static void init() {
        expectedUser = new User();
        expectedUser.setEmail("test@mail.ru");
        expectedUser.setFirstname("Test");
        expectedUser.setLastname("");
        expectedUser.setPassword("$2a$12$eulRnwp94lJ1pCIOe3nIS.mVtg4d9tdX03snWvn2GdgidIh/bYjCO");
        expectedUser.setCity("");
        expectedUser.getRoles().add(Role.ADMIN);
    }

    @Test
    public void findUserByIdTest() {
        userRepository.save(expectedUser);

        User user = userRepository.findByEmail(expectedUser.getEmail()).orElseThrow(IllegalArgumentException::new);

        Assertions.assertEquals(expectedUser.getEmail(), user.getEmail());
        Assertions.assertEquals(expectedUser.getFirstname(), user.getFirstname());
        Assertions.assertEquals(expectedUser.getLastname(), user.getLastname());
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
        duplicateUser.setFirstname("Duplicate");
        duplicateUser.setLastname("");
        duplicateUser.setPassword("$2a$12$eulRnwp94lJ1pCIOe3nIS.mVtg4d9tdX03snWvn2GdgidIh/bYjCO");
        duplicateUser.setCity("");
        duplicateUser.getRoles().add(Role.USER);

        Assertions.assertThrows(DuplicateKeyException.class, () -> userRepository.save(duplicateUser));

        userRepository.delete(expectedUser);
    }

}
