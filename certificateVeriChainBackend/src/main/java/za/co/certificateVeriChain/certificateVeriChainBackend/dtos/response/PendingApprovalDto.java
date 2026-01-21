package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

import java.time.Instant;

public class PendingApprovalDto {

    private String certificateUid;
    private String studentName;
    private String templateName;
    private Instant issuedAt;
    private String issuerName;

    public PendingApprovalDto(
            String certificateUid,
            String studentName,
            String templateName,
            Instant issuedAt,
            String issuerName
    ) {
        this.certificateUid = certificateUid;
        this.studentName = studentName;
        this.templateName = templateName;
        this.issuedAt = issuedAt;
        this.issuerName = issuerName;
    }

    // getters (no setters needed)
}
