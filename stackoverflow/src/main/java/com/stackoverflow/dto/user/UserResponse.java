package com.stackoverflow.dto.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {
    private String name;
    private String surname;
    private String username;
}
