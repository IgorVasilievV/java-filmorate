package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {

    private final Logger log = LoggerFactory.getLogger(MpaDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Mpa> getMpa(long id) {
        String sql = "select * from mpa where mpa_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if (mpaRows.next()) {
            Mpa mpa = new Mpa()
                    .setId(mpaRows.getInt("mpa_id"))
                    .setName(mpaRows.getString("mpa_type"));
            log.info("Найден MPA: {} {}", mpa.getId(), mpa.getName());
            return Optional.of(mpa);
        } else {
            log.info("MPA с id = {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> getMpaes() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa()
                .setId(rs.getInt("mpa_id"))
                .setName(rs.getString("mpa_type"));
    }
}
