package za.co.certificateVeriChain.certificateVeriChainBackend.controller.contactUsController;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.ContactRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContactUsController {

        @Autowired
        private final JavaMailSender mailSender;

        @PostMapping("/contact")
        public ResponseEntity<String> contact(@RequestBody ContactRequest request) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(request.email);
            message.setTo("Khethukuthulasimamane@gmail.com"); // Send the contact info to yourself
            message.setSubject("New Contact from " + request.getName());
            message.setText("From: " + request.getEmail() + "\n" +
                    "Org: " + request.getOrganization() + "\n\n" +
                    "Message: " + request.getMessage());

            mailSender.send(message);

            return ResponseEntity.ok("Your message has been sent!");
        }

}
