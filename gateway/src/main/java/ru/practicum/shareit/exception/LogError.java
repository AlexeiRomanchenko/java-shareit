package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogError {
    UNEXPECTED_ERROR("Произошла непредвиденная ошибка."),
    OBJECT_NOT_FOUND("Объект не найден."),
    OBJECT_ALREADY_EXIST("Объект уже существует"),
    INCORRECT_REQUEST("Некорректный запрос."),
    UNSUPPORTED_STATUS("Unknown state: UNSUPPORTED_STATUS");
    private final String message;

}