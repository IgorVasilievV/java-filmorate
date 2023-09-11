package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User getUser(long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("В хранилище нет пользователя с id = " + id));
    }

    @Override
    public User createUser(User user) {
        if (user.getId() == 0) {
            user.setId(++id);
        } else {
            id = user.getId();
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User removeUser(long id) {
        return Optional.ofNullable(users.remove(id))
                .orElseThrow(() -> new NotFoundException("В хранилище нет пользователя с id = " + id));
    }
}
