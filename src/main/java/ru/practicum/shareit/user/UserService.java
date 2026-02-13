package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AtomicLong idGenerator;

    public UserDto createUser(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(u -> {
            throw new DuplicatedDataException("Данный email уже используется");
        });

        User user = UserMapper.updateUserFields(idGenerator.getAndIncrement(), userDto);

        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        userRepository.findOne(userId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Пользователь с id: %d не найден", userId)
                )
        );

        User updatedUser = userRepository.update(UserMapper.updateUserFields(userId, userDto));

        return UserMapper.mapToUserDto(updatedUser);
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findOne(userId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Пользователь с id: %d не найден", userId)
                )
        );

        return UserMapper.mapToUserDto(user);
    }

    public Collection<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public void deleteUser(Long userId) {
        userRepository.findOne(userId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Пользователь с id: %d не найден", userId)
                )
        );

        userRepository.delete(userId);
    }
}
