package com.genius.lol.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String username;
    private String email;
    private String nickname;
}
