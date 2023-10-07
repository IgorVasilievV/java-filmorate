package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Set;

@Data
@Accessors(chain = true)
public class Film {
    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Long> likes;
    private Mpa mpa;
    private Set<Genre> genres;
}
