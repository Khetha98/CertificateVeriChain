package za.co.certificateVeriChain.certificateVeriChainBackend.controller.verificationController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.BatchVerificationResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.VerificationResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.CertificateBatch;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateBatchRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoService;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.MerkleService;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.CertificateService;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.FileStorageService;


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
    @Autowired
    CertificateBatchRepository certificateBatchRepository;
    @Autowired
    MerkleService merkleService;

    @GetMapping("/verify/{certificateUid}")
    public VerificationResponse verify(@PathVariable String certificateUid) {

        Certificate cert = certificateRepository
                .findByCertificateUid(certificateUid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Certificate not found"
                ));

        if (!"ACTIVE".equals(cert.getStatus())) {
            return new VerificationResponse(false, "Certificate not active");
        }

        // 🔹 BULK CERTIFICATE → MERKLE VERIFICATION
        if (cert.getBatch() != null) {

            boolean valid = merkleService.verify(
                    cert.getCertificateHash(),
                    cert.getMerkleProof(),
                    cert.getBatch().getMerkleRoot()
            );

            return new VerificationResponse(
                    valid,
                    valid ? "Certificate valid (Merkle verified)" : "Merkle proof invalid",
                    cert.getStudentName(),
                    cert.getOrganization().getName(),
                    cert.getIssuedAt()
            );
        }

        // 🔹 SINGLE CERTIFICATE → BLOCKCHAIN VERIFICATION
        Boolean valid = cardanoService.verifyHashAgainstTx(
                cleanTx(cert.getTxHash()),
                cert.getCertificateHash()
        ).block();

        return new VerificationResponse(
                Boolean.TRUE.equals(valid),
                Boolean.TRUE.equals(valid)
                        ? "Certificate valid (On-chain)"
                        : "Blockchain verification failed",
                cert.getStudentName(),
                cert.getOrganization().getName(),
                cert.getIssuedAt()
        );
    }




    @GetMapping("certificates/{uid}/pdf")
    public ResponseEntity<byte[]> download(@PathVariable String uid) {
        byte[] pdf = certificateService.generateVerifiedCertificate(uid);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=certificate-" + uid + ".pdf")
                .body(pdf);
    }

    private String cleanTx(String tx) {
        return tx.replace("\"", "").trim();
    }


}
