package com.dmgtalkies.movies.api;

import com.dmgtalkies.movies.domain.Booking;
import com.dmgtalkies.movies.repository.BookingRepository;
import com.dmgtalkies.movies.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
  private final BookingService bookings;
  private final BookingRepository bookingRepository;

  public BookingController(BookingService bookings, BookingRepository bookingRepository) {
    this.bookings = bookings;
    this.bookingRepository = bookingRepository;
  }

  @PostMapping("/hold")
  @ResponseStatus(HttpStatus.CREATED)
  public Booking hold(
      @AuthenticationPrincipal UserDetails user, @Valid @RequestBody HoldInput input) {
    return bookings.hold(user.getUsername(), input);
  }

  @PostMapping("/{id}/confirm")
  public Booking confirm(
      @AuthenticationPrincipal UserDetails user,
      @PathVariable Long id,
      @Valid @RequestBody PaymentInput input) {
    return bookings.confirm(user.getUsername(), id, input);
  }

  @PostMapping("/{id}/cancel")
  public Booking cancel(@AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
    return bookings.cancel(user.getUsername(), id);
  }

  @GetMapping
  public List<Booking> history(@AuthenticationPrincipal UserDetails user) {
    return bookingRepository.findByCustomerOrderByCreatedAtDesc(user.getUsername());
  }
}
