package com.dmgtalkies.movies.service;

import com.dmgtalkies.movies.domain.*;
import com.dmgtalkies.movies.repository.RefundPolicyRepository;
import java.math.BigDecimal;
import java.time.*;
import org.springframework.stereotype.Service;

@Service
public class RefundService {
    private final RefundPolicyRepository policies;

    public RefundService(RefundPolicyRepository policies) { this.policies = policies; }

    public BigDecimal refundFor(Booking booking) {
        long hours = Duration.between(LocalDateTime.now(), booking.show.startsAt).toHours();
        int percentage = policies.findByActiveTrueOrderByMinimumHoursDesc().stream()
                .filter(policy -> hours >= policy.minimumHours)
                .findFirst().map(policy -> policy.refundPercent).orElse(0);
        return booking.total.multiply(BigDecimal.valueOf(percentage)).divide(BigDecimal.valueOf(100));
    }
}
