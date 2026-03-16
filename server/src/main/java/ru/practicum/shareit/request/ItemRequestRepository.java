package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;


@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findByRequester_IdOrderByIdDesc(Long requesterId);

    List<ItemRequest> findByRequester_IdNot(Long requesterId, Pageable pageable);
}
