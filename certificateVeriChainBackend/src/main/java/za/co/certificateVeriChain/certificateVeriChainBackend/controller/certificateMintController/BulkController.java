package za.co.certificateVeriChain.certificateVeriChainBackend.controller.certificateMintController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.BulkMintResult;
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
    public ResponseEntity<BulkMintResult> bulkMint(
            @RequestParam Long templateId,
            @AuthenticationPrincipal User issuer,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        // save uploaded CSV to temp file
        Path temp = Files.createTempFile("bulk-mint-", ".csv");
        file.transferTo(temp);

        BulkMintResult result =
                bulkService.mintFromCsv(temp, templateId, issuer);

        return ResponseEntity.ok(result);
    }
}


