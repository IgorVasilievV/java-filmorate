package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Mpa {
    private long id;
    private String name;
}
