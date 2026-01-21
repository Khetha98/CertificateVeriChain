package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import com.bloxbean.cardano.client.transaction.spec.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

import java.util.List;

@Service
public class CardanoService {

    //private static final String ISSUER_ADDRESS = "addr_test1vqhmymwkaqn5wl9yqdtnf667sxdzxuj603vjwrltxyl5ssqe76aa8";

    @Autowired
    private CardanoClient cardanoClient;

    private static final Logger log =
            LoggerFactory.getLogger(CardanoClient.class);

    @Autowired
    private TxBuilderService txBuilderService;

    @Autowired
    MerkleService merkleService;

    @Autowired
    private TransactionSigner transactionSigner;

    public Mono<String> anchorHash(String certificateHash) {
        String issuerAddress = transactionSigner.getAddress(); // ✅ get issuer.addr

        return cardanoClient.getCurrentSlot()
                .flatMap(slot ->
                        cardanoClient.getUtxos(issuerAddress)
                                .flatMap(utxos -> {
                                    if (utxos.isEmpty())
                                        return Mono.error(new IllegalStateException("Issuer has no UTXOs"));

                                    // Build TX
                                    Transaction tx = txBuilderService.buildTx(utxos, issuerAddress, certificateHash, slot);
                                    System.out.println("Unsigned TX: " + tx);

                                    // Sign TX
                                    byte[] signedBytes = transactionSigner.sign(tx);
                                    System.out.println("Signed TX (hex): " + Hex.toHexString(signedBytes));

                                    // Submit
                                    return cardanoClient.submitTransaction(signedBytes);
                                })
                );
    }

    public Mono<Boolean> verifyHashAgainstTx(String txHash, String expectedHash) {
        return cardanoClient.getTransaction(txHash)
                .map(metadata -> {
                    String onChainHash = metadata.path("certificateHash").asText();
                    if (onChainHash == null || onChainHash.isEmpty()) {
                        log.warn("certificateHash not found in metadata");
                        return false;
                    }
                    return onChainHash.equals(expectedHash);
                });
    }

    public Mono<Boolean> verifyMerkleInclusion(
            String certHash,
            String merkleProof,
            String merkleRoot,
            String batchTxHash
    ) {
        boolean included = merkleService.verify(
                certHash,
                merkleProof,
                merkleRoot
        );

        return verifyHashAgainstTx(batchTxHash, merkleRoot)
                .map(anchored -> included && anchored);
    }



    private boolean metadataHasHash(JsonNode metadata, String certificateHash) {
        if (metadata == null || metadata.isEmpty()) return false;

        for (JsonNode entry : metadata) {
            if (certificateHash.equals(entry.asText())) {
                return true;
            }
        }
        return false;
    }

}
