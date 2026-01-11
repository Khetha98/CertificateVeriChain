package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*@Getter
@Setter
@ToString*/
public record MintCertificateRequest(
        String studentName,
        Long templateId
) {}
