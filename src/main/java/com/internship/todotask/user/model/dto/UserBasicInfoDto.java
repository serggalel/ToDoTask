package com.internship.todotask.user.model.dto;

import com.internship.todotask.user.model.dictionary.ConverterEnum;
import com.internship.todotask.user.model.dictionary.Role;
import jakarta.persistence.Convert;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserBasicInfoDto {

    private Long id;

    private String firstName;

    private String lastName;

    @Convert(converter = ConverterEnum.class)
    private Role role;

}
