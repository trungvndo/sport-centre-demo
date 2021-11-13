package com.demo.sport_centre_demo;

import com.demo.sport_centre_demo.entity.Booking;
import com.demo.sport_centre_demo.model.BookingRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void testGetBookings() {
        ResponseEntity<List> results = this.restTemplate.getForEntity(getApiPath(), List.class);
        Assertions.assertTrue(results.getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(results.getBody().size(), 0);
    }

    @Test
    public void testCreateBookingInvalidMissingDate() {
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setName("Test");
        bookingRequest.setEmail("test@gmail.com");
        HttpEntity<BookingRequest> entity = new HttpEntity<>(bookingRequest);
        ResponseEntity<Booking> response = this.restTemplate.postForEntity(getApiPath(), entity, Booking.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testCreateBookingInvalidRequest() {
        List<BookingRequest> bookingRequests = createInvalidBookingRequest();
        for (BookingRequest bookingRequest: bookingRequests) {
            HttpEntity<BookingRequest> entity = new HttpEntity<>(bookingRequest);
            ResponseEntity<Booking> response = this.restTemplate.postForEntity(getApiPath(), entity, Booking.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testCreateBookingValidRequest() {
        IntStream.range(0, 12).forEach((i) -> {
            BookingRequest bookingRequest = createCommonBookingRequest();
            HttpEntity<BookingRequest> entity = new HttpEntity<>(bookingRequest);
            ResponseEntity<Booking> response = this.restTemplate.postForEntity(getApiPath(), entity, Booking.class);
            Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(bookingRequest.getEmail(), response.getBody().getPlayerEmail());
        });

        // 13th request should fail
        BookingRequest bookingRequest = createCommonBookingRequest();
        HttpEntity<BookingRequest> entity = new HttpEntity<>(bookingRequest);
        ResponseEntity<Booking> response = this.restTemplate.postForEntity(getApiPath(), entity, Booking.class);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    public void testCreateBookingRequestSameEmailSameDay() {
            BookingRequest bookingRequest = new BookingRequest();
            bookingRequest.setName("test");
            Date date = new Date(122, 03, 02 );
            bookingRequest.setDate(date);
            bookingRequest.setEmail("test@test.com");
            HttpEntity<BookingRequest> entity1 = new HttpEntity<>(bookingRequest);
            ResponseEntity<Booking> response1 = this.restTemplate.postForEntity(getApiPath(), entity1, Booking.class);
            Assertions.assertEquals(response1.getStatusCode(), HttpStatus.OK);
            Assertions.assertEquals(bookingRequest.getEmail(), response1.getBody().getPlayerEmail());

            bookingRequest.setName("anotherName");
            HttpEntity<BookingRequest> entity2 = new HttpEntity<>(bookingRequest);
            ResponseEntity<Booking> response2 = this.restTemplate.postForEntity(getApiPath(), entity2, Booking.class);
            Assertions.assertEquals(response2.getStatusCode(), HttpStatus.CONFLICT);
    }



    private String getApiPath() {
        return "http://localhost:" + port + "/sport-centre/bookings";
    }

    private List<BookingRequest> createInvalidBookingRequest() {
        List<BookingRequest> bookingRequests = new ArrayList<>();
        BookingRequest missingDateRequest = new BookingRequest();
        missingDateRequest.setName("Test");
        missingDateRequest.setEmail("test@gmail.com");
        bookingRequests.add(missingDateRequest);

        BookingRequest pastDateRequest = new BookingRequest();
        Date pastDate = new Date();
        pastDate.setYear(100);
        pastDateRequest.setDate(pastDate);
        pastDateRequest.setName("Test");
        pastDateRequest.setEmail("test@gmail.com");
        bookingRequests.add(pastDateRequest);

        BookingRequest missingEmailRequest = new BookingRequest();
        Date date = new Date(2022, 02, 02 );
        missingEmailRequest.setDate(date);
        missingEmailRequest.setName("Test");
        bookingRequests.add(missingEmailRequest);

        BookingRequest invalidEmailRequest = new BookingRequest();
        invalidEmailRequest.setDate(date);
        invalidEmailRequest.setName("Test");
        invalidEmailRequest.setEmail("test");
        bookingRequests.add(invalidEmailRequest);

        BookingRequest blankNameRequest = new BookingRequest();
        blankNameRequest.setDate(date);
        blankNameRequest.setName("");
        blankNameRequest.setEmail("test");
        bookingRequests.add(blankNameRequest);


        return bookingRequests;
    }

    BookingRequest createCommonBookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setName("test");
        Date date = new Date(122, 02, 02 );
        request.setDate(date);
        request.setEmail(UUID.randomUUID().toString() + "@test.com");
        return request;
    }
}
