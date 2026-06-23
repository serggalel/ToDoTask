package com.internship.todotask.user.model.dto;

import com.internship.todotask.user.model.dictionary.ConverterEnum;
import com.internship.todotask.user.model.dictionary.Role;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
public class UserDetailsDto implements UserDetails {

    @NotNull(message = "Id is mandatory")
    @Positive
    private Long id;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    private String password;

    @Convert(converter = ConverterEnum.class)
    @NotNull(message = "Role is mandatory")
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.toString().toLowerCase(Locale.ROOT)));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
