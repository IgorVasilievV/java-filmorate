package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film getFilm(long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film removeFilm(long id);

    public void addLike(long idFilm, long idUser);

    public void deleteLike(long idFilm, long idUser);
}
