package za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.MintCertificateRequest;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.*;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.*;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoClient;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoService;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.MerkleService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class CertificateService {

    private static final Logger log =
            LoggerFactory.getLogger(CardanoClient.class);

    private final CardanoService cardanoService;
    private final ObjectMapper objectMapper;
    private final CertificateRepository certificateRepository;
    private final CertificateTemplateRepository templateRepo;
    private final ApprovalRepository approvalRepo;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepo;
    private final CertificateBatchRepository certificateBatchRepository;
    private final MerkleService merkleService;

    public CertificateService(CardanoService cardanoService, CertificateRepository certificateRepository, CertificateTemplateRepository templateRepo, ApprovalRepository approvalRepo, FileStorageService fileStorageService, UserRepository userRepo, CertificateBatchRepository certificateBatchRepository, MerkleService merkleService) {
        this.cardanoService = cardanoService;
        this.objectMapper = new ObjectMapper();
        this.certificateRepository = certificateRepository;
        this.templateRepo = templateRepo;
        this.approvalRepo = approvalRepo;
        this.fileStorageService = fileStorageService;
        this.userRepo = userRepo;
        this.certificateBatchRepository = certificateBatchRepository;
        this.merkleService = merkleService;

    }



    public Certificate issueCertificate(
            MintCertificateRequest request,
            User issuer
    ) {

        CertificateTemplate template = templateRepo
                .findById(request.templateId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid template"));

        Certificate cert = new Certificate();
        cert.setIssuedBy(issuer);
        cert.setCertificateType(request.certificateType());
        cert.setCertificateUid(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        cert.setStudentName(request.studentName());
        cert.setStudentSurname(request.studentSurname());
        cert.setOrganization(issuer.getOrganization());
        cert.setTemplate(template);
        cert.setStudentIdentifier(request.studentIdentifier());

        String hash = DigestUtils.sha256Hex(canonicalize(cert));
        String cleanTxHash = hash.replace("\"", "").trim();
        //certificate.setTxHash(cleanTxHash);
        cert.setCertificateHash(cleanTxHash);
        cert.setIssuedAt(Instant.now());
        cert.setStatus("PENDING_APPROVAL");
        cert.setVerificationCode(UUID.randomUUID().toString());

        log.info("Lengths => hash={}, txHash={}, verificationCode={}",
                cert.getCertificateHash().length(),
                cert.getTxHash() != null ? cert.getTxHash().length() : 0,
                cert.getVerificationCode().length()
        );

        List<User> orgUsers = userRepo.findByOrganizationId(
                issuer.getOrganization().getId()
        );

        if (orgUsers.size() == 1) {
            cert.setStatus("APPROVED");
            certificateRepository.save(cert);

            anchorToChain(cert.getCertificateUid());
            return cert;
        }

// Otherwise create approvals
        for (User u : orgUsers) {
            if (!u.getId().equals(issuer.getId())) {
                approvalRepo.save(
                        new GovernanceApproval(cert.getCertificateUid(), u.getId(), false)
                );
            }
        }

        return certificateRepository.save(cert);
    }

    public List<Certificate> createCertificates(
            List<MintCertificateRequest> requests,
            User issuer
    ) {
        CertificateTemplate template = templateRepo
                .findById(requests.get(0).templateId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid template"));

        List<Certificate> certificates = new ArrayList<>();

        for (MintCertificateRequest req : requests) {
            Certificate cert = createCertificate(issuer, req, template);
            certificates.add(cert);
        }

        return certificateRepository.saveAll(certificates);
    }

    public CertificateBatch anchorBatch(List<Certificate> certificates) {

        List<String> hashes = certificates.stream()
                .map(Certificate::getCertificateHash)
                .sorted()
                .toList();

        // Generate Merkle root
        String merkleRoot = merkleService.calculateRoot(hashes);

        // Generate proofs
        Map<String, List<String>> proofs = merkleService.generateProofs(hashes);

        // Anchor on-chain
        String txHash = cardanoService.anchorHash(merkleRoot).block();

        // Save batch
        CertificateBatch batch = new CertificateBatch();
        batch.setMerkleRoot(merkleRoot);
        batch.setTxHash(txHash);
        batch.setAnchoredAt(Instant.now());

        certificateBatchRepository.save(batch);

        // Attach batch + proof to each certificate
        certificates.forEach(cert -> {
            cert.setBatch(batch);

            List<String> proof = proofs.get(cert.getCertificateHash());
            cert.setMerkleProof(merkleService.toJson(proof));
        });

        return batch;
    }




    public void activateBatch(List<Certificate> certificates, String txHash) {
        for (Certificate cert : certificates) {
            cert.setTxHash(txHash);
            cert.setStatus("ACTIVE");
        }
        certificateRepository.saveAll(certificates);
    }

    private Certificate createCertificate(User issuer, MintCertificateRequest request, CertificateTemplate template){
        Certificate cert = new Certificate();
        cert.setIssuedBy(issuer);
        cert.setCertificateType(request.certificateType());
        cert.setCertificateUid(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        cert.setStudentName(request.studentName());
        cert.setStudentSurname(request.studentSurname());
        cert.setOrganization(issuer.getOrganization());
        cert.setTemplate(template);
        cert.setStudentIdentifier(request.studentIdentifier());

        String hash = DigestUtils.sha256Hex(canonicalize(cert));
        String cleanTxHash = hash.replace("\"", "").trim();
        //certificate.setTxHash(cleanTxHash);
        cert.setCertificateHash(cleanTxHash);
        cert.setIssuedAt(Instant.now());
        cert.setStatus("PENDING_APPROVAL");
        cert.setVerificationCode(UUID.randomUUID().toString());
        List<User> orgUsers = userRepo.findByOrganizationId(
                issuer.getOrganization().getId()
        );

        if (orgUsers.size() == 1) {
            cert.setStatus("APPROVED");
            certificateRepository.save(cert);

            return cert;
        }

// Otherwise create approvals
        for (User u : orgUsers) {
            if (!u.getId().equals(issuer.getId())) {
                approvalRepo.save(
                        new GovernanceApproval(cert.getCertificateUid(), u.getId(), false)
                );
            }
        }
        return cert;
    }


    private String canonicalize(Certificate cert) {
        try {
            Map<String, Object> data = Map.of(
                    "certificateUid", cert.getCertificateUid(),
                    "studentName", cert.getStudentName(),
                    "organizationId", cert.getOrganization().getId(),
                    "templateId", cert.getTemplate().getId()
            );
            System.out.println("Canonicalizing certificate: " + data);
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to canonicalize certificate", e);
        }
    }

    /** 🔐 anchor only after approvals */
    public void anchorToChain(String certificateUid) {
        Certificate cert = certificateRepository.findByCertificateUid(certificateUid)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

        if (!"APPROVED".equals(cert.getStatus())) {
            throw new IllegalStateException("Certificate not approved");
        }

        try {
            // Offload the reactive call to a boundedElastic thread pool to avoid deadlocks
            String txHash = Mono.fromCallable(() ->
                            cardanoService.anchorHash(cert.getCertificateHash())
                                    .doOnNext(hash -> System.out.println("Transaction hash received: " + hash))
                                    .doOnError(err -> System.err.println("TX submission failed: " + err.getMessage()))
                                    .block()
                    )
                    .subscribeOn(Schedulers.boundedElastic())
                    .block(); // Block safely on a separate thread

            cert.setTxHash(txHash);
            cert.setStatus("ACTIVE");
            certificateRepository.save(cert);

        } catch (Exception e) {
            System.err.println("Anchor failed: " + e.getMessage());
            cert.setStatus("ANCHOR_FAILED: " + e.getMessage());
            certificateRepository.save(cert);
        }
    }



    public byte[] generateCertificate(
            byte[] templatePdf,
            Certificate cert
    ) throws IOException {

        PDDocument doc = PDDocument.load(templatePdf);
        PDPage page = doc.getPage(0);

        PDPageContentStream cs = new PDPageContentStream(
                doc,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true
        );

        cs.setFont(PDType1Font.HELVETICA_BOLD, 24);
        cs.beginText();
        cs.newLineAtOffset(200, 420);
        cs.showText(cert.getStudentName() + " " + (cert.getStudentSurname() != null ? cert.getStudentSurname() : ""));
        cs.endText();

        cs.setFont(PDType1Font.HELVETICA, 14);

        int y = 380;
        cs.beginText();
        cs.newLineAtOffset(200, y);
        cs.showText("Certificate Type: " + cert.getCertificateType());
        cs.endText();

        y -= 30;
        cs.beginText();
        cs.newLineAtOffset(200, y);
        cs.showText("Student ID: " + cert.getStudentIdentifier());
        cs.endText();

        y -= 30;
        cs.beginText();
        cs.newLineAtOffset(200, y);
        cs.showText("Organization: " + cert.getOrganization().getName());
        cs.endText();

        y -= 30;
        cs.beginText();
        cs.newLineAtOffset(200, y);
        cs.showText("Certificate UID: " + cert.getCertificateUid());
        cs.endText();

        y -= 30;
        cs.beginText();
        cs.newLineAtOffset(200, y);
        cs.showText("Status: " + cert.getStatus());
        cs.endText();

        cs.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.save(out);
        doc.close();

        return out.toByteArray();
    }

    public byte[] addAdataFromBlankPage(Certificate cert) throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(doc, page);

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.setLeading(16f);
        cs.newLineAtOffset(50, 750);

        cs.showText("Certificate ID: " + cert.getCertificateUid());
        cs.newLine();
        cs.showText("Student Name: " + cert.getStudentName());
        cs.newLine();
        cs.showText("Surname: " + cert.getStudentSurname());
        cs.newLine();
        cs.showText("Identifier: " + cert.getStudentIdentifier());
        cs.newLine();
        cs.showText("Type: " + cert.getCertificateType());

        cs.endText();
        cs.close();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doc.save(outputStream);
        doc.close();

        return  outputStream.toByteArray();
    }


    public byte[] generateVerifiedCertificate(String uid) {
        try {
            Certificate cert = certificateRepository
                    .findByCertificateUid(uid)
                    .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

            if (!"ACTIVE".equals(cert.getStatus())) {
                throw new IllegalStateException("Certificate not active");
            }

            CertificateTemplate template = cert.getTemplate();
            byte[] templatePdf = fileStorageService.download(template.getFileUrl());

            return generateCertificate(templatePdf, cert);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate verified certificate", e);
        }
    }


}