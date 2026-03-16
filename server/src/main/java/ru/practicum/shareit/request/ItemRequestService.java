package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    public ItemRequestDto createRequest(Long requesterId, ItemRequestCreateDto requestCreateDto) {
        User requester = userService.findUserByIdOrThrow(requesterId);
        ItemRequest itemRequest = ItemRequestMapper.toEntity(requestCreateDto, requester);

        itemRequest = requestRepository.save(itemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    public Collection<ItemRequestDto> getRequests(Long requesterId) {
        userService.findUserByIdOrThrow(requesterId);

        Collection<ItemRequest> requests = requestRepository.findByRequester_IdOrderByIdDesc(requesterId);
        return mapToDtoWithItems(requests);
    }

    public Collection<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userService.findUserByIdOrThrow(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        Collection<ItemRequest> requests = requestRepository.findByRequester_IdNot(userId, pageable);
        return mapToDtoWithItems(requests);
    }

    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.findUserByIdOrThrow(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        ItemRequestDto dto = ItemRequestMapper.toDto(request);
        dto.setItems(
                itemService.findByRequestId(requestId).stream()
                        .map(item -> ItemRequestDto.ItemRequestItemDto.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .ownerId(item.getOwner().getId())
                                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                                .build())
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private Collection<ItemRequestDto> mapToDtoWithItems(Collection<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        Collection<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Collection<Item> items = itemService.findByRequestIdIn(requestIds);

        Map<Long, List<ItemRequestDto.ItemRequestItemDto>> itemsByRequestId = items.stream()
                .map(this::toItemRequestItemDto)
                .filter(dto -> dto.getRequestId() != null)
                .collect(Collectors.groupingBy(ItemRequestDto.ItemRequestItemDto::getRequestId));

        return requests.stream()
                .map(request -> {
                    ItemRequestDto dto = ItemRequestMapper.toDto(request);
                    dto.setItems(itemsByRequestId.getOrDefault(
                            request.getId(),
                            List.of()
                    ));
                    return dto;
                })
                .toList();
    }

    private ItemRequestDto.ItemRequestItemDto toItemRequestItemDto(Item item) {
        return ItemRequestDto.ItemRequestItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .requestId(getItemRequestId(item))
                .build();
    }

    private Long getItemRequestId(Item item) {
        return (item.getRequest() != null)
                ? item.getRequest().getId()
                : null;
    }
}
