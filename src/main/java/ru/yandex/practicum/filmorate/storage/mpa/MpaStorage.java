package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.models.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> getMpaes();
    Mpa getMpa(long id);
}
