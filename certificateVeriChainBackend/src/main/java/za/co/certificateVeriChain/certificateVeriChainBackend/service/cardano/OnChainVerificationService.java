package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.TxVerifyResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;

@Service
@RequiredArgsConstructor
public class OnChainVerificationService {

    private final CardanoClient cardanoClient;
    private final CertificateRepository certificateRepository;

    public Mono<TxVerifyResponse> verifyTx(String txHash) {

        Certificate cert = certificateRepository.findByTxHash(txHash)
                .orElseThrow();

        return cardanoClient.getTransaction(txHash)
                .map(meta -> {

                    String onChainHash =
                            meta.get("674")
                                    .get("certificate_hash")
                                    .asText();

                    TxVerifyResponse res = new TxVerifyResponse();
                    res.setTxHash(txHash);
                    res.setOnChainHash(onChainHash);
                    res.setDbHash(cert.getCertificateHash());
                    res.setValid(onChainHash.equals(cert.getCertificateHash()));

                    return res;
                });
    }
}
