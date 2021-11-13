package com.demo.sport_centre_demo.controller;

import com.demo.sport_centre_demo.entity.Booking;
import com.demo.sport_centre_demo.model.BookingRequest;
import com.demo.sport_centre_demo.model.InvalidRequestException;
import com.demo.sport_centre_demo.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class BookingController {

    private BookingService bookingService;

    @Autowired
    BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    @Operation(summary = "Register a booking")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful booking",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Booking.class))
                    }
            )
    })
    public Booking registerBooking(@RequestBody @Valid BookingRequest bookingRequest) throws InvalidRequestException {
        return this.bookingService.registerBooking(bookingRequest);
    }

    @Operation(summary = "Get all the bookings")
    @ApiResponse(
            responseCode = "200",
            description = "A list of bookings",
            content = {
                    @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Booking.class))
                    )
            }
    )
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return this.bookingService.getAllBookings();
    }
}
