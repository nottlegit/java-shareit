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
    private final String xSharerUserId = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestHeader(xSharerUserId) Long requesterId,
            @RequestBody ItemRequestCreateDto requestCreateDto) {
        return itemRequestService.createRequest(requesterId, requestCreateDto);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequests(@RequestHeader(xSharerUserId) Long requesterId) {
        return itemRequestService.getRequests(requesterId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests(
            @RequestHeader(xSharerUserId) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "20") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(xSharerUserId) Long userId,
            @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
