package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

import java.nio.file.Path;
import java.util.Comparator;

@Service
public class CardanoService {

    private static final String ISSUER_ADDRESS = "addr_test1vqhmymwkaqn5wl9yqdtnf667sxdzxuj603vjwrltxyl5ssqe76aa8";

    @Autowired
    MetadataService metadataService;
    @Autowired
    CardanoCliService cliService;
    @Autowired
    CardanoClient cardanoClient;
    @Autowired
    SigningService signingService;

    public Mono<String> anchorHash(String certificateHash) {
        Path metadata = metadataService.writeMetadataFile(certificateHash);

        return cardanoClient.getUtxos(ISSUER_ADDRESS)
                .flatMapMany(Flux::fromIterable)
                .sort(Comparator.comparingLong(UtxoResponse::getLovelace).reversed())
                .filter(utxo -> utxo.getLovelace() >= 5_000_000)
                .next()
                .switchIfEmpty(Mono.error(new IllegalStateException("Issuer address has no UTXOs")))
                .map(utxo -> cliService.buildTransaction(
                        utxo.getTx_hash(),
                        utxo.getTx_index(),
                        ISSUER_ADDRESS,
                        metadata
                ))
                .map(signingService::signTransaction)
                .map(cliService::readSignedTransaction)
                .flatMap(cardanoClient::submitTransaction)
                .onErrorResume(ex ->
                        Mono.just("ANCHOR_FAILED: " + ex.getMessage())
                );
    }

}




