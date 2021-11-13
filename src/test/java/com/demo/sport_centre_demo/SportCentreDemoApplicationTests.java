package com.demo.sport_centre_demo;

import com.demo.sport_centre_demo.controller.BookingController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SportCentreDemoApplicationTests {

	@Autowired
	private BookingController bookingController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(bookingController);
	}

}
