package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

public record CertificateTemplateDto(
        Long id,
        String templateName,
        String description,
        String fileUrl,
        String createdAt
) {}