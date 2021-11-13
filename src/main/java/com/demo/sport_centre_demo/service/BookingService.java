package com.demo.sport_centre_demo.service;

import com.demo.sport_centre_demo.entity.Booking;
import com.demo.sport_centre_demo.entity.Court;
import com.demo.sport_centre_demo.model.BookingRequest;
import com.demo.sport_centre_demo.model.InvalidRequestException;
import com.demo.sport_centre_demo.repository.BookingRepository;
import com.demo.sport_centre_demo.repository.CourtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Value("${sport-centre.max_booking_per_session}")
    private Integer maxBookingPerSession;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");

    private BookingRepository bookingRepository;

    private CourtRepository courtRepository;

    private NotificationService notificationService;

    @Autowired
    BookingService(BookingRepository bookingRepository, CourtRepository courtRepository, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
        this.notificationService = notificationService;
    }

    public List<Booking> getAllBookings() {
        List<Booking> allBookings = new ArrayList<>();
        this.bookingRepository.findAll().iterator().forEachRemaining(allBookings::add);
        return allBookings;
    }

    /**
     *
     * @param bookingRequest
     * @return booking
     * @throws InvalidRequestException
     */
    public Booking registerBooking(BookingRequest bookingRequest) throws InvalidRequestException {
        List<Booking> existingBookingsOfSameEmail = bookingRepository
                .findByPlayerEmailAndDate(bookingRequest.getEmail().toLowerCase(), bookingRequest.getDate());
        if (existingBookingsOfSameEmail.size() > 0) {
            String errorMessage = String.format("You already made the booking under this email address %s for this day %s",
                    bookingRequest.getEmail(), simpleDateFormat.format(bookingRequest.getDate()));
            throw new InvalidRequestException(errorMessage);
        }

        if (bookingRequest.getCourtId() != null) {
            Optional<Court> optionalCourt = courtRepository.findById(bookingRequest.getCourtId());
            if (optionalCourt.isEmpty()) {
                String errorMessage = String.format("Court %d does not exist", bookingRequest.getCourtId());
                throw new InvalidRequestException(errorMessage);
            }
            List<Booking> existingBookings = bookingRepository.findByCourtAndDate(optionalCourt.get(), bookingRequest.getDate());
            if (existingBookings.size() >= this.maxBookingPerSession) {
                String errorMessage = String.format("Court %d is occupied for this date %s",
                        bookingRequest.getCourtId(), simpleDateFormat.format(bookingRequest.getDate()));
                throw new InvalidRequestException(errorMessage);
            }
            Booking newBooking = createNewBooking(optionalCourt.get(), bookingRequest);
            bookingRepository.save(newBooking);
            existingBookings.add(newBooking);
            if (existingBookings.size() == this.maxBookingPerSession) {
                notificationService.notifyPlayers(existingBookings);
            }
            return newBooking;
        }

        // find an available court for the player
        Iterable<Court> allCourts = courtRepository.findAll();
        for (Court court: allCourts) {
            List<Booking> bookingListOfThisCourt = bookingRepository.findByCourtAndDate(court, bookingRequest.getDate());
            if (bookingListOfThisCourt.size() < this.maxBookingPerSession) {
                Booking newBooking = createNewBooking(court, bookingRequest);
                bookingRepository.save(newBooking);
                bookingListOfThisCourt.add(newBooking);
                if (bookingListOfThisCourt.size() == this.maxBookingPerSession) {
                    notificationService.notifyPlayers(bookingListOfThisCourt);
                }
                return newBooking;
            }
        }
        String errorMessage = String.format("All the courts are occupied for this day %s",
                simpleDateFormat.format(bookingRequest.getDate()));
        throw new InvalidRequestException(errorMessage);
    }

    private Booking createNewBooking(Court court, BookingRequest bookingRequest) {
        return new Booking(court, bookingRequest.getDate(),
                bookingRequest.getEmail().toLowerCase(), bookingRequest.getName());
    }
}
