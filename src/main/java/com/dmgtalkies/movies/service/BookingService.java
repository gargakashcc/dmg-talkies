package com.dmgtalkies.movies.service;

import com.dmgtalkies.movies.api.*;
import com.dmgtalkies.movies.domain.*;
import com.dmgtalkies.movies.repository.*;
import java.math.*;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BookingService {
    private final ShowRepository shows;
    private final SeatRepository seats;
    private final BookingRepository bookings;
    private final DiscountRepository discounts;
    private final PricingService pricing;
    private final RefundService refunds;
    private final Notifier notifier;

    public BookingService(ShowRepository shows, SeatRepository seats, BookingRepository bookings,
                          DiscountRepository discounts, PricingService pricing, RefundService refunds, Notifier notifier) {
        this.shows = shows; this.seats = seats; this.bookings = bookings; this.discounts = discounts;
        this.pricing = pricing; this.refunds = refunds; this.notifier = notifier;
    }

    @Transactional
    public Booking hold(String customer, HoldInput input) {
        expire();
        MovieShow show = shows.findById(input.showId()).orElseThrow(() -> bad("show not found"));
        if (!show.active || !show.startsAt.isAfter(LocalDateTime.now())) throw bad("show is unavailable");
        List<ShowSeat> locked = seats.lock(show.id, input.seats());
        if (locked.size() != input.seats().size()) throw bad("unknown seat");
        if (locked.stream().anyMatch(seat -> seat.status != SeatStatus.AVAILABLE)) throw conflict("one or more seats are unavailable");

        BigDecimal total = locked.stream().map(seat -> pricing.price(show, seat)).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (input.discountCode() != null && !input.discountCode().isBlank()) {
            Discount discount = discounts.findByCodeIgnoreCase(input.discountCode())
                    .filter(value -> value.active && (value.expiresAt == null || value.expiresAt.isAfter(LocalDateTime.now())))
                    .orElseThrow(() -> bad("invalid discount code"));
            total = pricing.applyDiscount(total, discount);
        }

        Booking booking = new Booking();
        booking.reference = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        booking.customer = customer; booking.show = show; booking.seats = new TreeSet<>(input.seats());
        booking.status = BookingStatus.HELD; booking.createdAt = LocalDateTime.now();
        booking.holdUntil = LocalDateTime.now().plusMinutes(10); booking.total = total.setScale(2, RoundingMode.HALF_UP);
        booking.discountCode = input.discountCode(); bookings.save(booking);
        locked.forEach(seat -> hold(seat, booking));
        return booking;
    }

    @Transactional
    public Booking confirm(String customer, Long id, PaymentInput payment) {
        Booking booking = owned(customer, id); expire();
        if (booking.status != BookingStatus.HELD || booking.holdUntil.isBefore(LocalDateTime.now())) throw bad("hold has expired");
        List<ShowSeat> locked = seats.lock(booking.show.id, booking.seats);
        if (locked.stream().anyMatch(seat -> !Objects.equals(seat.holdBookingId, booking.id))) throw conflict("hold lost");
        booking.status = BookingStatus.CONFIRMED;
        locked.forEach(this::book);
        notifier.confirmed(booking);
        return booking;
    }

    @Transactional
    public Booking cancel(String customer, Long id) {
        Booking booking = owned(customer, id); expire();
        if (booking.status != BookingStatus.CONFIRMED) throw bad("only confirmed bookings may be cancelled");
        booking.refund = refunds.refundFor(booking); booking.status = BookingStatus.CANCELLED;
        seats.lock(booking.show.id, booking.seats).forEach(this::release);
        notifier.cancelled(booking);
        return booking;
    }

    @Transactional
    @Scheduled(fixedDelay = 30_000)
    public void expire() {
        for (Booking booking : bookings.findByStatusAndHoldUntilBefore(BookingStatus.HELD, LocalDateTime.now())) {
            booking.status = BookingStatus.EXPIRED;
            seats.lock(booking.show.id, booking.seats).stream()
                    .filter(seat -> Objects.equals(seat.holdBookingId, booking.id)).forEach(this::release);
        }
    }

    private Booking owned(String customer, Long id) {
        Booking booking = bookings.findById(id).orElseThrow(() -> bad("booking not found"));
        if (!booking.customer.equals(customer)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not your booking");
        return booking;
    }
    private void hold(ShowSeat seat, Booking booking) { seat.status = SeatStatus.HELD; seat.holdBookingId = booking.id; seat.holdUntil = booking.holdUntil; }
    private void book(ShowSeat seat) { seat.status = SeatStatus.BOOKED; seat.holdBookingId = null; seat.holdUntil = null; }
    private void release(ShowSeat seat) { seat.status = SeatStatus.AVAILABLE; seat.holdBookingId = null; seat.holdUntil = null; }
    private ResponseStatusException bad(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
    private ResponseStatusException conflict(String message) { return new ResponseStatusException(HttpStatus.CONFLICT, message); }
}
