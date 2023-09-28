package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.Map;
import java.util.Optional;

public interface FilmDao {
    Map<Long, Film> getFilms();

    Optional<Film> getFilm(long id);

    Film addFilm(Film film);

    Optional<Film> updateFilm(Film film);

    Film removeFilm(long id);

    void addLike(long idFilm, long idUser);

    void deleteLike(long idFilm, long idUser);
}
