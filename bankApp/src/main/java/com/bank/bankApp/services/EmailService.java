package com.bank.bankApp.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    private final SendGrid sendGrid;
    private final String fromEmail;

    public EmailService(@Value("${sendgrid.api.key}") String sendGridApiKey, @Value("${sendgrid.from.email}") String fromEmail) {
        this.sendGrid = new SendGrid(sendGridApiKey);
        this.fromEmail = fromEmail;
    }

    public void sendEmail(String to, String subject, String body) throws IOException {

        Email from = new Email(fromEmail);
        Email recipient = new Email(to);

        String htmlContent = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color:#2d6cdf;'>Banking App Notification</h2>" +
                "<p>Hello " + fromEmail + ",</p>" +
//                "<p>Your OTP is: <b>" + otp + "</b></p>" +
                "<p>"+body+ "</p>" +
                "<p>If you did not request this, please contact support immediately.</p>" +
                "<br/>" +
                "<p style='font-size:12px;color:gray;'>-- Your Bank</p>" +
                "</body>" +
                "</html>";

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, recipient, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());



        Response response = sendGrid.api(request);

        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        System.out.println("Response Headers: " + response.getHeaders());

        if (response.getStatusCode() >= 400) {
            throw new RuntimeException("Failed to send email: " + response.getBody());
        }

    }

}
