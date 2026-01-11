package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

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
            String txHash, // The service should fetch the best UTXO, not just use this one blindly
            int txIndex,
            String senderAddr,
            Path metadataFile
    ) {
        try {
            Path unsignedTx = WORKDIR.resolve("tx.raw");

            // Define the full command exactly as separate strings
            ProcessBuilder pb = new ProcessBuilder(
                    "cardano-cli", "conway" ,"transaction","build", // Fixed: Only use "build" here
                    "--testnet-magic", "1",
                    "--socket-path", "/home/khetha/cardano/db/node.sock",
                    "--tx-in", txHash + "#" + txIndex, // This must be the *correct* UTXO hash
                    "--tx-out", senderAddr + "+" + "1000000",
                    "--change-address", senderAddr,
                    "--metadata-json-file", metadataFile.toString(),
                    "--out-file", unsignedTx.toString()
            );

            // Optional: Print the command for debugging purposes to match your terminal
            System.out.println("Executing CLI command: " + String.join(" ", pb.command()));

            pb.inheritIO().start().waitFor();
            return unsignedTx;

        } catch (Exception e) {
            throw new RuntimeException("Failed to build tx", e);
        }
    }



    public byte[] readSignedTransaction(Path signedTx) {
        try {
            byte[] txBytes = Files.readAllBytes(signedTx);
            System.out.println("Signed TX size: " + txBytes.length);
            return Files.readAllBytes(signedTx);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read signed tx", e);
        }
    }
}

/*@Service
public class CardanoCliService {

    private static final String CLI = "cardano-cli";
    private static final Path WORKDIR = Path.of("/tmp/cardano");
    private static final Path PAYMENT_SKEY =
            Path.of("/home/khetha/projects/blockchain-projects/CertificateVeriChain/certificateVeriChainBackend/issuer.skey");
    private static final String SOCKET_PATH = "/home/khetha/cardano/db/node.sock";
    private static final int TESTNET_MAGIC = 1;

    public CardanoCliService() throws IOException {
        Files.createDirectories(WORKDIR);
    }

    public Path buildTransaction(UtxoResponse utxo, String senderAddr, Path metadataFile) {
        try {
            long inputAmount = utxo.getLovelace();
            long feeBuffer = 200_000; // safe fee estimate
            long txOutAmount = inputAmount - feeBuffer;
            if (txOutAmount <= 0) throw new RuntimeException("UTXO too small to cover fee");

            Path unsignedTx = WORKDIR.resolve("tx.raw");

            ProcessBuilder pb = new ProcessBuilder(
                    CLI, "conway", "transaction", "build",
                    "--testnet-magic", String.valueOf(TESTNET_MAGIC),
                    "--socket-path", SOCKET_PATH,
                    "--tx-in", utxo.getTx_hash() + "#" + utxo.getTx_index(),
                    "--tx-out", senderAddr + "+" + txOutAmount,
                    "--change-address", senderAddr,
                    "--metadata-json-file", metadataFile.toString(),
                    "--out-file", unsignedTx.toString()
            );

            System.out.println("Executing CLI command: " + String.join(" ", pb.command()));
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
                    CLI, "conway", "transaction", "sign",
                    "--tx-body-file", unsignedTx.toString(),
                    "--signing-key-file", PAYMENT_SKEY.toString(),
                    "--testnet-magic", String.valueOf(TESTNET_MAGIC),
                    "--out-file", signedTx.toString()
            );
            pb.environment().put("CARDANO_NODE_SOCKET_PATH", SOCKET_PATH);
            pb.inheritIO();
            int exit = pb.start().waitFor();
            if (exit != 0) throw new RuntimeException("cardano-cli sign failed");
            return signedTx;
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign transaction", e);
        }
    }

    public void submitTransaction(Path signedTx) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    CLI, "conway", "transaction", "submit",
                    "--testnet-magic", String.valueOf(TESTNET_MAGIC),
                    "--tx-file", signedTx.toString()
            );
            pb.environment().put("CARDANO_NODE_SOCKET_PATH", SOCKET_PATH);
            pb.inheritIO();
            int exit = pb.start().waitFor();
            if (exit != 0) throw new RuntimeException("cardano-cli submit failed");
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit transaction", e);
        }
    }
}*/


