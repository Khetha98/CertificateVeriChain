package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import com.bloxbean.cardano.client.transaction.spec.Transaction;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

import java.util.List;

@Service
public class CardanoService {

    private static final String ISSUER_ADDRESS = "addr_test1vqhmymwkaqn5wl9yqdtnf667sxdzxuj603vjwrltxyl5ssqe76aa8";

    @Autowired
    private CardanoClient cardanoClient;

    @Autowired
    private TxBuilderService txBuilderService;

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

}
