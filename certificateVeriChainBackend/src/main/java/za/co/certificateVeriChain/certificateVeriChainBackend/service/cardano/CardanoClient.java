package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

import java.util.List;

@Service
public class CardanoClient {

    private static final String BLOCKFROST_KEY = "preprod_xxx";

    private final WebClient webClient;

    public CardanoClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://cardano-preprod.blockfrost.io/api/v0")
                .defaultHeader("project_id", BLOCKFROST_KEY)
                .build();
    }

    public Mono<List<UtxoResponse>> getUtxos(String address) {
        return webClient.get()
                .uri("/addresses/{address}/utxos", address)
                .retrieve()
                .bodyToFlux(UtxoResponse.class)
                .collectList();
    }

    public Mono<String> submitTransaction(byte[] signedTx) {
        return webClient.post()
                .uri("/tx/submit")
                .header("Content-Type", "application/cbor")
                .bodyValue(signedTx)
                .retrieve()
                .bodyToMono(String.class);
    }
}

