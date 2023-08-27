package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private long id = 0;
    private final LocalDate birthdayCinema = LocalDate.of(1895, Month.DECEMBER, 28);

    private final Map<Long, Film> films;
    private final Map<Long, User> users;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        films = filmStorage.getFilms();
        users = userStorage.getUsers();
    }

    public Film addFilm(Film film) {
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

    public Film updateFilm(Film film) {
        validateFilmBeforeUpdated(film);
        films.replace(film.getId(), film);
        log.debug("Обновлен фильм id={}. Новые данные: {}", film.getId(), film);
        return film;
    }

    public Film getFilm(long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("В хранилище нет фильма с id = " + id);
        }
    }

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
            NotFoundException e = new NotFoundException("Фильм с id= " + film.getId() + " не найден");
            log.debug("Валидация не пройдена. " + e.getMessage());
            throw e;
        }
    }

    public void addLike(long idFilm, long idUser) {
        if (films.containsKey(idFilm) && users.containsKey(idUser)) {
            Film film = films.get(idFilm);
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            film.getLikes().add(idUser);
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public void deleteLike(long idFilm, long idUser) {
        if (films.containsKey(idFilm) && users.containsKey(idUser)) {
            Film film = films.get(idFilm);
            if (film.getLikes() != null) {
                film.getLikes().remove(idUser);
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    public Set<Film> getPopularFilms(int count) {
        Set<Film> popularFilms = films.values().stream()
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
