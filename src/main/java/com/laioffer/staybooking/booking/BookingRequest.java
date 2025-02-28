package com.laioffer.staybooking.booking;

import java.time.LocalDate;

public record BookingRequest(
        long listingId,
        LocalDate checkInDate,
        LocalDate checkOutDate
        ) {

}
