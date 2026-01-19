package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.UtxoResponse;

import java.util.List;
import java.util.Map;

@Service
public class CardanoClient {

    private static final String BLOCKFROST_KEY =
            "preprodaXqyV7rVS0jrT9fOs6nckpd0IF2qe6mQ";
    ObjectMapper objectMapper = new ObjectMapper();

    private final WebClient webClient;
    private static final Logger log =
            LoggerFactory.getLogger(CardanoClient.class);

    public CardanoClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://cardano-preprod.blockfrost.io/api/v0")
                .defaultHeader("project_id", BLOCKFROST_KEY)
                .build();
    }

    /* ---------------- UTXOs ---------------- */
    public Mono<List<UtxoResponse>> getUtxos(String address) {
        return webClient.get()
                .uri("/addresses/{address}/utxos", address)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("Blockfrost error: " + body)
                                ))
                )
                .bodyToFlux(UtxoResponse.class)
                .collectList();
    }

    /* ---------------- CURRENT SLOT ---------------- */
    public Mono<Long> getCurrentSlot() {
        return webClient.get()
                .uri("/blocks/latest")
                .retrieve()
                .bodyToMono(Map.class) // ✅ map to generic Map
                .map(map -> ((Number) map.get("slot")).longValue());
    }


    /* ---------------- SUBMIT TX ---------------- */
    public Mono<String> submitTransaction(byte[] signedTx) {
        return webClient.post()
                .uri("/tx/submit")
                .header("Content-Type", "application/cbor")
                .bodyValue(signedTx)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(body -> {
                                    System.err.println("=== Blockfrost ERROR RESPONSE ===");
                                    System.err.println("HTTP Status: " + resp.statusCode());
                                    System.err.println("Response Body: " + body);
                                    System.err.println("================================");
                                    return Mono.error(new RuntimeException(
                                            "Blockfrost error: " + resp.statusCode() + " - " + body
                                    ));
                                })
                )
                // Use bodyToMono(String.class) with explicit text/plain handling
                .bodyToMono(String.class)
                .map(String::trim) // remove whitespace/newlines from Blockfrost response
                .doOnNext(txHash -> System.out.println("Transaction submitted, hash: " + txHash))
                .doOnError(err -> System.err.println("TX submission failed: " + err.getMessage()));
    }



    /* ---------------- GET TRANSACTION METADATA ----------------
    public Mono<JsonNode> getTransaction(String txHash) {
        return webClient.get()
                .uri("/txs/{hash}/metadata", txHash)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("Blockfrost error: " + body)
                                ))
                )
                .bodyToMono(JsonNode.class);
    }*/

    /* ---------------- GET TRANSACTION METADATA ---------------- */
    public Mono<JsonNode> getTransaction(String txHash) {
        return webClient.get()
                .uri("/txs/{txHash}/metadata", txHash)
                .retrieve()
                .bodyToMono(String.class) // first get raw JSON as String
                .flatMap(raw -> {
                    log.info("RAW BLOCKFROST METADATA: {}", raw);
                    try {
                        // parse JSON into JsonNode
                        JsonNode root = objectMapper.readTree(raw);

                        // usually Blockfrost returns an array: take first element
                        if (root.isArray() && root.size() > 0) {
                            return Mono.just(root.get(0).path("json_metadata"));
                        } else {
                            return Mono.just(root);
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse JSON metadata", e);
                        return Mono.error(e);
                    }
                });
    }
}
