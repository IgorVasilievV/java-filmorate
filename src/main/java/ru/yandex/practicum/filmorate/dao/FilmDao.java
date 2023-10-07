package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Optional;

public interface FilmDao {
    List<Film> getFilms();

    Optional<Film> getFilm(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(long id);

    void addLike(long idFilm, long idUser);

    void deleteLike(long idFilm, long idUser);
}
