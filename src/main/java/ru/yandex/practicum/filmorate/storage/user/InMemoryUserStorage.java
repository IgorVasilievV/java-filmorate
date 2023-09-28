package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.*;

@Repository("inMemoryUserStorage")
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

    @Override
    public void addFriend(long idUser, long idFriend) {
        if (users.keySet().containsAll(List.of(idUser, idFriend))) {
            User user = users.get(idUser);
            User friend = users.get(idFriend);
            if (user.getFriendsIds() == null) {
                user.setFriendsIds(new HashMap<>());
            }
            if (friend.getFriendsIds() == null) {
                friend.setFriendsIds(new HashMap<>());
            }
            if (!friend.getFriendsIds().containsKey(idUser)) {
                user.getFriendsIds().put(idFriend, false);
            } else {
                user.getFriendsIds().put(idFriend, true);
                friend.getFriendsIds().put(idUser, true);
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    @Override
    public void deleteFriend(long idUser, long idFriend) {
        if (users.keySet().containsAll(List.of(idUser, idFriend))) {
            User user = users.get(idUser);
            User friend = users.get(idFriend);
            if (user.getFriendsIds() != null) {
                user.getFriendsIds().remove(idFriend);
                if (friend.getFriendsIds() != null && friend.getFriendsIds().containsKey(idUser)) {
                    friend.getFriendsIds().put(idUser, false);
                }
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    @Override
    public List<User> getFriends(long idUser) {
        if (users.containsKey(idUser)) {
            List<User> friends = new ArrayList<>();
            if (users.get(idUser).getFriendsIds() != null) {
                Set<Long> idFriend = users.get(idUser).getFriendsIds().keySet();
                idFriend.forEach(s -> friends.add(users.get(s)));
            }
            return friends;
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }
}
