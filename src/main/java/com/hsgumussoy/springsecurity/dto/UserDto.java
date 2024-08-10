package com.hsgumussoy.springsecurity.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String nameSurname;
    private String password;
}
