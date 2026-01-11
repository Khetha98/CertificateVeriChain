package za.co.certificateVeriChain.certificateVeriChainBackend.controller.cardanoController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.TxVerifyResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.OnChainVerificationService;

@RestController
@RequestMapping("/verify-chain")
@RequiredArgsConstructor
public class TxVerificationController {

    private final OnChainVerificationService verificationService;

    @GetMapping("/tx/{hash}")
    public Mono<ResponseEntity<TxVerifyResponse>>
    verify(@PathVariable String hash) {

        return verificationService.verifyTx(hash)
                .map(ResponseEntity::ok);
    }
}

