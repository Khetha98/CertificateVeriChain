package za.co.certificateVeriChain.certificateVeriChainBackend.controller.certificateMintController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.MintCertificateRequest;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.CertificateApprovalDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.CertificateDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.CertificateIssuedDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.PendingApprovalDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.*;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.ApprovalRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateTemplateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano.RevocationService;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.CertificateService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/issuer/certificates")
@RequiredArgsConstructor
public class CertificateMintingController {

    private final CertificateService certificateService;
    private final RevocationService revocationService;
    private final CertificateRepository certificateRepository;
    private final CertificateTemplateRepository certificateTemplateRepository;
    private final ApprovalRepository approvalRepo;

    // ✅ Return list of DTOs instead of entities
    @GetMapping
    public ResponseEntity<List<CertificateDto>> list(@AuthenticationPrincipal User user) {
        List<CertificateDto> dtos = certificateRepository
                .findByOrganizationId(user.getOrganization().getId())
                .stream()
                .map(cert -> new CertificateDto(
                        cert.getCertificateUid(),
                        cert.getStudentName(),
                        cert.getStudentSurname(),
                        cert.getStatus(),
                        cert.getIssuedAt(),
                        cert.getVerificationCode(),
                        cert.getTemplate().getId()
                ))
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{uid}/revoke")
    @PreAuthorize("hasRole('ISSUER')")
    public ResponseEntity<CertificateDto> revoke(@PathVariable String uid) {
        Certificate cert = revocationService.revoke(uid).block();

        return ResponseEntity.ok(new CertificateDto(
                cert.getCertificateUid(),
                cert.getStudentName(),
                cert.getStudentSurname(),
                cert.getStatus(),
                cert.getIssuedAt(),
                cert.getVerificationCode(),
                cert.getTemplate().getId()
        ));
    }


    @GetMapping("/approvals/pending")
    public List<PendingApprovalDto> pendingApprovals(@AuthenticationPrincipal User user) {
        return approvalRepo.findPendingForUser(user.getId());
    }

    @PostMapping
    public ResponseEntity<CertificateIssuedDto> mint(
            @RequestBody MintCertificateRequest req,
            @AuthenticationPrincipal User user
    ) {
        Certificate cert = certificateService.issueCertificate(req, user);

        return ResponseEntity.ok(
                new CertificateIssuedDto(
                        cert.getCertificateUid(),
                        cert.getStudentName(),
                        cert.getStudentSurname(),
                        cert.getStudentIdentifier(),
                        cert.getCertificateType(),
                        cert.getOrganization().getName(),
                        cert.getIssuedAt(),
                        cert.getVerificationCode(),
                        cert.getStatus()
                )
        );
    }


    @PostMapping("/{uid}/approve")
    public ResponseEntity<?> approve(@PathVariable String uid, @AuthenticationPrincipal User user) {
        Certificate cert = certificateRepository
                .findByCertificateUid(uid)
                .orElseThrow();

        if (cert.getIssuedBy().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Issuer cannot approve their own certificate");
        }

        GovernanceApproval approval = approvalRepo
                .findByCertificateUidAndApproverUserId(uid, user.getId())
                .orElseThrow(() -> new IllegalStateException("No approval record"));

        if (approval.isApproved()) return ResponseEntity.ok().build();

        approval.setApproved(true);
        approvalRepo.save(approval);

        long total = approvalRepo.countByCertificateUid(uid);
        long approved = approvalRepo.countByCertificateUidAndApprovedTrue(uid);

        if (approved == total) {
            cert.setStatus("APPROVED");
            certificateRepository.save(cert);
            certificateService.anchorToChain(uid);
        }

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ISSUER')")
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable String uid, @AuthenticationPrincipal User user) {
        Certificate cert = certificateRepository.findByCertificateUid(uid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found"));

        if (!cert.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this certificate");
        }

        certificateRepository.delete(cert);
        return ResponseEntity.ok().build(); // ✅ always returns 200 OK with empty JSON
    }



}


