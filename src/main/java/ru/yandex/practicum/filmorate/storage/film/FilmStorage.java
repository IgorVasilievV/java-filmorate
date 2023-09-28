package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.HashSet;
import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getFilms();

    Film getFilm(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(long id);

    public void addLike(long idFilm, long idUser);

    public void deleteLike(long idFilm, long idUser);
}
