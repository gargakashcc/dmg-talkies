package com.dmgtalkies.movies.api;

import com.dmgtalkies.movies.domain.*;
import com.dmgtalkies.movies.repository.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final CityRepository cities;
    private final TheaterRepository theaters;
    private final ShowRepository shows;
    private final SeatRepository seats;
    private final DiscountRepository discounts;
    private final RefundPolicyRepository policies;

    public AdminController(CityRepository cities, TheaterRepository theaters, ShowRepository shows, SeatRepository seats,
                           DiscountRepository discounts, RefundPolicyRepository policies) {
        this.cities = cities; this.theaters = theaters; this.shows = shows; this.seats = seats;
        this.discounts = discounts; this.policies = policies;
    }

    @PostMapping("/cities") public City city(@Valid @RequestBody CityInput input) { return cities.save(new City(input.name())); }
    @PostMapping("/theaters") public Theater theater(@Valid @RequestBody TheaterInput input) {
        Theater theater = new Theater(); theater.city = cities.getReferenceById(input.cityId()); theater.name = input.name(); theater.address = input.address(); return theaters.save(theater);
    }
    @PostMapping("/shows") public MovieShow show(@Valid @RequestBody ShowInput input) {
        MovieShow show = new MovieShow(); show.theater = theaters.getReferenceById(input.theaterId()); show.title = input.title(); show.startsAt = input.startsAt(); show.regularPrice = input.regularPrice(); show.premiumPrice = input.premiumPrice(); show.weekendMarkupPercent = input.weekendMarkupPercent(); return shows.save(show);
    }
    @PostMapping("/shows/{id}/seats") public ShowSeat seat(@PathVariable Long id, @Valid @RequestBody SeatInput input) {
        ShowSeat seat = new ShowSeat(); seat.show = shows.getReferenceById(id); seat.label = input.label(); seat.premium = input.premium(); return seats.save(seat);
    }
    @PostMapping("/discounts") public Discount discount(@Valid @RequestBody DiscountInput input) {
        Discount discount = new Discount(); discount.code = input.code(); discount.percent = input.percent(); discount.expiresAt = input.expiresAt(); return discounts.save(discount);
    }
    @PostMapping("/refund-policies") public RefundPolicy policy(@Valid @RequestBody PolicyInput input) {
        RefundPolicy policy = new RefundPolicy(); policy.minimumHours = input.minimumHours(); policy.refundPercent = input.refundPercent(); return policies.save(policy);
    }
}
