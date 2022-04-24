package ru.manalyzer.dbmigrations;

import io.mongock.api.annotations.*;
import ru.manalyzer.persist.Role;
import ru.manalyzer.persist.User;
import ru.manalyzer.repository.UserRepository;

import java.util.List;

@ChangeUnit(id = "fill-collection-users", order = "2", author = "root")
public class UserCollectionFillChangeLog {

    private static final String EMAIL = "admin@mail.ru";

    @Execution
    public void execution(UserRepository userRepository) {
        User user = new User();
        user.setEmail(EMAIL);
        user.setFirstName("Admin");
        user.setLastName("");
        user.setPassword("$2a$12$eulRnwp94lJ1pCIOe3nIS.mVtg4d9tdX03snWvn2GdgidIh/bYjCO");
        user.setCity("Moscow");
        user.setRoles(List.of(Role.ADMIN));

        userRepository.save(user);
    }

    @RollbackExecution
    public void rollback(UserRepository userRepository) {
        User user = userRepository.findByEmail(EMAIL).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }
}
