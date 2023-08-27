package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getFilms();

}
