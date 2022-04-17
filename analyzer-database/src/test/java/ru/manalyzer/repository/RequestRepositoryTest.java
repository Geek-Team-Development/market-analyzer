package ru.manalyzer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.manalyzer.persist.Request;
import ru.manalyzer.persist.Role;
import ru.manalyzer.persist.User;

import java.util.Date;
import java.util.List;

@DataMongoTest(excludeAutoConfiguration= {EmbeddedMongoAutoConfiguration.class})
@ExtendWith(SpringExtension.class)
public class RequestRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    private static User testUser;

    @BeforeAll
    public static void init() {
        testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setFirstname("Test");
        testUser.setLastname("");
        testUser.setPassword("$2a$12$eulRnwp94lJ1pCIOe3nIS.mVtg4d9tdX03snWvn2GdgidIh/bYjCO");
        testUser.setCity("");
        testUser.getRoles().add(Role.USER);
    }

    @Test
    public void findRequestBySearchStringTest() {
        String searchString = "macbook";

        userRepository.save(testUser);

        Request expectedRequest1 = new Request();
        expectedRequest1.setUserId(testUser.getId());
        expectedRequest1.setSearchString(searchString);
        expectedRequest1.setSearchDate(new Date());

        Request expectedRequest2 = new Request();
        expectedRequest2.setUserId(testUser.getId());
        expectedRequest2.setSearchString(searchString);
        expectedRequest2.setSearchDate(new Date());

        requestRepository.save(expectedRequest1);
        requestRepository.save(expectedRequest2);

        List<Request> requests = requestRepository.findBySearchString(searchString);

        Assertions.assertEquals(2, requests.size());

        requests.forEach(
                request -> Assertions.assertEquals(searchString, request.getSearchString())
        );

        requestRepository.delete(expectedRequest1);
        requestRepository.delete(expectedRequest2);
        userRepository.delete(testUser);
    }

    @Test
    public void findRequestByUserIdTest() {
        String searchString = "macbook";

        userRepository.save(testUser);

        Request expectedRequest1 = new Request();
        expectedRequest1.setUserId(testUser.getId());
        expectedRequest1.setSearchString(searchString);
        expectedRequest1.setSearchDate(new Date());

        Request expectedRequest2 = new Request();
        expectedRequest2.setUserId(testUser.getId());
        expectedRequest2.setSearchString(searchString);
        expectedRequest2.setSearchDate(new Date());

        requestRepository.save(expectedRequest1);
        requestRepository.save(expectedRequest2);

        List<Request> requests = requestRepository.findByUserId(testUser.getId());

        Assertions.assertEquals(2, requests.size());

        requests.forEach(
                request -> Assertions.assertEquals(testUser.getId(), request.getUserId())
        );

        requestRepository.delete(expectedRequest1);
        requestRepository.delete(expectedRequest2);
        userRepository.delete(testUser);
    }

    @Test
    public void requestNotFoundTest() {
        Assertions.assertEquals(0, requestRepository.findBySearchString("unknown").size());
    }
}
