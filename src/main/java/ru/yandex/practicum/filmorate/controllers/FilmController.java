package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private int id = 0;
    private final List<Film> films = new ArrayList<>();
    private final LocalDate BIRTHDAY_CINEMA = LocalDate.of(1895, Month.DECEMBER, 28);

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
            validateFilmBeforeAdded(film);
            if (film.getId() == 0) {
                film.setId(++id);
            } else {
                id = film.getId();
            }
            films.add(film);
            log.debug("Добавлен фильм: {}", film.toString());
            return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        Film oldFilm = validateFilmBeforeUpdated(film);
        films.remove(oldFilm);
        films.add(film);
        log.debug("Обновлен фильм id={}. Новые данные: {}", film.getId(), film.toString());
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }


    private void validateFilmBeforeAdded(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            ValidationException e = new ValidationException("Пустое название фильма");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else if (film.getDescription().length() > 200) {
            ValidationException e = new ValidationException("Длина описания фильма превышает 200 символов");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else if (film.getReleaseDate().isBefore(BIRTHDAY_CINEMA)) {
            ValidationException e = new ValidationException("Дата релиза фильма раньше 28-12-1895");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else if (film.getDuration() < 0) {
            ValidationException e = new ValidationException("Отрицательная продолжительность фильма");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

    private Film validateFilmBeforeUpdated(Film film) throws ValidationException {
        Optional<Film> oldFilm = films.stream()
                .filter(f -> film.getId() == f.getId())
                .findFirst();
        if (oldFilm.isEmpty()) {
            ValidationException e = new ValidationException("Фильм с id= " + film.getId() + " не найден");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else {
            return oldFilm.get();
        }
    }

}
