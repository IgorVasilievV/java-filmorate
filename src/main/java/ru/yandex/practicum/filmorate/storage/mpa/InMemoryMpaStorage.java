package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;

@Repository("inMemoryMpaStorage")
public class InMemoryMpaStorage implements MpaStorage{
    @Override
    public List<Mpa> getMpaes() {
        return null;
    }

    @Override
    public Mpa getMpa(long id) {
        return null;
    }
}
