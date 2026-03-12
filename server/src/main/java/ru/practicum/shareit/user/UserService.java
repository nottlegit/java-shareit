package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Данный email уже используется");
        }

        User user = UserMapper.mapToUser(userDto);

        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Пользователь с id: %d не найден", userId)
                )
        );

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()
                && !userDto.getEmail().equals(existingUser.getEmail())) {

            userRepository.findByEmail(userDto.getEmail()).ifPresent(user -> {
                throw new DuplicateEmailException("Email уже используется");
            });
        }
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.mapToUserDto(updatedUser);
    }

    public UserDto getUserById(Long userId) {
        User user = findUserByIdOrThrow(userId);

        return UserMapper.mapToUserDto(user);
    }

    public Collection<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("Получен запрос на удаление пользователя с ID: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Пользователь с id: %d не найден", userId)
                )
        );

        userRepository.delete(user);
    }

    public User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(
                        String.format("Пользователь с id: %d не найден", userId)
                )
        );
    }
}