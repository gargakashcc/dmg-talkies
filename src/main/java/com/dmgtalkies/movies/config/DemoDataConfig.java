package com.dmgtalkies.movies.config;

import com.dmgtalkies.movies.domain.*;
import com.dmgtalkies.movies.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

@Configuration
public class DemoDataConfig {
  @Bean
  CommandLineRunner loadDemoData(
      CityRepository cities,
      TheaterRepository theaters,
      ShowRepository shows,
      SeatRepository seats,
      DiscountRepository discounts,
      RefundPolicyRepository policies) {
    return args -> {
      if (cities.count() > 0) return;
      City mumbai = cities.save(new City("Mumbai"));
      City delhi = cities.save(new City("Delhi"));
      Theater pvr = theater(theaters, mumbai, "PVR Phoenix", "Lower Parel");
      Theater inox = theater(theaters, delhi, "INOX Nehru Place", "Nehru Place");
      show(
          shows,
          seats,
          pvr,
          "The Last Horizon",
          LocalDate.now().plusDays(2).atTime(19, 30),
          250,
          400,
          15);
      show(
          shows,
          seats,
          pvr,
          "The Last Horizon",
          LocalDate.now().plusDays(3).atTime(14, 0),
          220,
          360,
          0);
      show(
          shows,
          seats,
          inox,
          "Midnight in Monsoon",
          LocalDate.now().plusDays(2).atTime(20, 0),
          280,
          450,
          10);
      discount(discounts, "WELCOME10", 10, LocalDateTime.now().plusMonths(1));
      discount(discounts, "MOVIE20", 20, LocalDateTime.now().plusWeeks(2));
      policy(policies, 48, 100);
      policy(policies, 24, 80);
      policy(policies, 2, 50);
    };
  }

  private Theater theater(TheaterRepository repository, City city, String name, String address) {
    Theater theater = new Theater();
    theater.city = city;
    theater.name = name;
    theater.address = address;
    return repository.save(theater);
  }

  private void show(
      ShowRepository shows,
      SeatRepository seats,
      Theater theater,
      String title,
      LocalDateTime startsAt,
      int regular,
      int premium,
      int weekendMarkup) {
    MovieShow show = new MovieShow();
    show.theater = theater;
    show.title = title;
    show.startsAt = startsAt;
    show.regularPrice = BigDecimal.valueOf(regular);
    show.premiumPrice = BigDecimal.valueOf(premium);
    show.weekendMarkupPercent = weekendMarkup;
    show = shows.save(show);
    for (String label : List.of("A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4")) {
      ShowSeat seat = new ShowSeat();
      seat.show = show;
      seat.label = label;
      seat.premium = label.startsWith("B");
      seats.save(seat);
    }
  }

  private void discount(
      DiscountRepository repository, String code, int percent, LocalDateTime expiresAt) {
    Discount discount = new Discount();
    discount.code = code;
    discount.percent = percent;
    discount.expiresAt = expiresAt;
    repository.save(discount);
  }

  private void policy(RefundPolicyRepository repository, int hours, int percent) {
    RefundPolicy policy = new RefundPolicy();
    policy.minimumHours = hours;
    policy.refundPercent = percent;
    repository.save(policy);
  }
}
