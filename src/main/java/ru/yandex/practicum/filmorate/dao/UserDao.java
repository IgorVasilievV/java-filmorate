package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> getUsers();

    Optional<User> getUser(long id);

    User createUser(User user);

    User updateUser(User user);

    User removeUser(long id);

    void addFriend(long idUser, long idFriend);

    void deleteFriend(long idUser, long idFriend);

    List<User> getFriends(long idUser);
}
