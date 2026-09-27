package com.dmgtalkies.movies.service;

import com.dmgtalkies.movies.domain.Booking;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class Notifier {
  @Async
  public void confirmed(Booking booking) {
    System.out.println("confirmation notification: " + booking.reference);
  }

  @Async
  public void cancelled(Booking booking) {
    System.out.println("cancellation notification: " + booking.reference);
  }
}
