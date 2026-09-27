package com.dmgtalkies.movies.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class MovieShow {
    @Id @GeneratedValue public Long id;
    @ManyToOne(optional = false) public Theater theater;
    public String title;
    public LocalDateTime startsAt;
    public BigDecimal regularPrice;
    public BigDecimal premiumPrice;
    public int weekendMarkupPercent;
    public boolean active = true;
    public MovieShow() {}
}
