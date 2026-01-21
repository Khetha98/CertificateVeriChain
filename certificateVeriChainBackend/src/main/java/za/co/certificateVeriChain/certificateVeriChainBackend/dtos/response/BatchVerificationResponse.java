package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BatchVerificationResponse {

    private boolean valid;
    private String message;

    private String batchUid;

    private String txHash;
    private ArrayList<VerificationResponse> verificationResponseList = new ArrayList<>();

    // Optional public info
    private String studentName;
    private String organizationName;
    private Instant issuedAt;

    public BatchVerificationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public BatchVerificationResponse(
            boolean valid,
            String message,
            String studentName,
            String organizationName,
            Instant issuedAt
    ) {
        this.valid = valid;
        this.message = message;
        this.studentName = studentName;
        this.organizationName = organizationName;
        this.issuedAt = issuedAt;
    }
}