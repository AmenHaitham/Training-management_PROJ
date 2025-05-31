package com.tms.Services;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailSender {

    public boolean sendEmail(String to, String subject, String body) {

        String from = "tmsteam.platform@gmail.com";
        String host = "smtp.gmail.com";
        String username = "tmsteam.platform@gmail.com";
        String password = "okoxbrsfyykoypdy"; // Move this to environment variable for security

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✓ Email sent successfully");

            return true;

        } catch (MessagingException e) {
            System.err.println("✗ Failed to send email: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        EmailSender emailSender = new EmailSender();
        emailSender.sendEmail("youssefabrahem6@gmail.com", "Test Email", "This is a test email.");
    }
}
