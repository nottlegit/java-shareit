package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("test-user-name-1")
                .email("test-email-1@test.com")
                .build();
    }

    @Test
    void createUser_ShouldReturnCreatedUser_WhenValidRequest() throws Exception {
        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
        userDto.setName(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        userDto.setName("   ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        userDto.setName("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsNull() throws Exception {
        userDto.setEmail(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsBlank() throws Exception {
        userDto.setEmail("   ");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        userDto.setEmail("");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        userDto.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsMissingAtSymbol() throws Exception {
        userDto.setEmail("test-email-1.test.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsMissingDomain() throws Exception {
        userDto.setEmail("test-email-1@");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenEmailIsMissingLocalPart() throws Exception {
        userDto.setEmail("@test.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(null);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidRequest() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("test-user-name-updated")
                .email("test-email-updated@test.com")
                .build();

        when(userClient.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenOnlyNameProvided() throws Exception {
        UserDto updateDto = UserDto.builder()
                .name("test-user-name-updated")
                .build();

        when(userClient.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenOnlyEmailProvided() throws Exception {
        UserDto updateDto = UserDto.builder()
                .email("test-email-updated@test.com")
                .build();

        when(userClient.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_ShouldReturnBadRequest_WhenUserIdIsInvalid() throws Exception {
        mockMvc.perform(patch("/users/{userId}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(userClient.updateUser(eq(1L), any(UserDto.class)))
                .thenReturn(null);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenValidRequest() throws Exception {
        when(userClient.getUser(eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_ShouldReturnBadRequest_WhenUserIdIsInvalid() throws Exception {
        mockMvc.perform(get("/users/{userId}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(userClient.getUser(eq(1L)))
                .thenReturn(null);

        mockMvc.perform(get("/users/{userId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers_WhenUsersExist() throws Exception {
        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(userClient.getAllUsers())
                .thenReturn(null);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ShouldReturnOk_WhenValidRequest() throws Exception {
        when(userClient.deleteUser(eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ShouldReturnBadRequest_WhenUserIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/users/{userId}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(userClient.deleteUser(eq(1L)))
                .thenReturn(null);

        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());
    }
}