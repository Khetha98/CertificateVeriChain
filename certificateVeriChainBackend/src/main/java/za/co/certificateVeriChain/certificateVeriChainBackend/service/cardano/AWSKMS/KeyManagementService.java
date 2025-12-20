package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.AWSKMS;

public interface KeyManagementService {
    byte[] sign(byte[] txHash);
}
