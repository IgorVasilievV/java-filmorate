package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.models.User;

import java.util.*;

public interface UserStorage {

    List<User> getUsers();

    User getUser(long id);

    User createUser(User user);

    User updateUser(User user);

    User removeUser(long id);

    void addFriend(long idUser, long idFriend);

    void deleteFriend(long idUser, long idFriend);

    List<User> getFriends(long idUser);
}
