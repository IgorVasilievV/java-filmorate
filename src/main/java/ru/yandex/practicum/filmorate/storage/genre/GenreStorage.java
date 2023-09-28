package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getGenres();
    Genre getGenre(long id);
}
