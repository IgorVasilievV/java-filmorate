package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.Collections;
import java.util.List;

@Repository
public class InMemoryGenreStorage implements GenreStorage {
    @Override
    public List<Genre> getGenres() {
        return Collections.emptyList();
    }

    @Override
    public Genre getGenre(long id) {
        return null;
    }
}
