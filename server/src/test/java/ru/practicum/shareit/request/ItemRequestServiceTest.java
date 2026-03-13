package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestService itemRequestService;

    private User requester;
    private User anotherUser;
    private ItemRequest itemRequest;
    private ItemRequestCreateDto createDto;
    private Item item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        requester = new User();
        requester.setId(1L);
        requester.setName("Елена Волкова");
        requester.setEmail("elena.volkova@yandex.ru");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Михаил Петров");
        anotherUser.setEmail("mikhail.petrov@mail.ru");

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Нужен велосипед для города")
                .requester(requester)
                .created(now)
                .build();

        createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужен велосипед для города");

        item = Item.builder()
                .id(1L)
                .name("Горный велосипед")
                .description("Скоростной горный велосипед")
                .available(true)
                .owner(anotherUser)
                .request(itemRequest)
                .build();
    }

    @Test
    void createRequest_ShouldCreateRequest_WhenValidRequest() {
        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(requester);
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.createRequest(1L, createDto);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
        assertNull(result.getItems());

        verify(userService).findUserByIdOrThrow(1L);
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userService.findUserByIdOrThrow(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(999L, createDto));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getRequests_ShouldReturnEmptyList_WhenUserHasNoRequests() {
        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(requester);
        when(requestRepository.findByRequester_IdOrderByIdDesc(anyLong())).thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.getRequests(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRequests_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userService.findUserByIdOrThrow(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequests(999L));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).findByRequester_IdOrderByIdDesc(anyLong());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequestsExceptUsers_WhenValidRequest() {
        List<ItemRequest> requests = List.of(itemRequest);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("created").descending());

        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(anotherUser);
        when(requestRepository.findByRequester_IdNot(eq(2L), any(Pageable.class))).thenReturn(requests);

        Collection<ItemRequestDto> result = itemRequestService.getAllRequests(2L, 0, 20);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.iterator().next().getId());

        verify(userService).findUserByIdOrThrow(2L);
        verify(requestRepository).findByRequester_IdNot(eq(2L), any(Pageable.class));
    }

    @Test
    void getAllRequests_ShouldReturnEmptyList_WhenNoOtherRequestsExist() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("created").descending());

        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(anotherUser);
        when(requestRepository.findByRequester_IdNot(eq(2L), any(Pageable.class))).thenReturn(List.of());

        Collection<ItemRequestDto> result = itemRequestService.getAllRequests(2L, 0, 20);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests_ShouldUseCorrectPagination_WhenFromAndSizeProvided() {
        List<ItemRequest> requests = List.of(itemRequest);

        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(anotherUser);
        when(requestRepository.findByRequester_IdNot(eq(2L), any(Pageable.class))).thenReturn(requests);

        Collection<ItemRequestDto> result = itemRequestService.getAllRequests(2L, 5, 15);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(requestRepository).findByRequester_IdNot(eq(2L), argThat(pageable ->
                pageable.getPageNumber() == 0 &&
                        pageable.getPageSize() == 15 &&
                        pageable.getSort().equals(Sort.by("created").descending())
        ));
    }

    @Test
    void getAllRequests_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userService.findUserByIdOrThrow(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(999L, 0, 20));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).findByRequester_IdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getRequestById_ShouldReturnRequestWithItems_WhenValidRequest() {
        List<Item> items = List.of(item);

        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(anotherUser);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemService.findByRequestId(anyLong())).thenReturn(items);

        ItemRequestDto result = itemRequestService.getRequestById(2L, 1L);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());

        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        ItemRequestDto.ItemRequestItemDto itemDto = result.getItems().iterator().next();
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
        assertEquals(itemRequest.getId(), itemDto.getRequestId());

        verify(userService).findUserByIdOrThrow(2L);
        verify(requestRepository).findById(1L);
        verify(itemService).findByRequestId(1L);
    }

    @Test
    void getRequestById_ShouldReturnRequestWithoutItems_WhenNoItemsExist() {
        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(anotherUser);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemService.findByRequestId(anyLong())).thenReturn(List.of());

        ItemRequestDto result = itemRequestService.getRequestById(2L, 1L);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getRequestById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(anotherUser);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(2L, 999L));

        assertEquals("Запрос не найден", exception.getMessage());
        verify(itemService, never()).findByRequestId(anyLong());
    }

    @Test
    void getRequestById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userService.findUserByIdOrThrow(anyLong())).thenThrow(new NotFoundException("Пользователь не найден"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(999L, 1L));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestById_ShouldHandleItemWithNullRequest() {
        Item itemWithNullRequest = Item.builder()
                .id(2L)
                .name("Самокат")
                .description("Электросамокат")
                .available(true)
                .owner(anotherUser)
                .request(null)
                .build();

        List<Item> items = List.of(item, itemWithNullRequest);

        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(anotherUser);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemService.findByRequestId(anyLong())).thenReturn(items);

        ItemRequestDto result = itemRequestService.getRequestById(2L, 1L);

        assertNotNull(result);
        assertEquals(2, result.getItems().size());

        boolean hasItemWithNullRequest = result.getItems().stream()
                .anyMatch(dto -> dto.getRequestId() == null);
        assertTrue(hasItemWithNullRequest);
    }
}