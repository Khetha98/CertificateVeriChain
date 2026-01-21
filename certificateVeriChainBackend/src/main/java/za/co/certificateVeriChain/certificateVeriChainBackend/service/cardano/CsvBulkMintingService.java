package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.MintCertificateRequest;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.BatchVerificationResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.BulkMintResult;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.VerificationResponse;
import za.co.certificateVeriChain.certificateVeriChainBackend.enums.CertificateType;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.CertificateBatch;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.CertificateService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvBulkMintingService {

    private final CertificateService certificateService;

    public BatchVerificationResponse mintFromCsv(
            Path csvFile,
            User issuer,
            Long templateId,
            CertificateType certificateType
    ) throws IOException {

        List<String> lines = Files.readAllLines(csvFile);
        List<MintCertificateRequest> requests = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).trim().split(",");

            if (parts.length < 3) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid CSV at line " + (i + 1)
                );
            }

            requests.add(new MintCertificateRequest(
                    parts[0].trim(),
                    parts[1].trim(),
                    parts[2].trim(),
                    certificateType,
                    templateId
            ));
        }

        List<Certificate> certs =
                certificateService.createCertificates(requests, issuer);

        CertificateBatch batch = certificateService.anchorBatch(certs);
        String txHash = batch.getTxHash();


        certificateService.activateBatch(certs, txHash);

        BatchVerificationResponse response = new BatchVerificationResponse();
        response.setValid(true);
        response.setMessage("Batch minted successfully");
        response.setBatchUid(batch.getMerkleRoot());
        response.setTxHash(batch.getTxHash());

        for (Certificate cert : certs) {
            VerificationResponse vr = new VerificationResponse(
                    true,
                    "Minted",
                    cert.getStudentName(),
                    cert.getOrganization().getName(),
                    cert.getIssuedAt()
            );
            vr.setCertificateUid(cert.getCertificateUid());
            vr.setCertificateType(cert.getCertificateType().name());

            response.getVerificationResponseList().add(vr);
        }

        return response;

    }

}


