package com.hsgumussoy.springsecurity.service;

import com.hsgumussoy.springsecurity.dto.UserDto;
import com.hsgumussoy.springsecurity.dto.UserRequest;
import com.hsgumussoy.springsecurity.dto.UserResponse;
import com.hsgumussoy.springsecurity.entity.User;
import com.hsgumussoy.springsecurity.enums.Role;
import com.hsgumussoy.springsecurity.repository.UserRepository;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public UserResponse save(UserDto userDto) {
        User user =
                User.builder()
                .username(userDto.getUsername())
                .nameSurname(userDto.getNameSurname())
                .password(userDto.getPassword())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        var token = jwtService.generateToken(user);

        return UserResponse.builder()
                .token(token)
                .build();
    }

    public UserResponse auth(UserRequest userRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(),userRequest.getPassword()));
        User user = userRepository.findByUsername(userRequest.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return UserResponse.builder().token(token).build();
    }
}
