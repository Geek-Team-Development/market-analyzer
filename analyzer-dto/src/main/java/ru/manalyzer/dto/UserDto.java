package ru.manalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.manalyzer.persist.Role;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends AbstractPersistentDto {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String city;

    private String telegramChatId;

    private List<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(this.getId(), userDto.getId()) && Objects.equals(email, userDto.email) &&
                Objects.equals(password, userDto.password) && Objects.equals(firstName, userDto.firstName) &&
                Objects.equals(lastName, userDto.lastName) && Objects.equals(city, userDto.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), email, password, firstName, lastName, city);
    }
}
