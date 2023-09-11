package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        validationBeforeCreateUser(user);
        userStorage.createUser(user);
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }


    public User updateUser(User user) {
        validationBeforeUpdatedUser(user);
        userStorage.updateUser(user);
        log.debug("Данные пользователя id= {} обновлены. Новые данные: {}", user.getId(), user);
        return user;
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    public User removeUser(long id) {
        return userStorage.removeUser(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
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
        if (!userStorage.getUsers().containsKey(user.getId())) {
            NotFoundException e = new NotFoundException("Пользователь с id= " + user.getId() + " не найден");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

    public void addFriend(long idUser, long idFriend) {
        if (userStorage.getUsers().keySet().containsAll(List.of(idUser, idFriend))) {
            User user = userStorage.getUsers().get(idUser);
            User friend = userStorage.getUsers().get(idFriend);
            if (user.getFriendsIds() == null) {
                user.setFriendsIds(new HashSet<>());
            }
            if (friend.getFriendsIds() == null) {
                friend.setFriendsIds(new HashSet<>());
            }
            user.getFriendsIds().add(idFriend);
            friend.getFriendsIds().add(idUser);
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public void deleteFriend(long idUser, long idFriend) {
        if (userStorage.getUsers().keySet().containsAll(List.of(idUser, idFriend))) {
            User user = userStorage.getUsers().get(idUser);
            User friend = userStorage.getUsers().get(idFriend);
            if (user.getFriendsIds() != null) {
                user.getFriendsIds().remove(idFriend);
            }
            if (friend.getFriendsIds() != null) {
                friend.getFriendsIds().remove(idUser);
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public List<User> getFriends(long idUser) {
        if (userStorage.getUsers().containsKey(idUser)) {
            List<User> friends = new ArrayList<>();
            if (userStorage.getUsers().get(idUser).getFriendsIds() != null) {
                Set<Long> idFriend = userStorage.getUsers().get(idUser).getFriendsIds();
                idFriend.forEach(s -> friends.add(userStorage.getUsers().get(s)));
            }
            return friends;
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public List<User> getCommonFriends(long idUser, long idFriend) {
        if (userStorage.getUsers().keySet().containsAll(List.of(idUser, idFriend))) {
            User user = userStorage.getUsers().get(idUser);
            User friend = userStorage.getUsers().get(idFriend);
            List<User> commonFriends = new ArrayList<>();
            if (user.getFriendsIds() != null && friend.getFriendsIds() != null) {
                Set<Long> idCommonFriends = new HashSet<>(user.getFriendsIds());
                idCommonFriends.retainAll(friend.getFriendsIds());
                if (!idCommonFriends.isEmpty()) {
                    idCommonFriends.forEach(s -> commonFriends.add(userStorage.getUsers().get(s)));
                }
            }
            return commonFriends;
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

}
