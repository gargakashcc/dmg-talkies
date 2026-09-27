package com.dmgtalkies.movies.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ShowSeat {
  @Id @GeneratedValue public Long id;

  @ManyToOne(optional = false)
  public MovieShow show;

  public String label;
  public boolean premium;

  @Enumerated(EnumType.STRING)
  public SeatStatus status = SeatStatus.AVAILABLE;

  public Long holdBookingId;
  public LocalDateTime holdUntil;
  @Version public Long version;

  public ShowSeat() {}
}
