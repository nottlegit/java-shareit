package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toDto_ShouldConvertRequestToDto_WhenRequestIsValid() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        User requester = new User();
        requester.setId(1L);
        requester.setName("Анна Смирнова");
        requester.setEmail("anna.smirnova@mail.ru");

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Нужен ноутбук")
                .requester(requester)
                .created(now)
                .build();

        ItemRequestDto result = ItemRequestMapper.toDto(request);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertNull(result.getItems());
    }

    @Test
    void toDto_ShouldReturnNull_WhenRequestIsNull() {
        ItemRequestDto result = ItemRequestMapper.toDto(null);
        assertNull(result);
    }

    @Test
    void toEntity_ShouldConvertCreateDtoToEntity_WhenDtoIsValid() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужен ноутбук");

        ItemRequest result = ItemRequestMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(dto.getDescription(), result.getDescription());
        assertNull(result.getId());
        assertNull(result.getRequester());
        assertNull(result.getCreated());
    }

    @Test
    void toEntity_ShouldReturnNull_WhenDtoIsNull() {
        ItemRequest result = ItemRequestMapper.toEntity((ItemRequestCreateDto) null);
        assertNull(result);
    }

    @Test
    void toEntity_WithRequester_ShouldConvertCreateDtoToEntityWithRequester_WhenDtoIsValid() {
        LocalDateTime beforeCreation = LocalDateTime.now().minusNanos(1);

        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужен ноутбук");

        User requester = new User();
        requester.setId(1L);
        requester.setName("Анна Смирнова");
        requester.setEmail("anna.smirnova@mail.ru");

        ItemRequest result = ItemRequestMapper.toEntity(dto, requester);

        assertNotNull(result);
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(requester, result.getRequester());
        assertNotNull(result.getCreated());
        assertTrue(result.getCreated().isAfter(beforeCreation));
        assertTrue(result.getCreated().isBefore(LocalDateTime.now().plusNanos(1)));
        assertNull(result.getId());
    }

    @Test
    void toEntity_WithRequester_ShouldReturnNull_WhenDtoIsNull() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest result = ItemRequestMapper.toEntity(null, requester);
        assertNull(result);
    }

    @Test
    void toEntity_WithRequester_ShouldReturnEntityWithNullRequester_WhenRequesterIsNull() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужен ноутбук");

        ItemRequest result = ItemRequestMapper.toEntity(dto, null);

        assertNotNull(result);
        assertEquals(dto.getDescription(), result.getDescription());
        assertNull(result.getRequester());
        assertNotNull(result.getCreated());
    }

    @Test
    void toEntity_WithRequester_ShouldSetCreatedTime_WhenDtoIsValid() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужен ноутбук");

        User requester = new User();
        requester.setId(1L);

        ItemRequest result1 = ItemRequestMapper.toEntity(dto, requester);
        ItemRequest result2 = ItemRequestMapper.toEntity(dto, requester);

        assertNotNull(result1.getCreated());
        assertNotNull(result2.getCreated());
        assertTrue(result2.getCreated().isAfter(result1.getCreated()) || result2.getCreated().equals(result1.getCreated()));
    }

    @Test
    void toDto_ShouldReturnDtoWithoutItems_WhenItemsAreNotSet() {
        LocalDateTime now = LocalDateTime.now();
        User requester = new User();
        requester.setId(1L);

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Нужен ноутбук")
                .requester(requester)
                .created(now)
                .build();

        ItemRequestDto result = ItemRequestMapper.toDto(request);

        assertNotNull(result);
        assertEquals(request.getId(), result.getId());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getCreated(), result.getCreated());
        assertNull(result.getItems());
    }

    @Test
    void toEntity_ShouldCreateEntityWithCorrectDescription_WhenDtoHasDescription() {
        String description = "Нужен мощный ноутбук с SSD не менее 512GB";
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription(description);

        ItemRequest result = ItemRequestMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
    }

    @Test
    void toEntity_WithRequester_ShouldCreateEntityWithCorrectDescriptionAndRequester_WhenDtoHasDescription() {
        String description = "Нужен мощный ноутбук с SSD не менее 512GB";
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription(description);

        User requester = new User();
        requester.setId(1L);
        requester.setName("Анна Смирнова");

        ItemRequest result = ItemRequestMapper.toEntity(dto, requester);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(requester, result.getRequester());
    }
}