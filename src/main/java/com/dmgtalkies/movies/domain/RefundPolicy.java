package com.dmgtalkies.movies.domain;

import jakarta.persistence.*;

@Entity
public class RefundPolicy {
    @Id @GeneratedValue public Long id;
    public int minimumHours;
    public int refundPercent;
    public boolean active = true;
    public RefundPolicy() {}
}
