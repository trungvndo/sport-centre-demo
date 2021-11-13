package com.demo.sport_centre_demo.repository;

import com.demo.sport_centre_demo.entity.Booking;
import com.demo.sport_centre_demo.entity.Court;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    List<Booking> findByCourtAndDate(Court court, Date date);

    List<Booking> findByPlayerEmailAndDate(String playerEmail, Date date);
}
