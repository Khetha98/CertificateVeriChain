package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

public record CertificateDto(
        Long certificateUid,
        String studentName,
        String status,
        String issuedAt,
        String verificationCode,
        Long templateId
) {}
