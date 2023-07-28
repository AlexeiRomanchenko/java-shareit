package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
public class User {
    @Positive
    private Long id;
    @Email
    @NotBlank
    private String email;
    @Size(max = 255)
    @NotBlank
    private String name;

}