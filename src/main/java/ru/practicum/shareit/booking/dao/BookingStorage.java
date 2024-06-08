package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatus(Long itemId, LocalDateTime time,
                                                                      Status status, Sort sort);

    Optional<Booking> findFirstByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime time,
                                                              Status bookingStatus, Sort sort);

    List<Booking> findAllByBookerId(Long bookerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, Status bookingStatus, PageRequest pageRequest);

    List<Booking> findByItemInAndStartLessThanEqualAndStatus(List<Item> items, LocalDateTime thisMoment,
                                                             Status approved, Sort end);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                          Status status, LocalDateTime end);

    List<Booking> findByItemInAndStartAfterAndStatus(List<Item> items, LocalDateTime thisMoment,
                                                     Status approved, Sort end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND current_timestamp BETWEEN b.start AND b.end")
    List<Booking> findAllByBookerIdAndStateCurrent(@Param("bookerId") Long bookerId, PageRequest pageRequest);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND current_timestamp > b.end")
    List<Booking> findAllByBookerIdAndStatePast(@Param("bookerId") Long bookerId, PageRequest pageRequest);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND current_timestamp < b.start")
    List<Booking> findAllByBookerIdAndStateFuture(@Param("bookerId") Long bookerId, PageRequest pageRequest);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId, PageRequest pageRequest);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND current_timestamp BETWEEN b.start AND b.end")
    List<Booking> findAllByOwnerIdAndStateCurrent(@Param("ownerId") Long ownerId, PageRequest pageRequest);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND current_timestamp > b.end")
    List<Booking> findAllByOwnerIdAndStatePast(@Param("ownerId") Long ownerId, PageRequest pageRequest);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND current_timestamp < b.start")
    List<Booking> findAllByOwnerIdAndStateFuture(@Param("ownerId") Long ownerId, PageRequest pageRequest);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.status = :bookingStatus")
    List<Booking> findAllByOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                            @Param("bookingStatus") Status bookingStatus, PageRequest pageRequest);


}
