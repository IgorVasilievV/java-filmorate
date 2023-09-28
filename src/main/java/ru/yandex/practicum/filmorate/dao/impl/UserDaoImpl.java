package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, User> getUsers() {
        String sql = "select user_id from users";
        List<Long> userIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"));
        Map<Long, User> users = new HashMap<>();
        userIds.forEach((ids) -> users.put(ids, getUser(ids)
                .orElseThrow(() -> new NotFoundException("Ошибка заполнения базы"))));
        return users;
    }

    @Override
    public Optional<User> getUser(long id) {
        String sql = "select * from users where user_id = ?";
        Integer count = jdbcTemplate.queryForObject("select count(*) from users where user_id = ?",
                Integer.class, id);
        if (count != null && count > 0) {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> createUserFromDb(rs, id), id);
        } else {
            return Optional.empty();
        }
    }

    private Optional<User> createUserFromDb(ResultSet rs, long id) throws SQLException {
        User user = new User()
                .setId(id)
                .setEmail(rs.getString("email"))
                .setLogin(rs.getString("login"))
                .setName(rs.getString("user_name"))
                .setBirthday(rs.getDate("birthday").toLocalDate())
                .setFriendsIds(createFriendshipFromDb(id));
        return Optional.of(user);
    }

    private Map<Long, Boolean> createFriendshipFromDb(long id) {
        String sql = "select * from friends where user_id = ?";
        Integer count = jdbcTemplate.queryForObject("select count(*) from friends where user_id = ?",
                Integer.class, id);
        if (count != null && count > 0) {
            List<Map<Long, Boolean>> friendships = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<Long, Boolean> map = new HashMap<>();
                map.put(rs.getLong("friend_id"), rs.getBoolean("status"));
                return map;
            }, id);
            Map<Long, Boolean> friendshipsMap;
            friendshipsMap = friendships.stream().reduce((m1, m2) -> {
                m1.putAll(m2);
                return m1;
            }).orElse(new HashMap<>());
            return friendshipsMap;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public User createUser(User user) {
        String sql = "insert into users (email, login, user_name, birthday) " +
                "values(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int statusCompiling = jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        if (statusCompiling > 0 && keyHolder.getKey() != null) {
            long id = keyHolder.getKey().longValue();
            if (user.getFriendsIds() != null && !user.getFriendsIds().isEmpty()) {
                Map<Long, Boolean> friendIds = user.getFriendsIds();
                friendIds.entrySet().stream().forEach((e) -> addFriendInDB(id, e.getKey(), e.getValue()));
            }
            user.setId(id);
            return user;
        } else {
            log.info("Ошибка добавления юзера: {}", user);
            return null;
        }
    }

    private void addFriendInDB(long userId, long friendId, boolean status) {
        String sql = "insert into friends(user_id, friend_id, status) values(?,?,?)";
        jdbcTemplate.update(sql, userId, friendId, status);
    }

    @Override
    public Optional<User> updateUser(User user) {
        String sql = "update users set " +
                "email = ?, " +
                "login = ?, " +
                "user_name = ?, " +
                "birthday = ? " +
                "where user_id = ?";
        int statusCompiling = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (statusCompiling > 0) {
            return Optional.of(user);
        } else {
            log.info("Ошибка обновления фильма: {}", user);
            return Optional.empty();
        }
    }

    @Override
    public User removeUser(long id) {
        User user = getUser(id).orElseThrow(() -> new NotFoundException("не найден юзер"));
        String sql = "delete from users where user_id = ?";
        jdbcTemplate.update(sql, id);
        return user;
    }

    @Override
    public void addFriend(long idUser, long idFriend) {
        Map<Long, User> users = getUsers();
        if (users.keySet().containsAll(List.of(idUser, idFriend))) {
            User friend = users.get(idFriend);
            if (!friend.getFriendsIds().containsKey(idUser)) {
                addFriendInDB(idUser, idFriend, false);
            } else {
                jdbcTemplate.update("update friends set status = true where user_id = ? and friend_id = ?",
                        idFriend, idUser);
                addFriendInDB(idUser, idFriend, true);
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    @Override
    public void deleteFriend(long idUser, long idFriend) {
        Map<Long, User> users = getUsers();
        if (users.keySet().containsAll(List.of(idUser, idFriend)) &&
                users.get(idUser).getFriendsIds().containsKey(idFriend)) {
            User friend = users.get(idFriend);
            jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?",
                    idUser, idFriend);
            if (friend.getFriendsIds().containsKey(idUser)) {
                jdbcTemplate.update("delete from friends where user_id = ? and friend_id = ?",
                        idFriend, idUser);
            }
        } else {
            throw new NotFoundException("В хранилище нет указанных id");
        }
    }

    @Override
    public List<User> getFriends(long idUser) {
        Set<Long> friendsId = createFriendshipFromDb(idUser).keySet();
        List<User> friends = new ArrayList<>();
        friendsId.forEach((id) -> friends.add(getUser(id)
                .orElseThrow(() -> new NotFoundException("ошибка заполнения базы данных"))));
        return friends;
    }
}
