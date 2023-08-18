package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private int id = 0;
    private final Map<Integer,Film> films = new HashMap<>();
    private final LocalDate birthdayCinema = LocalDate.of(1895, Month.DECEMBER, 28);

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        validateFilmBeforeAdded(film);
        if (film.getId() == 0) {
            film.setId(++id);
        } else {
            id = film.getId();
        }
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        validateFilmBeforeUpdated(film);
        films.replace(film.getId(), film);
        log.debug("Обновлен фильм id={}. Новые данные: {}", film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
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
        } else if (film.getReleaseDate().isBefore(birthdayCinema)) {
            ValidationException e = new ValidationException("Дата релиза фильма раньше 28-12-1895");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        } else if (film.getDuration() < 0) {
            ValidationException e = new ValidationException("Отрицательная продолжительность фильма");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

    private void validateFilmBeforeUpdated(Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            ValidationException e = new ValidationException("Фильм с id= " + film.getId() + " не найден");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

}
