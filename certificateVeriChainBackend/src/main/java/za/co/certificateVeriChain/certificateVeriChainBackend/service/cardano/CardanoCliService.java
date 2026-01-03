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

    public Path buildTransaction(
            String txHash,
            int txIndex,
            String senderAddr,
            Path metadataFile
    ) {
        try {
            Path unsignedTx = WORKDIR.resolve("tx.raw");

            ProcessBuilder pb = new ProcessBuilder(
                    CLI, "transaction", "build",
                    "--babbage-era",
                    "--testnet-magic", "1",
                    "--tx-in", txHash + "#" + txIndex,
                    "--tx-out", senderAddr + "+2000000",
                    "--change-address", senderAddr,
                    "--metadata-json-file", metadataFile.toString(),
                    "--out-file", unsignedTx.toString()
            );

            pb.inheritIO().start().waitFor();
            return unsignedTx;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build tx", e);
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

