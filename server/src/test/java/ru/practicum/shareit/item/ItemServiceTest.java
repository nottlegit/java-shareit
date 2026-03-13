package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemService itemService;

    private UserDto ownerDto;
    private User owner;
    private User booker;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentCreateDto commentCreateDto;
    private Booking booking;
    private ItemRequest itemRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);

        ownerDto = UserDto.builder()
                .id(1L)
                .name("test-owner-name")
                .email("test-owner@test.com")
                .build();

        owner = new User();
        owner.setId(1L);
        owner.setName("test-owner-name");
        owner.setEmail("test-owner@test.com");

        booker = new User();
        booker.setId(2L);
        booker.setName("test-booker-name");
        booker.setEmail("test-booker@test.com");

        item = Item.builder()
                .id(1L)
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .owner(owner)
                .build();

        itemDto = ItemDto.builder()
                .name("test-item-name")
                .description("test-item-description")
                .available(true)
                .requestId(1L)
                .build();

        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setText("test-comment-text");

        comment = Comment.builder()
                .id(1L)
                .text("test-comment-text")
                .item(item)
                .author(booker)
                .created(now)
                .build();

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.minusDays(5));
        booking.setEnd(now.minusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("test-request-description");
        itemRequest.setRequester(booker);
        itemRequest.setCreated(now.minusDays(10));
    }

    @Test
    void createItem_ShouldCreateItem_WhenValidRequest() {
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(1L, itemDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());

        verify(userService).getUserById(1L);
        verify(requestRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldCreateItemWithoutRequest_WhenRequestIdIsNull() {
        itemDto.setRequestId(null);

        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(1L, itemDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());

        verify(userService).getUserById(1L);
        verify(requestRepository, never()).findById(anyLong());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createItem_ShouldThrowNotFoundException_WhenRequestNotFound() {
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createItem(1L, itemDto));

        assertEquals("Запрос с id 1 не найден", exception.getMessage());
        verify(requestRepository).findById(1L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldUpdateAllFields_WhenValidRequest() {
        ItemDto updateDto = ItemDto.builder()
                .name("test-item-name-updated")
                .description("test-item-description-updated")
                .available(false)
                .build();

        Item updatedItem = Item.builder()
                .id(1L)
                .name("test-item-name-updated")
                .description("test-item-description-updated")
                .available(false)
                .owner(owner)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals("test-item-name-updated", result.getName());
        assertEquals("test-item-description-updated", result.getDescription());
        assertFalse(result.getAvailable());

        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldUpdateOnlyName_WhenOnlyNameProvided() {
        ItemDto updateDto = ItemDto.builder()
                .name("test-item-name-updated")
                .build();

        Item updatedItem = Item.builder()
                .id(1L)
                .name("test-item-name-updated")
                .description("test-item-description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals("test-item-name-updated", result.getName());
        assertEquals("test-item-description", result.getDescription());
        assertTrue(result.getAvailable());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldUpdateOnlyDescription_WhenOnlyDescriptionProvided() {
        ItemDto updateDto = ItemDto.builder()
                .description("test-item-description-updated")
                .build();

        Item updatedItem = Item.builder()
                .id(1L)
                .name("test-item-name")
                .description("test-item-description-updated")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals("test-item-name", result.getName());
        assertEquals("test-item-description-updated", result.getDescription());
        assertTrue(result.getAvailable());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldUpdateOnlyAvailable_WhenOnlyAvailableProvided() {
        ItemDto updateDto = ItemDto.builder()
                .available(false)
                .build();

        Item updatedItem = Item.builder()
                .id(1L)
                .name("test-item-name")
                .description("test-item-description")
                .available(false)
                .owner(owner)
                .build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals("test-item-name", result.getName());
        assertEquals("test-item-description", result.getDescription());
        assertFalse(result.getAvailable());

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldReturnOriginalItem_WhenNoFieldsToUpdate() {
        ItemDto updateDto = ItemDto.builder().build();

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowAccessDeniedException_WhenUserIsNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> itemService.updateItem(2L, 1L, itemDto));

        assertEquals("Только владелец может редактировать вещь", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 999L, itemDto));

        assertEquals("Вещь с id: 999 не найдена", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItemById_ShouldReturnItem_WhenUserIsOwner() {
        List<Booking> bookings = List.of(booking);
        List<Comment> comments = List.of(comment);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatusNotOrderByStart(eq(1L), eq(BookingStatus.WAITING)))
                .thenReturn(bookings);
        when(commentRepository.findByItemId(anyLong())).thenReturn(comments);

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());

        verify(bookingRepository).findByItemIdAndStatusNotOrderByStart(1L, BookingStatus.WAITING);
        verify(commentRepository).findByItemId(1L);
    }

    @Test
    void getItemById_ShouldReturnItem_WhenUserIsNotOwner() {
        List<Booking> bookings = List.of(booking);
        List<Comment> comments = List.of(comment);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatusNotOrderByStart(eq(1L), eq(BookingStatus.WAITING)))
                .thenReturn(bookings);
        when(commentRepository.findByItemId(anyLong())).thenReturn(comments);

        ItemDto result = itemService.getItemById(1L, 2L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());

        verify(bookingRepository).findByItemIdAndStatusNotOrderByStart(1L, BookingStatus.WAITING);
        verify(commentRepository).findByItemId(1L);
    }

    @Test
    void getItemById_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999L, 1L));

        assertEquals("Вещь с id: 999 не найдена", exception.getMessage());
    }

    @Test
    void getItemsByOwner_ShouldReturnItems_WhenUserExists() {
        List<Item> items = List.of(item);

        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(items);

        Collection<ItemDto> result = itemService.getItemsByOwner(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.iterator().next().getId());

        verify(userService).getUserById(1L);
        verify(itemRepository).findByOwnerId(1L);
    }

    @Test
    void getItemsByOwner_ShouldReturnEmptyList_WhenUserHasNoItems() {
        when(userService.getUserById(anyLong())).thenReturn(ownerDto);
        when(itemRepository.findByOwnerId(anyLong())).thenReturn(List.of());

        Collection<ItemDto> result = itemService.getItemsByOwner(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_ShouldReturnItems_WhenTextIsValid() {
        List<Item> items = List.of(item);

        when(itemRepository.searchAvailableItems(anyString())).thenReturn(items);

        Collection<ItemDto> result = itemService.searchItems("test-search-text");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.iterator().next().getId());

        verify(itemRepository).searchAvailableItems("test-search-text");
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsNull() {
        Collection<ItemDto> result = itemService.searchItems(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, never()).searchAvailableItems(anyString());
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsBlank() {
        Collection<ItemDto> result = itemService.searchItems("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, never()).searchAvailableItems(anyString());
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenNoMatchesFound() {
        when(itemRepository.searchAvailableItems(anyString())).thenReturn(List.of());

        Collection<ItemDto> result = itemService.searchItems("test-nonexistent-text");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository).searchAvailableItems("test-nonexistent-text");
    }

    @Test
    void addComment_ShouldAddComment_WhenUserHasBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 2L, commentCreateDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowIllegalArgumentException_WhenUserHasNoBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.findUserByIdOrThrow(anyLong())).thenReturn(booker);
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.addComment(1L, 2L, commentCreateDto));

        assertEquals("Нельзя оставить комментарий, если пользователь не брал вещь в аренду", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(999L, 2L, commentCreateDto));

        assertEquals("Вещь с id: 999 не найдена", exception.getMessage());
    }

    @Test
    void findItemByIdOrThrow_ShouldReturnItem_WhenItemExists() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item result = itemService.findItemByIdOrThrow(1L);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void findItemByIdOrThrow_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.findItemByIdOrThrow(999L));

        assertEquals("Вещь с id: 999 не найдена", exception.getMessage());
    }

    @Test
    void findByOwnerId_ShouldReturnItems_WhenOwnerHasItems() {
        List<Item> items = List.of(item);

        when(itemRepository.findByOwnerIdOrderById(anyLong())).thenReturn(items);

        Collection<Item> result = itemService.findByOwnerId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.iterator().next().getId());

        verify(itemRepository).findByOwnerIdOrderById(1L);
    }

    @Test
    void findByRequestIdIn_ShouldReturnItems_WhenRequestsExist() {
        List<Long> requestIds = List.of(1L, 2L);
        List<Item> items = List.of(item);

        when(itemRepository.findByRequestIdIn(anyCollection())).thenReturn(items);

        Collection<Item> result = itemService.findByRequestIdIn(requestIds);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.iterator().next().getId());

        verify(itemRepository).findByRequestIdIn(requestIds);
    }

    @Test
    void findByRequestId_ShouldReturnItems_WhenRequestExists() {
        List<Item> items = List.of(item);

        when(itemRepository.findByRequestId(anyLong())).thenReturn(items);

        Collection<Item> result = itemService.findByRequestId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.iterator().next().getId());

        verify(itemRepository).findByRequestId(1L);
    }
}