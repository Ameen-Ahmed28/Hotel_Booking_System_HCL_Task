package com.hotel.system.service.impl;

import com.hotel.system.dto.DTOs;
import com.hotel.system.entity.Booking;
import com.hotel.system.entity.Room;
import com.hotel.system.entity.User;
import com.hotel.system.exception.BadRequestException;
import com.hotel.system.exception.ResourceNotFoundException;
import com.hotel.system.exception.RoomNotAvailableException;
import com.hotel.system.repository.BookingRepository;
import com.hotel.system.repository.RoomRepository;
import com.hotel.system.repository.UserRepository;
import com.hotel.system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BookingServiceImpl {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Booking createBooking(DTOs.BookingRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate dates
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past.");
        }
        if (request.getCheckOutDate().isBefore(request.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date.");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // Check if room is available for the requested dates
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate());

        if (!overlappingBookings.isEmpty()) {
            throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() + " is not available for the selected dates. " +
                    "Please choose different dates or another room.");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setStatus(Booking.BookingStatus.BOOKED);

        long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (days < 1) days = 1;
        booking.setTotalPrice(room.getPricePerNight().multiply(BigDecimal.valueOf(days)));

        Booking savedBooking = bookingRepository.save(booking);

        // Send booking confirmation email
        emailService.sendBookingConfirmationEmail(
                user.getEmail(),
                user.getName(),
                room.getHotel().getName(),
                room.getRoomNumber(),
                room.getType().getDisplayName(),
                request.getCheckInDate().toString(),
                request.getCheckOutDate().toString(),
                booking.getTotalPrice().toString()
        );

        return savedBooking;
    }

    public List<Booking> getMyBookings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUser_Id(user.getId());
    }

    public Page<Booking> getMyBookings(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return bookingRepository.findByUser_Id(user.getId(), pageable);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking is already cancelled.");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Send cancellation email
        User user = booking.getUser();
        Room room = booking.getRoom();
        emailService.sendCancellationEmail(
                user.getEmail(),
                user.getName(),
                room.getHotel().getName(),
                room.getRoomNumber(),
                booking.getCheckInDate().toString(),
                booking.getCheckOutDate().toString()
        );
    }

    @Transactional
    public Booking extendBooking(Long id, DTOs.ExtendBookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != Booking.BookingStatus.BOOKED) {
            throw new BadRequestException("Cannot extend a cancelled booking.");
        }

        if (request.getNewCheckOutDate().isBefore(booking.getCheckOutDate())) {
            throw new BadRequestException("New checkout date must be after current checkout date.");
        }

        // Check if room is available for the extended period
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                booking.getRoom().getId(),
                booking.getCheckOutDate().plusDays(1),
                request.getNewCheckOutDate()
        );

        // Filter out the current booking
        overlappingBookings = overlappingBookings.stream()
                .filter(b -> !b.getId().equals(booking.getId()))
                .collect(Collectors.toList());

        if (!overlappingBookings.isEmpty()) {
            throw new RoomNotAvailableException(
                    "Cannot extend booking. Room is booked for the requested dates.");
        }

        booking.setCheckOutDate(request.getNewCheckOutDate());

        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        if (days < 1) days = 1;

        booking.setTotalPrice(booking.getRoom().getPricePerNight().multiply(BigDecimal.valueOf(days)));

        return bookingRepository.save(booking);
    }

    // Get room availability for a hotel
    public List<DTOs.RoomAvailability> getRoomAvailability(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        List<Room.RoomType> roomTypes = roomRepository.findDistinctRoomTypesByHotelId(hotelId);
        
        return roomTypes.stream().map(type -> {
            long totalRooms = roomRepository.countByHotelIdAndType(hotelId, type);
            
            // Get all rooms of this type
            List<Room> roomsOfType = roomRepository.findByHotelIdAndType(hotelId, type);
            
            // Find which rooms are booked during the requested period
            List<Long> bookedRoomIds = roomsOfType.stream()
                    .filter(room -> {
                        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                                room.getId(), checkIn, checkOut);
                        return !overlapping.isEmpty();
                    })
                    .map(Room::getId)
                    .collect(Collectors.toList());
            
            long availableRooms = totalRooms - bookedRoomIds.size();
            
            // Get price from first room of this type
            BigDecimal pricePerNight = roomsOfType.isEmpty() ? 
                    BigDecimal.ZERO : roomsOfType.get(0).getPricePerNight();
            
            return new DTOs.RoomAvailability(
                    type.name(),
                    type.getDisplayName(),
                    (int) totalRooms,
                    (int) availableRooms,
                    pricePerNight,
                    bookedRoomIds
            );
        }).collect(Collectors.toList());
    }

    // Get available rooms for a specific room type
    public List<Room> getAvailableRooms(Long hotelId, Room.RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        List<Room> roomsOfType = roomRepository.findByHotelIdAndType(hotelId, roomType);
        
        return roomsOfType.stream()
                .filter(room -> {
                    List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                            room.getId(), checkIn, checkOut);
                    return overlapping.isEmpty();
                })
                .collect(Collectors.toList());
    }
}
