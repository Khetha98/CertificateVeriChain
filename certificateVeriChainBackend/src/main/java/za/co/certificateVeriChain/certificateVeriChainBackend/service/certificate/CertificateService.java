package za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoService;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class CertificateService {

    private final CardanoService cardanoService;
    private final ObjectMapper objectMapper;
    private final CertificateRepository certificateRepository;

    public CertificateService(CardanoService cardanoService, CertificateRepository certificateRepository) {
        this.cardanoService = cardanoService;
        this.objectMapper = new ObjectMapper();
        this.certificateRepository = certificateRepository;
    }

    public Mono<Certificate> issueCertificate(Certificate cert) {

        String canonicalJson = canonicalize(cert);
        String hash = DigestUtils.sha256Hex(canonicalJson);

        cert.setCertificateHash(hash);
        cert.setIssuedAt(Instant.now().toString());
        cert.setStatus("PENDING");
        cert.setVerificationCode(UUID.randomUUID().toString());

        return cardanoService.anchorHash(hash)
                .map(txHash -> {
                    cert.setTxHash(txHash);
                    cert.setStatus("ACTIVE");
                    return certificateRepository.save(cert);
                });
    }




    private String canonicalize(Certificate cert) {
        try {
            Map<String, Object> data = Map.of(
                    "certificateUid", cert.getCertificateUid(),
                    "studentName", cert.getStudentName(),
                    "organizationId", cert.getOrganization().getId(),
                    "templateId", cert.getTemplate().getId()
            );
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to canonicalize certificate", e);
        }
    }
}
