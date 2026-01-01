package za.co.certificateVeriChain.certificateVeriChainBackend.controller.verificationController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;

@RestController
@RequestMapping
public class VerificationController {

    CertificateRepository certificateRepository;

    @GetMapping("/verify/{code}")
    public Certificate verify(@PathVariable String code) {
        Certificate cert = certificateRepository
                .findByVerificationCode(code)
                .orElseThrow();

        if ("REVOKED".equals(cert.getStatus())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Certificate revoked");
        }

        return cert;
    }

}
