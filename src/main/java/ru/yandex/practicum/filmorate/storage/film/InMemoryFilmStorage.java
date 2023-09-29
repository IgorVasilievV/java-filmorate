package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.*;

@Repository("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
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

    @Override
    public void addLike(long idFilm, long idUser) {
        if (films.containsKey(idFilm)) {
            Film film = films.get(idFilm);
            if (film.getLikes() == null) {
                film.setLikes(new HashSet<>());
            }
            film.getLikes().add(idUser);
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    @Override
    public void deleteLike(long idFilm, long idUser) {
        if (films.containsKey(idFilm)) {
            Film film = films.get(idFilm);
            if (film.getLikes() != null) {
                film.getLikes().remove(idUser);
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }
}
