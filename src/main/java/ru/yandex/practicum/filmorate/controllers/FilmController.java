package ru.yandex.practicum.filmorate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        filmService.addFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        filmService.updateFilm(film);
        return film;
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        return filmService.getFilm(id);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike (@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike (@PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count){
        return filmService.getPopularFilms(count);
    }

}
