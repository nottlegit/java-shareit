package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

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
    private ItemClient itemClient;

    private ItemDto itemDto;
    private CommentCreateDto commentCreateDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .name("test-item-name-1")
                .description("test-item-description-1")
                .available(true)
                .build();

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("test-comment-text-1");
    }

    @Test
    void createItem_ShouldReturnCreatedItem_WhenValidRequest() throws Exception {
        when(itemClient.createItem(eq(1L), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenNameIsNull() throws Exception {
        itemDto.setName(null);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
        itemDto.setName("   ");

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenDescriptionIsNull() throws Exception {
        itemDto.setDescription(null);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenDescriptionIsBlank() throws Exception {
        itemDto.setDescription("   ");

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenDescriptionIsEmpty() throws Exception {
        itemDto.setDescription("");

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenAvailableIsNull() throws Exception {
        itemDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createItem_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsByOwner_ShouldReturnItems_WithDefaultParams() throws Exception {
        when(itemClient.getAllUserItems(eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByOwner_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_ShouldReturnItem_WhenValidRequest() throws Exception {
        when(itemClient.getItem(eq(1L), eq(1L)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getItemById_ShouldReturnBadRequest_WhenItemIdIsInvalid() throws Exception {
        mockMvc.perform(get("/items/{itemId}", "invalid")
                        .header(X_SHARER_USER_ID, "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/items/{itemId}", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem_WhenValidRequest() throws Exception {
        ItemDto updateDto = ItemDto.builder()
                .name("test-item-name-updated")
                .build();

        when(itemClient.updateItem(eq(1L), eq(1L), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenItemIdIsInvalid() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", "invalid")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItems_ShouldReturnItems_WithDefaultParams() throws Exception {
        when(itemClient.searchItems(eq("test"), eq(0), eq(20)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_ShouldReturnItems_WithCustomParams() throws Exception {
        when(itemClient.searchItems(eq("test"), eq(5), eq(15)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .param("from", "5")
                        .param("size", "15"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_ShouldReturnCreatedComment_WhenValidRequest() throws Exception {
        when(itemClient.addComment(eq(1L), eq(1L), any(CommentCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenTextIsNull() throws Exception {
        commentCreateDto.setText(null);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenTextIsBlank() throws Exception {
        commentCreateDto.setText("   ");

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenTextIsEmpty() throws Exception {
        commentCreateDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenItemIdIsInvalid() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", "invalid")
                        .header(X_SHARER_USER_ID, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addComment_ShouldReturnBadRequest_WhenHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isBadRequest());
    }
}