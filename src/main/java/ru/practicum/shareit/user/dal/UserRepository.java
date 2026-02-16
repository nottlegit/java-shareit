package ru.practicum.shareit.user.dal;

import lombok.Data;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Data
@Repository
public class UserRepository {
    private final HashMap<Long, User> users;

    public Optional<User> findOne(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }

    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        return this.save(user);
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public void delete(Long userId) {
        users.remove(userId);
    }
}