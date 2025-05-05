package com.example.demo.services;


import com.example.demo.Dtos.ContactRequestDTO;
import com.example.demo.Dtos.UserRegisteredEvent;
import com.example.demo.constants.ApplicationConstants;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Environment env;

    public void sendEmail( String subject, String contentHtml) throws IOException {
        Email from = new Email("malinandkrzyslaw@gmail.com");
        Email to = new Email("krzysztofkandyba6@gmail.com");

        Content content = new Content("text/html", contentHtml);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(env.getProperty(ApplicationConstants.SendGrid_API_KEY));
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
    }
    public String loadHtmlTemplate(ContactRequestDTO dto) throws IOException {
        String path = "src/main/resources/templates/email-template.html";
        String template = new String(Files.readAllBytes(Paths.get(path)));

        return template
                .replace("{{name}}", dto.getName())
                .replace("{{email}}", dto.getEmail())
                .replace("{{subject}}", dto.getSubject())
                .replace("{{message}}", dto.getMessage());
    }
    public void sendWelcomeEmail(UserRegisteredEvent event) throws IOException {
        Email from = new Email("malinandkrzyslaw@gmail.com");
        Email to = new Email(event.getEmail());

        String subject = "Welcome to FootballBetApp!";
        String contentHtml = loadWelcomeTemplate(event.getEmail());

        Content content = new Content("text/html", contentHtml);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(ApplicationConstants.SendGrid_API_KEY);
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);
        System.out.println("Mail status: " + response.getStatusCode());
    }

    private String loadWelcomeTemplate(String name) throws IOException {
        String path = "src/main/resources/templates/welcome-email-template.html";
        String template = new String(Files.readAllBytes(Paths.get(path)));

        return template.replace("{{name}}", name);
    }

}
