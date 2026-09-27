package com.dmgtalkies.movies.repository;

import com.dmgtalkies.movies.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerOrderByCreatedAtDesc(String customer);
    List<Booking> findByStatusAndHoldUntilBefore(BookingStatus status, LocalDateTime time);
}
