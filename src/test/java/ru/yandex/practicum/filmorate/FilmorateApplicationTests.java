package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Mpa;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;

    User userValid = new User()
            .setEmail("practicum@mail.ru")
            .setLogin("login")
            .setName("Vasya")
            .setBirthday(LocalDate.of(2000, 12, 1));
    User friendValid = new User()
            .setEmail("friend@mail.ru")
            .setLogin("login")
            .setName("friend")
            .setBirthday(LocalDate.of(2000, 12, 1));
    User commonFriendValid = new User()
            .setEmail("commonfriend@mail.ru")
            .setLogin("login")
            .setName("commonfriend")
            .setBirthday(LocalDate.of(2000, 12, 1));
    Film filmValid = new Film()
            .setName("Terminator")
            .setDescription("3 parts")
            .setReleaseDate(LocalDate.of(2000, 1, 29))
            .setDuration(30).setMpa(new Mpa().setId(1));

    @Test
    public void testUserAndFilmDao() {

        userStorage.createUser(userValid);
        userStorage.createUser(friendValid);
        userStorage.createUser(commonFriendValid);

        Optional<User> userOptional = Optional.of(userStorage.getUser(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );

        userStorage.updateUser(userValid.setName("newName"));
        userOptional = Optional.of(userStorage.getUser(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "newName")
                );

        Optional<Map<Long, User>> usersOptional = Optional.of(userStorage.getUsers());
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(users ->
                        assertThat(users.size()).isEqualTo(3)
                );

        userStorage.addFriend(1, 2);
        userOptional = Optional.of(userStorage.getUser(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getFriendsIds().size()).isEqualTo(1)

                );

        List<User> commonFriends = userStorage.getFriends(1);
        assertThat(commonFriends.size()).isEqualTo(1);

        userStorage.deleteFriend(1, 2);
        userOptional = Optional.of(userStorage.getUser(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getFriendsIds().size()).isEqualTo(0)

                );

        userStorage.removeUser(2);
        usersOptional = Optional.of(userStorage.getUsers());
        assertThat(usersOptional)
                .isPresent()
                .hasValueSatisfying(users ->
                        assertThat(users.size()).isEqualTo(2)
                );


        filmStorage.addFilm(filmValid);

        Optional<Film> filmOptional = Optional.of(filmStorage.getFilm(1));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );

        filmStorage.updateFilm(filmValid.setName("newName"));
        filmOptional = Optional.of(filmStorage.getFilm(1));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "newName")
                );

        Optional<Map<Long, Film>> filmsOptional = Optional.of(filmStorage.getFilms());
        assertThat(filmsOptional)
                .isPresent()
                .hasValueSatisfying(films ->
                        assertThat(films.size()).isEqualTo(1)
                );

        filmStorage.addLike(1, 1);
		assertThat(filmStorage.getFilm(1).getLikes().size()).isEqualTo(1);

		filmStorage.deleteLike(1, 1);
		assertThat(filmStorage.getFilm(1).getLikes().size()).isEqualTo(0);

		filmStorage.removeFilm(1);
		filmsOptional = Optional.of(filmStorage.getFilms());
		assertThat(filmsOptional)
				.isPresent()
				.hasValueSatisfying(films ->
						assertThat(films.size()).isEqualTo(0)
				);

    }

    @Test
    public void testMpaDao() {
        Optional<Mpa> mpaOptional = Optional.of(mpaStorage.getMpa(1));
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G")
                );

        List<Mpa> mpaes = mpaStorage.getMpaes();
        assertThat(mpaes.size())
                .isEqualTo(5);
    }

    @Test
    public void testGenreDao() {
        Optional<Genre> genreOptional = Optional.of(genreStorage.getGenre(1));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия")
                );

        List<Genre> genres = genreStorage.getGenres();
        assertThat(genres.size())
                .isEqualTo(6);
    }
}
