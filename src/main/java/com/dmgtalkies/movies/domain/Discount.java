package com.dmgtalkies.movies.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Discount {
    @Id @GeneratedValue public Long id;
    @Column(unique = true) public String code;
    public int percent;
    public LocalDateTime expiresAt;
    public boolean active = true;
    public Discount() {}
}
