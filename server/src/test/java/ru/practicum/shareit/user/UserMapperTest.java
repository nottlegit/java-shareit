package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void mapToUserDto_ShouldConvertUserToUserDto_WhenUserIsValid() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("name@test.com")
                .build();

        UserDto result = UserMapper.mapToUserDto(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void mapToUserDto_ShouldReturnDtoWithNullFields_WhenUserHasNullFields() {
        User user = User.builder()
                .id(1L)
                .name(null)
                .email(null)
                .build();

        UserDto result = UserMapper.mapToUserDto(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void mapToUserDto_ShouldReturnDtoWithNullId_WhenUserIdIsNull() {
        User user = User.builder()
                .id(null)
                .name("name")
                .email("name@test.com")
                .build();

        UserDto result = UserMapper.mapToUserDto(user);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void mapToUser_ShouldConvertUserDtoToUser_WhenUserDtoIsValid() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@test.com")
                .build();

        User result = UserMapper.mapToUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void mapToUser_ShouldReturnUserWithNullFields_WhenUserDtoHasNullFields() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(null)
                .email(null)
                .build();

        User result = UserMapper.mapToUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void mapToUser_ShouldReturnUserWithNullId_WhenUserDtoIdIsNull() {
        UserDto userDto = UserDto.builder()
                .id(null)
                .name("name")
                .email("name@test.com")
                .build();

        User result = UserMapper.mapToUser(userDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void mapToUserDtoAndBack_ShouldPreserveAllFields() {
        User originalUser = User.builder()
                .id(1L)
                .name("name")
                .email("name@test.com")
                .build();

        UserDto userDto = UserMapper.mapToUserDto(originalUser);
        User resultUser = UserMapper.mapToUser(userDto);

        assertNotNull(resultUser);
        assertEquals(originalUser.getId(), resultUser.getId());
        assertEquals(originalUser.getName(), resultUser.getName());
        assertEquals(originalUser.getEmail(), resultUser.getEmail());
    }

    @Test
    void mapToUserAndBack_ShouldPreserveAllFields() {
        UserDto originalDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("name@test.com")
                .build();

        User user = UserMapper.mapToUser(originalDto);
        UserDto resultDto = UserMapper.mapToUserDto(user);

        assertNotNull(resultDto);
        assertEquals(originalDto.getId(), resultDto.getId());
        assertEquals(originalDto.getName(), resultDto.getName());
        assertEquals(originalDto.getEmail(), resultDto.getEmail());
    }

    @Test
    void mapToUserDto_ShouldHandleUserWithAllFieldsNull() {
        User user = User.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        UserDto result = UserMapper.mapToUserDto(user);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void mapToUser_ShouldHandleUserDtoWithAllFieldsNull() {
        UserDto userDto = UserDto.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        User result = UserMapper.mapToUser(userDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void mapToUserDto_ShouldNotThrowException_WhenUserIsNull() {
        assertThrows(NullPointerException.class, () -> UserMapper.mapToUserDto(null));
    }

    @Test
    void mapToUser_ShouldNotThrowException_WhenUserDtoIsNull() {
        assertThrows(NullPointerException.class, () -> UserMapper.mapToUser(null));
    }
}