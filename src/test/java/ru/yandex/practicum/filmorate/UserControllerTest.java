package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    private UserController userController;
    private User userValid;
    private User userWithEmptyEmail;
    private User userWithInvalidEmail;
    private User userWithEmptyLogin;
    private User userWithInvalidLogin;
    private User userWithInvalidBirthday;

    @BeforeEach
    private void createUserControllerInstant() {
        userController = new UserController();
    }

    @BeforeEach
    private void createUser() {
        userValid = new User()
                .setEmail("practicum@mail.ru")
                .setLogin("login")
                .setName("Vasya")
                .setBirthday(LocalDate.of(2000, 12, 01));
        userWithEmptyEmail = new User()
                .setLogin("login")
                .setName("Vasya")
                .setBirthday(LocalDate.of(2000, 12, 01));
        userWithInvalidEmail = new User()
                .setEmail("@practicummail.ru")
                .setLogin("login")
                .setName("Vasya")
                .setBirthday(LocalDate.of(2000, 12, 01));
        userWithEmptyLogin = new User()
                .setEmail("practicum@mail.ru")
                .setName("Vasya")
                .setBirthday(LocalDate.of(2000, 12, 01));
        userWithInvalidLogin = new User()
                .setEmail("practicum@mail.ru")
                .setLogin("log in")
                .setName("Vasya")
                .setBirthday(LocalDate.of(2000, 12, 01));
        userWithInvalidBirthday = new User()
                .setEmail("practicum@mail.ru")
                .setLogin("login")
                .setName("Vasya")
                .setBirthday(LocalDate.of(3000, 12, 01));
    }

    @Test
    void createUser_shouldCreateValidUser() {
        userController.createUser(userValid);
        User userActual = userController.getAllUsers().get(0);
        assertEquals(userValid, userActual, "Пользователь добавлен неверно");
    }

    @Test
    void createUser_shouldNotCreateUserWithEmptyEmail() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> userController.createUser(userWithEmptyEmail));
        int sizeListUsersActual = userController.getAllUsers().size();
        assertAll(
                () -> assertEquals(0, sizeListUsersActual, "Пользователь добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Некорректный формат почты")
        );
    }

    @Test
    void createUser_shouldNotCreateUserWithInvalidEmail() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> userController.createUser(userWithInvalidEmail));
        int sizeListUsersActual = userController.getAllUsers().size();
        assertAll(
                () -> assertEquals(0, sizeListUsersActual, "Пользователь добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Некорректный формат почты")
        );
    }

    @Test
    void createUser_shouldNotCreateUserWithEmptyLogin() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> userController.createUser(userWithEmptyLogin));
        int sizeListUsersActual = userController.getAllUsers().size();
        assertAll(
                () -> assertEquals(0, sizeListUsersActual, "Пользователь добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Некорректный формат логина")
        );
    }

    @Test
    void createUser_shouldNotCreateUserWithInvalidLogin() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> userController.createUser(userWithInvalidLogin));
        int sizeListUsersActual = userController.getAllUsers().size();
        assertAll(
                () -> assertEquals(0, sizeListUsersActual, "Пользователь добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Некорректный формат логина")
        );
    }

    @Test
    void createUser_shouldNotCreateUserWithInvalidBirthday() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> userController.createUser(userWithInvalidBirthday));
        int sizeListUsersActual = userController.getAllUsers().size();
        assertAll(
                () -> assertEquals(0, sizeListUsersActual, "Пользователь добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Некорректная дата рождения")
        );
    }

}