package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedAsc(Long userId);

//    @Query("select itemRequest from ItemRequest itemRequest " +
//            "where itemRequest.requester.id != :userId")
//    List<ItemRequest> findAllPageable(@Param("userId") Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequesterIdNotLikeOrderByCreatedAsc(Long userId, Pageable pageable);
}
