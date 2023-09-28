package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao {
    Optional<Genre> getGenre(long id);

    List<Genre> getGenres();
}
