package com.internship.todotask.user.mapper;

import com.internship.todotask.user.model.dto.UserDetailsDto;
import com.internship.todotask.user.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsDtoMapper {

    public UserDetailsDto fromEntity(UserEntity userEntity) {
        UserDetailsDto userDto = new UserDetailsDto();
        userDto.setId(userEntity.getId());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setEmail(userEntity.getEmail());
        userDto.setPassword(userEntity.getPassword());
        userDto.setRole(userEntity.getRole());
        return userDto;
    }

}
