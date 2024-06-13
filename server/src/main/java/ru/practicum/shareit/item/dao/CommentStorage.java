package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;


public interface CommentStorage extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByItemId(Long itemId);

    Collection<Comment> findByItemIn(List<Item> ownerItems, Sort created);
}
