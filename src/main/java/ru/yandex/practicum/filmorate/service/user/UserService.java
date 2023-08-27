package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService {

    private final Map<Long, User> users;
    private long id = 0;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        users = userStorage.getUsers();
    }

    public User createUser(User user) {
        validationBeforeCreateUser(user);
        if (user.getId() == 0) {
            user.setId(++id);
        } else {
            id = user.getId();
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }


    public User updateUser(User user) {
        validationBeforeUpdatedUser(user);
        users.put(user.getId(), user);
        log.debug("Данные пользователя id= {} обновлены. Новые данные: {}", user.getId(), user);
        return user;
    }

    public User getUser(long id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            return user;
        } else {
            throw new NotFoundException("В хранилище нет пользователя с id = " + id);
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void validationBeforeCreateUser(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isBlank() ||
                !Pattern.matches("^(\\S+)(@)(\\S+)$", user.getEmail())) {
            ValidationException e = new ValidationException("Некорректный формат почты");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            ValidationException e = new ValidationException("Некорректный формат логина");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else if (LocalDate.now().isBefore(user.getBirthday())) {
            ValidationException e = new ValidationException("Некорректная дата рождения");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

    private void validationBeforeUpdatedUser(User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            NotFoundException e = new NotFoundException("Пользователь с id= " + user.getId() + " не найден");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

    public void addFriend(long idUser, long idFriend) {
        if (users.keySet().containsAll(List.of(idUser, idFriend))) {
            User user = users.get(idUser);
            User friend = users.get(idFriend);
            if (user.getFriends() == null) {
                user.setFriends(new HashSet<>());
            }
            if (friend.getFriends() == null) {
                friend.setFriends(new HashSet<>());
            }
            user.getFriends().add(idFriend);
            friend.getFriends().add(idUser);
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public void deleteFriend(long idUser, long idFriend) {
        if (users.keySet().containsAll(List.of(idUser, idFriend))) {
            User user = users.get(idUser);
            User friend = users.get(idFriend);
            if (user.getFriends() != null) {
                user.getFriends().remove(idFriend);
            }
            if (friend.getFriends() != null) {
                friend.getFriends().remove(idUser);
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public List<User> getFriends(long idUser) {
        if (users.containsKey(idUser)) {
            List<User> friends = new ArrayList<>();
            if (users.get(idUser).getFriends() != null) {
                Set<Long> idFriend = users.get(idUser).getFriends();
                idFriend.forEach(s -> friends.add(users.get(s)));
            }
            return friends;
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public List<User> getCommonFriends(long idUser, long idFriend) {
        if (users.keySet().containsAll(List.of(idUser, idFriend))) {
            User user = users.get(idUser);
            User friend = users.get(idFriend);
            List<User> commonFriends = new ArrayList<>();
            if (user.getFriends() != null && friend.getFriends() != null) {
                Set<Long> idCommonFriends = new HashSet<>(user.getFriends());
                idCommonFriends.retainAll(friend.getFriends());
                if (!idCommonFriends.isEmpty()) {
                    idCommonFriends.forEach(s -> commonFriends.add(users.get(s)));
                }
            }
            return commonFriends;
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

}
