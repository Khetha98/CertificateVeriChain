package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class SigningService {

    private static final String CLI = "cardano-cli";
    private static final String NETWORK = "--testnet-magic 1"; // preprod
    private static final Path WORKDIR = Path.of("/tmp/cardano");

    //private static final Path PAYMENT_SKEY = Path.of("/home/khetha/projects/blockchain-projects/CertificateVeriChain/certificateVeriChainBackend");
    private static final Path PAYMENT_SKEY = Path.of("/home/khetha/projects/blockchain-projects/CertificateVeriChain/certificateVeriChainBackend/issuer.skey");

    public Path signTransaction(Path unsignedTx) {
        try {
            Path signedTx = WORKDIR.resolve("tx.signed");

            ProcessBuilder pb = new ProcessBuilder(
                    CLI,
                    "conway","transaction",  "sign",
                    "--tx-body-file", unsignedTx.toString(),
                    "--signing-key-file", PAYMENT_SKEY.toString(),
                    "--testnet-magic", "1",
                    "--out-file", signedTx.toString()
            );

            pb.environment().put(
                    "CARDANO_NODE_SOCKET_PATH",
                    "/home/khetha/cardano/db/node.sock"
            );

            pb.inheritIO();
            int exit = pb.start().waitFor();

            if (exit != 0) {
                throw new RuntimeException("cardano-cli sign failed");
            }

            return signedTx;

        } catch (Exception e) {
            throw new RuntimeException("Failed to sign transaction", e);
        }
    }

}
