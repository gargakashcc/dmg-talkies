package com.dmgtalkies.movies.repository;

import com.dmgtalkies.movies.domain.RefundPolicy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {
    List<RefundPolicy> findByActiveTrueOrderByMinimumHoursDesc();
}
