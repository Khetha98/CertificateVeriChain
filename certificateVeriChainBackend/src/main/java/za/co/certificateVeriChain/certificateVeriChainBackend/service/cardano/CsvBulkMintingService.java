package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.BulkMintResult;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.CertificateService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvBulkMintingService {

    private final CertificateService certificateService;

    public BulkMintResult mintFromCsv(
            Path csvFile,
            Long templateId,
            User issuer
    ) throws IOException {

        List<String> lines = Files.readAllLines(csvFile);

        BulkMintResult result = new BulkMintResult();
        result.setTotal(lines.size());

        int minted = 0;

        for (String line : lines) {
            String[] p = line.split(",");

            certificateService.issueCertificate(
                    p[0].trim(),   // student name
                    templateId,
                    issuer
            );
            minted++;
        }

        result.setMinted(minted);
        return result;
    }
}


