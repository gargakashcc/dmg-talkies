package com.dmgtalkies.movies.service;

import com.dmgtalkies.movies.domain.*;
import java.math.*;
import org.springframework.stereotype.Service;

@Service
public class PricingService {
  public BigDecimal price(MovieShow show, ShowSeat seat) {
    BigDecimal base = seat.premium ? show.premiumPrice : show.regularPrice;
    return switch (show.startsAt.getDayOfWeek()) {
      case SATURDAY, SUNDAY ->
          base.multiply(BigDecimal.valueOf(100 + show.weekendMarkupPercent))
              .divide(BigDecimal.valueOf(100));
      default -> base;
    };
  }

  public BigDecimal applyDiscount(BigDecimal total, Discount discount) {
    return total
        .multiply(BigDecimal.valueOf(100 - discount.percent))
        .divide(BigDecimal.valueOf(100));
  }
}
