package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {

    Collection<Item> findAllByOwnerId(Long ownerId, PageRequest pageRequest);

    @Query("select item from Item item " +
            "where item.available = true " +
            "and (lower(item.name) like %:text% " +
            "or lower(item.description) like %:text%)")
    List<Item> search(@Param("text") String text, PageRequest pageRequest);

    @Query("select item from Item item " +
            "where item.itemRequest.id = :requestId")
    List<Item> findByItemRequestId(@Param("requestId") Long requestId);

    @Query("select item from Item item " +
            "where item.itemRequest.id in :ids")
    List<Item> searchByRequestIds(@Param("ids") List<Long> ids);
}
