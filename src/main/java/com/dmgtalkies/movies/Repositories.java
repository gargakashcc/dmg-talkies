package com.dmgtalkies.movies;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*; 
import java.util.*;

interface CityRepo extends JpaRepository<City,Long>{}
interface TheaterRepo extends JpaRepository<Theater,Long>{}
interface ShowRepo extends JpaRepository<MovieShow,Long>{ List<MovieShow> findByTheater_City_IdAndActiveTrue(Long cityId); }
interface SeatRepo extends JpaRepository<ShowSeat,Long>{ List<ShowSeat> findByShow_IdOrderByLabel(Long id); @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from ShowSeat s where s.show.id=:showId and s.label in :labels") List<ShowSeat> lock(@Param("showId")Long showId,@Param("labels")Set<String> labels); }
interface BookingRepo extends JpaRepository<Booking,Long>{ List<Booking> findByCustomerOrderByCreatedAtDesc(String customer); List<Booking> findByStatusAndHoldUntilBefore(BookingStatus s, LocalDateTime t); }
interface DiscountRepo extends JpaRepository<Discount,Long>{ Optional<Discount> findByCodeIgnoreCase(String code); }
interface PolicyRepo extends JpaRepository<RefundPolicy,Long>{ List<RefundPolicy> findByActiveTrueOrderByMinimumHoursDesc(); }
