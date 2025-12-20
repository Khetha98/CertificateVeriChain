package za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoService;

import java.util.Map;

@Service
public class CertificateService {

    private final CardanoService cardanoService;
    private final ObjectMapper objectMapper;

    public CertificateService(CardanoService cardanoService) {
        this.cardanoService = cardanoService;
        this.objectMapper = new ObjectMapper();
    }

    public Mono<String> issueCertificate(Certificate cert) {

        String canonicalJson = canonicalize(cert);
        String sha256 = DigestUtils.sha256Hex(canonicalJson);

        return cardanoService.mintCertificateHash(sha256);
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
