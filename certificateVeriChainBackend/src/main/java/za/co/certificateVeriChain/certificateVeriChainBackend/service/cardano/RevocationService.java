package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;

@Service
@RequiredArgsConstructor
public class RevocationService {

    private final CertificateRepository certificateRepository;

    public Mono<Certificate> revoke(String uid) {

        Certificate cert =
                certificateRepository.findByCertificateUid(uid)
                        .orElseThrow();

        cert.setStatus("REVOKED");

        return Mono.just(
                certificateRepository.save(cert)
        );
    }
}

