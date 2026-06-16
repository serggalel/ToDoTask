package com.internship.todotask.user.service.impl;

import com.internship.todotask.user.mapper.UserDetailsDtoMapper;
import com.internship.todotask.user.model.entity.UserEntity;
import com.internship.todotask.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    /* in our case we will use email field instead of username,
    * since it is the email that is unique and not the username
    */

    private final UserRepository userRepository;

    private final UserDetailsDtoMapper userDetailsDtoMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return userDetailsDtoMapper.fromEntity(userEntity);
    }

}
