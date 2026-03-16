package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestDtoTest {

    @Test
    void shouldCreateItemRequestDtoUsingBuilder() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        ItemRequestDto.ItemRequestItemDto itemDto = ItemRequestDto.ItemRequestItemDto.builder()
                .id(1L)
                .name("test-item-name")
                .ownerId(2L)
                .requestId(1L)
                .build();

        Collection<ItemRequestDto.ItemRequestItemDto> items = List.of(itemDto);

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("test-request-description")
                .created(now)
                .items(items)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test-request-description", dto.getDescription());
        assertEquals(now, dto.getCreated());
        assertEquals(1, dto.getItems().size());

        ItemRequestDto.ItemRequestItemDto resultItem = dto.getItems().iterator().next();
        assertEquals(1L, resultItem.getId());
        assertEquals("test-item-name", resultItem.getName());
        assertEquals(2L, resultItem.getOwnerId());
        assertEquals(1L, resultItem.getRequestId());
    }

    @Test
    void shouldCreateItemRequestDtoUsingToBuilder() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        ItemRequestDto original = ItemRequestDto.builder()
                .id(1L)
                .description("test-request-description")
                .created(now)
                .build();

        ItemRequestDto modified = original.toBuilder()
                .description("test-request-description-updated")
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("test-request-description-updated", modified.getDescription());
        assertEquals(now, modified.getCreated());
        assertNull(modified.getItems());
    }

    @Test
    void shouldCreateItemRequestDtoUsingConstructor() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        ItemRequestDto.ItemRequestItemDto itemDto = new ItemRequestDto.ItemRequestItemDto(
                1L, "test-item-name", 2L, 1L
        );

        Collection<ItemRequestDto.ItemRequestItemDto> items = List.of(itemDto);

        ItemRequestDto dto = new ItemRequestDto(1L, "test-request-description", now, items);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test-request-description", dto.getDescription());
        assertEquals(now, dto.getCreated());
        assertEquals(1, dto.getItems().size());
    }

    @Test
    void shouldCreateEmptyItemRequestDtoUsingNoArgsConstructor() {
        ItemRequestDto dto = new ItemRequestDto();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getDescription());
        assertNull(dto.getCreated());
        assertNull(dto.getItems());
    }

    @Test
    void shouldSetAndGetFields() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        ItemRequestDto dto = new ItemRequestDto();

        dto.setId(1L);
        dto.setDescription("test-request-description");
        dto.setCreated(now);

        ItemRequestDto.ItemRequestItemDto itemDto = new ItemRequestDto.ItemRequestItemDto();
        itemDto.setId(1L);
        itemDto.setName("test-item-name");
        itemDto.setOwnerId(2L);
        itemDto.setRequestId(1L);

        Collection<ItemRequestDto.ItemRequestItemDto> items = List.of(itemDto);
        dto.setItems(items);

        assertEquals(1L, dto.getId());
        assertEquals("test-request-description", dto.getDescription());
        assertEquals(now, dto.getCreated());
        assertEquals(1, dto.getItems().size());

        ItemRequestDto.ItemRequestItemDto resultItem = dto.getItems().iterator().next();
        assertEquals(1L, resultItem.getId());
        assertEquals("test-item-name", resultItem.getName());
        assertEquals(2L, resultItem.getOwnerId());
        assertEquals(1L, resultItem.getRequestId());
    }

    @Test
    void shouldHandleNullItems() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("test-request-description")
                .created(LocalDateTime.now())
                .items(null)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getItems());
    }

    @Test
    void testEqualsWithSameObject() {
        ItemRequestDto dto = ItemRequestDto.builder().id(1L).build();

        assertEquals(dto, dto);
    }

    @Test
    void testEqualsWithNull() {
        ItemRequestDto dto = ItemRequestDto.builder().id(1L).build();

        assertNotEquals(null, dto);
    }

    @Test
    void testEqualsWithDifferentClass() {
        ItemRequestDto dto = ItemRequestDto.builder().id(1L).build();

        assertNotEquals("string", dto);
    }

    @Test
    void shouldCreateItemRequestItemDtoUsingBuilder() {
        ItemRequestDto.ItemRequestItemDto dto = ItemRequestDto.ItemRequestItemDto.builder()
                .id(1L)
                .name("test-item-name")
                .ownerId(2L)
                .requestId(1L)
                .build();

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test-item-name", dto.getName());
        assertEquals(2L, dto.getOwnerId());
        assertEquals(1L, dto.getRequestId());
    }

    @Test
    void shouldCreateItemRequestItemDtoUsingToBuilder() {
        ItemRequestDto.ItemRequestItemDto original = ItemRequestDto.ItemRequestItemDto.builder()
                .id(1L)
                .name("test-item-name")
                .ownerId(2L)
                .requestId(1L)
                .build();

        ItemRequestDto.ItemRequestItemDto modified = original.toBuilder()
                .name("test-item-name-updated")
                .build();

        assertEquals(1L, modified.getId());
        assertEquals("test-item-name-updated", modified.getName());
        assertEquals(2L, modified.getOwnerId());
        assertEquals(1L, modified.getRequestId());
    }

    @Test
    void shouldCreateItemRequestItemDtoUsingConstructor() {
        ItemRequestDto.ItemRequestItemDto dto = new ItemRequestDto.ItemRequestItemDto(
                1L, "test-item-name", 2L, 1L
        );

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("test-item-name", dto.getName());
        assertEquals(2L, dto.getOwnerId());
        assertEquals(1L, dto.getRequestId());
    }

    @Test
    void shouldCreateEmptyItemRequestItemDtoUsingNoArgsConstructor() {
        ItemRequestDto.ItemRequestItemDto dto = new ItemRequestDto.ItemRequestItemDto();

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getOwnerId());
        assertNull(dto.getRequestId());
    }

    @Test
    void shouldSetAndGetItemRequestItemDtoFields() {
        ItemRequestDto.ItemRequestItemDto dto = new ItemRequestDto.ItemRequestItemDto();

        dto.setId(1L);
        dto.setName("test-item-name");
        dto.setOwnerId(2L);
        dto.setRequestId(1L);

        assertEquals(1L, dto.getId());
        assertEquals("test-item-name", dto.getName());
        assertEquals(2L, dto.getOwnerId());
        assertEquals(1L, dto.getRequestId());
    }
}