package za.co.certificateVeriChain.certificateVeriChainBackend.controller.verificationController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;

@RestController
@RequestMapping
public class VerificationController {

    CertificateRepository certificateRepository;

    @GetMapping("/verify/{hash}")
    public Certificate verify(@PathVariable String hash) {
        return certificateRepository
                .findByCertificateHash(hash)
                .orElseThrow();
    }
}
