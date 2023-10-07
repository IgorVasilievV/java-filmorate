package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;

@Repository("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final MpaDao mpaDao;

    @Override
    public List<Mpa> getMpaes() {
        return mpaDao.getMpaes();
    }

    @Override
    public Mpa getMpa(long id) {
        return mpaDao.getMpa(id).orElseThrow(() -> new NotFoundException("Не найден Mpa с id = " + id));
    }
}
