package com.dmgtalkies.movies.domain;

import jakarta.persistence.*;

@Entity
public class Theater {
    @Id @GeneratedValue public Long id;
    @ManyToOne(optional = false) public City city;
    public String name;
    public String address;
    public Theater() {}
}
