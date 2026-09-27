package com.dmgtalkies.movies.api;

import com.dmgtalkies.movies.domain.*;
import com.dmgtalkies.movies.repository.*;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CatalogController {
  private final CityRepository cities;
  private final ShowRepository shows;
  private final SeatRepository seats;

  public CatalogController(CityRepository cities, ShowRepository shows, SeatRepository seats) {
    this.cities = cities;
    this.shows = shows;
    this.seats = seats;
  }

  @GetMapping("/cities")
  public List<City> cities() {
    return cities.findAll();
  }

  @GetMapping("/cities/{id}/shows")
  public List<MovieShow> shows(@PathVariable Long id) {
    return shows.findByTheater_City_IdAndActiveTrue(id);
  }

  @GetMapping("/shows/{id}/seats")
  public List<ShowSeat> seats(@PathVariable Long id) {
    return seats.findByShow_IdOrderByLabel(id);
  }
}
