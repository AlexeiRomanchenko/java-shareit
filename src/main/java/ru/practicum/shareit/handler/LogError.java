package ru.practicum.shareit.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogError {
    UNEXPECTED_ERROR("Произошла непредвиденная ошибка."),
    OBJECT_NOT_FOUND("Объект не найден."),
    OBJECT_ALREADY_EXIST("Объект уже существует"),
    INCORRECT_REQUEST("Некорректный запрос.");
    private final String message;

}