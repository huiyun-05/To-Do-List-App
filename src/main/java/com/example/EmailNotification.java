package com.example;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.text.*;
import java.text.ParseException;

public class EmailNotification {

    // Method to send the email
    public static void sendEmail(String receiverEmail, String taskName, String dueDate) {
        String userEmail = "youremail@gmail.com"; // Sender's email
        String password = "yourpassword"; // Your email password 
        
        // Set SMTP properties for Gmail
        Properties pr = new Properties();
        pr.put("mail.smtp.host", "smtp.gmail.com");
        pr.put("mail.smtp.port", "587");
        pr.put("mail.smtp.auth", "true");
        pr.put("mail.smtp.starttls.enable", "true");

        // Create session
        Session session = Session.getInstance(pr, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userEmail, password);
            }
        });

        try {
            
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            message.setSubject("Task Reminder: " + taskName);
            message.setText("Reminder: The task \"" + taskName + "\" is due in 24 hours on " + dueDate + ".");

            // Send message
            Transport.send(message);
            System.out.println("Reminder email sent successfully!");

        } catch (MessagingException e) {
           System.err.println("Email cannot be sent :"  + e.getMessage());
        }
    }

    // Method to calculate the time difference and send reminder if within 24 hours
    public static void checkTaskDueDate(String receiverEmail, String taskName, String dueDateStr) {
        try {
            // Parse the due date (assuming format "yyyy-MM-dd HH:mm")
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dueDate = dateFormat.parse(dueDateStr);
            Date currentDate = new Date();

            // Calculate time difference
            long diffInMillies = dueDate.getTime() - currentDate.getTime();
            long diffInHours = diffInMillies / (60 * 60 * 1000);

            // Send an email reminder if the due date is within 24 hours
            if (diffInHours <= 24 && diffInHours > 0) {
                sendEmail(receiverEmail, taskName, dueDateStr);
            } else {
                System.out.println("No reminder necessary. Task is not due within 24 hours.");
            }

        }catch (ParseException e) {
            
            System.out.println("The date format you entered is incorrect. Please try again with the correct format (yyyy-MM-dd HH:mm).");
        }
    }

    public static void main(String[] args) {
        // User input: configure the email address and task details
        String receiverEmail = "recipientemail@example.com";  // Receiver's email
        String taskName = "Finish Assignment";
        String dueDate = "2025-01-02 10:00";  // Task due date (example)

        // Check if the task is due within 24 hours
        checkTaskDueDate(receiverEmail, taskName, dueDate);
    }
}
