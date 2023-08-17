package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;

@SpringBootTest
public class FilmControllerTest {
    private FilmController filmController;
    private Film filmValid;
    private Film filmWithoutName;
    private Film filmWithOverLengthDescription;
    private Film filmWithOlderRelease;
    private Film filmWithNegativeDuration;

    @BeforeEach
    private void createFilmControllerInstant() {
        filmController = new FilmController();
    }

    @BeforeEach
    private void createFilm() {
        filmValid = new Film()
                .setName("Terminator")
                .setDescription("3 parts")
                .setReleaseDate(LocalDate.of(2000, 01, 29))
                .setDuration(30);
        filmWithoutName = new Film()
                .setDescription("3 parts")
                .setReleaseDate(LocalDate.of(2000, 01, 29))
                .setDuration(30);
        filmWithOverLengthDescription = new Film()
                .setName("Terminator")
                .setDescription("Over length description>Over length description>Over length description>" +
                        "Over length description>Over length description>Over length description>" +
                        "Over length description>Over length description>Over length description>")
                .setReleaseDate(LocalDate.of(2000, 01, 29))
                .setDuration(30);
        filmWithOlderRelease = new Film()
                .setName("Terminator")
                .setDescription("3 parts")
                .setReleaseDate(LocalDate.of(1500, 01, 29))
                .setDuration(30);
        filmWithNegativeDuration = new Film()
                .setName("Terminator")
                .setDescription("3 parts")
                .setReleaseDate(LocalDate.of(2000, 01, 29))
                .setDuration(-30);
    }

    @Test
    void addFilm_shouldAddValidFilm() {
        filmController.addFilm(filmValid);
        Film filmActual = filmController.getAllFilms().get(0);
        assertEquals(filmValid, filmActual, "Фильм добавлен неверно");
    }

    @Test
    void addFilm_shouldNotAddFilmWithoutName() {
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(filmWithoutName));
        int sizeListFilmsActual = filmController.getAllFilms().size();
        assertAll(
                () -> assertEquals(0, sizeListFilmsActual, "Фильм добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Пустое название фильма")
        );
    }

    @Test
    void addFilm_shouldNotAddFilmWithOverLengthDescription() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> filmController.addFilm(filmWithOverLengthDescription));
        int sizeListFilmsActual = filmController.getAllFilms().size();
        assertAll(
                () -> assertEquals(0, sizeListFilmsActual, "Фильм добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Длина описания фильма превышает 200 символов")
        );
    }

    @Test
    void addFilm_shouldNotAddFilmWithOlderRelease() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> filmController.addFilm(filmWithOlderRelease));
        int sizeListFilmsActual = filmController.getAllFilms().size();
        assertAll(
                () -> assertEquals(0, sizeListFilmsActual, "Фильм добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Дата релиза фильма раньше 28-12-1895")
        );
    }

    @Test
    void addFilm_shouldNotAddFilmWithNegativeDuration() {
        ValidationException e = assertThrows(ValidationException.class,
                () -> filmController.addFilm(filmWithNegativeDuration));
        int sizeListFilmsActual = filmController.getAllFilms().size();
        assertAll(
                () -> assertEquals(0, sizeListFilmsActual, "Фильм добавлен неверно"),
                () -> assertEquals(e.getMessage(), "Отрицательная продолжительность фильма")
        );
    }
}
