package ru.manalyzer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.manalyzer.AnalyzerBackendApiAppApplication;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.UserRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AnalyzerBackendApiAppApplication.class)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static UserDto newUser;

    @BeforeAll
    public static void initClass() {
        newUser = new UserDto();
        newUser.setEmail("new@mail.ru");
        newUser.setFirstName("first");
        newUser.setLastName("last");
        newUser.setPassword("pass");
        newUser.setCity("city");
    }

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void signInTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/signin")
                        .with(httpBasic("admin@mail.ru", "admin")))
//                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(authenticated().withUsername("admin@mail.ru"));

    }

    @Test
    public void signUpTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(newUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/signin")
                        .with(httpBasic(newUser.getEmail(), newUser.getPassword())))
//                .andExpect(authenticated().withRoles("USER"))
                .andExpect(authenticated().withUsername(newUser.getEmail()));

        deleteNewUser();
    }

    private void deleteNewUser() {
        User savedUser = userRepository.findByEmail(newUser.getEmail()).get();
        userRepository.delete(savedUser);
    }

    @Test
    public void failSignInTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/signin")
                        .with(httpBasic("admin@mail.ru", "")))
                .andExpect(unauthenticated());
    }
}
