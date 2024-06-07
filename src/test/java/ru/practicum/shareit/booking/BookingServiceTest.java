package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingOwnerException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;
    @Mock
    BookingStorage bookingStorage;

    @InjectMocks
    BookingServiceImpl bookingService;

    BookingDto bookingDto;
    User user;
    Item item;
    User user2;
    Booking booking;

    @BeforeEach
    public void init() {
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .itemId(1L)
                .build();
        user = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .name("item")
                .description("desc")
                .available(true)
                .owner(user)
                .itemRequest(null)
                .build();
        booking = BookingMapper.toBooking(bookingDto, item, user);
      //  booking.setId(1L);
    }

    @Test
    @SneakyThrows
    public void addBookingItemNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.empty());

        ItemNotFoundException e = assertThrows(ItemNotFoundException.class,
                () -> bookingService.add(bookingDto, 1L));
        assertEquals("Item with id 1 not found.", e.getMessage());
    }

    @Test
    @SneakyThrows
    public void addBookingItemUserNotFound() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> bookingService.add(bookingDto, 1L));
        assertEquals("User with id 1 not found.", e.getMessage());
    }

    @Test
    @SneakyThrows
    public void addBookingItemNotAvailable() {
        item.setAvailable(false);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.add(bookingDto, 2L));
        assertEquals("User can't book unavailable items.", e.getMessage());
    }

    @Test
    @SneakyThrows
    public void addBookingOwnerExceptionTest() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user2));
        BookingOwnerException e = assertThrows(BookingOwnerException.class,
                () -> bookingService.add(bookingDto, 1L));
        assertEquals("User can't book own item.", e.getMessage());
    }

    @Test
    @SneakyThrows
    public void changeStatusBookingNotFound() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.empty());
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        BookingNotFoundException e = assertThrows(BookingNotFoundException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertEquals("Booking with id 1 not found.", e.getMessage());
    }

    @Test
    @SneakyThrows
  public   void changeStatusBooking_REJECTED() {
        booking.setStatus(Status.REJECTED);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));

        BadRequestException e = assertThrows(BadRequestException.class,
                () -> bookingService.approve(1L, 1L, true));
        assertEquals("Only WAITING can be approved or rejected", e.getMessage());
    }

    @Test
    @SneakyThrows
  public   void getBookingByIdOwnerTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingDtoOut bookingDtoOut = bookingService.getById(booking.getId(), user.getId());
        assertNotNull(bookingDtoOut);
        assertEquals(booking.getItem().getName(), bookingDtoOut.getItem().getName());
    }

    @Test
    @SneakyThrows
  public   void getBookingByIdNotOwnerTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.of(booking));
        BookingOwnerException e = assertThrows(BookingOwnerException.class,
                () -> bookingService.getById(booking.getId(), 50L));
        assertEquals("Only owner or booker can get booking info.", e.getMessage());
    }

    @Test
    @SneakyThrows
   public void getBookingByIdBookingNotFoundTest() {
        when(bookingStorage.findById(anyLong())).thenReturn(Optional.empty());
        BookingNotFoundException e = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getById(1L, 1L));
        assertEquals("Booking with id 1 not found.", e.getMessage());
    }

    @Test
    @SneakyThrows
   public void getByBookerAllStateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByUser(user.getId(), Status.ALL,1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    @SneakyThrows
   public void getByBookerCurrentStateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStateCurrent(anyLong(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByUser(user.getId(), Status.CURRENT, 1, 10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByBookerIdAndStateCurrent(anyLong(), any(), any());
    }

    @Test
    @SneakyThrows
    public void getByBookerPastStateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStatePast(anyLong(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByUser(user.getId(), Status.PAST, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByBookerIdAndStatePast(anyLong(), any(), any());
    }

    @Test
    @SneakyThrows
  public   void getByBookerFutureStateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStateFuture(anyLong(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByUser(user.getId(), Status.FUTURE, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByBookerIdAndStateFuture(anyLong(), any(), any());
    }

    @Test
    @SneakyThrows
   public void getByBookerWaitingStatusTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByUser(user.getId(), Status.WAITING, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(),bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByBookerIdAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    @SneakyThrows
  public   void getByBookerRejectedStatusTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByBookerIdAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByUser(user.getId(), Status.REJECTED, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByBookerIdAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    @SneakyThrows
   public void getByOwnerCurrentState() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStateCurrent(anyLong(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByOwner(user.getId(), Status.CURRENT, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByOwnerIdAndStateCurrent(anyLong(), any(), any());
    }

    @Test
    @SneakyThrows
  public   void getByOwnerPastStateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStatePast(anyLong(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByOwner(user.getId(), Status.PAST, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByOwnerIdAndStatePast(anyLong(), any(), any());
    }

    @Test
    @SneakyThrows
  public   void getByOwnerFutureStateTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStateFuture(anyLong(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByOwner(user.getId(), Status.FUTURE, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByOwnerIdAndStateFuture(anyLong(), any(), any());
    }

    @Test
    @SneakyThrows
  public   void getByOwnerWaitingStatusTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByOwner(user.getId(), Status.WAITING, 1,10);
        assertFalse(bookings.isEmpty());
        assertEquals(booking.getItem().getName(), bookings.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByOwnerIdAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    @SneakyThrows
  public   void getByOwnerRejectedStatusTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.findAllByOwnerIdAndStatus(anyLong(), any(), any(), any())).thenReturn(Collections.singletonList(booking));
        List<BookingDtoOut> resp = (List<BookingDtoOut>) bookingService.getAllByOwner(user.getId(), Status.REJECTED, 1,10);
        assertFalse(resp.isEmpty());
        assertEquals(booking.getItem().getName(), resp.get(0).getItem().getName());
        verify(bookingStorage, times(1)).findAllByOwnerIdAndStatus(anyLong(), any(), any(), any());
    }

    @Test
    @SneakyThrows
   public void getByOwnerBookerNotFoundTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.empty());
        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> bookingService.getAllByOwner(user.getId(), Status.ALL, 1,10));
        assertEquals("User with id 1 not found.", e.getMessage());
    }

    @Test
    @SneakyThrows
 public    void addValidBookingTest() {
       BookingDtoOut bookingDtoOut = BookingMapper.toBookingDtoOut(booking);
        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingStorage.save(any())).thenReturn(booking);
        bookingService.add(bookingDto, 2L);
        assertEquals(bookingDtoOut.getId(), bookingDto.getId());
        verify(bookingStorage).save(any());
    }
}
