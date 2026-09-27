package com.dmgtalkies.movies.repository;

import com.dmgtalkies.movies.domain.ShowSeat;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface SeatRepository extends JpaRepository<ShowSeat, Long> {
    List<ShowSeat> findByShow_IdOrderByLabel(Long showId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ShowSeat s where s.show.id = :showId and s.label in :labels")
    List<ShowSeat> lock(@Param("showId") Long showId, @Param("labels") Set<String> labels);
}
