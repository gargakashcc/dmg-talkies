package com.dmgtalkies.movies.repository;

import com.dmgtalkies.movies.domain.MovieShow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<MovieShow, Long> {
  List<MovieShow> findByTheater_City_IdAndActiveTrue(Long cityId);
}
