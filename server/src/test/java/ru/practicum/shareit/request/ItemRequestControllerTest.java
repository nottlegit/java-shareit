package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestCreateDto createDto;
    private ItemRequestDto responseDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужна фотокамера Canon");

        ItemRequestDto.ItemRequestItemDto itemDto = ItemRequestDto.ItemRequestItemDto.builder()
                .id(1L)
                .name("Canon EOS 5D")
                .ownerId(2L)
                .requestId(1L)
                .build();

        responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна фотокамера Canon")
                .created(now)
                .items(List.of(itemDto))
                .build();
    }

    @Test
    void createItemRequest_ShouldReturnCreatedRequest_WhenValidRequest() throws Exception {
        when(itemRequestService.createRequest(eq(1L), any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна фотокамера Canon"))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Canon EOS 5D"))
                .andExpect(jsonPath("$.items[0].ownerId").value(2))
                .andExpect(jsonPath("$.items[0].requestId").value(1))
                .andExpect(jsonPath("$.created").exists());
    }


    @Test
    void getRequests_ShouldReturnListOfRequests_WhenValidRequest() throws Exception {
        Collection<ItemRequestDto> requests = List.of(responseDto);

        when(itemRequestService.getRequests(1L))
                .thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна фотокамера Canon"))
                .andExpect(jsonPath("$[0].items[0].id").value(1))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getRequests_ShouldReturnEmptyList_WhenUserHasNoRequests() throws Exception {
        when(itemRequestService.getRequests(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllRequests_ShouldReturnListOfRequests_WithDefaultParams() throws Exception {
        Collection<ItemRequestDto> requests = List.of(responseDto);

        when(itemRequestService.getAllRequests(eq(1L), eq(0), eq(20)))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна фотокамера Canon"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllRequests_ShouldReturnListOfRequests_WithCustomParams() throws Exception {
        Collection<ItemRequestDto> requests = List.of(responseDto);

        when(itemRequestService.getAllRequests(eq(1L), eq(5), eq(15)))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, "1")
                        .param("from", "5")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllRequests_ShouldReturnEmptyList_WhenNoRequestsExist() throws Exception {
        when(itemRequestService.getAllRequests(eq(1L), eq(0), eq(20)))
                .thenReturn(List.of());

        mockMvc.perform(get("/requests/all")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getRequestById_ShouldReturnRequest_WhenValidRequest() throws Exception {
        when(itemRequestService.getRequestById(1L, 1L))
                .thenReturn(responseDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна фотокамера Canon"))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Canon EOS 5D"));
    }
}