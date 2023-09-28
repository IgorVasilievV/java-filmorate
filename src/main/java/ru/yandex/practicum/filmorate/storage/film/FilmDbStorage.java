package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.Map;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final FilmDao filmDao;

    @Override
    public Map<Long, Film> getFilms() {
        return filmDao.getFilms();
    }

    @Override
    public Film getFilm(long id) {
        return filmDao.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + id + " не найден"));
    }

    @Override
    public Film addFilm(Film film) {
        return filmDao.addFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        return filmDao.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + film.getId() + " не обновлен"));
    }

    @Override
    public Film removeFilm(long id) {
        return filmDao.removeFilm(id);
    }

    @Override
    public void addLike(long idFilm, long idUser) {
        filmDao.addLike(idFilm, idUser);
    }

    @Override
    public void deleteLike(long idFilm, long idUser) {
        filmDao.deleteLike(idFilm, idUser);
    }
}
