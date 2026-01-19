package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

public record CertificateDto(
        String certificateUid,
        String studentName,
        String status,
        String issuedAt,
        String verificationCode,
        Long templateId
) {}
