package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class CardanoCliService {

    private static final String CLI = "cardano-cli";
    private static final String NETWORK = "--testnet-magic 1"; // preprod
    private static final Path WORKDIR = Path.of("/tmp/cardano");

    private static final Path PAYMENT_SKEY =
            Path.of("/opt/cardano/keys/payment.skey");

    public CardanoCliService() throws IOException {
        Files.createDirectories(WORKDIR);
    }

    public Path buildTransaction(Path metadataFile) {
        try {
            Path unsignedTx = WORKDIR.resolve("tx.raw");

            ProcessBuilder pb = new ProcessBuilder(
                    CLI, "transaction", "build-raw",
                    "--tx-in", "<UTXO>#0",              // replace later
                    "--tx-out", "<ADDR>+2000000",       // min ADA
                    "--metadata-json-file", metadataFile.toString(),
                    "--fee", "0",
                    "--out-file", unsignedTx.toString()
            );

            pb.inheritIO().start().waitFor();
            return unsignedTx;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build transaction", e);
        }
    }

    public Path signTransaction(Path unsignedTx) {
        try {
            Path signedTx = WORKDIR.resolve("tx.signed");

            ProcessBuilder pb = new ProcessBuilder(
                    CLI, "transaction", "sign",
                    "--tx-body-file", unsignedTx.toString(),
                    "--signing-key-file", PAYMENT_SKEY.toString(),
                    NETWORK,
                    "--out-file", signedTx.toString()
            );

            pb.inheritIO().start().waitFor();
            return signedTx;

        } catch (Exception e) {
            throw new RuntimeException("Failed to sign transaction", e);
        }
    }

    public byte[] readSignedTransaction(Path signedTx) {
        try {
            return Files.readAllBytes(signedTx);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read signed tx", e);
        }
    }
}

