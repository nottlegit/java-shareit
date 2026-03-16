package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private ItemRequestDto itemRequestDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        itemRequestDto = ItemRequestDto.builder()
                .description("test-request-description-1")
                .created(now)
                .build();
    }

    @Test
    void createRequest_ShouldReturnCreatedRequest_WhenValidRequest() throws Exception {
        when(itemRequestClient.createRequest(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createRequest_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOwnRequests_ShouldReturnRequests_WhenValidRequest() throws Exception {
        when(itemRequestClient.getOwnRequests(eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnRequests_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests_WithDefaultParams() throws Exception {
        when(itemRequestClient.getAllRequests(eq(1L), eq(0), eq(20)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests_WithCustomParams() throws Exception {
        when(itemRequestClient.getAllRequests(eq(1L), eq(5), eq(15)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, "1")
                        .param("from", "5")
                        .param("size", "15"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById_ShouldReturnRequest_WhenValidRequest() throws Exception {
        when(itemRequestClient.getRequestById(eq(1L), eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById_ShouldReturnBadRequest_WhenRequestIdIsInvalid() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", "invalid")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRequestById_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequest_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(itemRequestClient.createRequest(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(null);

        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnRequests_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(itemRequestClient.getOwnRequests(eq(1L)))
                .thenReturn(null);

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(itemRequestClient.getAllRequests(eq(1L), eq(0), eq(20)))
                .thenReturn(null);

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById_ShouldHandleNullResponse_WhenClientReturnsNull() throws Exception {
        when(itemRequestClient.getRequestById(eq(1L), eq(1L)))
                .thenReturn(null);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }
}