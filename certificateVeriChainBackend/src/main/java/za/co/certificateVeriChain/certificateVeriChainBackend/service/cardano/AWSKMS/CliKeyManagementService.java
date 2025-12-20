package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.AWSKMS;

import org.springframework.stereotype.Service;

@Service
public class CliKeyManagementService implements KeyManagementService {
    public byte[] sign(byte[] txHash) {
        // delegated to cardano-cli
        return null;
    }
}
