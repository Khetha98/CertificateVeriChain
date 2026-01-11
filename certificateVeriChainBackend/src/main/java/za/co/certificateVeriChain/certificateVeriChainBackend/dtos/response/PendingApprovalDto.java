package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

public class PendingApprovalDto {

    private Long certificateUid;
    private String studentName;
    private String templateName;
    private String issuedAt;
    private String issuerName;

    public PendingApprovalDto(
            Long certificateUid,
            String studentName,
            String templateName,
            String issuedAt,
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
