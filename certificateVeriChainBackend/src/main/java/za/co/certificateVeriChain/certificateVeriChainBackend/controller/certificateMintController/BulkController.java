package za.co.certificateVeriChain.certificateVeriChainBackend.controller.certificateMintController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.MintCertificateRequest;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.BatchVerificationResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.BulkMintResult;
import za.co.certificateVeriChain.certificateVeriChainBackend.enums.CertificateType;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CsvBulkMintingService;

import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/issuer/bulk")
@RequiredArgsConstructor
public class BulkController {

    private final CsvBulkMintingService bulkService;

    @PostMapping("/mint")
    public ResponseEntity<BatchVerificationResponse> bulkMint(
            @AuthenticationPrincipal User issuer,
            @RequestParam("file") MultipartFile file,
            @RequestParam("templateId") Long templateId,
            @RequestParam("certificateType") CertificateType certificateType
    ) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file is required");
        }

        Path temp = Files.createTempFile("bulk-mint-", ".csv");
        file.transferTo(temp);

        BatchVerificationResponse result = bulkService.mintFromCsv(temp, issuer, templateId, certificateType);

        return ResponseEntity.ok(result);
    }

}



