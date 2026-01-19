package za.co.certificateVeriChain.certificateVeriChainBackend.controller.verificationController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.VerificationResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoService;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.CertificateService;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.FileStorageService;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class VerificationController {

    @Autowired
    CertificateRepository certificateRepository;
    @Autowired
    CertificateService certificateService;
    @Autowired
    CardanoService cardanoService;
    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/verify/{certificateUid}")
    public Mono<VerificationResponse> verifyCertificate(
            @PathVariable String certificateUid
    ) {
        Certificate cert = certificateRepository
                .findByCertificateUid(certificateUid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Certificate not found"
                ));

        if (!"ACTIVE".equals(cert.getStatus())) {
            return Mono.just(new VerificationResponse(
                    false, "Certificate is not active"
            ));
        }

        return cardanoService
                .verifyHashAgainstTx(
                        cert.getTxHash(),
                        cert.getCertificateHash()
                )
                .map(valid -> new VerificationResponse(
                        valid,
                        valid ? "Certificate is valid" : "Certificate hash mismatch",
                        cert.getStudentName(),
                        cert.getOrganization().getName(),
                        cert.getIssuedAt()
                ))
                .onErrorReturn(new VerificationResponse(
                        false, "Blockchain verification failed"
                ));
    }





    @GetMapping(
            value = "/certificates/{uid}/download",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public byte[] download(@PathVariable String uid) {
        return fileStorageService.download("issued-certificates/" + uid + ".pdf");
    }

}
