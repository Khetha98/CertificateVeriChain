package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

import java.nio.file.Path;

@Service
public class CardanoService {

    private static final String ISSUER_ADDRESS = "addr_test1...";

    @Autowired
    MetadataService metadataService;
    @Autowired
    CardanoCliService cliService;
    @Autowired
    CardanoClient cardanoClient;

    public Mono<String> anchorHash(String certificateHash) {

        Path metadata = metadataService.writeMetadataFile(certificateHash);

        return cardanoClient.getUtxos(ISSUER_ADDRESS)
                .flatMapMany(Flux::fromIterable)
                .next()
                .switchIfEmpty(Mono.<UtxoResponse>error(
                        new IllegalStateException("Issuer address has no UTXOs")
                ))
                .map(utxo -> cliService.buildTransaction(
                        utxo.getTx_hash(),
                        utxo.getTx_index(),
                        ISSUER_ADDRESS,
                        metadata
                ))
                .map(cliService::signTransaction)
                .map(cliService::readSignedTransaction)
                .flatMap(cardanoClient::submitTransaction);
    }
}




