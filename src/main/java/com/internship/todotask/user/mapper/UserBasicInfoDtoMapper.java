package com.internship.todotask.user.mapper;

import com.internship.todotask.user.model.dto.UserBasicInfoDto;
import com.internship.todotask.user.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserBasicInfoDtoMapper {

    public UserBasicInfoDto fromEntity(UserEntity userEntity) {
        UserBasicInfoDto userBasicInfoDto = new UserBasicInfoDto();
        userBasicInfoDto.setId(userEntity.getId());
        userBasicInfoDto.setFirstName(userEntity.getFirstName());
        userBasicInfoDto.setLastName(userEntity.getLastName());
        return userBasicInfoDto;
    }

}
