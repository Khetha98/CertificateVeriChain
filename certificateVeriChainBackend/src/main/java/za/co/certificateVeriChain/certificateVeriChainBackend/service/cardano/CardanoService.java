package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@Service
public class CardanoService {

    private final MetadataService metadataService;
    private final CardanoCliService cliService;
    private final CardanoClient cardanoClient;

    public CardanoService(
            MetadataService metadataService,
            CardanoCliService cliService,
            CardanoClient cardanoClient
    ) {
        this.metadataService = metadataService;
        this.cliService = cliService;
        this.cardanoClient = cardanoClient;
    }

    /**
     * Mint = anchor certificate hash on Cardano
     */
    public Mono<String> mintCertificateHash(String certificateHash) {

        Path metadataFile = metadataService.writeMetadataFile(certificateHash);
        Path unsignedTx = cliService.buildTransaction(metadataFile);
        Path signedTx = cliService.signTransaction(unsignedTx);
        byte[] cbor = cliService.readSignedTransaction(signedTx);

        return cardanoClient.submitTransaction(cbor);
    }
}


