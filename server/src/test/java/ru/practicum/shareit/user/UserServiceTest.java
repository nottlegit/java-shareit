package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDto userDto;
    private UserDto updateDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("test-user-name-1")
                .email("test-email-1@test.com")
                .build();

        userDto = UserDto.builder()
                .name("test-user-name-1")
                .email("test-email-1@test.com")
                .build();

        updateDto = UserDto.builder()
                .name("test-user-name-updated")
                .email("test-email-updated@test.com")
                .build();
    }

    @Test
    void createUser_ShouldCreateUser_WhenEmailIsUnique() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).existsByEmail("test-email-1@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowDuplicateEmailException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
                () -> userService.createUser(userDto));

        assertEquals("Данный email уже используется", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateAllFields_WhenValidRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("test-user-name-updated", result.getName());
        assertEquals("test-email-updated@test.com", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("test-email-updated@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        UserDto nameUpdateDto = UserDto.builder()
                .name("test-user-name-updated")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, nameUpdateDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("test-user-name-updated", result.getName());
        assertEquals("test-email-1@test.com", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateOnlyEmail_WhenOnlyEmailProvided() {
        UserDto emailUpdateDto = UserDto.builder()
                .email("test-email-updated@test.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, emailUpdateDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("test-user-name-1", result.getName());
        assertEquals("test-email-updated@test.com", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("test-email-updated@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldNotUpdate_WhenNameIsBlank() {
        UserDto blankNameDto = UserDto.builder()
                .name("   ")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, blankNameDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("test-user-name-1", result.getName());
        assertEquals("test-email-1@test.com", result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldNotUpdate_WhenEmailIsBlank() {
        UserDto blankEmailDto = UserDto.builder()
                .email("   ")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, blankEmailDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("test-user-name-1", result.getName());
        assertEquals("test-email-1@test.com", result.getEmail());

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldReturnOriginalUser_WhenNoFieldsToUpdate() {
        UserDto emptyDto = UserDto.builder().build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, emptyDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("test-user-name-1", result.getName());
        assertEquals("test-email-1@test.com", result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowDuplicateEmailException_WhenEmailAlreadyExists() {
        User existingUserWithEmail = User.builder()
                .id(2L)
                .name("test-user-name-2")
                .email("test-email-updated@test.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(existingUserWithEmail));

        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
                () -> userService.updateUser(1L, updateDto));

        assertEquals("Email уже используется", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldNotCheckEmailUniqueness_WhenEmailIsSame() {
        UserDto sameEmailDto = UserDto.builder()
                .email("test-email-1@test.com")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, sameEmailDto);

        assertNotNull(result);
        assertEquals("test-email-1@test.com", result.getEmail());

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(999L, updateDto));

        assertEquals("Пользователь с id: 999 не найден", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(999L));

        assertEquals("Пользователь с id: 999 не найден", exception.getMessage());
    }

    @Test
    void getUsers_ShouldReturnListOfUsers_WhenUsersExist() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        Collection<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        UserDto dto = result.iterator().next();
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void getUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        Collection<UserDto> result = userService.getUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(999L));

        assertEquals("Пользователь с id: 999 не найден", exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void findUserByIdOrThrow_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User result = userService.findUserByIdOrThrow(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void findUserByIdOrThrow_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.findUserByIdOrThrow(999L));

        assertEquals("Пользователь с id: 999 не найден", exception.getMessage());
    }
}
