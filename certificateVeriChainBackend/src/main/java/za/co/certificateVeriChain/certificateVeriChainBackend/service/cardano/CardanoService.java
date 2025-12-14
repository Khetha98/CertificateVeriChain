package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;

@Service
public class CardanoService {

    public String mintCertificateHash(String certificateHash) {
        // Call cardano-cli OR Java SDK
        // Submit transaction
        // Return txHash
        return "tx_hash_here";
    }
}
