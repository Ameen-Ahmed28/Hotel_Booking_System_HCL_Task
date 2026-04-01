package com.hotel.system.repository;

import com.hotel.system.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel_Id(Long hotelId);
    
    // Count rooms by hotel and type
    long countByHotelIdAndType(Long hotelId, Room.RoomType type);
    
    // Get all rooms by hotel and type
    List<Room> findByHotelIdAndType(Long hotelId, Room.RoomType type);
    
    // Get all unique room types for a hotel
    @Query("SELECT DISTINCT r.type FROM Room r WHERE r.hotel.id = :hotelId")
    List<Room.RoomType> findDistinctRoomTypesByHotelId(@Param("hotelId") Long hotelId);
    
    // Count all rooms for a hotel
    long countByHotelId(Long hotelId);
}
