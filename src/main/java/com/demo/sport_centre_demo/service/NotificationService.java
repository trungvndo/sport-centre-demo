package com.demo.sport_centre_demo.service;

import com.demo.sport_centre_demo.entity.Booking;
import com.demo.sport_centre_demo.entity.Court;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {
    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");


    public void notifyPlayers(List<Booking> bookingList) {
        assert bookingList.size() > 0;
        Booking firstBooking = bookingList.get(0);
        Court court = firstBooking.getCourt();
        Date bookingDate = firstBooking.getDate();
        logger.info("The game is on on court: {}, date: {}. Participants are: \n",
                court.getName(), simpleDateFormat.format(bookingDate));
        bookingList.stream().forEach(b -> {
            logger.info("** Name: {}, Email: {}\n", b.getPlayerName(), b.getPlayerEmail());
        });
    }
}
