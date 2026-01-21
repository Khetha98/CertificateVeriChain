package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.certificateVeriChain.certificateVeriChainBackend.enums.CertificateType;

/*@Getter
@Setter
@ToString*/
public record MintCertificateRequest(
        String studentName,
        String studentSurname,
        String studentIdentifier,
        CertificateType certificateType,
        Long templateId
) {}