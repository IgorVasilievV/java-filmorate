package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final Logger log = LoggerFactory.getLogger(GenreDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> getGenre(long id) {
        String sql = "select * from genre where genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);
        if (genreRows.next()) {
            Genre genre = new Genre()
                    .setId(genreRows.getInt("genre_id"))
                    .setName(genreRows.getString("genre_type"));
            log.info("Найден Genre: {} {}", genre.getId(), genre.getName());
            return Optional.of(genre);
        } else {
            log.info("Genre с id = {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenres() {
        String sql = "select * from genre";
        return jdbcTemplate.query(sql, (rs, rowNum)-> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre()
                .setId(rs.getInt("genre_id"))
                .setName(rs.getString("genre_type"));
    }
}
