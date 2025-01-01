package com.example;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import org.springframework.boot.rsocket.server.RSocketServer.Transport;

public class EmailNotification {

    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        // Configuration for sending email

        String host = "smtp.gmail.com"; // Example: Gmail SMTP
        String fromEmail = "youremail@gmail.com"; // user email address 
        String password = "yourpassword"; // user email password

        // Setup the mail server properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Get the email session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
        try {
            // Create a MimeMessage for the email
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            Transport.send(message);
            System.out.println("Reminder email sent.");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String toEmail = "useremail@example.com"; // Replace with the user email address
        String taskName = "Finish Assignment"; // Example task
        String dueDate = "2024-12-26 23:59"; // Example due date as string
        String subject = "Task Reminder: " + taskName;
        String body = "Reminder: The task \"" + taskName + "\" is due in 24 hours (" + dueDate + ").";

        // Send email reminder
        sendEmail(toEmail, subject, body);
    }
}
