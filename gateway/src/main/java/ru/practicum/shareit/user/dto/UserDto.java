package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDto {
    @Positive
    private Long id;
    @Email
    @NotBlank
    private String email;
    @Size(max = 255)
    @NotBlank
    private String name;

}