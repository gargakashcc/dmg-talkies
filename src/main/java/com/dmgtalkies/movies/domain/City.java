package com.dmgtalkies.movies.domain;

import jakarta.persistence.*;

@Entity
public class City {
  @Id @GeneratedValue public Long id;

  @Column(unique = true, nullable = false)
  public String name;

  public City() {}

  public City(String name) {
    this.name = name;
  }
}
