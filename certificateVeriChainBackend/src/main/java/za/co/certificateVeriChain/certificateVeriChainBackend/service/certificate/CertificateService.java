package za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.MintCertificateRequest;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.CertificateTemplate;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.GovernanceApproval;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.ApprovalRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateTemplateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.UserRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.CardanoService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CertificateService {

    private final CardanoService cardanoService;
    private final ObjectMapper objectMapper;
    private final CertificateRepository certificateRepository;
    private final CertificateTemplateRepository templateRepo;
    private final ApprovalRepository approvalRepo;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepo;

    public CertificateService(CardanoService cardanoService, CertificateRepository certificateRepository, CertificateTemplateRepository templateRepo, ApprovalRepository approvalRepo, FileStorageService fileStorageService, UserRepository userRepo) {
        this.cardanoService = cardanoService;
        this.objectMapper = new ObjectMapper();
        this.certificateRepository = certificateRepository;
        this.templateRepo = templateRepo;
        this.approvalRepo = approvalRepo;
        this.fileStorageService = fileStorageService;
        this.userRepo = userRepo;
    }



    public Certificate issueCertificate(
            String studentName,
            Long templateId,
            User issuer
    ) {

        System.out.println("ISSUE CERTIFICATE CALLED");

        CertificateTemplate template = templateRepo
                .findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid template"));

        Certificate cert = new Certificate();
        cert.setIssuedBy(issuer);
        cert.setCertificateUid(System.currentTimeMillis());
        cert.setStudentName(studentName);
        cert.setOrganization(issuer.getOrganization());
        cert.setTemplate(template);

        String hash = DigestUtils.sha256Hex(canonicalize(cert));
        cert.setCertificateHash(hash);
        cert.setIssuedAt(Instant.now().toString());
        cert.setStatus("PENDING_APPROVAL");
        cert.setVerificationCode(UUID.randomUUID().toString());


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


    private String canonicalize(Certificate cert) {
        try {
            Map<String, Object> data = Map.of(
                    "certificateUid", cert.getCertificateUid(),
                    "studentName", cert.getStudentName(),
                    "organizationId", cert.getOrganization().getId(),
                    "templateId", cert.getTemplate().getId()
            );
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to canonicalize certificate", e);
        }
    }

    /** 🔐 anchor only after approvals */
    public void anchorToChain(Long certificateUid) {
        System.out.println("ANCHOR START for uid=" + certificateUid);
        Certificate cert = certificateRepository
                .findByCertificateUid(certificateUid)
                .orElseThrow();

        if (!"APPROVED".equals(cert.getStatus())) {
            throw new IllegalStateException("Certificate not approved");
        }

        // async anchor
        //cardanoService.anchorHash(cert.getCertificateHash())
                /*.subscribe(txHash -> {
                    cert.setTxHash(txHash);
                    cert.setStatus("ACTIVE");
                    certificateRepository.save(cert);
                });*/
        cardanoService.anchorHash(cert.getCertificateHash())
                .subscribe(txHash -> {
                    cert.setTxHash(txHash);
                    cert.setStatus("ACTIVE");
                    certificateRepository.save(cert);
                }, err -> {
                    cert.setStatus("ANCHOR_FAILED");
                    certificateRepository.save(cert);
                });
    }

    public byte[] generateCertificate(
            byte[] templatePdf,
            String studentName,
            String course,
            String issuer,
            String uid
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
        cs.showText(studentName);
        cs.endText();

        cs.setFont(PDType1Font.HELVETICA, 14);
        cs.beginText();
        cs.newLineAtOffset(200, 380);
        cs.showText(course);
        cs.endText();

        cs.beginText();
        cs.newLineAtOffset(200, 340);
        cs.showText("Certificate ID: " + uid);
        cs.endText();

        cs.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.save(out);
        doc.close();

        return out.toByteArray();
    }

    public byte[] generateVerifiedCertificate(String uid) {
        try {
            Certificate cert = certificateRepository
                    .findByCertificateUid(Long.parseLong(uid))
                    .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

            if (!"ACTIVE".equals(cert.getStatus())) {
                throw new IllegalStateException("Certificate not active");
            }

            CertificateTemplate template = cert.getTemplate();

            // 🔹 Download template PDF from MinIO
            byte[] templatePdf = fileStorageService.download(template.getFileUrl());

            return generateCertificate(
                    templatePdf,
                    cert.getStudentName(),
                    template.getDescription(), // or course name
                    cert.getOrganization().getName(),
                    String.valueOf(cert.getCertificateUid())
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate verified certificate", e);
        }
    }

}