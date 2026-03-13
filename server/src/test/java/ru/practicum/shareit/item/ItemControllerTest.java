package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDto createdItemDto;
    private CommentCreateDto commentCreateDto;
    private CommentDto commentDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        itemDto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        createdItemDto = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("test-comment-text-1");

        commentDto = CommentDto.builder()
                .id(1L)
                .text("test-comment-text-1")
                .authorName("test-author-name-1")
                .created(now)
                .build();
    }

    @Test
    void createItem_ShouldReturnCreatedItem_WhenValidRequest() throws Exception {
        when(itemService.createItem(eq(1L), any(ItemDto.class)))
                .thenReturn(createdItemDto);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-item-name-1"))
                .andExpect(jsonPath("$.description").value("test-item-description-1"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void addComment_ShouldReturnCreatedComment_WhenValidRequest() throws Exception {
        when(itemService.addComment(eq(1L), eq(1L), any(CommentCreateDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("test-comment-text-1"))
                .andExpect(jsonPath("$.authorName").value("test-author-name-1"))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenValidRequest() throws Exception {
        ItemDto updatedItemDto = ItemDto.builder()
                .name("test-item-name-updated")
                .description("test-item-description-updated")
                .available(false)
                .build();

        ItemDto returnedItemDto = ItemDto.builder()
                .id(1L)
                .name("test-item-name-updated")
                .description("test-item-description-updated")
                .available(false)
                .build();

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(returnedItemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-item-name-updated"))
                .andExpect(jsonPath("$.description").value("test-item-description-updated"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenOnlyNameIsProvided() throws Exception {
        ItemDto updatedItemDto = ItemDto.builder()
                .name("test-item-name-updated")
                .build();

        ItemDto returnedItemDto = ItemDto.builder()
                .id(1L)
                .name("test-item-name-updated")
                .description("test-item-description-1")
                .available(true)
                .build();

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(returnedItemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-item-name-updated"))
                .andExpect(jsonPath("$.description").value("test-item-description-1"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenOnlyDescriptionIsProvided() throws Exception {
        ItemDto updatedItemDto = ItemDto.builder()
                .description("test-item-description-updated")
                .build();

        ItemDto returnedItemDto = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-updated")
                .available(true)
                .build();

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(returnedItemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-item-name-1"))
                .andExpect(jsonPath("$.description").value("test-item-description-updated"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenOnlyAvailableIsProvided() throws Exception {
        ItemDto updatedItemDto = ItemDto.builder()
                .available(false)
                .build();

        ItemDto returnedItemDto = ItemDto.builder()
                .id(1L)
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(false)
                .build();

        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(returnedItemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-item-name-1"))
                .andExpect(jsonPath("$.description").value("test-item-description-1"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItemById_ShouldReturnItem_WhenValidRequest() throws Exception {
        when(itemService.getItemById(1L, 1L))
                .thenReturn(createdItemDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-item-name-1"))
                .andExpect(jsonPath("$.description").value("test-item-description-1"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemsByOwner_ShouldReturnListOfItems_WhenValidRequest() throws Exception {
        List<ItemDto> items = List.of(createdItemDto);

        when(itemService.getItemsByOwner(1L))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("test-item-name-1"))
                .andExpect(jsonPath("$[0].description").value("test-item-description-1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getItemsByOwner_ShouldReturnEmptyList_WhenUserHasNoItems() throws Exception {
        when(itemService.getItemsByOwner(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchItems_ShouldReturnListOfItems_WhenValidText() throws Exception {
        List<ItemDto> items = List.of(createdItemDto);

        when(itemService.searchItems("test-search-text"))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test-search-text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("test-item-name-1"))
                .andExpect(jsonPath("$[0].description").value("test-item-description-1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsEmpty() throws Exception {
        when(itemService.searchItems(""))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsBlank() throws Exception {
        when(itemService.searchItems(" "))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", " "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenNoMatchesFound() throws Exception {
        when(itemService.searchItems("test-nonexistent-text"))
                .thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "test-nonexistent-text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}