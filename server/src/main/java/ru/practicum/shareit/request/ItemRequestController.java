package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader(X_SHARER_USER_ID) Long requesterId,
            @RequestBody ItemRequestCreateDto requestCreateDto) {
        return itemRequestService.createRequest(requesterId, requestCreateDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequests(@RequestHeader(X_SHARER_USER_ID) Long requesterId) {
        return itemRequestService.getRequests(requesterId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
