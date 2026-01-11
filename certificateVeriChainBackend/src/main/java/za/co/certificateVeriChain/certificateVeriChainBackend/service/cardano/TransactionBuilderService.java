package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class TransactionBuilderService {

    private static final String TESTNET_MAGIC = "1";
    private static final String ISSUER_ADDRESS =
            "addr_test1vqhmymwkaqn5wl9yqdtnf667sxdzxuj603vjwrltxyl5ssqe76aa8";

    private static final String PROTOCOL_PARAMS = "protocol.json";

    /*public Path buildTransaction(
            String txHash,
            int txIndex,
            Path metadataFile
    ) {
        ProcessBuilder pb = new ProcessBuilder(
                "cardano-cli", "conway", "build",
                "--testnet-magic", TESTNET_MAGIC,

                "--tx-in", txHash + "#" + txIndex,

                "--tx-out", ISSUER_ADDRESS + "+3000000",
                "--change-address", ISSUER_ADDRESS,

                "--metadata-json-file", metadataFile.toString(),

                "--out-file", "tx.raw"
        );

        System.out.println("Working dir = " + Path.of("").toAbsolutePath());
        System.out.println("Protocol exists = " + Path.of(PROTOCOL_PARAMS).toFile().exists());

        try {
            pb.inheritIO(); //  IMPORTANT: see CLI errors
            pb.environment().put(
                    "CARDANO_NODE_SOCKET_PATH",
                    "/home/khetha/cardano/db/node.sock"
            );
            Process p = pb.inheritIO().start();
            int exit = p.waitFor();

            if (exit != 0) {
                throw new RuntimeException("cardano-cli build failed");
            }

        } catch (Exception ex) {
            throw new RuntimeException("Transaction build failed", ex);
        }

        return Path.of("tx.raw");
    }*/
}
