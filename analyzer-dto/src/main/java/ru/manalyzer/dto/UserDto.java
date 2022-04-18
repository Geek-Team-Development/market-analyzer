package ru.manalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends AbstractPersistentDto {

    private String email;

    private String password;

    private String firstname;

    private String lastname;

    private String city;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(this.getId(), userDto.getId()) && Objects.equals(email, userDto.email) &&
                Objects.equals(password, userDto.password) && Objects.equals(firstname, userDto.firstname) &&
                Objects.equals(lastname, userDto.lastname) && Objects.equals(city, userDto.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), email, password, firstname, lastname, city);
    }
}
