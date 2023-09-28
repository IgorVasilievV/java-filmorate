package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmDaoImpl implements FilmDao {

    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Override
    public Map<Long, Film> getFilms() {
        String sql = "select film_id from films";
        List<Long> filmIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("film_id"));
        Map<Long, Film> films = new HashMap<>();
        filmIds.forEach((ids) -> films.put(ids, getFilm(ids)
                .orElseThrow(() -> new NotFoundException("Ошибка заполнения базы"))));
        return films;
    }

    @Override
    public Optional<Film> getFilm(long id) {
        String sql = "select * from films where film_id = ?";
        Integer count = jdbcTemplate.queryForObject("select count(*) from films where film_id = ?", Integer.class, id);
        if (count != null && count > 0) {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> createFilm(rs, id), id);
        } else {
            return Optional.empty();
        }
    }

    private Optional<Film> createFilm(ResultSet rs, long id) throws SQLException {
        Film film = new Film()
                .setId(id)
                .setName(rs.getString("film_name"))
                .setDescription(rs.getString("description"))
                .setReleaseDate(rs.getDate("release_date").toLocalDate())
                .setDuration(rs.getInt("duration"))
                .setMpa(mpaService.getMpa(rs.getInt("mpa_id")))
                .setLikes(findLikesInDB(id))
                .setGenres(findGenresInDB(id));
        return Optional.of(film);
    }

    private Set<Long> findLikesInDB(long id) {
        String sql = "select user_id from likes where film_id = ?";
        List<Long> likes = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), id);
        return new HashSet<>(likes);
    }

    private Set<Genre> findGenresInDB(long id) {
        String sql = "select genre_id from genre_film where film_id = ?";
        List<Long> genreIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("genre_id"), id);
        Set<Genre> genresByFilmId = new HashSet<>();
        genreIds.forEach((idS) -> genresByFilmId.add(genreService.getGenre(idS)));
        return genresByFilmId;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "insert into films (film_name, description, release_date, duration, mpa_id) " +
                "values(?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int statusCompiling = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, (int) film.getMpa().getId());
            return stmt;
        }, keyHolder);
        if (statusCompiling > 0 && keyHolder.getKey() != null) {
            long id = keyHolder.getKey().longValue();
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                Set<Long> genreIds = film.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet());

                addGenresToFilmInDB(id, genreIds);
            }
            if (film.getLikes() != null) {
                addLikesToFilmInDB(id, film.getLikes());
            }
            film.setId(id);
            return film;
        } else {
            log.info("Ошибка добавления фильма: {}", film);
            return null;
        }
    }

    private void addGenresToFilmInDB(long filmId, Set<Long> genreIds) {
        String sql = "insert into genre_film(film_id, genre_id) values(?,?)";
        for (Long genreId : genreIds) {
            jdbcTemplate.update(sql, filmId, genreId);
        }
    }

    private void addLikesToFilmInDB(long filmId, Set<Long> userIds) {
        String sql = "insert into likes (film_id, user_id) values(?,?)";
        for (Long userId : userIds) {
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    @Override
    public Optional<Film> updateFilm(Film film) {
        String sql = "update films set " +
                "film_name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? where film_id = ?";
        int statusCompiling = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (statusCompiling > 0) {
            removeGenresByFilmId(film.getId());
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                Set<Long> genreIds = film.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .collect(Collectors.toSet());
                addGenresToFilmInDB(film.getId(), genreIds);
                film.setGenres(findGenresInDB(film.getId()));
            }
            removeLikesByFilmId(film.getId());
            if (film.getLikes() != null) {
                addLikesToFilmInDB(film.getId(), film.getLikes());
            }
            return Optional.of(film);
        } else {
            log.info("Ошибка обновления фильма: {}", film);
            return Optional.empty();
        }
    }

    private void removeGenresByFilmId(long filmId) {
        String sql = "delete from genre_film where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void removeLikesByFilmId(long filmId) {
        String sql = "delete from likes where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Film removeFilm(long id) {
        Film film = getFilm(id).orElseThrow(() -> new NotFoundException("не найден фильм"));
        String sql = "delete from films where film_id = ?";
        jdbcTemplate.update(sql, id);
        removeGenresByFilmId(id);
        removeLikesByFilmId(id);
        return film;
    }

    @Override
    public void addLike(long idFilm, long idUser) {
        if (validateForLike(idFilm, idUser)) {
            String sql = "insert into likes (film_id, user_id) values(?,?)"; //пока нет юзеров скорее всего не сработает вставлять через основные таблицы, не на прямую
            jdbcTemplate.update(sql, idFilm, idUser);
        } else {
            throw new NotFoundException("Id не найдены в хранилище");
        }
    }

    private boolean validateForLike(long idFilm, long idUser) {
        Integer countUsers = jdbcTemplate.queryForObject("select count(*) from users where user_id = ?",
                Integer.class, idUser);
        Integer countFilms = jdbcTemplate.queryForObject("select count(*) from films where film_id = ?",
                Integer.class, idFilm);
        return countUsers != null && countUsers > 0 && countFilms != null && countFilms > 0;
    }

    @Override
    public void deleteLike(long idFilm, long idUser) {
        if (validateForLike(idFilm, idUser)) {
            String sql = "delete from likes where film_id = ? and user_id = ?";
            jdbcTemplate.update(sql, idFilm, idUser);
        } else {
            throw new NotFoundException("Id не найдены в хранилище");
        }
    }
}
