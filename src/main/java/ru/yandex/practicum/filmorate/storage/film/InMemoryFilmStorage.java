package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilm(long id) {
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("В хранилище нет фильма с id = " + id));
    }


    @Override
    public Film addFilm(Film film) {
        if (film.getId() == 0) {
            film.setId(++id);
        } else {
            id = film.getId();
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film removeFilm(long id) {
        return Optional.ofNullable(films.remove(id))
                .orElseThrow(() -> new NotFoundException("В хранилище нет фильма с id = " + id));
    }
}
