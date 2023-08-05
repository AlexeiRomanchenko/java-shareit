package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Builder(toBuilder = true)
@Data
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