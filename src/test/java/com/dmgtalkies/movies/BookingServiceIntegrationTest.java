package com.dmgtalkies.movies;

import static org.junit.jupiter.api.Assertions.*;

import com.dmgtalkies.movies.api.*;
import com.dmgtalkies.movies.domain.*;
import com.dmgtalkies.movies.repository.*;
import com.dmgtalkies.movies.service.BookingService;
import java.math.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@SpringBootTest
class BookingServiceIntegrationTest {
  @Autowired BookingService booking;
  @Autowired CityRepository cities;
  @Autowired TheaterRepository theaters;
  @Autowired ShowRepository shows;
  @Autowired SeatRepository seats;
  @Autowired BookingRepository bookings;
  MovieShow show;

  @BeforeEach
  void setup() {
    City c = cities.save(new City("Test city " + UUID.randomUUID()));
    Theater t = new Theater();
    t.city = c;
    t.name = "T";
    t = theaters.save(t);
    show = new MovieShow();
    show.theater = t;
    show.title = "Film";
    show.startsAt = LocalDateTime.now().plusDays(2);
    show.regularPrice = BigDecimal.valueOf(100);
    show.premiumPrice = BigDecimal.valueOf(200);
    show = shows.save(show);
    for (String label : List.of("A1", "A2")) {
      ShowSeat s = new ShowSeat();
      s.show = show;
      s.label = label;
      seats.save(s);
    }
  }

  @Test
  void holdPreventsDoubleAllocationAndConfirmationBooksSeats() {
    Booking b = booking.hold("customer", new HoldInput(show.id, Set.of("A1"), null));
    assertEquals(BookingStatus.HELD, b.status);
    assertThrows(
        Exception.class, () -> booking.hold("other", new HoldInput(show.id, Set.of("A1"), null)));
    booking.confirm("customer", b.id, new PaymentInput("payment-ok"));
    assertEquals(BookingStatus.CONFIRMED, bookings.findById(b.id).orElseThrow().status);
    assertEquals(SeatStatus.BOOKED, seats.findByShow_IdOrderByLabel(show.id).getFirst().status);
  }

  @Test
  void onlyOneOfTwentyConcurrentRequestsCanHoldTheLastTwoSeats() throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(20);
    CountDownLatch ready = new CountDownLatch(20), start = new CountDownLatch(1);
    try {
      List<Future<Boolean>> requests = new ArrayList<>();
      for (int i = 0; i < 20; i++) {
        int n = i;
        requests.add(
            pool.submit(
                () -> {
                  ready.countDown();
                  start.await();
                  try {
                    booking.hold("customer" + n, new HoldInput(show.id, Set.of("A1", "A2"), null));
                    return true;
                  } catch (Exception ignored) {
                    return false;
                  }
                }));
      }
      assertTrue(ready.await(5, TimeUnit.SECONDS));
      start.countDown();
      long winners = 0;
      for (Future<Boolean> request : requests) if (request.get(15, TimeUnit.SECONDS)) winners++;
      assertEquals(1, winners);
      assertEquals(SeatStatus.HELD, seats.findByShow_IdOrderByLabel(show.id).getFirst().status);
    } finally {
      pool.shutdownNow();
    }
  }
}
