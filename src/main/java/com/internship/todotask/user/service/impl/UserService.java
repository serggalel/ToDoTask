package com.internship.todotask.user.service.impl;

import com.internship.todotask.user.model.dto.UserDto;
import com.internship.todotask.user.model.entity.UserEntity;
import com.internship.todotask.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .roles(userEntity.getRole().toString())
                .build();
    }

    public UserDto getCurrentUser() {
        return (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
