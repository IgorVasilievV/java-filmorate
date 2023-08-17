package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final List<User> users = new ArrayList<>();
    private int id = 0;

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        validationBeforeCreateUser(user);
        if (user.getId() == 0) {
            user.setId(++id);
        } else {
            id = user.getId();
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.add(user);
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }


    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        User oldUser = validationBeforeUpdatedUser(user);
        users.remove(oldUser);
        users.add(user);
        log.debug("Данные пользователя id= {} обновлены. Новые данные: {}", user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
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

    private User validationBeforeUpdatedUser(User user) throws ValidationException {
        Optional<User> oldUser = users.stream()
                .filter(u -> user.getId() == u.getId())
                .findFirst();
        if (oldUser.isEmpty()) {
            ValidationException e = new ValidationException("Пользователь с id= " + user.getId() + " не найден");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else {
            return oldUser.get();
        }
    }
}
