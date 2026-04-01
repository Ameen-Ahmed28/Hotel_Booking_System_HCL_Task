package com.hotel.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@hotel.com}")
    private String fromEmail;

    @Async
    public void sendVerificationEmail(String toEmail, String token, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Hotel Management System - Verify Your Email");
            
            String verificationUrl = frontendUrl + "/verify-email?token=" + token;
            
            message.setText(
                "Dear " + userName + ",\n\n" +
                "Thank you for registering with Hotel Management System!\n\n" +
                "Please click the following link to verify your email address:\n" +
                verificationUrl + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not create an account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Hotel Management System Team"
            );
            
            mailSender.send(message);
            System.out.println("Verification email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
            // Don't throw exception - we don't want to break registration if email fails
        }
    }

    @Async
    public void sendBookingConfirmationEmail(String toEmail, String userName, String hotelName, 
            String roomNumber, String roomType, String checkIn, String checkOut, String totalPrice) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Hotel Management System - Booking Confirmation");
            
            message.setText(
                "Dear " + userName + ",\n\n" +
                "Your booking has been confirmed!\n\n" +
                "Booking Details:\n" +
                "-----------------\n" +
                "Hotel: " + hotelName + "\n" +
                "Room Number: " + roomNumber + "\n" +
                "Room Type: " + roomType + "\n" +
                "Check-in Date: " + checkIn + "\n" +
                "Check-out Date: " + checkOut + "\n" +
                "Total Price: $" + totalPrice + "\n\n" +
                "Thank you for choosing our hotel!\n\n" +
                "Best regards,\n" +
                "Hotel Management System Team"
            );
            
            mailSender.send(message);
            System.out.println("Booking confirmation email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    @Async
    public void sendCancellationEmail(String toEmail, String userName, String hotelName, 
            String roomNumber, String checkIn, String checkOut) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Hotel Management System - Booking Cancelled");
            
            message.setText(
                "Dear " + userName + ",\n\n" +
                "Your booking has been cancelled.\n\n" +
                "Cancelled Booking Details:\n" +
                "-----------------\n" +
                "Hotel: " + hotelName + "\n" +
                "Room Number: " + roomNumber + "\n" +
                "Check-in Date: " + checkIn + "\n" +
                "Check-out Date: " + checkOut + "\n\n" +
                "We hope to see you again soon!\n\n" +
                "Best regards,\n" +
                "Hotel Management System Team"
            );
            
            mailSender.send(message);
            System.out.println("Cancellation email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }
    }
}
