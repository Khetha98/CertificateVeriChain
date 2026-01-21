package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

import za.co.certificateVeriChain.certificateVeriChainBackend.enums.CertificateType;

import java.time.Instant;

public record CertificateIssuedDto(
        String certificateUid,
        String studentName,
        String studentSurname,
        String studentIdentifier,
        CertificateType certificateType,
        String organizationName,
        Instant issuedAt,
        String verificationCode,
        String status
) {}

