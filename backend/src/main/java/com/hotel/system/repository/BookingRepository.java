package com.hotel.system.repository;

import com.hotel.system.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId); // Keep for existing logic if any

    Page<Booking> findByUser_Id(Long userId, Pageable pageable);

    List<Booking> findByRoom_Id(Long roomId);

    // Find overlapping bookings for a specific room
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status = 'BOOKED' " +
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findOverlappingBookings(@Param("roomId") Long roomId, 
                                          @Param("checkIn") LocalDate checkIn, 
                                          @Param("checkOut") LocalDate checkOut);

    // Find all bookings for a room in a specific date range
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status = 'BOOKED' " +
           "AND b.checkOutDate >= :startDate AND b.checkInDate <= :endDate")
    List<Booking> findBookingsInDateRange(@Param("roomId") Long roomId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    // Count bookings for a specific room type at a hotel in a date range
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.room.hotel.id = :hotelId " +
           "AND b.room.type = :roomType " +
           "AND b.status = 'BOOKED' " +
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    Long countBookedRoomsByTypeAndDateRange(@Param("hotelId") Long hotelId,
                                            @Param("roomType") String roomType,
                                            @Param("checkIn") LocalDate checkIn,
                                            @Param("checkOut") LocalDate checkOut);

    // Find all bookings for a hotel in a date range
    @Query("SELECT b FROM Booking b WHERE b.room.hotel.id = :hotelId " +
           "AND b.status = 'BOOKED' " +
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findBookingsByHotelAndDateRange(@Param("hotelId") Long hotelId,
                                                   @Param("checkIn") LocalDate checkIn,
                                                   @Param("checkOut") LocalDate checkOut);
}
