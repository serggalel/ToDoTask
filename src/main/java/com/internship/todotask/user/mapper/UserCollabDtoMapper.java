package com.internship.todotask.user.mapper;

import com.internship.todotask.user.model.dto.UserCollabDto;
import com.internship.todotask.user.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserCollabDtoMapper {

    public UserCollabDto fromEntity(UserEntity userEntity) {
        UserCollabDto userCollabDto = new UserCollabDto();
        userCollabDto.setFirstName(userEntity.getFirstName());
        userCollabDto.setLastName(userEntity.getLastName());
        return userCollabDto;
    }

}
