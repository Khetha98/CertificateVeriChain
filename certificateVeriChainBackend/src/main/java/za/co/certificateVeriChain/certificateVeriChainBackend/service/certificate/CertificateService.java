package za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate;

import org.springframework.stereotype.Service;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoService;

@Service
public class CertificateService {

    private final CardanoService cardanoService;

    public CertificateService(CardanoService cardanoService) {
        this.cardanoService = cardanoService;
    }

    public void issueCertificate(Certificate cert) {
        String hash = hash(cert);
        cardanoService.mintCertificateHash(hash);
    }
}
