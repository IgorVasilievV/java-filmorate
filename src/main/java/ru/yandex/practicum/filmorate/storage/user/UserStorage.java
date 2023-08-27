package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.models.User;

import java.util.Map;

public interface UserStorage {
    Map<Long, User> getUsers();
}
