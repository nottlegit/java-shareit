package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private UserDto createdUserDto;
    private UserDto updatedUserDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Иван Петров")
                .email("ivan.petrov@yandex.ru")
                .build();

        createdUserDto = UserDto.builder()
                .id(1L)
                .name("Иван Петров")
                .email("ivan.petrov@yandex.ru")
                .build();

        updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Петр Иванов")
                .email("petr.ivanov@mail.ru")
                .build();
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WhenValidRequest() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(createdUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.email").value("ivan.petrov@yandex.ru"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidRequest() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("Петр Иванов")
                .email("petr.ivanov@mail.ru")
                .build();

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Петр Иванов"))
                .andExpect(jsonPath("$.email").value("petr.ivanov@mail.ru"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenOnlyNameProvided() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("Петр Иванов")
                .build();

        UserDto returnedDto = UserDto.builder()
                .id(1L)
                .name("Петр Иванов")
                .email("ivan.petrov@yandex.ru")
                .build();

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(returnedDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Петр Иванов"))
                .andExpect(jsonPath("$.email").value("ivan.petrov@yandex.ru"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenOnlyEmailProvided() throws Exception {
        UserDto updateDto = UserDto.builder()
                .email("petr.ivanov@mail.ru")
                .build();

        UserDto returnedDto = UserDto.builder()
                .id(1L)
                .name("Иван Петров")
                .email("petr.ivanov@mail.ru")
                .build();

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(returnedDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.email").value("petr.ivanov@mail.ru"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenValidRequest() throws Exception {
        when(userService.getUserById(1L)).thenReturn(createdUserDto);

        mockMvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.email").value("ivan.petrov@yandex.ru"));
    }

    @Test
    void getUsers_ShouldReturnListOfUsers_WhenUsersExist() throws Exception {
        Collection<UserDto> users = List.of(createdUserDto);

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Иван Петров"))
                .andExpect(jsonPath("$[0].email").value("ivan.petrov@yandex.ru"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getUsers_ShouldReturnEmptyList_WhenNoUsersExist() throws Exception {
        when(userService.getUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deleteUser_ShouldReturnOk_WhenValidRequest() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }
}