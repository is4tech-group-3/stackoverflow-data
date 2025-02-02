package com.stackoverflow.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long idUser;
    private String name;
    private String surname;
    private String username;
    private String image;
}
