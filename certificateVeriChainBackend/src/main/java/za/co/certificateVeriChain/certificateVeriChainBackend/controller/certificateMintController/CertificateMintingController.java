package za.co.certificateVeriChain.certificateVeriChainBackend.controller.certificateMintController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.CertificateService;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/issuer/certificates")
@RequiredArgsConstructor
public class CertificateMintingController {

    private final CertificateService certificateService;

    @PostMapping("/issue")
    public Mono<ResponseEntity<Certificate>> issue(@RequestBody Certificate cert) {
        return certificateService.issueCertificate(cert)
                .map(ResponseEntity::ok);
    }
}

