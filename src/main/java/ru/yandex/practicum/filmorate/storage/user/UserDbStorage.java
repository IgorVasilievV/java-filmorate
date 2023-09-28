package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Map;

@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage{

    private final UserDao userDao;

    @Override
    public Map<Long, User> getUsers() {
        return userDao.getUsers();
    }

    @Override
    public User getUser(long id) {
        return userDao.getUser(id).orElseThrow(() -> new NotFoundException("Юзер с id = " + id + " не найден"));
    }

    @Override
    public User createUser(User user) {
        return userDao.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        return userDao.updateUser(user)
                .orElseThrow(() -> new NotFoundException("Юзер с id = " + user.getId() + " не обновлен"));
    }

    @Override
    public User removeUser(long id) {
        return userDao.removeUser(id);
    }

    @Override
    public void addFriend(long idUser, long idFriend) {
        userDao.addFriend(idUser,idFriend);
    }

    @Override
    public void deleteFriend(long idUser, long idFriend) {
        userDao.deleteFriend(idUser,idFriend);
    }

    @Override
    public List<User> getFriends(long idUser) {
        return userDao.getFriends(idUser);
    }
}
