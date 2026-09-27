package com.dmgtalkies.movies.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

@Entity
public class Booking {
    @Id @GeneratedValue public Long id;
    @Column(unique = true) public String reference;
    public String customer;
    @ManyToOne(optional = false) public MovieShow show;
    @ElementCollection public Set<String> seats = new TreeSet<>();
    @Enumerated(EnumType.STRING) public BookingStatus status;
    public LocalDateTime createdAt;
    public LocalDateTime holdUntil;
    public BigDecimal total;
    public String discountCode;
    public BigDecimal refund = BigDecimal.ZERO;
    public Booking() {}
}
