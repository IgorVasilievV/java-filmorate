package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;

@Repository("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final GenreDao genreDao;

    @Override
    public List<Genre> getGenres() {
        return genreDao.getGenres();
    }

    @Override
    public Genre getGenre(long id) {
        return genreDao.
                getGenre(id)
                .orElseThrow(() -> new NotFoundException("Не найден Genre с id = " + id));
    }
}
