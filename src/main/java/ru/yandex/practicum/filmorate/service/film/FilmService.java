package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final LocalDate birthdayCinema = LocalDate.of(1895, Month.DECEMBER, 28);

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validateFilmBeforeAdded(film);
        filmStorage.addFilm(film);
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        validateFilmBeforeUpdated(film);
        filmStorage.updateFilm(film);
        log.debug("Обновлен фильм id={}. Новые данные: {}", film.getId(), film);
        return film;
    }

    public Film getFilm(long id) {
        return filmStorage.getFilm(id);
    }

    public Film removeFilm(long id) {
        return filmStorage.removeFilm(id);
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.getFilms().values());
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
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            NotFoundException e = new NotFoundException("Фильм с id= " + film.getId() + " не найден");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

    public void addLike(long idFilm, long idUser) {
        filmStorage.addLike(idFilm, idUser);
    }

    public void deleteLike(long idFilm, long idUser) {
        filmStorage.deleteLike(idFilm, idUser);
    }

    public Set<Film> getPopularFilms(int count) {
        Set<Film> popularFilms = filmStorage.getFilms().values().stream()
                .sorted((film1, film2) -> {
                    if (film2.getLikes() != null && film1.getLikes() != null) {
                        return film2.getLikes().size() - film1.getLikes().size();
                    } else if (film2.getLikes() != null) {
                        return 1;
                    } else {
                        return -1;
                    }
                })
                .limit(count)
                .collect(Collectors.toSet());
        return popularFilms;
    }

}
